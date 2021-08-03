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
import controllers.sqlHandlers.OrderQueries;
import controllers.sqlHandlers.ParkQueries;
import controllers.sqlHandlers.TravelersQueries;
import logic.Messages;
import logic.Order;
import logic.OrderStatusName;
import logic.Park;
import resources.MsgTemplates;

public class CancelOrders implements Runnable {

	private final int second = 1000;
	private final int minute = second * 60;
	private OrderQueries orderQueries;
	private ParkQueries parkQueries;
	private TravelersQueries travelerQueries;
	
	public CancelOrders(Connection mysqlconnection) {
		orderQueries = new OrderQueries(mysqlconnection);
		parkQueries = new ParkQueries(mysqlconnection);
		travelerQueries = new TravelersQueries(mysqlconnection);
	}
	
	@Override
	public void run() {
		
		while (true) {
			System.out.println("Looking for relevant orders to cancel");
			ArrayList<Order> pendingOrders = getRelevantOrders();

			for (Order order : pendingOrders) {
				if (isDateLessThan22Hours(order.getOrderDate(), order.getOrderTime())) {
					System.out.println(order.getOrderId());
					String status = OrderStatusName.CANCELED.toString();
					String orderId = String.valueOf(order.getOrderId());
					orderQueries.setOrderStatusWithIDandStatus(new ArrayList<String>(Arrays.asList(status, orderId)));
					
					/* Need to Send cancel order msg */
					sendCancelMessage(order);

					NotifyWaitingList notifyWaitingList = new NotifyWaitingList(order);
					new Thread(notifyWaitingList).start();
				}
			}

			try {
				Thread.sleep(1 * minute);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
	
	private void sendCancelMessage(Order order){
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		LocalDateTime now = LocalDateTime.now();
		String dateAndTime = dtf.format(now);

		String date = dateAndTime.split(" ")[0];
		String time = dateAndTime.split(" ")[1];
		String travelerId = order.getTravelerId();
		int orderId = order.getOrderId();

		Park park = parkQueries.getParkById(new ArrayList<String>(Arrays.asList(String.valueOf(order.getParkId()))));
		String subject = MsgTemplates.orderCancel[0];
		String content = String.format(MsgTemplates.orderCancel[1].toString(), park.getParkName(),
				order.getOrderDate(), order.getOrderTime());

		Messages msg = new Messages(0, travelerId, date, time, subject, content, orderId);

		/* Send email */
		EmailControl.sendEmail(msg);

		/* Add message to DB */
		ArrayList<String> parameters = new ArrayList<>(
				Arrays.asList(travelerId, date, time, subject, content, String.valueOf(orderId)));
		travelerQueries.sendMessageToTraveler(parameters);
	}
	
	private ArrayList<Order> getRelevantOrders() {
		return orderQueries.getPendingOrders();
	}
	
	private boolean isDateLessThan22Hours(String date, String time) {

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

		if (diffInHour <= 22 && diffInHour > 0)
			return true;
		return false;
	}


}
