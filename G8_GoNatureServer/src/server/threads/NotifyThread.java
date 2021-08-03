package server.threads;

import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import controllers.EmailControl;
import controllers.WaitingListControl;
import controllers.sqlHandlers.OrderQueries;
import controllers.sqlHandlers.ParkQueries;
import controllers.sqlHandlers.TravelersQueries;
import logic.Messages;
import logic.Order;
import logic.OrderStatusName;
import logic.Park;
import resources.MsgTemplates;

/**
 * NotifyThread class implements Runnable.
 * 
 * This class handle all the automated functionality:
 * Send reminder 24 hours before visit.
 * Cancel the visit if the traveler did not confirmed within two hours.
 * Notify the next in the waiting list
 *
 */
public class NotifyThread implements Runnable {

	private final int second = 1000;
	private final int minute = second * 60;
	private OrderQueries orderQueries;
	private ParkQueries parkQueries;
	private TravelersQueries travelerQueries;

	public NotifyThread(Connection mysqlconnection) {
		orderQueries = new OrderQueries(mysqlconnection);
		parkQueries = new ParkQueries(mysqlconnection);
		travelerQueries = new TravelersQueries(mysqlconnection);
	}

	/**
	 * This function handle all the automated functionality:
	 * Send reminder 24 hours before visit.
	 * Cancel the visit if the traveler did not confirmed within two hours.
	 * Notify the next in the waiting list
	 */
	@Override
	public void run() {

		while (true) {

			ArrayList<Order> pendingOrders = getPendingOrders();
			for (Order order : pendingOrders) {
				if (isDateLessThan24Hours(order.getOrderDate(), order.getOrderTime())) {
					String status = OrderStatusName.PENDING_EMAIL_SENT.toString();
					String orderId = String.valueOf(order.getOrderId());
					orderQueries.setOrderStatusWithIDandStatus(new ArrayList<String>(Arrays.asList(status, orderId)));
					sendConfirmationMessage(order);
					orderQueries.addOrderAlert(order.getOrderId(), LocalDate.now().toString(),
							LocalTime.now().toString(), LocalTime.now().plusHours(2).toString());
				}
			}
			CancelOrderAndNotify();
			deleteAlerts();
			try {
				Thread.sleep(1 * minute);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void deleteAlerts() {
		ArrayList<Integer> alertToDelete = orderQueries.getOrderAlertIdWithExpiryAlertTime(LocalDate.now().toString(),
				LocalTime.now().toString());
		for (Integer alertId : alertToDelete)
			orderQueries.deleteOrderAlert(alertId);
	}

	private void CancelOrderAndNotify() {

		ArrayList<Order> ordersToChangeStatus = orderQueries.getOrderWithExpiryAlertTime(LocalDate.now().toString(),
				LocalTime.now().toString());
		for (Order order : ordersToChangeStatus) {
			ArrayList<String> parameters = new ArrayList<>(
					Arrays.asList(OrderStatusName.CANCELED.toString(), String.valueOf(order.getOrderId())));
			orderQueries.setOrderStatusWithIDandStatus(parameters);
			sendCancelMessage(order);
			WaitingListControl.notifyPersonFromWaitingList(order);
		}
	}

	/*
	 * This message is for a person to approve his visit.
	 */
	private void sendConfirmationMessage(Order order) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		LocalDateTime now = LocalDateTime.now();
		String dateAndTime = dtf.format(now);

		String date = dateAndTime.split(" ")[0];
		String time = dateAndTime.split(" ")[1];
		String travelerId = order.getTravelerId();
		int orderId = order.getOrderId();

		Park park = parkQueries.getParkById(new ArrayList<String>(Arrays.asList(String.valueOf(order.getParkId()))));
		String subject = MsgTemplates.ConfirmOrder24hoursBeforeVisit[0];
		String content = String.format(MsgTemplates.ConfirmOrder24hoursBeforeVisit[1].toString(), park.getParkName(),
				order.getOrderDate(), order.getOrderTime());

		Messages msg = new Messages(0, travelerId, date, time, subject, content, orderId);

		/* Send email */
		EmailControl.sendEmail(msg);

		/* Add message to DB */
		ArrayList<String> parameters = new ArrayList<>(
				Arrays.asList(travelerId, date, time, subject, content, String.valueOf(orderId)));
		travelerQueries.sendMessageToTraveler(parameters);

	}

	/*
	 * This message is for an canceled order.
	 */
	private void sendCancelMessage(Order order) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		LocalDateTime now = LocalDateTime.now();
		String dateAndTime = dtf.format(now);

		String date = dateAndTime.split(" ")[0];
		String time = dateAndTime.split(" ")[1];
		String travelerId = order.getTravelerId();
		int orderId = order.getOrderId();

		Park park = parkQueries.getParkById(new ArrayList<String>(Arrays.asList(String.valueOf(order.getParkId()))));
		String subject = MsgTemplates.orderCancel[0];
		String content = String.format(MsgTemplates.orderCancel[1].toString(), park.getParkName(), order.getOrderDate(),
				order.getOrderTime());

		Messages msg = new Messages(0, travelerId, date, time, subject, content, orderId);

		/* Send email */
		EmailControl.sendEmail(msg);

		/* Add message to DB */
		ArrayList<String> parameters = new ArrayList<>(
				Arrays.asList(travelerId, date, time, subject, content, String.valueOf(orderId)));
		travelerQueries.sendMessageToTraveler(parameters);
	}

	/*
	 * This message is for a person in the waiting list.
	 */

	private ArrayList<Order> getPendingOrders() {
		return orderQueries.getPendingOrders();
	}

	private boolean isDateLessThan24Hours(String date, String time) {

		String combinedVisit = date + " " + time;
		String combinedToday = LocalDate.now().toString() + " " + LocalTime.now().toString();

		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Date visitDate = new Date();
		Date todayDate = new Date();

		try {
			todayDate = sdfDate.parse(combinedToday);
			visitDate = sdfDate.parse(combinedVisit);
		} catch (ParseException e) {
			System.out.println("Failed to parse dates");
			e.printStackTrace();
			return false;
		}

		long diffInMills = visitDate.getTime() - todayDate.getTime();
		long diffInHour = TimeUnit.MILLISECONDS.toHours(diffInMills);

		if (diffInHour < 24 && diffInHour > 22)
			return true;
		return false;
	}

}
