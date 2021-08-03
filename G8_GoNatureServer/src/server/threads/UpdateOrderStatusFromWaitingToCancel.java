package server.threads;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import controllers.sqlHandlers.OrderQueries;
import logic.Order;
import logic.OrderStatusName;

/**
 * UpdateOrderStatusFromWaitingToCancel class implements Runnable.
 * 
 * This class automatically cancels waiting orders that has been expired
 *
 */
public class UpdateOrderStatusFromWaitingToCancel implements Runnable {

	private final int second = 1000;
	private final int minute = second * 60;
	private OrderQueries orderQueries;

	public UpdateOrderStatusFromWaitingToCancel(Connection mysqlconnection) {
		orderQueries = new OrderQueries(mysqlconnection);

	}

	/**
	 * This function updates the order's status is waiting and the order time expires
	 */
	@Override
	public void run() {

		while (true) {

			ArrayList<Order> orders = getRelevantOrders();

			for (Order order : orders) {
				String status = OrderStatusName.CANCELED.toString();
				String orderId = String.valueOf(order.getOrderId());
				orderQueries.setOrderStatusWithIDandStatus(new ArrayList<String>(Arrays.asList(status, orderId)));
			}

			try {
				Thread.sleep(1 * minute);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	private ArrayList<Order> getRelevantOrders() {
		return orderQueries.getWaitingOrdersToCancel();
	}
}
