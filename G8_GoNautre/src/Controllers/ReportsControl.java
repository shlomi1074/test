package Controllers;

import java.util.ArrayList;
import java.util.Arrays;
import client.ChatClient;
import client.ClientUI;
import logic.ClientToServerRequest;
import logic.Report;
import logic.VisitReport;
import logic.ClientToServerRequest.Request;
import logic.Order;

/**
 * ReportsControl class handles all the report related functionalities
 */
@SuppressWarnings("unchecked")
public class ReportsControl {

	// Refactor
	public static IReportsManager reportsManager = new ReportsManager();

	public ReportsControl() {
		ReportsControl.reportsManager = new ReportsManager();
	}

	public ReportsControl(IReportsManager reportsManager) {
		ReportsControl.reportsManager = reportsManager;
	}

	/**
	 * This function gets a report and insert it to the database.
	 * 
	 * @param r the report to add to database
	 * @return true on success, false otherwise
	 */
	public static boolean addReport(Report r) {
		removeReport(r);
		ClientToServerRequest<Report> request = new ClientToServerRequest<>(Request.INSERT_REPORT,
				new ArrayList<Report>(Arrays.asList(r)));
		ClientUI.chat.accept(request);
		return ChatClient.responseFromServer.isResult();
	}

	/**
	 * This function gets a report and delete it from the database.
	 * 
	 * @param r the report to delete from the database
	 */
	private static void removeReport(Report r) {
		ClientToServerRequest<Report> request = new ClientToServerRequest<>(Request.DELETE_REPORT,
				new ArrayList<Report>(Arrays.asList(r)));
		ClientUI.chat.accept(request);
	}

	/**
	 * This function receive month of current year and asks the server
	 * to send the number of solo visitors at this month by entrance hour.
	 * 
	 * @param month the current month number
	 */
	/* Refactor : changed from void to return ArrayList */
	public static ArrayList<VisitReport> countSolosEnterTime(int month) {
		ClientToServerRequest<String> request = new ClientToServerRequest<>(Request.COUNT_ENTER_SOLO_VISITORS,
				new ArrayList<String>(Arrays.asList(String.valueOf(month))));
		ClientUI.chat.accept(request);
		return (ArrayList<VisitReport>) ChatClient.responseFromServer.getResultSet();
	}

	/**
	 * This function receive month of current year and asks the server
	 * to send the number of subscriber visitors at this month by entrance hour.
	 * 
	 * @param month the current month number
	 */
	/* Refactor : changed from void to return ArrayList */
	public static ArrayList<VisitReport> countSubsEnterTime(int month) {
		ClientToServerRequest<String> request = new ClientToServerRequest<>(Request.COUNT_ENTER_SUBS_VISITORS,
				new ArrayList<String>(Arrays.asList(String.valueOf(month))));
		ClientUI.chat.accept(request);
		return (ArrayList<VisitReport>) ChatClient.responseFromServer.getResultSet();
	}

	/**
	 * This function receive month of current year and asks the server to send the number of group visitors at this month by entrance hour.
	 * 
	 * @param month the current month number
	 */
	/* Refactor : changed from void to return ArrayList */
	public static ArrayList<VisitReport> countGroupsEnterTime(int month) {
		ClientToServerRequest<String> request = new ClientToServerRequest<>(Request.COUNT_ENTER_GROUP_VISITORS,
				new ArrayList<String>(Arrays.asList(String.valueOf(month))));
		ClientUI.chat.accept(request);
		return (ArrayList<VisitReport>) ChatClient.responseFromServer.getResultSet();
	}

	/**
	 * This function receive month of current year and asks the server
	 * to send the number of solo visitors at this month dived by their visit time.
	 * 
	 * @param month the current month number
	 */
	public static void countSolosVisitTime(int month) {
		ClientToServerRequest<String> request = new ClientToServerRequest<>(Request.COUNT_VISIT_SOLO_VISITORS,
				new ArrayList<String>(Arrays.asList(String.valueOf(month))));
		ClientUI.chat.accept(request);
	}

	/**
	 * This function receive month of current year and asks the server
	 * to send the number of subscriber visitors at this month dived by their visit time.
	 * 
	 * @param month the current month number
	 */
	public static void countSubsVisitTime(int month) {
		ClientToServerRequest<String> request = new ClientToServerRequest<>(Request.COUNT_VISIT_SUBS_VISITORS,
				new ArrayList<String>(Arrays.asList(String.valueOf(month))));
		ClientUI.chat.accept(request);
	}

	/**
	 * This function receive month of current year and asks the server
	 * to send the number of group visitors at this month dived by their visit time.
	 * 
	 * @param month the current month number
	 */
	public static void countGroupsVisitTime(int month) {
		ClientToServerRequest<String> request = new ClientToServerRequest<>(Request.COUNT_VISIT_GROUP_VISITORS,
				new ArrayList<String>(Arrays.asList(String.valueOf(month))));
		ClientUI.chat.accept(request);
	}

	/**
	 * This function gets reports from DB
	 * 
	 * @return ArrayList of reports
	 */
	public static ArrayList<Report> getReports() {
		ClientToServerRequest<String> request = new ClientToServerRequest<>(Request.GET_REPORTS,
				new ArrayList<String>(Arrays.asList()));
		ClientUI.chat.accept(request);
		return ChatClient.responseFromServer.getResultSet();
	}

	/**
	 * This function gets cancelled orders number for a certain park in a specific month - for cancel report.
	 * 
	 * @param parkID park's ID
	 * @param month  month we want report for
	 * @return ArrayList with number of cancelled orders
	 */
	public static ArrayList<Integer> getParkCancels(int parkID, int month) {
		ClientToServerRequest<Integer> request = new ClientToServerRequest<>(Request.GET_CANCELS,
				new ArrayList<Integer>(Arrays.asList(parkID, month)));
		ClientUI.chat.accept(request);
		return ChatClient.responseFromServer.getResultSet();
	}

	/**
	 * this function shows the report for the specified type and month
	 * 
	 * @param arrayOfRequests the array contains month number and the park id
	 */
	public static void showReport(ArrayList<String> arrayOfRequests) {
		ClientToServerRequest<?> request = new ClientToServerRequest<>(Request.MANAGER_REPORT, arrayOfRequests);
		ClientUI.chat.accept(request);
	}

	/**
	 * adds new report with the specified type and month.
	 * 
	 * @param monthAndType month number and report type
	 */
	public static void addNewReportToDB(ArrayList<String> monthAndType) {
		ClientToServerRequest<?> request = new ClientToServerRequest<>(Request.ADD_REPORT_TO_DB, monthAndType);
		ClientUI.chat.accept(request);
	}

	/**
	 * This function get pending orders after date has passed for a certain park in a specific month - for cancel report.
	 * 
	 * @param parkID park's ID
	 * @param month  month we want report for
	 * @return ArrayList with number of pending orders that passed todays date
	 */
	public static ArrayList<Integer> getParkPendingDatePassed(int parkID, int month) {
		ClientToServerRequest<Integer> request = new ClientToServerRequest<>(Request.GET_PENDING_AFTER_DATE_PASSED,
				new ArrayList<Integer>(Arrays.asList(parkID, month)));
		ClientUI.chat.accept(request);
		return ChatClient.responseFromServer.getResultSet();
	}

	/**
	 * This function returns all the orders in a given month which are Solo visit and the traveler is not subscriber
	 * 
	 * @param parkID      park's ID
	 * @param monthNumber the month of the orders
	 * 
	 * @return ArrayList of order object
	 */
	public static ArrayList<Order> getSolosOrdersVisitorsReport(int monthNumber, int parkID) {
		ClientToServerRequest<Integer> request = new ClientToServerRequest<>(Request.GET_SOLOS_ORDERS,
				new ArrayList<Integer>(Arrays.asList(monthNumber, parkID)));
		ClientUI.chat.accept(request);
		return ChatClient.responseFromServer.getResultSet();
	}

	/**
	 * This function returns all the orders in a given month which made by Subscribers
	 * 
	 * @param monthNumber the month of the orders
	 * @param parkID      The park id
	 * 
	 * @return ArrayList of order object
	 */
	public static ArrayList<Order> getSubscribersOrdersVisitorsReport(int monthNumber, int parkID) {
		ClientToServerRequest<Integer> request = new ClientToServerRequest<>(Request.GET_SUBSCRIBERS_ORDERS,
				new ArrayList<Integer>(Arrays.asList(monthNumber, parkID)));
		ClientUI.chat.accept(request);
		return ChatClient.responseFromServer.getResultSet();
	}

	/**
	 * This function returns all the orders in a given month which are Group visits
	 * 
	 * @param monthNumber the month of the orders
	 * @param parkID      The park id
	 * 
	 * @return ArrayList of order object
	 */
	public static ArrayList<Order> getGroupOrdersVisitorsReport(int monthNumber, int parkID) {
		ClientToServerRequest<Integer> request = new ClientToServerRequest<>(Request.GET_GROUPS_ORDERS,
				new ArrayList<Integer>(Arrays.asList(monthNumber, parkID)));
		ClientUI.chat.accept(request);
		return ChatClient.responseFromServer.getResultSet();
	}

	/**
	 * This function returns all the orders of a given month and day of current year
	 * which are solo visitors
	 * 
	 * @param month The report's month
	 * @param day   The day in the month
	 * @return
	 */
	public static ArrayList<VisitReport> countSolosEnterTimeWithDays(int month, String day) {
		ClientToServerRequest<String> request = new ClientToServerRequest<>(
				Request.COUNT_ENTER_SOLOS_VISITORS_WITH_DAYS,
				new ArrayList<String>(Arrays.asList(String.valueOf(month), day)));
		ClientUI.chat.accept(request);
		return (ArrayList<VisitReport>) ChatClient.responseFromServer.getResultSet();
	}

	/**
	 * This function returns all the orders of a given month and day of current year
	 * which are subscriber visitors
	 * 
	 * @param month The report's month
	 * @param day   The day in the month
	 * @return
	 */
	/* Refactor : changed from void to return ArrayList */
	public static ArrayList<VisitReport> countSubsEnterTimeWithDays(int month, String day) {
		ClientToServerRequest<String> request = new ClientToServerRequest<>(Request.COUNT_ENTER_SUBS_VISITORS_WITH_DAYS,
				new ArrayList<String>(Arrays.asList(String.valueOf(month), day)));
		ClientUI.chat.accept(request);
		return (ArrayList<VisitReport>) ChatClient.responseFromServer.getResultSet();
	}

	/**
	 * This function returns all the orders of a given month and day of current year
	 * which are group visitors
	 * 
	 * @param month The report's month
	 * @param day   The day in the month
	 * @return
	 */
	/* Refactor : changed from void to return ArrayList */
	public static ArrayList<VisitReport> countGroupsEnterTimeWithDays(int month, String day) {
		ClientToServerRequest<String> request = new ClientToServerRequest<>(
				Request.COUNT_ENTER_GROUPS_VISITORS_WITH_DAYS,
				new ArrayList<String>(Arrays.asList(String.valueOf(month), day)));
		ClientUI.chat.accept(request);
		return (ArrayList<VisitReport>) ChatClient.responseFromServer.getResultSet();
	}

	/**
	 * This function receive month and day of current year and asks the server
	 * to send the number of solo visitors at this month dived by their visit time.
	 * 
	 * @param month The report's month
	 * @param day   The day in the month
	 */
	/* Refactor : changed from void to return ArrayList */
	public static void countSolosVisitTimeWithDay(int month, String day) {
		ClientToServerRequest<String> request = new ClientToServerRequest<>(
				Request.COUNT_VISIT_SOLOS_VISITORS_WITH_DAYS,
				new ArrayList<String>(Arrays.asList(String.valueOf(month), day)));
		ClientUI.chat.accept(request);

	}

	/**
	 * This function receive month and day of current year and asks the server
	 * to send the number of subscriber visitors at this month dived by their visit time.
	 * 
	 * @param month The report's month
	 * @param day   The day in the month
	 */
	public static void countSubsVisitTimeWithDay(int month, String day) {
		ClientToServerRequest<String> request = new ClientToServerRequest<>(Request.COUNT_VISIT_SUBS_VISITORS_WITH_DAYS,
				new ArrayList<String>(Arrays.asList(String.valueOf(month), day)));
		ClientUI.chat.accept(request);

	}

	/**
	 * This function receive month and day of current year and asks the server
	 * to send the number of group visitors at this month dived by their visit time.
	 * 
	 * @param month The report's month
	 * @param day   The day in the month
	 */
	public static void countGroupsVisitTimeWithDay(int month, String day) {
		ClientToServerRequest<String> request = new ClientToServerRequest<>(
				Request.COUNT_VISIT_GROUPS_VISITORS_WITH_DAYS,
				new ArrayList<String>(Arrays.asList(String.valueOf(month), day)));
		ClientUI.chat.accept(request);

	}

	// Refactor
	/* Wrapper for reports methods */
	public static class ReportsManager implements IReportsManager {

		@Override
		public ArrayList<VisitReport> countSolosEnterTime(int month) {
			return ReportsControl.countSolosEnterTime(month);
		}

		@Override
		public ArrayList<VisitReport> countSubsEnterTime(int month) {
			return ReportsControl.countSubsEnterTime(month);
		}

		@Override
		public ArrayList<VisitReport> countGroupsEnterTime(int month) {
			return ReportsControl.countGroupsEnterTime(month);
		}

		@Override
		public ArrayList<VisitReport> countSolosEnterTimeWithDays(int month, String day) {
			return ReportsControl.countSolosEnterTimeWithDays(month, day);
		}

		@Override
		public ArrayList<VisitReport> countSubsEnterTimeWithDays(int month, String day) {
			return ReportsControl.countSubsEnterTimeWithDays(month, day);
		}

		@Override
		public ArrayList<VisitReport> countGroupsEnterTimeWithDays(int month, String day) {
			return ReportsControl.countGroupsEnterTimeWithDays(month, day);
		}

	}

}
