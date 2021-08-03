package UnitTest;

import static org.junit.Assert.*;
import java.sql.Connection;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;
import controllers.sqlHandlers.MysqlConnection;
import controllers.sqlHandlers.ReportsQueries;
import logic.VisitReport;

public class EntranceReportServerSide {

	private static Connection mysqlconnection;
	private ReportsQueries dbQueries;
	private ArrayList<String> parameters;

	@Before
	public void setUp() throws Exception {
		mysqlconnection = MysqlConnection.getInstance().getConnection();
		dbQueries = new ReportsQueries(mysqlconnection);
		parameters = new ArrayList<>();
	}

	/**
	 * This test checks the count of solo in order to check CountSolosEnterTime query
	 * input: "1"
	 * expected result: 10
	 */
	@Test
	public void CountSolosEnterTimeTest() {
		parameters.add("1");
		ArrayList<VisitReport> actual = dbQueries.CountSolosEnterTime(parameters);
		int expected = 10;
		assertEquals(expected, actual.size());
	}

	/**
	 * This test checks the count of solo in an empty month in order to check CountSolosEnterTime query
	 * input: "2"
	 * expected result: 0
	 */
	@Test
	public void CountSolosEnterTimeEmptyMonthTest() {
		parameters.add("2");
		ArrayList<VisitReport> actual = dbQueries.CountSolosEnterTime(parameters);
		int expected = 0;
		assertEquals(expected, actual.size());
	}

	/**
	 * This test checks the count of solo when connection failed in order to check CountSolosEnterTime exception
	 * input: "2" , dbQueries = ReportsQueries(null)
	 * expected result: null
	 */
	@Test
	public void CountSolosEnterTimeFailedSQLTest() {
		dbQueries = new ReportsQueries(null);
		parameters.add("2");
		ArrayList<VisitReport> actual = dbQueries.CountSolosEnterTime(parameters);
		assertEquals(null, actual);
	}

	/**
	 * This test checks the count of subscriber in order to check CountSubsEnterTime query
	 * input: "1"
	 * expected result: 13
	 */
	@Test
	public void CountSubsEnterTimeTest() {
		parameters.add("1");
		ArrayList<VisitReport> actual = dbQueries.CountSubsEnterTime(parameters);
		int expected = 13;
		assertEquals(expected, actual.size());
	}

	/**
	 * This test checks the count of subscriber in an empty month in order to check CountSubsEnterTime query
	 * input: "2"
	 * expected result: 0
	 */
	@Test
	public void CountSubsEnterTimeEmptyMonthTest() {
		parameters.add("2");
		ArrayList<VisitReport> actual = dbQueries.CountSubsEnterTime(parameters);
		int expected = 0;
		assertEquals(expected, actual.size());
	}

	/**
	 * This test checks the count of subscriber when connection failed in order to check CountSubsEnterTime exception
	 * input: "2" , dbQueries = ReportsQueries(null)
	 * expected result: null
	 */
	@Test
	public void CountSubsEnterTimeFailedSQLTest() {
		dbQueries = new ReportsQueries(null);
		parameters.add("2");
		ArrayList<VisitReport> actual = dbQueries.CountSubsEnterTime(parameters);
		assertEquals(null, actual);
	}

	/**
	 * This test checks the count of group in order to check CountGroupsEnterTime query
	 * input: "1"
	 * expected result: 22
	 */
	@Test
	public void CountGroupsEnterTimeTest() {
		parameters.add("1");
		ArrayList<VisitReport> actual = dbQueries.CountGroupsEnterTime(parameters);
		int expected = 22;
		assertEquals(expected, actual.size());
	}

	/**
	 * This test checks the count of group in an empty month in order to check CountGroupsEnterTime query
	 * input: "2"
	 * expected result: 0
	 */
	@Test
	public void CountGroupsEnterTimeEmptyMonthTest() {
		parameters.add("2");
		ArrayList<VisitReport> actual = dbQueries.CountGroupsEnterTime(parameters);
		int expected = 0;
		assertEquals(expected, actual.size());
	}

	/**
	 * This test checks the count of group when connection failed in order to check CountGroupsEnterTime exception
	 * input: "2" , dbQueries = ReportsQueries(null)
	 * expected result: null
	 */
	@Test
	public void CountGroupsEnterTimeFailedSQLTest() {
		dbQueries = new ReportsQueries(null);
		parameters.add("2");
		ArrayList<VisitReport> actual = dbQueries.CountGroupsEnterTime(parameters);
		assertEquals(null, actual);
	}

	/**
	 * This test checks the count of solo in specific day order to check CountSolosEnterTimeWithDays query
	 * input: day = "1" , month = "1"
	 * expected result: 1
	 */
	@Test
	public void CountSolosEnterTimeWithDaysTest() {
		parameters.add("1");
		parameters.add("1");
		ArrayList<VisitReport> actual = dbQueries.CountSolosEnterTimeWithDays(parameters);
		int expected = 1;
		assertEquals(expected, actual.size());
	}

	/**
	 * This test checks the count of solo in empty specific day order to check CountSolosEnterTimeWithDays query
	 * input: day = "5" , month = "1"
	 * expected result: 0
	 */
	@Test
	public void CountSolosEnterTimeWithDaysEmptyDayTest() {
		parameters.add("1");
		parameters.add("5");
		ArrayList<VisitReport> actual = dbQueries.CountSolosEnterTimeWithDays(parameters);
		int expected = 0;
		assertEquals(expected, actual.size());
	}

	/**
	 * This test checks the count of solo in specific day when connection failed in order to check CountSolosEnterTimeWithDays exception
	 * input: day = "2" , month = "2" , dbQueries = ReportsQueries(null)
	 * expected result: null
	 */
	@Test
	public void CountSolosEnterTimeWithDaysFailedSQLTest() {
		dbQueries = new ReportsQueries(null);
		parameters.add("2");
		parameters.add("2");
		ArrayList<VisitReport> actual = dbQueries.CountSolosEnterTimeWithDays(parameters);
		assertEquals(null, actual);
	}

	/**
	 * This test checks the count of subscriber in specific day order to check CountSubsEnterTimeWithDays query
	 * input: day = "4" , month = "1"
	 * expected result: 3
	 */
	@Test
	public void CountSubsEnterTimeWithDaysTest() {
		parameters.add("1");
		parameters.add("4");
		ArrayList<VisitReport> actual = dbQueries.CountSubsEnterTimeWithDays(parameters);
		int expected = 3;
		assertEquals(expected, actual.size());
	}

	/**
	 * This test checks the count of subscriber in empty specific day order to check CountSubsEnterTimeWithDays query
	 * input: day = "5" , month = "1"
	 * expected result: 0
	 */
	@Test
	public void CountSubsEnterTimeWithDaysEmptyDayTest() {
		parameters.add("1");
		parameters.add("5");
		ArrayList<VisitReport> actual = dbQueries.CountSubsEnterTimeWithDays(parameters);
		int expected = 0;
		assertEquals(expected, actual.size());
	}

	/**
	 * This test checks the count of subscriber in specific day when connection failed in order to check CountSubsEnterTimeWithDays exception
	 * input: day = "2" , month = "2" , dbQueries = ReportsQueries(null)
	 * expected result: null
	 */
	@Test
	public void CountSubsEnterTimeWithDaysFailedSQLTest() {
		dbQueries = new ReportsQueries(null);
		parameters.add("2");
		parameters.add("2");
		ArrayList<VisitReport> actual = dbQueries.CountSubsEnterTimeWithDays(parameters);
		assertEquals(null, actual);
	}

	/**
	 * This test checks the count of group in specific day order to check CountGroupsEnterTimeWithDays query
	 * input: day = "6" , month = "1"
	 * expected result: 11
	 */
	@Test
	public void CountGroupsEnterTimeWithDaysTest() {
		parameters.add("1");
		parameters.add("6");
		ArrayList<VisitReport> actual = dbQueries.CountGroupsEnterTimeWithDays(parameters);
		int expected = 11;
		assertEquals(expected, actual.size());
	}

	/**
	 * This test checks the count of group in empty specific day order to check CountGroupsEnterTimeWithDays query
	 * input: day = "5" , month = "1"
	 * expected result: 0
	 */
	@Test
	public void CountGroupsEnterTimeWithDaysEmptyDayTest() {
		parameters.add("1");
		parameters.add("5");
		ArrayList<VisitReport> actual = dbQueries.CountGroupsEnterTimeWithDays(parameters);
		int expected = 0;
		assertEquals(expected, actual.size());
	}

	/**
	 * This test checks the count of group in specific day when connection failed in order to check CountGroupsEnterTimeWithDays exception
	 * input: day = "2" , month = "2" , dbQueries = ReportsQueries(null)
	 * expected result: null
	 */
	@Test
	public void CountGroupsEnterTimeWithDaysFailedSQLTest() {
		dbQueries = new ReportsQueries(null);
		parameters.add("2");
		parameters.add("2");
		ArrayList<VisitReport> actual = dbQueries.CountGroupsEnterTimeWithDays(parameters);
		assertEquals(null, actual);
	}

}