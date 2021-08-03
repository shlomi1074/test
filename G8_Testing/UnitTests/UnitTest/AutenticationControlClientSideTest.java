package UnitTest;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import Controllers.AutenticationControl;
import Controllers.IAutenticationManager;
import Controllers.IDataBaseManager;
import logic.Subscriber;

public class AutenticationControlClientSideTest {

	/**
	 * This is a stub class.
	 * With this class we bypass all the dependencies which are related to authentication.
	 */
	public class stubAutenticationManager implements IAutenticationManager {

		@Override
		public boolean isConnected(String id) {
			return connectedCondition;
		}

		@Override
		public boolean isTravelerExist(String id) {
			return travelerExisitCondition;
		}

		@Override
		public void insertTologgedinTable(String id) {
			System.out.println(id + " is updated in database");
		}

	}

	/**
	 * This is a stub class.
	 * With this class we bypass all the dependencies which are related to the Database.
	 */
	public class stubDataBaseManager implements IDataBaseManager {

		@Override
		public Subscriber getSubBySubId(String subId) {
			return sub;
		}

		@Override
		public boolean isMemberExist(String id, String pass) {
			return memberExistCondition;
		}

	}

	public AutenticationControl ac;
	public IAutenticationManager acm;
	public IDataBaseManager dbc;

	public static Subscriber sub;

	/* Conditions variables */
	public static boolean connectedCondition;
	public static boolean travelerExisitCondition;
	public static boolean memberExistCondition;

	@Before
	public void setUp() throws Exception {
		connectedCondition = false;
		travelerExisitCondition = false;
		memberExistCondition = false;

		sub = new Subscriber(0, "308438084", "Shlomi", "Amar", "s@gmail.com", "0544411005", null, "Solo", 1);

		/* Constructor injection */
		acm = new stubAutenticationManager();
		dbc = new stubDataBaseManager();
		ac = new AutenticationControl(acm, dbc);
	}

	/**
	 * Failed traveler login
	 * This test checks loginById function when the user is already connected.
	 * 
	 * input: connectedCondition = true, id = "205843899"
	 * expected result: 1
	 */
	@Test
	public void failedLoginByIdTravelerAlreadyConnectedTest() {
		AutenticationControlClientSideTest.connectedCondition = true;
		int actualValue = AutenticationControl.loginById("205843899");
		int expetedValue = 1;
		assertEquals(expetedValue, actualValue);
	}

	/**
	 * Failed traveler login
	 * This test checks loginById function when the user does not exist.
	 * 
	 * input: connectedCondition = false, travelerExisitCondition = false, id = "308438084"
	 * expected result: 2
	 */
	@Test
	public void failedLoginByIdTravelerDoesNotExistTest() {
		AutenticationControlClientSideTest.connectedCondition = false;
		AutenticationControlClientSideTest.travelerExisitCondition = false;
		int actualValue = AutenticationControl.loginById("308438084");
		int expetedValue = 2;
		assertEquals(expetedValue, actualValue);
	}

	/**
	 * Success traveler login
	 * This test checks loginById function when the user exist and not logged in.
	 * 
	 * input: connectedCondition = false, travelerExisitCondition = true, id = "205843899"
	 * expected result: 0
	 */
	@Test
	public void successLoginByIdTravelerExistTest() {
		AutenticationControlClientSideTest.connectedCondition = false;
		AutenticationControlClientSideTest.travelerExisitCondition = true;
		int actualValue = AutenticationControl.loginById("205843899");
		int expetedValue = 0;
		assertEquals(expetedValue, actualValue);
	}

	/**
	 * Failed subscriber login
	 * This test checks loginBySubId function when the user does not exist.
	 * 
	 * input: sub = null, id = "8383838333323"
	 * expected result: 2
	 */
	@Test
	public void failedLoginBySubIdTravelerDoesNotExistTest() {
		AutenticationControlClientSideTest.sub = null;
		int actualValue = AutenticationControl.loginBySubId("8383838333323");
		int expetedValue = 2;
		assertEquals(expetedValue, actualValue);
	}

	/**
	 * Failed subscriber login
	 * This test checks loginBySubId function when the user already connected.
	 * 
	 * input: connectedCondition = true, id = "308438084"
	 * sub = (0, "308438084", "Shlomi", "Amar", "s@gmail.com","0544411005", null, "Solo", 1);
	 * 
	 * expected result: 1
	 */
	@Test
	public void failedLoginBySubIdTravelerExistButConnectedTest() {
		AutenticationControlClientSideTest.connectedCondition = true;
		AutenticationControlClientSideTest.sub = new Subscriber(0, "308438084", "Shlomi", "Amar", "s@gmail.com",
				"0544411005", null, "Solo", 1);
		int actualValue = AutenticationControl.loginBySubId("308438084");
		int expetedValue = 1;
		assertEquals(expetedValue, actualValue);
	}

	/**
	 * Success subscriber login
	 * This test checks loginBySubId function when the user exist and not logged in.
	 * 
	 * input: connectedCondition = false, id = "308438084"
	 * sub = (0, "308438084", "Shlomi", "Amar", "s@gmail.com","0544411005", null, "Solo", 1);
	 * 
	 * expected result: 0
	 */
	@Test
	public void successLoginBySubIdTravelerExistTest() {
		AutenticationControlClientSideTest.connectedCondition = false;
		AutenticationControlClientSideTest.sub = new Subscriber(0, "308438084", "Shlomi", "Amar", "s@gmail.com",
				"0544411005", null, "Solo", 1);
		int actualValue = AutenticationControl.loginBySubId("308438084");
		int expetedValue = 0;
		assertEquals(expetedValue, actualValue);
	}

	/**
	 * Failed worker login
	 * This test checks memberLoginHandler function when the worker is already connected.
	 * 
	 * input: connectedCondition = true, memberExistCondition = true, id = "308438084"
	 * 
	 * expected result: 1
	 */
	@Test
	public void failedWorkerLoginWorkerConnectedTest() {
		AutenticationControlClientSideTest.connectedCondition = true;
		AutenticationControlClientSideTest.memberExistCondition = true;
		int actualValue = AutenticationControl.memberLoginHandler("308438084", "123");
		int expetedValue = 1;
		assertEquals(expetedValue, actualValue);
	}

	/**
	 * Failed worker login
	 * This test checks memberLoginHandler function when the worker does not exist.
	 * 
	 * input: connectedCondition = false, memberExistCondition = false, id = "308438084"
	 * 
	 * expected result: 2
	 */
	@Test
	public void failedWorkerLoginWorkerDoesNotExistTest() {
		AutenticationControlClientSideTest.connectedCondition = false;
		AutenticationControlClientSideTest.memberExistCondition = false;
		int actualValue = AutenticationControl.memberLoginHandler("308438084", "123");
		int expetedValue = 2;
		assertEquals(expetedValue, actualValue);
	}

	/**
	 * Success worker login
	 * This test checks memberLoginHandler function when the worker exist and not logged in.
	 * 
	 * input: connectedCondition = false, memberExistCondition = true, id = "308438084"
	 * 
	 * expected result: 0
	 */
	@Test
	public void successWorkerLoginWorkerExistTest() {
		AutenticationControlClientSideTest.connectedCondition = false;
		AutenticationControlClientSideTest.memberExistCondition = true;

		int actualValue = AutenticationControl.memberLoginHandler("308438084", "123");
		int expetedValue = 0;
		assertEquals(expetedValue, actualValue);
	}

}
