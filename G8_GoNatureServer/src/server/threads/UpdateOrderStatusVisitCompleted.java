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
 * updateOrderStatusVisitCompleted class implements Runnable.
 * 
 * This class updates the order's status when the traveler leaves the park
 */
public class UpdateOrderStatusVisitCompleted implements Runnable {

	private final int second = 1000;
	private final int minute = second * 60;
	private OrderQueries orderQueries;
	private ParkQueries parkQueries;

	public UpdateOrderStatusVisitCompleted(Connection mysqlconnection) {
		orderQueries = new OrderQueries(mysqlconnection);
		parkQueries = new ParkQueries(mysqlconnection);

	}

	/**
	 * This function updates the order's status when the traveler leaves the park
	 */
	@Override
	public void run() {

		while (true) {

			ArrayList<Order> orders = getRelevantOrders();

			for (Order order : orders) {
				String status = OrderStatusName.COMPLETED.toString();
				String orderId = String.valueOf(order.getOrderId());
				orderQueries.setOrderStatusWithIDandStatus(new ArrayList<String>(Arrays.asList(status, orderId)));

				String parkId = String.valueOf(order.getParkId());
				Park park = parkQueries.getParkById(new ArrayList<String>(Arrays.asList(parkId)));
				String newNumOfVisitors = String.valueOf(park.getCurrentVisitors() - order.getNumberOfParticipants());
				parkQueries.updateNumberOfVisitors(new ArrayList<String>(Arrays.asList(newNumOfVisitors, parkId)));

			}

			try {
				Thread.sleep(1 * minute);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	private ArrayList<Order> getRelevantOrders() {
		return orderQueries.getCompletedOrders();
	}

}
