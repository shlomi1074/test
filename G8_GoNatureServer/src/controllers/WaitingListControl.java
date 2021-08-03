package controllers;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

import controllers.sqlHandlers.OrderQueries;
import controllers.sqlHandlers.ParkQueries;
import controllers.sqlHandlers.TravelersQueries;
import logic.Messages;
import logic.Order;
import logic.OrderStatusName;
import logic.Park;
import resources.MsgTemplates;
import server.GoNatureServer;

/**
 * WaitingListControl class handle all the Waiting list travelers in the system.
 * 
 */

public class WaitingListControl {

	private static OrderQueries orderQueries = new OrderQueries(GoNatureServer.mysqlconnection);
	private static ParkQueries parkQueries = new ParkQueries(GoNatureServer.mysqlconnection);
	private static TravelersQueries travelerQueries = new TravelersQueries(GoNatureServer.mysqlconnection);

	/**
	 * This function receives canceled order and notify the next order in the waiting list.
	 * 
	 * @param order The canceled order.
	 */
	public static void notifyPersonFromWaitingList(Order order) {
		Park park = parkQueries.getParkById(new ArrayList<String>(Arrays.asList(String.valueOf(order.getParkId()))));
		Order toNotify = getOrderFromWaitingList(order.getOrderDate(), order.getOrderTime(), park);

		if (toNotify == null)
			return;

		String orderId = String.valueOf(toNotify.getOrderId());
		String status = OrderStatusName.WAITING_HAS_SPOT.toString();
		orderQueries.setOrderStatusWithIDandStatus(new ArrayList<String>(Arrays.asList(status, orderId)));

		orderQueries.addOrderAlert(toNotify.getOrderId(), LocalDate.now().toString(), LocalTime.now().toString(),
				LocalTime.now().plusHours(1).toString());
		sendWaitingListMessage(toNotify);
	}

	private static Order getOrderFromWaitingList(String date, String hour, Park park) {
		String parkId = String.valueOf(park.getParkId());
		String maxVisitors = String.valueOf(park.getMaxVisitors());
		String estimatedStayTime = String.valueOf(park.getEstimatedStayTime());
		String gap = String.valueOf(park.getGapBetweenMaxAndCapacity());
		ArrayList<String> parameters = new ArrayList<String>(
				Arrays.asList(parkId, maxVisitors, estimatedStayTime, date, hour, gap));
		ArrayList<Order> ordersThatMatchWaitingList = orderQueries.findMatchingOrdersInWaitingList(parameters);
		return ordersThatMatchWaitingList.size() == 0 ? null : ordersThatMatchWaitingList.get(0);
	}

	private static void sendWaitingListMessage(Order order) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		LocalDateTime now = LocalDateTime.now();
		String dateAndTime = dtf.format(now);

		String date = dateAndTime.split(" ")[0];
		String time = dateAndTime.split(" ")[1];
		String travelerId = order.getTravelerId();
		int orderId = order.getOrderId();

		Park park = parkQueries.getParkById(new ArrayList<String>(Arrays.asList(String.valueOf(order.getParkId()))));
		String subject = MsgTemplates.waitingListPlaceInPark[0].toString();
		String content = String.format(MsgTemplates.waitingListPlaceInPark[1].toString(), park.getParkName(),
				order.getOrderDate(), order.getOrderTime());

		Messages msg = new Messages(0, travelerId, date, time, subject, content, orderId);

		/* Send email */
		EmailControl.sendEmail(msg);

		/* Add message to DB */
		ArrayList<String> parameters = new ArrayList<>(
				Arrays.asList(travelerId, date, time, subject, content, String.valueOf(orderId)));
		travelerQueries.sendMessageToTraveler(parameters);

	}

}
