package controllers.sqlHandlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

import logic.Order;
import logic.OrderStatusName;
import logic.VisitReport;
import logic.Report;

/**
 * This class handles all the queries which are related to reports
 * 
 */
public class ReportsQueries {
	private Connection conn;

	public ReportsQueries(Connection conn) {
		this.conn = conn;
	}

	/**
	 * This function DELETE a report from reports table in the database
	 * 
	 * @param parameters report type, park id, report month
	 */
	public void deleteReport(ArrayList<?> parameters) {
		String sql = "DELETE FROM g8gonature.reports where reportType = ? and parkID = ? and month = ?";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			Report r = (Report) parameters.get(0);
			query.setString(1, r.getReportType());
			query.setInt(2, r.getParkID());
			query.setInt(3, r.getMonth());
			query.executeUpdate();

		} catch (SQLException e) {
			System.out.println("Could not execute deleteReport");
			e.printStackTrace();
		}
	}

	/**
	 * This function INSERT a report to reports table in the database
	 * 
	 * @param parameters report type, park id, month, comment
	 * @return true on success, false otherwise
	 */
	public boolean insertReport(ArrayList<?> parameters) {

		String sql = "INSERT INTO g8gonature.reports  (reportType, parkID, month, comment)  values (?, ?,?,?)";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			Report r = (Report) parameters.get(0);
			query.setString(1, r.getReportType());
			query.setInt(2, r.getParkID());
			query.setInt(3, r.getMonth());
			query.setString(4, r.getComment());
			query.executeUpdate();
			return true;
		} catch (SQLException e) {
			System.out.println("Could not execute insertReport");
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * This creates objects of solo visitors dived by their entrance time
	 * 
	 * @param parameters current year and selected month
	 * @return ArrayList of VisitReport objects
	 */
	public ArrayList<VisitReport> CountSolosEnterTime(ArrayList<?> parameters) {
		ArrayList<VisitReport> rep = new ArrayList<VisitReport>();
		int month = Integer.parseInt((String) parameters.get(0));
		int year = Calendar.getInstance().get(Calendar.YEAR);

		String sql = "SELECT SUM(test.numberOfParticipants),test.entrenceTime "
				+ "FROM (SELECT visit.travelerId,order.numberOfParticipants, visit.entrenceTime FROM g8gonature.visit , g8gonature.order "
				+ "WHERE NOT EXISTS ( SELECT subscriber.travelerId FROM g8gonature.subscriber WHERE visit.travelerId = subscriber.travelerId) "
				+ "AND visit.travelerId = order.travelerId AND order.orderType = 'Solo Visit' "
				+ "AND order.orderStatus = 'Visit completed' AND visit.visitDate = order.orderDate "
				+ "AND MONTH(visit.visitDate) = ? AND YEAR(visit.visitDate) = ?) as test GROUP BY test.entrenceTime;";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			query.setInt(1, month);
			query.setInt(2, year);
			ResultSet res = query.executeQuery();
			while (res.next()) {
				VisitReport report = new VisitReport(res.getInt(1), res.getString(2));
				rep.add(report);
			}

		} catch (NullPointerException | SQLException e) {
			System.out.println("Could not execute CountSolos query");
			return null;
		}
		return rep;
	}

	/**
	 * This creates objects of subscribers visitors dived by their entrance time
	 * 
	 * @param parameters current year and selected month
	 * @return ArrayList of VisitReport objects
	 */
	public ArrayList<VisitReport> CountSubsEnterTime(ArrayList<?> parameters) {
		ArrayList<VisitReport> rep = new ArrayList<VisitReport>();
		int month = Integer.parseInt((String) parameters.get(0));
		int year = Calendar.getInstance().get(Calendar.YEAR);

		String sql = "SELECT SUM(test.numberOfParticipants),test.entrenceTime "
				+ "FROM (SELECT visit.travelerId,order.numberOfParticipants, visit.entrenceTime "
				+ "FROM g8gonature.visit, g8gonature.order, g8gonature.subscriber "
				+ "WHERE visit.travelerId = order.travelerId "
				+ "AND (order.orderType = 'Solo Visit' OR order.orderType = 'Family Visit') "
				+ "AND visit.travelerId = subscriber.travelerId AND order.orderStatus = 'Visit completed' AND visit.visitDate = order.orderDate "
				+ "AND MONTH(visit.visitDate) = ? AND YEAR(visit.visitDate) = ? ) as test GROUP BY test.entrenceTime;";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			query.setInt(1, month);
			query.setInt(2, year);
			ResultSet res = query.executeQuery();
			while (res.next()) {
				VisitReport report = new VisitReport(res.getInt(1), res.getString(2));
				rep.add(report);
			}

		} catch (NullPointerException | SQLException e) {
			System.out.println("Could not execute CountSubs query");
			return null;
		}
		return rep;
	}

	/**
	 * This creates objects of group visitors dived by their entrance time
	 * 
	 * @param parameters current year and selected month
	 * @return ArrayList of VisitReport objects
	 */
	public ArrayList<VisitReport> CountGroupsEnterTime(ArrayList<?> parameters) { 
		ArrayList<VisitReport> rep = new ArrayList<VisitReport>();
		int month = Integer.parseInt((String) parameters.get(0));
		int year = Calendar.getInstance().get(Calendar.YEAR);

		String sql = "SELECT SUM(test.numberOfParticipants), test.entrenceTime FROM "
				+ "(SELECT visit.travelerId,order.numberOfParticipants,visit.entrenceTime "
				+ "FROM g8gonature.visit, g8gonature.order WHERE visit.travelerId = order.travelerId "
				+ "AND order.orderType = 'Group Visit' AND order.orderStatus = 'Visit completed' "
				+ "AND visit.visitDate = order.orderDate AND visit.entrenceTime = order.orderTime "
				+ "AND MONTH(visit.visitDate) = ? AND YEAR(visit.visitDate) = ?) AS test GROUP BY test.entrenceTime;";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			query.setInt(1, month);
			query.setInt(2, year);
			ResultSet res = query.executeQuery();
			while (res.next()) {
				VisitReport report = new VisitReport(res.getInt(1), res.getString(2));
				rep.add(report);
			}

		} catch (NullPointerException | SQLException e) {
			System.out.println("Could not execute CountGroup query");
			return null;
		}
		return rep;
	}

	/**
	 * This creates objects of solo visitors dived by their stay time of visit
	 * 
	 * @param parameters current year and selected month
	 * @return ArrayList of VisitReport objects
	 */
	public ArrayList<VisitReport> CountSolosVisitTime(ArrayList<?> parameters) {
		ArrayList<VisitReport> rep = new ArrayList<VisitReport>();
		int month = Integer.parseInt((String) parameters.get(0));
		int year = Calendar.getInstance().get(Calendar.YEAR);

		String sql = "SELECT SUM(test.numberOfParticipants),TIMEDIFF (test.exitTime,test.entrenceTime) "
				+ "FROM (SELECT visit.travelerId,order.numberOfParticipants, visit.entrenceTime,visit.exitTime "
				+ "FROM g8gonature.visit , g8gonature.order WHERE NOT exists ( SELECT subscriber.travelerId FROM g8gonature.subscriber "
				+ "WHERE visit.travelerId = subscriber.travelerId) AND visit.travelerId = order.travelerId AND order.orderType = 'Solo Visit' "
				+ "AND order.orderStatus = 'Visit completed' AND visit.visitDate = order.orderDate "
				+ "AND MONTH(visit.visitDate) = ? AND YEAR(visit.visitDate) = ?) as test GROUP BY TIMEDIFF(test.exitTime, test.entrenceTime);";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			query.setInt(1, month);
			query.setInt(2, year);
			ResultSet res = query.executeQuery();
			while (res.next()) {
				VisitReport report = new VisitReport(res.getInt(1), res.getString(2));
				rep.add(report);
			}

		} catch (SQLException e) {
			System.out.println("Could not execute CountSolos query");
			e.printStackTrace();
		}
		return rep;
	}

	/**
	 * This creates objects of subscribers visitors dived by their stay time of visit
	 * 
	 * @param parameters current year and selected month
	 * @return ArrayList of VisitReport objects
	 */
	public ArrayList<VisitReport> CountSubsVisitTime(ArrayList<?> parameters) {
		ArrayList<VisitReport> rep = new ArrayList<VisitReport>();
		int month = Integer.parseInt((String) parameters.get(0));
		int year = Calendar.getInstance().get(Calendar.YEAR);

		String sql = "SELECT SUM(test.numberOfParticipants),TIMEDIFF (test.exitTime,test.entrenceTime) "
				+ "FROM (SELECT visit.travelerId,order.numberOfParticipants, visit.entrenceTime, visit.exitTime "
				+ "FROM g8gonature.visit, g8gonature.order, g8gonature.subscriber WHERE visit.travelerId = order.travelerId "
				+ "AND (order.orderType = 'Solo Visit' OR order.orderType = 'Family Visit') AND visit.travelerId = subscriber.travelerId "
				+ "AND order.orderStatus = 'Visit completed' AND visit.visitDate = order.orderDate "
				+ "AND MONTH(visit.visitDate) = ? AND YEAR(visit.visitDate) = ? ) as test GROUP BY TIMEDIFF(test.exitTime, test.entrenceTime);;";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			query.setInt(1, month);
			query.setInt(2, year);
			ResultSet res = query.executeQuery();
			while (res.next()) {
				VisitReport report = new VisitReport(res.getInt(1), res.getString(2));
				rep.add(report);
			}

		} catch (SQLException e) {
			System.out.println("Could not execute CountSubs query");
			e.printStackTrace();
		}
		return rep;
	}

	/**
	 * This creates objects of group visitors dived by their stay time of visit
	 * 
	 * @param parameters current year and selected month
	 * @return ArrayList of VisitReport objects
	 */
	public ArrayList<VisitReport> CountGroupsVisitTime(ArrayList<?> parameters) {
		ArrayList<VisitReport> rep = new ArrayList<VisitReport>();
		int month = Integer.parseInt((String) parameters.get(0));
		int year = Calendar.getInstance().get(Calendar.YEAR);

		String sql = "SELECT SUM(test.numberOfParticipants),TIMEDIFF (test.exitTime,test.entrenceTime) "
				+ "FROM (SELECT visit.travelerId,order.numberOfParticipants, visit.entrenceTime, visit.exitTime "
				+ "FROM g8gonature.visit,g8gonature.order WHERE "
				+ "visit.travelerId = order.travelerId AND order.orderType = 'Group Visit' "
				+ "AND order.orderStatus = 'Visit completed' AND visit.visitDate = order.orderDate AND visit.entrenceTime = order.orderTime "
				+ "AND MONTH(visit.visitDate) = ? AND YEAR(visit.visitDate) = ?) as test GROUP BY TIMEDIFF(test.exitTime, test.entrenceTime);";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			query.setInt(1, month);
			query.setInt(2, year);
			ResultSet res = query.executeQuery();
			while (res.next()) {
				VisitReport report = new VisitReport(res.getInt(1), res.getString(2));
				rep.add(report);
			}

		} catch (SQLException e) {
			System.out.println("Could not execute CountGroup query");
			e.printStackTrace();
		}
		return rep;
	}

	/**
	 * This function get reports from data base
	 * 
	 * @param parameters unused
	 * @return ArrayList of reports objects
	 */
	public ArrayList<Report> getReports(ArrayList<?> parameters) {
		ArrayList<Report> reports = new ArrayList<Report>();
		String sql = "SELECT * FROM g8gonature.reports ORDER BY reportID DESC";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			ResultSet res = query.executeQuery();
			/* getting all reports from query into array list */
			while (res.next()) {
				Report rep = new Report(res.getInt(1), res.getString(2), res.getInt(3), res.getInt(4),
						res.getString(5));
				reports.add(rep);
			}
		} catch (SQLException e) {
			System.out.println("Could not execute getMessages");
			e.printStackTrace();
		}
		return reports;
	}

	/**
	 * This query gets cancelled orders from order table for park ID in specific month
	 * 
	 * @param parameters ArrayList with parkId and month
	 * @return ArrayList with number of cancelled orders
	 */
	public ArrayList<Integer> getParkCancels(ArrayList<?> parameters) {
		ArrayList<Integer> cancels = new ArrayList<Integer>();
		int parkId = (int) parameters.get(0);
		int month = (int) parameters.get(1);

		String sql = "SELECT COUNT(*) FROM g8gonature.order WHERE parkId = ? AND MONTH(orderDate) = ? AND orderStatus = ?";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			query.setInt(1, parkId);
			query.setInt(2, month);
			query.setString(3, OrderStatusName.CANCELED.toString());

			ResultSet res = query.executeQuery();
			while (res.next())
				cancels.add(new Integer(res.getInt(1)));
		} catch (SQLException e) {
			System.out.println("Could not execute getParkCancels query");
			e.printStackTrace();
		}
		return cancels;
	}

	/**
	 * This function returns all the orders in a given month which are Solo visit and the traveler is not subscriber
	 * 
	 * @param month  The month of the orders
	 * @param parkID The park id
	 * @return ArrayList of order object
	 */
	public ArrayList<Order> getSolosOrdersVisitorsReport(int month, int parkID) {
		ArrayList<Order> orders = new ArrayList<Order>();

		String sql = "SELECT * FROM g8gonature.order WHERE month(orderDate)=" + month + " AND parkId =" + parkID
				+ " AND orderType='Solo Visit' AND g8gonature.order.travelerId NOT IN"
				+ "(  SELECT g8gonature.subscriber.travelerId   FROM  g8gonature.subscriber) AND orderStatus='Visit completed'";

		try {
			PreparedStatement query = conn.prepareStatement(sql);
			ResultSet res = query.executeQuery();

			while (res.next()) {
				orders.add(new Order(res.getInt(1), res.getString(2), res.getInt(3), res.getString(4), res.getString(5),
						res.getString(6), res.getInt(7), res.getString(8), res.getDouble(9), res.getString(10)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return orders;
	}

	/**
	 * This function returns all the orders in a given month which made by Subscribers and the traveler is not subscriber
	 * 
	 * @param month  The month of the orders
	 * @param parkID The park id
	 * 
	 * @return ArrayList of order object
	 */
	public ArrayList<Order> getSubscribersOrdersVisitorsReport(int month, int parkID) {
		ArrayList<Order> orders = new ArrayList<Order>();

		String sql = "SELECT * FROM g8gonature.order WHERE month(orderDate)=" + month + " AND parkId =" + parkID
				+ " AND (orderType='Family Visit' OR orderType='Solo Visit') AND g8gonature.order.travelerId IN "
				+ "(  SELECT g8gonature.subscriber.travelerId   FROM  g8gonature.subscriber) AND orderStatus = 'Visit completed'";

		try {
			PreparedStatement query = conn.prepareStatement(sql);
			ResultSet res = query.executeQuery();

			while (res.next()) {
				orders.add(new Order(res.getInt(1), res.getString(2), res.getInt(3), res.getString(4), res.getString(5),
						res.getString(6), res.getInt(7), res.getString(8), res.getDouble(9), res.getString(10)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return orders;
	}

	/**
	 * This function returns all the orders in a given month which are Group visit and the traveler is not subscriber
	 * 
	 * @param month  The month of the orders
	 * @param parkID The park id
	 * 
	 * @return ArrayList of order object
	 */
	public ArrayList<Order> getGroupsOrdersVisitorsReport(int month, int parkID) {
		ArrayList<Order> orders = new ArrayList<Order>();

		String sql = "SELECT * FROM g8gonature.order WHERE month(orderDate)=" + month + " AND parkId =" + parkID
				+ " AND orderType='Group Visit' AND orderStatus = 'Visit completed'";

		try {
			PreparedStatement query = conn.prepareStatement(sql);
			ResultSet res = query.executeQuery();

			while (res.next()) {
				orders.add(new Order(res.getInt(1), res.getString(2), res.getInt(3), res.getString(4), res.getString(5),
						res.getString(6), res.getInt(7), res.getString(8), res.getDouble(9), res.getString(10)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return orders;
	}

	/**
	 * this method calculates the number of visitors for each type and return it in a list.
	 * 
	 * @param month  The month of the orders
	 * @param parkID The park id
	 * 
	 * @return The number of visitors for each type
	 */
	public ArrayList<?> createNumberOfVisitorsReport(int month, int parkID) { // individual visitors (solo,familty) , orginized (Group) , subscribers.
		ArrayList<Integer> numberOfVisitorsPerType = new ArrayList<>();

		String sql1 = "SELECT SUM(numberOfParticipants) FROM g8gonature.order WHERE month(orderDate)=" + month
				+ " AND parkId =" + parkID + " AND orderType='Solo Visit' AND g8gonature.order.travelerId NOT IN"
				+ "(  SELECT g8gonature.subscriber.travelerId   FROM  g8gonature.subscriber) AND orderStatus='Visit completed'";

		String sql2 = "SELECT SUM(numberOfParticipants) FROM g8gonature.order WHERE month(orderDate)=" + month
				+ " AND parkId =" + parkID
				+ " AND (orderType='Family Visit' OR orderType='Solo Visit') AND g8gonature.order.travelerId IN "
				+ "(  SELECT g8gonature.subscriber.travelerId   FROM  g8gonature.subscriber) AND orderStatus = 'Visit completed'";

		String sql3 = "SELECT SUM(numberOfParticipants) FROM g8gonature.order WHERE month(orderDate)=" + month
				+ " AND parkId =" + parkID + " AND orderType='Group Visit' AND orderStatus = 'Visit completed'";

		PreparedStatement query;

		try {
			query = conn.prepareStatement(sql1);
			ResultSet res = query.executeQuery();

			if (res.next()) {
				numberOfVisitorsPerType.add(res.getInt(1));
			} else
				numberOfVisitorsPerType.add(0);

			query = conn.prepareStatement(sql2);
			res = query.executeQuery();

			if (res.next()) {
				numberOfVisitorsPerType.add(res.getInt(1));
			} else
				numberOfVisitorsPerType.add(0);

			query = conn.prepareStatement(sql3);
			res = query.executeQuery();

			if (res.next()) {
				numberOfVisitorsPerType.add(res.getInt(1));
			} else
				numberOfVisitorsPerType.add(0);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return numberOfVisitorsPerType;
	}

	/**
	 * this method calculates the income from visitors for each type of visitor, and return it in a list.
	 * 
	 * @param month  The month of the orders
	 * @param parkID The park id
	 * 
	 * @return The income from visitors for each type of visitor
	 */
	public ArrayList<?> createIncomeReport(int month, int parkID) { // individual visitors (solo,familty) , orginized (Group) , subscribers.

		ArrayList<Integer> totalIncomePerType = new ArrayList<>();

		String sql1 = "SELECT SUM(price) FROM g8gonature.order WHERE month(orderDate)=" + month + " AND parkId ="
				+ parkID + " AND orderType='Solo Visit' AND g8gonature.order.travelerId NOT IN"
				+ "(  SELECT g8gonature.subscriber.travelerId   FROM  g8gonature.subscriber) AND orderStatus='Visit completed'";

		String sql2 = "SELECT SUM(price) FROM g8gonature.order WHERE month(orderDate)=" + month + " AND parkId ="
				+ parkID
				+ " AND (orderType='Family Visit' OR orderType='Solo Visit') AND g8gonature.order.travelerId IN "
				+ "(  SELECT g8gonature.subscriber.travelerId   FROM  g8gonature.subscriber) AND orderStatus = 'Visit completed'";

		String sql3 = "SELECT SUM(price) FROM g8gonature.order WHERE month(orderDate)=" + month + " AND parkId ="
				+ parkID + " AND orderType='Group Visit' AND orderStatus = 'Visit completed'";

		PreparedStatement query;

		try {
			query = conn.prepareStatement(sql1);
			ResultSet res = query.executeQuery();

			if (res.next()) {
				totalIncomePerType.add(res.getInt(1));
			} else
				totalIncomePerType.add(0);

			query = conn.prepareStatement(sql2);
			res = query.executeQuery();

			if (res.next()) {
				totalIncomePerType.add(res.getInt(1));
			} else
				totalIncomePerType.add(0);

			query = conn.prepareStatement(sql3);
			res = query.executeQuery();

			if (res.next()) {
				totalIncomePerType.add(res.getInt(1));
			} else
				totalIncomePerType.add(0);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return totalIncomePerType;
	}

	/**
	 * this method adds a new report in database.
	 * 
	 * @param parameters The report info to add tp the database
	 */
	public void createNewReportInDB(ArrayList<?> parameters) {

		String sql = "INSERT INTO g8gonature.reports (reportType,parkId,month,comment) values (?,?,?,?)";

		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);

			query.setString(1, (String) parameters.get(1));
			query.setString(2, (String) parameters.get(2));
			query.setString(3, (String) parameters.get(0));
			query.setString(4, (String) parameters.get(3));

			query.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * This query gets confirmed orders after date passed for park ID in specific month
	 * 
	 * @param parameters ArrayList with parkId and month
	 * @return ArrayList with number of confirmed orders after date passed
	 */
	public ArrayList<Integer> getParkPendingDatePassed(ArrayList<?> parameters) {
		ArrayList<Integer> pending = new ArrayList<Integer>();
		int parkId = (int) parameters.get(0);
		int month = (int) parameters.get(1);

		String sql = "SELECT COUNT(*) FROM g8gonature.order WHERE parkId = ? AND MONTH(orderDate) = ? AND orderStatus = ? AND orderDate < curdate()";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			query.setInt(1, parkId);
			query.setInt(2, month);
			query.setString(3, OrderStatusName.CONFIRMED.toString());

			ResultSet res = query.executeQuery();
			while (res.next())
				pending.add(new Integer(res.getInt(1)));
		} catch (SQLException e) {
			System.out.println("Could not execute getParkPendingDatePassed query");
			e.printStackTrace();
		}
		return pending;
	}

	/**
	 * This creates objects of solo visitors divided by their entrance time
	 * 
	 * @param parameters current year ,selected month and day.
	 * @return ArrayList of VisitReport objects
	 */
	public ArrayList<VisitReport> CountSolosEnterTimeWithDays(ArrayList<?> parameters) {
		ArrayList<VisitReport> rep = new ArrayList<VisitReport>();
		int month = Integer.parseInt((String) parameters.get(0));
		int year = Calendar.getInstance().get(Calendar.YEAR);
		int day = Integer.parseInt((String) parameters.get(1));

		String sql = "SELECT SUM(test.numberOfParticipants),test.entrenceTime "
				+ "FROM (SELECT visit.travelerId,order.numberOfParticipants, visit.entrenceTime FROM g8gonature.visit , g8gonature.order "
				+ "WHERE NOT EXISTS ( SELECT subscriber.travelerId FROM g8gonature.subscriber WHERE visit.travelerId = subscriber.travelerId) "
				+ "AND visit.travelerId = order.travelerId AND order.orderType = 'Solo Visit' "
				+ "AND order.orderStatus = 'Visit completed' AND visit.visitDate = order.orderDate "
				+ "AND MONTH(visit.visitDate) = ? AND YEAR(visit.visitDate) = ? AND DAY(visit.visitDate) = ?) as test GROUP BY test.entrenceTime;";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			query.setInt(1, month);
			query.setInt(2, year);
			query.setInt(3, day);
			ResultSet res = query.executeQuery();
			while (res.next()) {
				VisitReport report = new VisitReport(res.getInt(1), res.getString(2));
				rep.add(report);
			}

		} catch (NullPointerException | SQLException e) {
			System.out.println("Could not execute CountSolosEnterTimeWithDays query");
			return null;
		}
		return rep;
	}

	/**
	 * This creates objects of subscribers visitors divided by their entrance time
	 * 
	 * @param parameters current year ,selected month and day.
	 * @return ArrayList of VisitReport objects
	 */
	public ArrayList<VisitReport> CountSubsEnterTimeWithDays(ArrayList<?> parameters) {
		ArrayList<VisitReport> rep = new ArrayList<VisitReport>();
		int month = Integer.parseInt((String) parameters.get(0));
		int year = Calendar.getInstance().get(Calendar.YEAR);
		int day = Integer.parseInt((String) parameters.get(1));

		String sql = "SELECT SUM(test.numberOfParticipants),test.entrenceTime "
				+ "FROM (SELECT visit.travelerId,order.numberOfParticipants, visit.entrenceTime "
				+ "FROM g8gonature.visit, g8gonature.order, g8gonature.subscriber "
				+ "WHERE visit.travelerId = order.travelerId "
				+ "AND (order.orderType = 'Solo Visit' OR order.orderType = 'Family Visit') "
				+ "AND visit.travelerId = subscriber.travelerId AND order.orderStatus = 'Visit completed' AND visit.visitDate = order.orderDate "
				+ "AND MONTH(visit.visitDate) = ? AND YEAR(visit.visitDate) = ? AND DAY(visit.visitDate) = ?) as test GROUP BY test.entrenceTime;";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			query.setInt(1, month);
			query.setInt(2, year);
			query.setInt(3, day);
			ResultSet res = query.executeQuery();
			while (res.next()) {
				VisitReport report = new VisitReport(res.getInt(1), res.getString(2));
				rep.add(report);
			}

		} catch (NullPointerException | SQLException e) {
			System.out.println("Could not execute CountSubsEnterTimeWithDays query");
			return null;
		}
		return rep;
	}

	/**
	 * This creates objects of group visitors divided by their entrance time
	 * 
	 * @param parameters current year ,selected month and day.
	 * @return ArrayList of VisitReport objects
	 */
	public ArrayList<VisitReport> CountGroupsEnterTimeWithDays(ArrayList<?> parameters) { 
		ArrayList<VisitReport> rep = new ArrayList<VisitReport>();
		int month = Integer.parseInt((String) parameters.get(0));
		int year = Calendar.getInstance().get(Calendar.YEAR);
		int day = Integer.parseInt((String) parameters.get(1));

		String sql = "SELECT SUM(test.numberOfParticipants), test.entrenceTime FROM "
				+ "(SELECT visit.travelerId,order.numberOfParticipants,visit.entrenceTime "
				+ "FROM g8gonature.visit, g8gonature.order WHERE visit.travelerId = order.travelerId "
				+ "AND order.orderType = 'Group Visit' AND order.orderStatus = 'Visit completed' "
				+ "AND visit.visitDate = order.orderDate AND visit.entrenceTime = order.orderTime "
				+ "AND MONTH(visit.visitDate) = ? AND YEAR(visit.visitDate) = ? AND DAY(visit.visitDate) = ?) AS test GROUP BY test.entrenceTime;";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			query.setInt(1, month);
			query.setInt(2, year);
			query.setInt(3, day);
			ResultSet res = query.executeQuery();
			while (res.next()) {
				VisitReport report = new VisitReport(res.getInt(1), res.getString(2));
				rep.add(report);
			}

		} catch (NullPointerException | SQLException e) {
			System.out.println("Could not execute CountGroupsEnterTimeWithDays query");
			return null;
		}
		return rep;
	}

	/**
	 * This creates objects of solo visitors dived by their stay time of visit
	 * 
	 * @param parameters current year selected month and selected day
	 * @return ArrayList of VisitReport objects
	 */
	public ArrayList<VisitReport> CountSolosVisitTimeWithDays(ArrayList<?> parameters) {
		ArrayList<VisitReport> rep = new ArrayList<VisitReport>();
		int month = Integer.parseInt((String) parameters.get(0));
		int year = Calendar.getInstance().get(Calendar.YEAR);
		int day = Integer.parseInt((String) parameters.get(1));

		String sql = "SELECT SUM(test.numberOfParticipants),TIMEDIFF (test.exitTime,test.entrenceTime) "
				+ "FROM (SELECT visit.travelerId,order.numberOfParticipants, visit.entrenceTime,visit.exitTime "
				+ "FROM g8gonature.visit , g8gonature.order WHERE NOT exists ( SELECT subscriber.travelerId FROM g8gonature.subscriber "
				+ "WHERE visit.travelerId = subscriber.travelerId) AND visit.travelerId = order.travelerId AND order.orderType = 'Solo Visit' "
				+ "AND order.orderStatus = 'Visit completed' AND visit.visitDate = order.orderDate "
				+ "AND MONTH(visit.visitDate) = ? AND YEAR(visit.visitDate) = ? AND DAY(visit.visitDate) = ?) as test GROUP BY TIMEDIFF(test.exitTime, test.entrenceTime);";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			query.setInt(1, month);
			query.setInt(2, year);
			query.setInt(3, day);
			ResultSet res = query.executeQuery();
			while (res.next()) {
				VisitReport report = new VisitReport(res.getInt(1), res.getString(2));
				rep.add(report);
			}

		} catch (SQLException e) {
			System.out.println("Could not execute CountSolos query");
			e.printStackTrace();
		}
		return rep;
	}

	/**
	 * This creates objects of subscriber visitors dived by their stay time of visit
	 * 
	 * @param parameters current year selected month and selected day
	 * @return ArrayList of VisitReport objects
	 */
	public ArrayList<VisitReport> CountSubsVisitTimeWithDays(ArrayList<?> parameters) {
		ArrayList<VisitReport> rep = new ArrayList<VisitReport>();
		int month = Integer.parseInt((String) parameters.get(0));
		int year = Calendar.getInstance().get(Calendar.YEAR);
		int day = Integer.parseInt((String) parameters.get(1));

		String sql = "SELECT SUM(test.numberOfParticipants),TIMEDIFF (test.exitTime,test.entrenceTime) "
				+ "FROM (SELECT visit.travelerId,order.numberOfParticipants, visit.entrenceTime, visit.exitTime "
				+ "FROM g8gonature.visit, g8gonature.order, g8gonature.subscriber WHERE visit.travelerId = order.travelerId "
				+ "AND (order.orderType = 'Solo Visit' OR order.orderType = 'Family Visit') AND visit.travelerId = subscriber.travelerId "
				+ "AND order.orderStatus = 'Visit completed' AND visit.visitDate = order.orderDate "
				+ "AND MONTH(visit.visitDate) = ? AND YEAR(visit.visitDate) = ? AND DAY(visit.visitDate) = ?) as test GROUP BY TIMEDIFF(test.exitTime, test.entrenceTime);;";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			query.setInt(1, month);
			query.setInt(2, year);
			query.setInt(3, day);
			ResultSet res = query.executeQuery();
			while (res.next()) {
				VisitReport report = new VisitReport(res.getInt(1), res.getString(2));
				rep.add(report);
			}

		} catch (SQLException e) {
			System.out.println("Could not execute CountSubs query");
			e.printStackTrace();
		}
		return rep;
	}

	/**
	 * This creates objects of group visitors dived by their stay time of visit
	 * 
	 * @param parameters current year selected month and selected day
	 * @return ArrayList of VisitReport objects
	 */
	public ArrayList<VisitReport> CountGroupsVisitTimeWithDays(ArrayList<?> parameters) { 
		ArrayList<VisitReport> rep = new ArrayList<VisitReport>();
		int month = Integer.parseInt((String) parameters.get(0));
		int year = Calendar.getInstance().get(Calendar.YEAR);
		int day = Integer.parseInt((String) parameters.get(1));

		String sql = "SELECT SUM(test.numberOfParticipants),TIMEDIFF (test.exitTime,test.entrenceTime) "
				+ "FROM (SELECT visit.travelerId,order.numberOfParticipants, visit.entrenceTime, visit.exitTime "
				+ "FROM g8gonature.visit,g8gonature.order WHERE "
				+ "visit.travelerId = order.travelerId AND order.orderType = 'Group Visit' "
				+ "AND order.orderStatus = 'Visit completed' AND visit.visitDate = order.orderDate AND visit.entrenceTime = order.orderTime "
				+ "AND MONTH(visit.visitDate) = ? AND YEAR(visit.visitDate) = ? AND DAY(visit.visitDate) = ?) as test GROUP BY TIMEDIFF(test.exitTime, test.entrenceTime);";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			query.setInt(1, month);
			query.setInt(2, year);
			query.setInt(3, day);
			ResultSet res = query.executeQuery();
			while (res.next()) {
				VisitReport report = new VisitReport(res.getInt(1), res.getString(2));
				rep.add(report);
			}

		} catch (SQLException e) {
			System.out.println("Could not execute CountGroup query");
			e.printStackTrace();
		}
		return rep;
	}

}
