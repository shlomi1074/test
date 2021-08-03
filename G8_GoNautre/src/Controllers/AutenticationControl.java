package Controllers;

import java.util.ArrayList;
import java.util.Arrays;
import client.ChatClient;
import client.ClientUI;
import logic.ClientToServerRequest;
import logic.ClientToServerRequest.Request;
import logic.Employees;
import logic.ServerToClientResponse;
import logic.Subscriber;
import logic.Traveler;

/**
 * AutenticationControl class handles all the authentication related functionalities
 */
public class AutenticationControl {

	// Refactor
	public static IAutenticationManager autenticationManager = new AutenticationManager();
	public static IDataBaseManager dataBaseManager = new DataBaseManager();

	public AutenticationControl(IAutenticationManager autenticationManager, IDataBaseManager dataBaseManager) {
		AutenticationControl.autenticationManager = autenticationManager;
		AutenticationControl.dataBaseManager = dataBaseManager;
	}

	/**
	 * This function handle the traveler login by id
	 * 
	 * @param id the traveler's id
	 * @return 0 on success
	 * @return 1 traveler already connected
	 * @return 2 traveler id does not exist
	 */
	public static int loginById(String id) { // Refactor
		if (autenticationManager.isConnected(id))
			return 1;
		else {
			if (autenticationManager.isTravelerExist(id)) {
				autenticationManager.insertTologgedinTable(id);
				return 0;
			}
			return 2;
		}
	}

	/**
	 * This function handle the traveler login by subscriber id
	 * 
	 * @param subID the traveler's subscriber id
	 * @return 0 on success
	 * @return 1 traveler already connected
	 * @return 2 traveler id does not exist
	 */
	public static int loginBySubId(String subID) { // Refactor

		Subscriber sub = dataBaseManager.getSubBySubId(subID);
		if (sub == null)
			return 2;
		else {
			String id = sub.getTravelerId();
			if (autenticationManager.isConnected(id)) {
				return 1;
			} else {
				autenticationManager.insertTologgedinTable(id);

				// ClientUI.chat.accept(request);
				ServerToClientResponse<Subscriber> response = new ServerToClientResponse<Subscriber>();
				response.setResultSet(new ArrayList<Subscriber>(Arrays.asList(sub)));
				ChatClient.responseFromServer = response;
				return 0;
			}
		}
	}

	/**
	 * This function get a traveler's id and ask the server to
	 * insert it to "loggedIn" table.
	 * 
	 * @param id the traveler's id.
	 */
	public static void insertTologgedinTable(String id) {
		ClientToServerRequest<String> request = new ClientToServerRequest<>(Request.INSERT_TO_LOGGEDIN,
				new ArrayList<String>(Arrays.asList(id)));
		ClientUI.chat.accept(request);

	}

	/**
	 * This function gets a user id and check if the user is connected to the system.
	 * 
	 * @param id the user id (traveler of member)
	 * @return true if connected, false otherwise.
	 */
	public static boolean isConnected(String id) {
		ClientToServerRequest<String> request = new ClientToServerRequest<>(Request.IS_CONNECTED,
				new ArrayList<String>(Arrays.asList(id)));
		ClientUI.chat.accept(request);
		return ChatClient.responseFromServer.isResult();
	}

	/**
	 * This function gets an id and checks if there is such traveler with the same id.
	 * 
	 * @param id the id to check
	 * @return true if there is traveler with this id.
	 */
	public static boolean isTravelerExist(String id) {
		ClientToServerRequest<String> request = new ClientToServerRequest<>(Request.TRAVELER_LOGIN_ID,
				new ArrayList<String>(Arrays.asList(id)));
		ClientUI.chat.accept(request);
		Traveler traveler = (Traveler) ChatClient.responseFromServer.getResultSet().get(0);
		if (traveler == null)
			return false;
		return true;

	}

	/**
	 * This function gets an id and checks if there is such member(employee) with the same id
	 * 
	 * @param id   the traveler's id
	 * @param pass the member's password
	 * @return true traveler exists.
	 * @return false traveler not exists.
	 */
	public static boolean isMemberExist(String id, String pass) {
		ClientToServerRequest<String> request = new ClientToServerRequest<>(Request.MEMBER_LOGIN,
				new ArrayList<String>(Arrays.asList(id, pass)));
		ClientUI.chat.accept(request);
		Employees member = (Employees) ChatClient.responseFromServer.getResultSet().get(0);
		if (member == null)
			return false;
		return true;

	}

	/**
	 * This function handle the member(employee) login by subscriber id
	 * 
	 * @param id       The employee's id
	 * @param password The employee's password
	 * 
	 * @return 0 on success
	 * @return 1 member already connected
	 * @return 2 member id does not exist
	 */
	public static int memberLoginHandler(String id, String password) { // Refactor
		boolean connected = autenticationManager.isConnected(id);
		boolean mem_exsit = dataBaseManager.isMemberExist(id, password);
		if (connected && mem_exsit)
			return 1;
		if (!connected && mem_exsit) {
			autenticationManager.insertTologgedinTable(id);
			return 0;
		}
		return 2;

	}

	/**
	 * This function get a user's id and ask the server to
	 * delete it from "loggedIn" table.
	 * 
	 * @param id the user's id.
	 */
	public static void userLogout(String id) {
		ClientToServerRequest<String> request = new ClientToServerRequest<>(Request.LOGOUT,
				new ArrayList<String>(Arrays.asList(id)));
		ClientUI.chat.accept(request);
	}

	// Refactor
	/* Wrapper for authentication methods */
	static class AutenticationManager implements IAutenticationManager {

		@Override
		public boolean isConnected(String id) {
			return AutenticationControl.isConnected(id);
		}

		@Override
		public boolean isTravelerExist(String id) {
			return TravelerControl.isTravelerExist(id);
		}

		@Override
		public void insertTologgedinTable(String id) {
			AutenticationControl.insertTologgedinTable(id);

		}

	}

	/* Wrapper for database methods */
	static class DataBaseManager implements IDataBaseManager {

		@Override
		public Subscriber getSubBySubId(String subId) {
			ClientToServerRequest<String> request = new ClientToServerRequest<>(Request.SUBSCRIBER_LOGIN_SUBID,
					new ArrayList<String>(Arrays.asList(subId)));
			ClientUI.chat.accept(request);
			return (Subscriber) ChatClient.responseFromServer.getResultSet().get(0);
		}

		@Override
		public boolean isMemberExist(String id, String pass) {
			return AutenticationControl.isMemberExist(id, pass);
		}

	}

}
