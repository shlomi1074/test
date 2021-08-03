package logic;

import java.io.Serializable;

/**
 * This class represents a request for change in park parameter
 */
@SuppressWarnings("serial")
public class Request implements Serializable { 
	private int requestId;
	private String changeName;
	private String newValue;
	private String oldValue;
	private String requestDate;
	private int parkId;
	private String requestStatus;
	
	public Request(int requestId, String changeName, String newValue, String oldValue, String requestDate, // requestId- Stinrg to int -- ofir n
			int parkId, String requestStatus) {
		this.requestId = requestId;
		this.changeName = changeName;
		this.newValue = newValue;
		this.oldValue = oldValue;
		this.requestDate = requestDate;
		this.parkId = parkId;
		this.requestStatus = requestStatus;
	}

	public int getRequestId() {
		return requestId;
	}

	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}

	public String getChangeName() {
		return changeName;
	}

	public void setChangeName(String changeName) {
		this.changeName = changeName;
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	public String getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(String requestDate) {
		this.requestDate = requestDate;
	}

	public int getParkId() {
		return parkId;
	}

	public void setParkId(int parkId) {
		this.parkId = parkId;
	}

	public String getRequestStatus() {
		return requestStatus;
	}

	public void setRequestStatus(String requestStatus) {
		this.requestStatus = requestStatus;
	}
	
	
}