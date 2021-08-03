package server.threads;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import controllers.EmailControl;
import controllers.sqlHandlers.OrderQueries;
import controllers.sqlHandlers.ParkQueries;
import controllers.sqlHandlers.TravelersQueries;
import controllers.sqlHandlers.MysqlConnection;
import logic.Messages;
import logic.Order;
import logic.OrderStatusName;
import logic.Park;
import resources.MsgTemplates;

/**
 * NotifyWaitingList class implements Runnable.
 * 
 * This class handle all the automated functionality:
 * Notify a person from waiting list that he has a spot in the park for his order.
 * Monitoring the order status for 1 hour.
 * Notify the next in the waiting list if the traveler did not confirm his order.
 *
 */
public class NotifyWaitingList implements Runnable {

	private String date, hour;
	private Park park;
	private Connection mysqlconnection;
	private OrderQueries orderQueries;
	private ParkQueries parkQueries;
	private TravelersQueries travelerQueries;
	private Order order;

	private final int second = 1000;
	private final int minute = second * 60;

	
	public NotifyWaitingList(Order order) {
		try {
			mysqlconnection = MysqlConnection.getInstance().getConnection();
			orderQueries = new OrderQueries(mysqlconnection);
			parkQueries = new ParkQueries(mysqlconnection);
			travelerQueries = new TravelersQueries(mysqlconnection);
			this.date = order.getOrderDate();
			this.hour = order.getOrderTime();
			ArrayList<String> parameters = new ArrayList<>(Arrays.asList(String.valueOf(order.getParkId())));
			this.park = parkQueries.getParkById(parameters);
			this.order = order;
		} catch (Exception e) {
			System.out.println("Exception was thrown - notify waiting list");
			e.printStackTrace();
		}
	}

	/**
	 * This function check if the traveler confirmed or canceled his order.
	 * if he did not confirmed his order within one hour - the order canceled
	 * automatically and we notify the next in the waiting list (if there is
	 * someone)
	 * 
	 */
	@Override
	public void run() {
		Order order = notifyPersonFromWaitingList(date, hour, park);

		System.out.println("Entered notify waiting list");
		if (order == null) 
			return;
		

		String orderId = String.valueOf(order.getOrderId());
		String status = OrderStatusName.WAITING_HAS_SPOT.toString();
		orderQueries.setOrderStatusWithIDandStatus(new ArrayList<String>(Arrays.asList(status, orderId)));
		sendMessages(order);

		int totalSleep = 0;
		Order updatedOrder = null;

		while (totalSleep != 60) {
			System.out.println("Entred while");
			updatedOrder = orderQueries.getOrderByID(order.getOrderId());
			if (updatedOrder.getOrderStatus().equals(OrderStatusName.CANCELED.toString())
					|| updatedOrder.getOrderStatus().equals(OrderStatusName.CONFIRMED.toString()))
				break;
			try {
				Thread.sleep(1 * minute);
				totalSleep += 1;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		if (!updatedOrder.getOrderStatus().equals(OrderStatusName.CONFIRMED.toString())) {
			status = OrderStatusName.CANCELED.toString();
			orderId = String.valueOf(updatedOrder.getOrderId());
			orderQueries.setOrderStatusWithIDandStatus(new ArrayList<String>(Arrays.asList(status, orderId)));

			// Passing the orignal order that was canceled.
			NotifyWaitingList notifyWaitingList = new NotifyWaitingList(this.order);
			new Thread(notifyWaitingList).start();
		}

	}

	// Email + DB
	private void sendMessages(Order order) {
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

	private Order notifyPersonFromWaitingList(String date, String hour, Park park) {
		String parkId = String.valueOf(park.getParkId());
		String maxVisitors = String.valueOf(park.getMaxVisitors());
		String estimatedStayTime = String.valueOf(park.getEstimatedStayTime());
		String gap = String.valueOf(park.getGapBetweenMaxAndCapacity());
		ArrayList<String> parameters = new ArrayList<String>(
				Arrays.asList(parkId, maxVisitors, estimatedStayTime, date, hour, gap));
		ArrayList<Order> ordersThatMatchWaitingList = orderQueries.findMatchingOrdersInWaitingList(parameters);
		return ordersThatMatchWaitingList.size() == 0 ? null : ordersThatMatchWaitingList.get(0);
	}

}