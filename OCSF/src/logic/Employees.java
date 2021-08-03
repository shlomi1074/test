package logic;

import java.io.Serializable;

/**
 * This class represents employee in the park
 */
@SuppressWarnings("serial")
public class Employees implements Serializable{
	private int employeeId;
	private WorkerType role;
	private int parkId;
	private String firstName;
	private String lastName;
	private String email;

	public Employees(int employeeId, WorkerType role, int parkId, String firstName, String lastName, String email) {
		this.employeeId = employeeId;
		this.role = role;
		this.parkId = parkId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
	}

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

	public WorkerType getRole() {
		return role;
	}

	public void setRole(WorkerType role) {
		this.role = role;
	}

	public int getParkId() {
		return parkId;
	}

	public void setParkId(int parkId) {
		this.parkId = parkId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
