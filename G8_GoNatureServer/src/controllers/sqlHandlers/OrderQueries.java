package controllers.sqlHandlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import logic.Order;
import logic.OrderStatusName;

/**
 * This class handles all the queries which are related to orders
 */
public class OrderQueries {
	private Connection conn;

	public OrderQueries(Connection conn) {
		this.conn = conn;
	}

	/**
	 * This function retrieve from the database all the orders that are between given times.
	 * 
	 * @param parameters park id, date, start time, end time
	 * @return ArrayList of relevant orders
	 */
	public ArrayList<Order> getOrderBetweenTimes(ArrayList<?> parameters) {
		ArrayList<Order> orders = new ArrayList<Order>();
		String sql = "SELECT * FROM g8gonature.order WHERE parkId = ? and orderDate = ? and orderTime >= ? and orderTime <= ? AND (orderStatus = ? OR orderStatus = ? OR orderStatus = ? OR orderStatus = ?)";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			query.setInt(1, Integer.parseInt((String) parameters.get(0)));
			query.setString(2, (String) parameters.get(1));
			query.setString(3, (String) parameters.get(2));
			query.setString(4, (String) parameters.get(3));
			query.setString(5, OrderStatusName.PENDING.toString());
			query.setString(6, OrderStatusName.CONFIRMED.toString());
			query.setString(7, OrderStatusName.PENDING_EMAIL_SENT.toString());
			query.setString(8, OrderStatusName.WAITING_HAS_SPOT.toString());
			ResultSet res = query.executeQuery();

			while (res.next())
				orders.add(new Order(res.getInt(1), res.getString(2), res.getInt(3), res.getString(4), res.getString(5),
						res.getString(6), res.getInt(7), res.getString(8), res.getDouble(9), res.getString(10)));
		} catch (SQLException e) {
			System.out.println("Could not execute getOrderBetweenTimes query");
			e.printStackTrace();
		}

		return orders;
	}

	/**
	 * This function get an Order object and adds it to the database
	 * 
	 * @param obj The order to add
	 * @return true on success, false otherwise
	 */
	public boolean addNewOrder(Object obj) {
		Order orderToAdd = (Order) obj;
		int result = 0;
		String sql = "INSERT INTO g8gonature.order (travelerId, parkId, orderDate, orderTime, orderType, numberOfParticipants, email, price, orderStatus) "
				+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			query.setString(1, orderToAdd.getTravelerId());
			query.setInt(2, orderToAdd.getParkId());
			query.setString(3, orderToAdd.getOrderDate());
			query.setString(4, orderToAdd.getOrderTime());
			query.setString(5, orderToAdd.getOrderType());
			query.setInt(6, orderToAdd.getNumberOfParticipants());
			query.setString(7, orderToAdd.getEmail());
			query.setDouble(8, orderToAdd.getPrice());
			query.setString(9, orderToAdd.getOrderStatus());
			result = query.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Could not execute AddNewOrder query");
			e.printStackTrace();
		}
		return result > 0;
	}

	/**
	 * This function get a traveler's id and retrieve his most recent order
	 * 
	 * @param parameters the traveler's id.
	 * @return Order object - the most recent order
	 */
	public Order getRecentOrder(ArrayList<?> parameters) {
		Order order = null;

		String sql = "SELECT * FROM g8gonature.order WHERE travelerId = ? ORDER BY orderId DESC";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			query.setString(1, (String) parameters.get(0));
			ResultSet res = query.executeQuery();
			if (res.next())
				order = new Order(res.getInt(1), res.getString(2), res.getInt(3), res.getString(4), res.getString(5),
						res.getString(6), res.getInt(7), res.getString(8), res.getDouble(9), res.getString(10));
		} catch (SQLException e) {
			System.out.println("Could not execute getRecentOrder query");
			e.printStackTrace();
		}
		return order;
	}

	/**
	 * This function return orders in waiting list that can replace the can canceled order.
	 * 
	 * @param parameters ArrayList containing: parkId,maxVisitors in the park,
	 *                   estimatedStayTime in the park, date of the canceled order, timeToCheck of the canceled order,
	 *                   gap between max and current in the park
	 * @return ArrayList of object Order containing matching orders.
	 */
	public ArrayList<Order> findMatchingOrdersInWaitingList(ArrayList<?> parameters) {
		ArrayList<Order> resultArray = new ArrayList<Order>();

		String parkId = (String) parameters.get(0);
		String maxVisitors = (String) parameters.get(1);
		String estimatedStayTime = (String) parameters.get(2);
		String date = (String) parameters.get(3);
		String timeToCheck = (String) parameters.get(4);
		String gap = (String) parameters.get(5);

		int estimated = Integer.parseInt(estimatedStayTime);
		int maxVisitor = Integer.parseInt(maxVisitors);
		int gapInPark = Integer.parseInt(gap);

		int maxAllowedInPark = maxVisitor - gapInPark;

		int hour = Integer.parseInt(timeToCheck.split(":")[0]);

		String hourAfterEstimated = (hour + estimated) + ":00";
		String hourBeforeEstimated = (hour - estimated) + ":00";

		ArrayList<String> par = new ArrayList<String>(
				Arrays.asList(parkId, date, hourBeforeEstimated, hourAfterEstimated));
		ArrayList<Order> resultOrders = getOrderBetweenTimes(par);
		int count = 0;
		for (Order o : resultOrders)
			count += o.getNumberOfParticipants();

		String sql = "SELECT * FROM g8gonature.order WHERE parkId = ? AND "
				+ "orderDate = ? AND orderTime BETWEEN ? AND ? AND orderStatus = ?";
		ResultSet rs;
		PreparedStatement query;

		try {
			query = conn.prepareStatement(sql);
			query.setInt(1, Integer.parseInt(parkId));
			query.setString(2, date);
			query.setString(3, hourBeforeEstimated);
			query.setString(4, hourAfterEstimated);
			query.setString(5, OrderStatusName.WAITING.toString());
			rs = query.executeQuery();
			while (rs.next()) {
				if (rs.getInt(7) + count <= maxAllowedInPark) {
					Order order = new Order(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getString(4),
							rs.getString(5), rs.getString(6), rs.getInt(7), rs.getString(8), rs.getDouble(9),
							rs.getString(10));
					resultArray.add(order);
				}
			}

		} catch (SQLException e) {
			System.out.println("Could not execute findMatchingOrdersInWaitingList");
			e.printStackTrace();
		}

		return resultArray;
	}

	/**
	 * This function return orders in a specific park.
	 * 
	 * @param parameters - ArrayList containing: parkId
	 * @return ArrayList of object Order containing matching orders.
	 */
	public ArrayList<Order> getOrdersForPark(ArrayList<?> parameters) {
		int parkId = Integer.parseInt((String) parameters.get(0));
		ArrayList<Order> orders = new ArrayList<Order>();

		String sql = "SELECT * FROM g8gonature.order WHERE parkId = ? ORDER BY orderId DESC";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			query.setInt(1, parkId);
			ResultSet res = query.executeQuery();
			while (res.next()) {
				Order order = new Order(res.getInt(1), res.getString(2), res.getInt(3), res.getString(4),
						res.getString(5), res.getString(6), res.getInt(7), res.getString(8), res.getDouble(9),
						res.getString(10));
				orders.add(order);
			}

		} catch (SQLException e) {
			System.out.println("Could not execute getOrdersForPark query");
			e.printStackTrace();
		}
		return orders;

	}

	/**
	 * This function return orders in a specific park for a certain traveler.
	 * 
	 * @param parameters - ArrayList containing: parkId, travelerId.
	 * @return ArrayList of object Order containing matching orders.
	 */
	public ArrayList<Order> getOrderForTravelerInPark(ArrayList<?> parameters) {
		int parkId = Integer.parseInt((String) parameters.get(0));
		String travId = (String) parameters.get(1);
		ArrayList<Order> orders = new ArrayList<Order>();

		String sql = "SELECT * FROM g8gonature.order WHERE parkId = ? AND travelerId = ?";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			query.setInt(1, parkId);
			query.setString(2, travId);
			ResultSet res = query.executeQuery();
			while (res.next()) {
				Order order = new Order(res.getInt(1), res.getString(2), res.getInt(3), res.getString(4),
						res.getString(5), res.getString(6), res.getInt(7), res.getString(8), res.getDouble(9),
						res.getString(10));
				orders.add(order);
			}

		} catch (SQLException e) {
			System.out.println("Could not execute getOrderForTravelerInPark query");
			e.printStackTrace();
		}
		return orders;
	}

	/**
	 * This function return orders that their status is "Pending".
	 * 
	 * @return ArrayList of object Order containing matching orders.
	 */
	public ArrayList<Order> getPendingOrders() {
		ArrayList<Order> orders = new ArrayList<Order>();
		String sql = "SELECT * FROM g8gonature.order WHERE orderStatus = ?";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			query.setString(1, OrderStatusName.PENDING.toString());
			ResultSet res = query.executeQuery();
			while (res.next()) {
				Order order = new Order(res.getInt(1), res.getString(2), res.getInt(3), res.getString(4),
						res.getString(5), res.getString(6), res.getInt(7), res.getString(8), res.getDouble(9),
						res.getString(10));
				orders.add(order);
			}

		} catch (SQLException e) {
			System.out.println("Could not execute getAllOrdersForId");
			e.printStackTrace();
		}
		return orders;
	}

	/**
	 * This function gets an order id and retrieve the relevant order from the database
	 * 
	 * @param orderId the order id to retrieve
	 * @return Order object
	 */
	public Order getOrderByID(int orderId) {
		Order order = null;
		String sql = "SELECT * FROM g8gonature.order WHERE orderId = ?";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			query.setInt(1, orderId);
			ResultSet res = query.executeQuery();
			if (res.next()) {
				order = new Order(res.getInt(1), res.getString(2), res.getInt(3), res.getString(4), res.getString(5),
						res.getString(6), res.getInt(7), res.getString(8), res.getDouble(9), res.getString(10));
			}

		} catch (SQLException e) {
			System.out.println("Could not execute getAllOrdersForId");
			e.printStackTrace();
		}
		return order;

	}

	/**
	 * This function retrieve all the relevant order of a traveler for a given date
	 * 
	 * @param parameters traveler id, order date, start time, end time
	 * @return ArrayList with all the relevant orders
	 */
	public ArrayList<Order> getRelevantOrderForParkEntrance(ArrayList<?> parameters) {
		ArrayList<Order> orders = new ArrayList<>();
		String sql = "SELECT * FROM g8gonature.order where travelerId = ? AND orderDate = ? AND orderTime >= ? AND orderTime <= ? AND orderStatus = ?";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			query.setString(1, (String) parameters.get(0));
			query.setString(2, (String) parameters.get(1));
			query.setString(3, (String) parameters.get(2));
			query.setString(4, (String) parameters.get(3));
			query.setString(5, OrderStatusName.CONFIRMED.toString());
			ResultSet res = query.executeQuery();

			if (res.next()) {
				orders.add(new Order(res.getInt(1), res.getString(2), res.getInt(3), res.getString(4), res.getString(5),
						res.getString(6), res.getInt(7), res.getString(8), res.getDouble(9), res.getString(10)));
			} else {
				orders.add(null);
			}
		} catch (SQLException e) {
			System.out.println("Could not execute getRelevantOrderForParkEntrance query");
			e.printStackTrace();
		}

		return orders;
	}

	/**
	 * This function retrieve all the relevant order of a traveler for a given date
	 * 
	 * @param parameters traveler id, order date, start time, end time
	 * @return ArrayList with all the relevant orders
	 */
	public ArrayList<Order> getRelevantOrderForParkExit(ArrayList<?> parameters) {
		ArrayList<Order> orders = new ArrayList<>();
		String sql = "SELECT * FROM g8gonature.order where travelerId = ? AND orderStatus = ? order by orderId DESC";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			query.setInt(1, Integer.parseInt((String) parameters.get(0)));
			query.setString(2, OrderStatusName.ENTERED_THE_PARK.toString());
			ResultSet res = query.executeQuery();

			if (res.next()) {
				orders.add(new Order(res.getInt(1), res.getString(2), res.getInt(3), res.getString(4), res.getString(5),
						res.getString(6), res.getInt(7), res.getString(8), res.getDouble(9), res.getString(10)));
			} else {
				orders.add(null);
			}
		} catch (SQLException e) {
			System.out.println("Could not execute getRelevantOrderForParkExit query");
			e.printStackTrace();
		}

		return orders;
	}

	/**
	 * This function return all of the orders for a spesific traveler.
	 * 
	 * @param parameters - ArrayList containing:travelerId.
	 * @return ArrayList with all the relevant orders
	 */
	public ArrayList<Order> getAllOrdersForID(ArrayList<?> parameters) {
		ArrayList<Order> orders = new ArrayList<Order>();

		String sql = "SELECT * FROM g8gonature.order WHERE travelerId = ? ORDER BY orderId DESC";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			query.setInt(1, Integer.parseInt((String) parameters.get(0))); // was setString
			ResultSet res = query.executeQuery();
			while (res.next()) {
				Order order = new Order(res.getInt(1), res.getString(2), res.getInt(3), res.getString(4),
						res.getString(5), res.getString(6), res.getInt(7), res.getString(8), res.getDouble(9),
						res.getString(10));
				orders.add(order);
			}

		} catch (SQLException e) {
			System.out.println("Could not execute getAllOrdersForID");
			e.printStackTrace();
		}
		return orders;
	}

	/**
	 * This function return all of the orders that are in the system.
	 * 
	 * @return ArrayList of object Order.
	 */
	public ArrayList<Order> getAllOrders() {
		ArrayList<Order> orders = new ArrayList<Order>();

		String sql = "SELECT * FROM g8gonature.order";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			ResultSet res = query.executeQuery();
			while (res.next()) {
				Order order = new Order(res.getInt(1), res.getString(2), res.getInt(3), res.getString(4),
						res.getString(5), res.getString(6), res.getInt(7), res.getString(8), res.getDouble(9),
						res.getString(10));
				orders.add(order);
			}

		} catch (SQLException e) {
			System.out.println("Could not execute getAllOrdersForId");
			e.printStackTrace();
		}
		return orders;
	}

	/**
	 * This function set order status for a specific order and status.
	 * 
	 * @param parameters - ArrayList containing:order status, orderId.
	 * @return true on success, false otherwise.
	 */
	public boolean setOrderStatusWithIDandStatus(ArrayList<?> parameters) {
		String sql = "UPDATE g8gonature.order SET orderStatus = ? WHERE orderId = ?";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			query.setString(1, (String) parameters.get(0));
			query.setInt(2, Integer.parseInt((String) parameters.get(1)));
			int res = query.executeUpdate();
			return res == 1;
		} catch (SQLException e) {
			System.out.println("Could not execute setOrderStatusWithIDandStatus");
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * This function set number of visitors for a specific order.
	 * 
	 * @param parameters - ArrayList containing:number of participants, orderId.
	 * @return true on success, false otherwise.
	 */
	public boolean UpdateNumberOfVisitorsForOrder(ArrayList<?> parameters) {

		String sql = "UPDATE g8gonature.order SET numberOfParticipants = ? WHERE orderId = ?";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			query.setInt(1, Integer.parseInt((String) parameters.get(0)));
			query.setInt(2, Integer.parseInt((String) parameters.get(1)));
			int res = query.executeUpdate();
			return res == 1;
		} catch (SQLException e) {
			System.out.println("Could not execute UpdateNumberOfVisitorsForOrder query");
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * This function set price for a specific order.
	 * 
	 * @param parameters - ArrayList containing:price, orderId.
	 * @return true on success, false otherwise.
	 */
	public boolean UpdatePriceForOrder(ArrayList<?> parameters) {

		String sql = "UPDATE g8gonature.order SET price = ? WHERE orderId = ?";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			query.setDouble(1, Double.parseDouble((String) parameters.get(0)));
			query.setInt(2, Integer.parseInt((String) parameters.get(1)));
			int res = query.executeUpdate();
			return res == 1;
		} catch (SQLException e) {
			System.out.println("Could not execute UpdatePriceForOrder query");
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * This function return all orders with status - Entered the park, With time that has passed
	 * 
	 * @return ArrayList of object Order with relevant orders.
	 */
	public ArrayList<Order> getEnteredOrdersWithTimePassed() {
		ArrayList<Order> orders = new ArrayList<Order>();
		String sql = "SELECT g8gonature.order.* FROM g8gonature.visit,g8gonature.order WHERE visitDate = ? AND exitTime < ? AND orderStatus = ? "
				+ "AND g8gonature.order.travelerId = g8gonature.visit.travelerId GROUP BY orderId";

		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			query.setString(1, LocalDate.now().toString());
			query.setString(2, LocalTime.now().toString());
			query.setString(3, OrderStatusName.ENTERED_THE_PARK.toString());

			ResultSet res = query.executeQuery();
			while (res.next())
				orders.add(new Order(res.getInt(1), res.getString(2), res.getInt(3), res.getString(4), res.getString(5),
						res.getString(6), res.getInt(7), res.getString(8), res.getDouble(9), res.getString(10)));

		} catch (SQLException e) {
			System.out.println("Could not execute getEnteredOrdersWithTimePassed");
			e.printStackTrace();
		}
		return orders;
	}

	/**
	 * This function adds a new order alert to orders_alert table in the database
	 * 
	 * @param orderId   The order id associate with the alert
	 * @param date      The date of the alert
	 * @param startTime The time the alert has been sent
	 * @param endTime   The time alert has been end
	 * 
	 * @return true on success, false otherwise
	 */
	public boolean addOrderAlert(int orderId, String date, String startTime, String endTime) {
		int result = 0;
		String sql = "INSERT INTO g8gonature.orders_alerts (orderId, alertDate, alertSendTime, alertEndTime) "
				+ "values (?, ?, ?, ?)";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			query.setInt(1, orderId);
			query.setString(2, date);
			query.setString(3, startTime);
			query.setString(4, endTime);
			result = query.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Could not execute addOrderAlert query");
			e.printStackTrace();
		}
		return result > 0;
	}

	/**
	 * This function deletes order alert from orders_alert table in the database
	 * 
	 * @param alertId The alert id to delete
	 * 
	 * @return true on success, false otherwise
	 */
	public boolean deleteOrderAlert(int alertId) {
		int result = 0;
		String sql = "DELETE FROM g8gonature.orders_alerts WHERE alertId = ? ";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			query.setInt(1, alertId);
			query.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Could not execute deleteOrderAlert query");
			e.printStackTrace();
		}
		return result > 0;
	}

	/**
	 * This function returns all the orders that their alert time has ended and they are not confirmed
	 * 
	 * @param date The current date
	 * @param time The current time
	 * 
	 * @return ArrayList of orders
	 */
	public ArrayList<Order> getOrderWithExpiryAlertTime(String date, String time) {

		ArrayList<Order> orders = new ArrayList<Order>();
		String sql = "SELECT g8gonature.order.* " + "FROM g8gonature.orders_alerts, g8gonature.order "
				+ "WHERE alertDate = ? AND alertEndTime < ? AND (orderStatus = ? OR orderStatus = ?) AND g8gonature.order.orderId = g8gonature.orders_alerts.orderId";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			query.setString(1, date);
			query.setString(2, time);
			query.setString(3, OrderStatusName.PENDING_EMAIL_SENT.toString());
			query.setString(4, OrderStatusName.WAITING_HAS_SPOT.toString());

			ResultSet res = query.executeQuery();

			while (res.next()) {
				Order order = new Order(res.getInt(1), res.getString(2), res.getInt(3), res.getString(4),
						res.getString(5), res.getString(6), res.getInt(7), res.getString(8), res.getDouble(9),
						res.getString(10));
				orders.add(order);
			}
		} catch (SQLException e) {
			System.out.println("Could not execute getOrderWithExpiryAlertTime query");
			e.printStackTrace();
		}
		return orders;

	}

	/**
	 * This function returns all the orders that their alert time has ended and they are not confirmed
	 * 
	 * @param date The current date
	 * @param time The current time
	 * 
	 * @return ArrayList of orders
	 */
	public ArrayList<Integer> getOrderAlertIdWithExpiryAlertTime(String date, String time) {

		ArrayList<Integer> orders = new ArrayList<Integer>();
		String sql = "SELECT g8gonature.orders_alerts.alertId " + "FROM g8gonature.orders_alerts, g8gonature.order "
				+ "WHERE alertDate = ? AND alertEndTime < ? AND (orderStatus = ? OR orderStatus = ?) AND g8gonature.order.orderId = g8gonature.orders_alerts.orderId";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			query.setString(1, date);
			query.setString(2, time);
			query.setString(3, OrderStatusName.CONFIRMED.toString());
			query.setString(4, OrderStatusName.CANCELED.toString());

			ResultSet res = query.executeQuery();

			while (res.next())
				orders.add(res.getInt(1));

		} catch (SQLException e) {
			System.out.println("Could not execute getOrderWithExpiryAlertTime query");
			e.printStackTrace();
		}
		return orders;
	}

	/**
	 * This function returns all the orders that have been completed and their status is ENTERED_THE_PARK
	 * 
	 * @return ArrayList of Order objects
	 */
	public ArrayList<Order> getCompletedOrders() {
		ArrayList<Order> orders = new ArrayList<Order>();
		String sql = "SELECT g8gonature.order.* FROM g8gonature.order, g8gonature.visit"
				+ " WHERE g8gonature.order.orderStatus = ? AND g8gonature.order.travelerId = g8gonature.visit.travelerId"
				+ " AND g8gonature.order.parkId = g8gonature.visit.parkId AND g8gonature.order.parkId = g8gonature.visit.parkId"
				+ " AND g8gonature.order.orderTime = g8gonature.visit.entrenceTime AND g8gonature.order.orderDate = g8gonature.visit.visitDate"
				+ " AND g8gonature.visit.exitTime < ?";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			query.setString(1, OrderStatusName.ENTERED_THE_PARK.toString());
			query.setString(2, LocalTime.now().toString());
			ResultSet res = query.executeQuery();

			while (res.next()) {
				Order order = new Order(res.getInt(1), res.getString(2), res.getInt(3), res.getString(4),
						res.getString(5), res.getString(6), res.getInt(7), res.getString(8), res.getDouble(9),
						res.getString(10));
				orders.add(order);
			}

		} catch (SQLException e) {
			System.out.println("Could not execute getCompletedOrders query");
			e.printStackTrace();
		}
		return orders;

	}
	

	/**
	 * This function returns all the orders that have been completed and their status is ENTERED_THE_PARK
	 * 
	 * @return ArrayList of Order objects
	 */
	public ArrayList<Order> getWaitingOrdersToCancel() {
		ArrayList<Order> orders = new ArrayList<Order>();
		String sql = "SELECT * FROM g8gonature.order WHERE orderStatus = ? AND ((orderDate = ? AND orderTime <= ?) || orderDate < ?)";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			query.setString(1, OrderStatusName.WAITING.toString());
			query.setString(2, LocalDate.now().toString());
			query.setString(3, LocalTime.now().toString());
			query.setString(4, LocalDate.now().toString());
			ResultSet res = query.executeQuery();

			while (res.next()) {
				Order order = new Order(res.getInt(1), res.getString(2), res.getInt(3), res.getString(4),
						res.getString(5), res.getString(6), res.getInt(7), res.getString(8), res.getDouble(9),
						res.getString(10));
				orders.add(order);
			}

		} catch (SQLException e) {
			System.out.println("Could not execute getCompletedOrders query");
			e.printStackTrace();
		}
		return orders;

	}
}
