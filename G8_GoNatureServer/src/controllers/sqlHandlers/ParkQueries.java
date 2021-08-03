package controllers.sqlHandlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import logic.Order;
import logic.Park;

/**
 * This class handles all the queries which are related to parks
 */
public class ParkQueries {
	private Connection conn;

	public ParkQueries(Connection conn) {
		this.conn = conn;
	}

	/**
	 * This function gets a park id and retrieve the relevant park from the database
	 * 
	 * @param parameters the park id
	 * @return Park object
	 */
	public Park getParkById(ArrayList<?> parameters) {
		Park park = null;
		String sql = "SELECT * FROM g8gonature.park WHERE parkId = ? ";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			query.setInt(1, Integer.parseInt((String) parameters.get(0)));
			ResultSet res = query.executeQuery();

			if (res.next())
				park = new Park(res.getInt(1), res.getString(2), res.getInt(3), res.getInt(4), res.getInt(5),
						res.getInt(6));
		} catch (SQLException e) {
			System.out.println("Could not execute getParkById query");
			e.printStackTrace();
		}

		return park;
	}

	/**
	 * This function gets a park name and retrieve the relevant park from the database
	 * 
	 * @param parameters the park name
	 * @return Park object
	 */
	public Park getParkByName(ArrayList<?> parameters) {
		Park park = null;
		String sql = "SELECT * FROM g8gonature.park WHERE parkName = ? ";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			query.setString(1, (String) parameters.get(0));
			ResultSet res = query.executeQuery();

			if (res.next())
				park = new Park(res.getInt(1), res.getString(2), res.getInt(3), res.getInt(4), res.getInt(5),
						res.getInt(6));
		} catch (SQLException e) {
			System.out.println("Could not execute getParkByName query");
			e.printStackTrace();
		}

		return park;
	}

	/**
	 * This function retrieve all the parks from the database
	 * 
	 * @return ArrayList of Park objects
	 */
	public ArrayList<Park> getAllParks() {
		ArrayList<Park> parks = new ArrayList<Park>();
		String sql = "SELECT * FROM g8gonature.park";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			ResultSet res = query.executeQuery();

			while (res.next())
				parks.add(new Park(res.getInt(1), res.getString(2), res.getInt(3), res.getInt(4), res.getInt(5),
						res.getInt(6)));
		} catch (SQLException e) {
			System.out.println("Could not execute getAllParks query");
			e.printStackTrace();
		}

		return parks;
	}

	/**
	 * This function retrieve all the comments column from 'fullparkdate' table for a given date and park
	 * 
	 * @param parameters date to check, park to check
	 * @return ArrayList with all the comments
	 */
	public ArrayList<String> isParkIsFullAtDate(ArrayList<?> parameters) {
		ArrayList<String> comment = new ArrayList<String>();

		String sql = "SELECT comment FROM g8gonature.fullparkdate WHERE date = ? and parkId = ?";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			query.setString(1, (String) parameters.get(0));
			query.setInt(2, Integer.parseInt((String) parameters.get(1)));
			ResultSet res = query.executeQuery();

			while (res.next())
				comment.add(res.getString(1));

			if (comment.size() == 0)
				comment.add("notFull");

		} catch (SQLException e) {
			System.out.println("Could not execute isParkIsFullAtDate");
			e.printStackTrace();
		}
		return comment;
	}

	/**
	 * This function insert row to 'fullparkdate' table
	 * 
	 * @param parameters park id, date, max visitors, comment
	 * @return true on success, false otherwise
	 */
	public boolean insertToFullParkDate(ArrayList<?> parameters) {
		String sql = "INSERT INTO g8gonature.fullparkdate  (parkId, date, maxVisitors,comment)  values (?, ?,?,?)";
		int res = 0;
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			query.setInt(1, Integer.parseInt((String) parameters.get(0)));
			query.setString(2, (String) parameters.get(1));
			query.setInt(3, Integer.parseInt((String) parameters.get(2)));
			query.setString(4, (String) parameters.get(3));
			res = query.executeUpdate();
			return res == 1;
		} catch (SQLException e) {
			System.out.println("Could not execute insertToFullParkDate query");
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * This function update number of visitors in a certion park.
	 * 
	 * @param parameters - ArrayList parkId, new number of current visitors.
	 * @return true on success, false otherwise
	 */
	public boolean updateNumberOfVisitors(ArrayList<?> parameters) {
		int res = 0;
		String sql = "UPDATE g8gonature.park SET currentVisitors = ? WHERE parkId = ?";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			query.setString(1, (String) parameters.get(0));
			query.setInt(2, Integer.parseInt((String) parameters.get(1)));
			res = query.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Could not execute updateNumberOfVisitors query");
			e.printStackTrace();
		}
		return res == 1;
	}

	/**
	 * This function add a new visit to the database
	 * 
	 * @param parameters - ArrayList containing: travelerId,parkId,entrence Time,estimated stay time,visitDate
	 * @return true on success, false otherwise
	 */
	public boolean addVisit(ArrayList<?> parameters) {

		String enterTime = (String) parameters.get(2);
		String estimated = (String) parameters.get(3);

		LocalTime exitTime = LocalTime.parse(enterTime).plusHours(Integer.parseInt(estimated));
		int res = 0;

		String sql = "INSERT INTO g8gonature.visit (travelerId,parkId,entrenceTime,exitTime,visitDate) "
				+ "VALUES (?,?,?,?,?)";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			query.setString(1, (String) parameters.get(0));
			query.setString(2, (String) parameters.get(1));
			query.setString(3, (String) parameters.get(2));
			query.setString(4, exitTime.toString());
			query.setString(5, (String) parameters.get(4));
			res = query.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Could not execute addVisit query");
			e.printStackTrace();
		}
		return res == 1;
	}

	/**
	 * This function retrieve all the 'card_reader_simulator' table
	 * 
	 * @return ArrayList of Strings with all the data from card_reader_simulator
	 */
	public ArrayList<String> getSimulatorTravelersId() {
		ArrayList<String> travelersID = new ArrayList<>();
		String sql = "SELECT * FROM g8gonature.card_reader_simulator";
		PreparedStatement query;
		try {
			query = conn.prepareStatement(sql);
			ResultSet rs = query.executeQuery();

			while (rs.next()) {
				travelersID.add(rs.getString(1) + " " + rs.getString(2));
			}
		} catch (SQLException e) {
			System.out.println("Could not execute getSimulatorTravelersId query");
			e.printStackTrace();
		}

		return travelersID;
	}

	/**
	 * This function updates the exit time of a visit in the database
	 * This function is only for the card reader simulation.
	 * 
	 * @param order the order to update
	 * @param exitTime The traveler exit time
	 */
	public void updateVisitExitTimeSimulator(Order order, String exitTime) {
		String sql = "UPDATE g8gonature.visit SET exitTime = ? WHERE travelerId = ? AND parkId = ? AND entrenceTime = ? AND visitDate = ?";
		PreparedStatement query;
		String time = order.getOrderTime();
		String newExitTime = null;
		if (exitTime.isEmpty())
			newExitTime = String.valueOf(Integer.parseInt(time.split(":")[0]) + 3) + ":" + time.split(":")[1];
		else 
			newExitTime = exitTime;
		try {
			query = conn.prepareStatement(sql);
			query.setString(1, newExitTime);
			query.setString(2, order.getTravelerId());
			query.setInt(3, order.getParkId());
			query.setString(4, order.getOrderTime());
			query.setString(5, order.getOrderDate());
			query.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Could not execute updateVisitExitTimeSimulator");
			e.printStackTrace();
		}

	}

	/**
	 * This function UPDATE the park parameters in the database
	 * 
	 * @param parameters the new park parameters
	 */
	public void changeParkParametersInDB(ArrayList<?> parameters) {

		PreparedStatement query;
		String typeOfRequest = null;
		String sql;

		if (((String) parameters.get(0)).equals("UPDATE MAX VISITORS"))
			typeOfRequest = "maxVisitors";
		if (((String) parameters.get(0)).equals("UPDATE ESTIMATED STAY TIME"))
			typeOfRequest = "estimatedStayTime";
		if (((String) parameters.get(0)).equals("UPDATE GAP"))
			typeOfRequest = "gapBetweenMaxAndCapacity";

		try {

			sql = "UPDATE g8gonature.park SET " + typeOfRequest + "=? WHERE parkId=" + parameters.get(2) + "";
			query = conn.prepareStatement(sql);
			query.setInt(1, Integer.parseInt((String) parameters.get(1)));
			query.executeUpdate();

		}

		catch (SQLException e) {
			e.printStackTrace();
		}

	}
}
