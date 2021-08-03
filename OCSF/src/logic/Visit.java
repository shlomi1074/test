package logic;

/**
 * this class represent a visit in the park
 */

public class Visit {
	private String visitId;
	private String travelerId;
	private int parkId;
	private String entrenceTime;
	private String exitTime;
	private String visitDate;

	public Visit(String visitId, String travelerId, int parkId, String entrenceTime, String exitTime,
			String visitDate) {
		this.visitId = visitId;
		this.travelerId = travelerId;
		this.parkId = parkId;
		this.entrenceTime = entrenceTime;
		this.exitTime = exitTime;
		this.visitDate = visitDate;
	}

	public String getVisitId() {
		return visitId;
	}

	public void setVisitId(String visitId) {
		this.visitId = visitId;
	}

	public String getTravelerId() {
		return travelerId;
	}

	public void setTravelerId(String travelerId) {
		this.travelerId = travelerId;
	}

	public int getParkId() {
		return parkId;
	}

	public void setParkId(int parkId) {
		this.parkId = parkId;
	}

	public String getEntrenceTime() {
		return entrenceTime;
	}

	public void setEntrenceTime(String entrenceTime) {
		this.entrenceTime = entrenceTime;
	}

	public String getExitTime() {
		return exitTime;
	}

	public void setExitTime(String exitTime) {
		this.exitTime = exitTime;
	}

	public String getVisitDate() {
		return visitDate;
	}

	public void setVisitDate(String visitDate) {
		this.visitDate = visitDate;
	}

}
