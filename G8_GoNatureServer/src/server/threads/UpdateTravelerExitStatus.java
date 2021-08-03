package server.threads;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import controllers.sqlHandlers.OrderQueries;
import controllers.sqlHandlers.ParkQueries;
import logic.Order;
import logic.OrderStatusName;
import logic.Park;

/**
 * UpdateTravelerExitStatus class implements Runnable.
 * 
 * This class handle all the automated functionality:
 * Monitor the exiting visitors from the park.
 * Change the order status when the traveler exit.
 * Update the park current visitors parameter.
 *
 */
public class UpdateTravelerExitStatus implements Runnable {

	private final int second = 1000;
	private final int minute = second * 60;

	private OrderQueries orderQueries;
	private ParkQueries parkQueries;

	public UpdateTravelerExitStatus(Connection mysqlconnection) {
		orderQueries = new OrderQueries(mysqlconnection);
		parkQueries = new ParkQueries(mysqlconnection);
	}

	private ArrayList<Order> getRelevantOrders() {
		return orderQueries.getEnteredOrdersWithTimePassed();
	}

	/**
	 * This function check if the traveler exited the park, based on estimated exit time.
	 * If the thread did find a traveler that exited, it will change his order status
	 * and update the park current visitors.
	 */
	@Override
	public void run() {

		while (true) {
			System.out.println("Looking for relevant orders for travelers who exited");
			ArrayList<Order> orders = getRelevantOrders();

			String newNumberOfParticipants = "";
			Park park;
			String orderId = "";
			for (Order order : orders) {
				park = parkQueries.getParkById(new ArrayList<String>(Arrays.asList(String.valueOf(order.getParkId()))));
				newNumberOfParticipants = String.valueOf(park.getCurrentVisitors() - order.getNumberOfParticipants());
				orderId = String.valueOf(order.getOrderId());
				parkQueries.updateNumberOfVisitors(new ArrayList<String>(
						Arrays.asList(newNumberOfParticipants, String.valueOf(park.getParkId()))));
				orderQueries.setOrderStatusWithIDandStatus(
						new ArrayList<String>(Arrays.asList(OrderStatusName.COMPLETED.toString(), orderId)));
			}
			try {
				Thread.sleep(1 * minute);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
