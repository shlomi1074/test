package controllers.sqlHandlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

import logic.Discount;
import logic.OrderStatusName;
import logic.Request;

/**
 * This class handles all the queries which are related to requests
 * 
 */
public class RequestsQueries {
	private Connection conn;

	public RequestsQueries(Connection conn) {
		this.conn = conn;
	}

	/**
	 * This function retrieve the discount with the highest discount amount for a given date
	 * 
	 * @param parameters discount status, park id, date
	 * @return Discount object
	 */
	public Discount getMaxDisount(ArrayList<?> parameters) {

		Discount discount = null;
		String sql = "SELECT * FROM g8gonature.discount WHERE status = ? and parkId = ? and startDate <= ? and endDate >= ? ORDER BY amount DESC";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			query.setString(1, OrderStatusName.CONFIRMED.toString());
			query.setInt(2, (int) Integer.parseInt((String) parameters.get(1)));
			query.setString(3, (String) parameters.get(0));
			query.setString(4, (String) parameters.get(0));
			ResultSet res = query.executeQuery();
			if (res.next())
				discount = new Discount(res.getInt(1), res.getDouble(2), res.getString(3), res.getString(4),
						res.getInt(5), res.getString(6));
		} catch (SQLException e) {
			System.out.println("Could not execute getMaxDisount query");
			e.printStackTrace();
		}
		return discount;
	}

	/**
	 * this method is called after the Park Manager sent his request
	 * 
	 * @param managerRequests -type of request, value, old value, date,parkID,status
	 * 
	 */
	public void insertAllNewRequestsFromParkManager(ArrayList<?> managerRequests) {

		String sql = "INSERT INTO g8gonature.request (changeName,newValue,oldValue,requestDate,parkId,requestStatus) values (?,?,?,?,?,?)";
		String sql2 = "INSERT INTO g8gonature.discount (amount,startDate,endDate,parkId,status) values (?,?,?,?,?)";

		java.sql.Date date = new java.sql.Date(Calendar.getInstance().getTime().getTime());
		String parkID = (String) managerRequests.get(6);

		PreparedStatement query;
		PreparedStatement query2;

		try {
			query = conn.prepareStatement(sql); // handles updates

			if (managerRequests.get(0) != null && !((String) managerRequests.get(0)).equals("")) {

				query.setString(1, "UPDATE MAX VISITORS");
				query.setString(2, (String) managerRequests.get(0));
				query.setInt(3, getOldValFromParkParameters("maxVisitors", Integer.parseInt(parkID))); // edited
				query.setDate(4, date);
				query.setInt(5, Integer.parseInt(parkID));
				query.setString(6, OrderStatusName.PENDING.toString());
				query.executeUpdate();
			}
			if (managerRequests.get(1) != null && !((String) managerRequests.get(1)).equals("")) {

				query.setString(1, "UPDATE ESTIMATED STAY TIME");
				query.setString(2, (String) managerRequests.get(1));
				query.setInt(3, getOldValFromParkParameters("estimatedStayTime", Integer.parseInt(parkID)));// edited
				query.setDate(4, date);
				query.setInt(5, Integer.parseInt(parkID));
				query.setString(6, OrderStatusName.PENDING.toString());
				query.executeUpdate();

			}

			if (managerRequests.get(2) != null && !((String) managerRequests.get(2)).equals("")) {

				query.setString(1, "UPDATE GAP");
				query.setString(2, (String) managerRequests.get(2));
				query.setInt(3, getOldValFromParkParameters("gapBetweenMaxAndCapacity", Integer.parseInt(parkID)));
				query.setDate(4, date);
				query.setInt(5, Integer.parseInt(parkID));
				query.setString(6, OrderStatusName.PENDING.toString());
				query.executeUpdate();

			}

			query2 = conn.prepareStatement(sql2); /// handles discount

			if (managerRequests.get(3) != null && managerRequests.get(4) != null && managerRequests.get(5) != null
					&& !((String) managerRequests.get(3)).equals("") && !((String) managerRequests.get(4)).equals("")
					&& !((String) managerRequests.get(5)).equals("")) {

				query2.setString(1, (String) managerRequests.get(5));
				query2.setString(2, (String) managerRequests.get(3)); //
				query2.setString(3, (String) managerRequests.get(4)); //
				query2.setInt(4, Integer.parseInt(parkID));
				query2.setString(5, OrderStatusName.PENDING.toString());

				query2.executeUpdate();
			}

		} catch (SQLException e) {
			System.out.println("Could not execute checkIfConnected query");
			e.printStackTrace();
		}

	}

	/**
	 * this method gets data from request table and put it in a Request object.
	 * 
	 * @return ArrayList of Request objects - all the requests from the database
	 */
	public ArrayList<?> GetRequestsFromDB() {
		ArrayList<Request> requests = new ArrayList<>();
		int i = 0;
		String sql = "SELECT * FROM g8gonature.request ORDER BY requestId DESC";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			ResultSet res = query.executeQuery();

			while (res.next()) {
				requests.add(i,
						new Request(res.getInt(1), res.getString(2), res.getString(3), res.getString(4), null,
								res.getInt(6), // added parkId
								res.getString(7)));
				i++;
			}
		} catch (SQLException e) {
			System.out.println("Could not execute checkIfConnected query");
			e.printStackTrace();
		}

		return requests;

	}

	/**
	 * this method changes the status of a request according to Department Manager decision
	 * 
	 * @param bool 'true' to confirm, 'false' to cancel
	 * @param requestsID The request id to change
	 */
	public void changeStatusOfRequest(boolean bool, int requestsID) {
		String sql;

		if (bool)
			sql = "UPDATE g8gonature.request SET requestStatus='Confirmed' WHERE requestId=" + requestsID;

		else {
			sql = "UPDATE g8gonature.request SET requestStatus='declined' WHERE requestId=" + requestsID;
		}

		PreparedStatement query;

		try {
			query = conn.prepareStatement(sql);
			query.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * this method changes the status of a discount according to Department Manager decision
	 * 
	 * @param bool 'true' to confirm, 'false' to cancel
	 * @param discountsID The discount id to change
	 */
	public void changeStatusOfDiscount(boolean bool, int discountsID) {

		String sql;

		if (bool)
			sql = "UPDATE g8gonature.discount SET status='confirmed' WHERE discountId=" + discountsID;

		else {
			sql = "UPDATE g8gonature.discount SET status='declined' WHERE discountId=" + discountsID;
		}

		PreparedStatement query;

		try {
			query = conn.prepareStatement(sql);
			query.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * this method gets data from discount table and put it in a Discount object.
	 * 
	 * @return ArrayList of Discount objects
	 */
	public ArrayList<?> GetDiscountsFromDB() {

		ArrayList<Discount> discount = new ArrayList<>();
		int i = 0;
		String sql = "SELECT * FROM g8gonature.discount ORDER BY discountId DESC";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			ResultSet res = query.executeQuery();

			while (res.next()) {
				discount.add(i, new Discount(res.getInt(1), res.getDouble(2), res.getString(3), res.getString(4),
						res.getInt(5), res.getString(6))); // id was changed to int from String
				i++;
			}
		} catch (SQLException e) {
			System.out.println("Could not execute checkIfConnected query");
			e.printStackTrace();
		}

		return discount;
	}

	/**
	 * This function gets the old value of specific parameter
	 * 
	 * @param nameOfColumn The name of the parameter
	 * @param parkID The park id
	 * @return value from park table
	 */
	public int getOldValFromParkParameters(String nameOfColumn, int parkID) {

		String sql = "SELECT g8gonature.park." + nameOfColumn + " FROM g8gonature.park WHERE g8gonature.park.parkId="
				+ parkID + "";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			ResultSet res = query.executeQuery();

			if (res.next()) {
				return res.getInt(1);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;

	}

}
