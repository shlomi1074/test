package Controllers;

import java.util.ArrayList;
import java.util.Arrays;
import client.ChatClient;
import client.ClientUI;
import logic.ClientToServerRequest;
import logic.Employees;
import logic.ClientToServerRequest.Request;

/**
 * WorkerControl class handles all the worker related functionalities
 */
@SuppressWarnings("unchecked")
public class WorkerControl {

	/**
	 * This function get an employee's id and returns Employee object,
	 * or null if there is no Employee with the given id.
	 * 
	 * @param id The employee's id.
	 * @return Employee object or null.
	 */
	public static Employees getEmployeeByID(String id) {

		ClientToServerRequest<String> request = new ClientToServerRequest<>(Request.GET_EMPLOYEE,
				new ArrayList<String>(Arrays.asList(id)));
		ClientUI.chat.accept(request);
		Employees employee = (Employees) ChatClient.responseFromServer.getResultSet().get(0);
		return employee;

	}

	/**
	 * This function gets an id of employee and return his email and password in array list.
	 * 
	 * @param id The employee's id
	 * @return ArrayList. at index 0 the employee's email, at index 1 the employee's password.
	 */
	public static ArrayList<String> getEmployeeEmailAndPassword(String id) {

		ClientToServerRequest<String> request = new ClientToServerRequest<>(Request.GET_EMPLOYEE_PASSWORD,
				new ArrayList<String>(Arrays.asList(id)));
		ClientUI.chat.accept(request);
		ArrayList<String> employeeInfo = ChatClient.responseFromServer.getResultSet();
		return employeeInfo;

	}
}
