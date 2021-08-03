package UnitTest;

import static org.junit.Assert.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import controllers.sqlHandlers.MysqlConnection;
import controllers.sqlHandlers.TravelersQueries;
import logic.Employees;
import logic.Subscriber;
import logic.Traveler;
import logic.WorkerType;

public class AutenticationControlServerSideTest {

	private static Connection mysqlconnection;
	private TravelersQueries dbQueries;
	private ArrayList<String> parameters;

	@Before
	public void setUp() throws Exception {
		mysqlconnection = MysqlConnection.getInstance().getConnection();
		dbQueries = new TravelersQueries(mysqlconnection);
		parameters = new ArrayList<>();
	}

	/**
	 * This test checks if isTravelerExist returns null if the id does not exist in the database
	 * 
	 * input: parameters = "307383406"
	 * expected result: null
	 */
	@Test
	public void isTravelerExistInputDoesNotExitInDBTest() {
		parameters = new ArrayList<String>(Arrays.asList("307383406"));
		Traveler actual = dbQueries.isTravelerExist(parameters);
		Traveler expected = null;
		assertEquals(expected, actual);
	}

	/**
	 * This test checks if isTravelerExist returns Traveler object if the id exists in the database
	 * 
	 * input: parameters = "555000000"
	 * expected result: Traveler object with "555000000" as id.
	 */
	@Test
	public void isTravelerExistSuccessTest() {
		parameters = new ArrayList<String>(Arrays.asList("555000000"));
		Traveler actual = dbQueries.isTravelerExist(parameters);
		assertEquals("555000000", actual.getTravelerId());
	}

	/**
	 * This test check if isTravelerExist returns null when it failed to execute the SQL query
	 * 
	 * input: parameters = "555000000"
	 * expected result: null.
	 */
	@Test
	public void isTravelerExistFailedSQLTest() {
		dbQueries = new TravelersQueries(null);
		parameters = new ArrayList<String>(Arrays.asList("555000000"));
		Traveler actual = dbQueries.isTravelerExist(parameters);
		assertEquals(null, actual);
	}

	/**
	 * This test checks if getSubscriberBySubId returns null if the subscriber id does not exist in the database
	 * 
	 * input: parameters = "5555"
	 * expected result: null
	 */
	@Test
	public void getSubscriberBySubIdInputDoesNotExitInDBTest() {
		parameters = new ArrayList<String>(Arrays.asList("5555"));
		Subscriber actual = dbQueries.getSubscriberBySubId(parameters);
		Subscriber expected = null;
		assertEquals(expected, actual);
	}

	/**
	 * This test checks if getSubscriberBySubId returns Subscriber object if the subscriber id exists in the database
	 * 
	 * input: parameters = "5556"
	 * expected result: Subscriber object with "5556" as subscriber id.
	 */
	@Test
	public void getSubscriberBySubIdSuccessTest() {
		parameters = new ArrayList<String>(Arrays.asList("5556"));
		Subscriber actualTraveler = dbQueries.getSubscriberBySubId(parameters);
		assertEquals(5556, actualTraveler.getSubscriberNumber());
	}

	/**
	 * This test check if getSubscriberBySubId returns null when it failed to execute the SQL query
	 * 
	 * input: parameters = "5556"
	 * expected result: null.
	 */
	@Test
	public void getSubscriberBySubIdFailedSQLTest() {
		dbQueries = new TravelersQueries(null);
		parameters = new ArrayList<String>(Arrays.asList("5556"));
		Subscriber actual = dbQueries.getSubscriberBySubId(parameters);
		assertEquals(null, actual);
	}

	/**
	 * This test checks if getSubscriberById returns null if the id does not exist in the database
	 * 
	 * input: parameters = "747374632"
	 * expected result: null
	 */
	@Test
	public void getSubscriberByIdInputDoesNotExitInDBTest() {
		parameters = new ArrayList<String>(Arrays.asList("747374632"));
		Subscriber actual = dbQueries.getSubscriberById(parameters);
		Subscriber expected = null;
		assertEquals(expected, actual);
	}

	/**
	 * This test checks if getSubscriberById returns Subscriber object if the id exists in the database
	 * 
	 * input: parameters = "206487274"
	 * expected result: Subscriber object with "206487274" as id.
	 */
	@Test
	public void getSubscriberByIdSuccessTest() {
		parameters = new ArrayList<String>(Arrays.asList("206487274"));
		Subscriber actual = dbQueries.getSubscriberById(parameters);
		assertEquals("206487274", actual.getTravelerId());
	}

	/**
	 * This test check if getSubscriberById returns null when it failed to execute the SQL query
	 * 
	 * input: parameters = "206487274"
	 * expected result: null.
	 */
	@Test
	public void getSubscriberByIdFailedSQLTest() {
		dbQueries = new TravelersQueries(null);
		parameters = new ArrayList<String>(Arrays.asList("206487274"));
		Subscriber actual = dbQueries.getSubscriberById(parameters);
		assertEquals(null, actual);
	}

	/**
	 * This test checks if isMemberExist returns null if the worker's id does not exist in the database
	 * 
	 * input: parameters = "747374632", "123"
	 * expected result: null
	 */
	@Test
	public void isMemberExistInputDoesNotExitInDBTest() {
		parameters = new ArrayList<String>(Arrays.asList("747374632", "123"));
		Employees actual = dbQueries.isMemberExist(parameters);
		Employees expected = null;
		assertEquals(expected, actual);
	}

	/**
	 * This test checks if isMemberExist returns Employees object if the worker's id exists in the database.
	 * 
	 * input: parameters = "123456789", "123"
	 * expected result: Employees object with "123456789" as id and WorkerType.DEPARTMENT_MANAGER as role type
	 */
	@Test
	public void isMemberExistSuccessTest() {
		parameters = new ArrayList<String>(Arrays.asList("123456789", "123"));
		Employees actual = dbQueries.isMemberExist(parameters);
		assertEquals(123456789, actual.getEmployeeId());
		assertEquals(WorkerType.DEPARTMENT_MANAGER, actual.getRole());
	}

	/**
	 * This test check if isMemberExist returns null when it failed to execute the SQL query
	 * 
	 * input: parameters = "123456789", "123"
	 * expected result: null.
	 */
	@Test
	public void isMemberExistFailedSQLTest() {
		dbQueries = new TravelersQueries(null);
		parameters = new ArrayList<String>(Arrays.asList("123456789", "123"));
		Employees actual = dbQueries.isMemberExist(parameters);
		assertEquals(null, actual);
	}

	/**
	 * This test checks id checkIfConnected returns false when the given id is not connected to the system.
	 * 
	 * input: parameters = "123456789"
	 * expected result: false.
	 */
	@Test
	public void checkIfConnectedIdNotConnectedTest() {
		parameters = new ArrayList<String>(Arrays.asList("123456789"));
		boolean actual = dbQueries.checkIfConnected(parameters);
		boolean expected = false;
		assertEquals(expected, actual);
	}

	
	/**
	 * This test checks id checkIfConnected returns true when the given id is connected to the system.
	 * 
	 * input: parameters = "123456789"
	 * expected result: true.
	 */
	@Test
	public void checkIfConnectedIdAlreadyConnectedTest() {
		parameters = new ArrayList<String>(Arrays.asList("123456789"));
		dbQueries.insertToLoggedInTable(parameters);

		parameters = new ArrayList<String>(Arrays.asList("123456789"));
		boolean actual = dbQueries.checkIfConnected(parameters);
		boolean expected = true;

		parameters = new ArrayList<String>(Arrays.asList("123456789"));
		dbQueries.removeFromLoggedInTable(parameters);

		assertEquals(expected, actual);

	}

	/**
	 * This test check if checkIfConnected returns null when it failed to execute the SQL query
	 * 
	 * input: parameters = "123456789", "123"
	 * expected result: null.
	 */
	@Test
	public void checkIfConnectedFailedSQLTest() {
		dbQueries = new TravelersQueries(null);
		parameters = new ArrayList<String>(Arrays.asList("123456789", "123"));
		Boolean actual = dbQueries.checkIfConnected(parameters);
		assertEquals(null, actual);
	}

}
