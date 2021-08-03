package logic;

import java.io.Serializable;

/**
 * Report class represent a report in the park system.
 */
@SuppressWarnings("serial")
public class Report implements Serializable{
	
	private int reportID;
	private String reportType;
	private int parkID;
	private int month;
	private String comment;
	
	public Report(int reportID, String reportType, int parkID, int month, String comment) {
		super();
		this.reportID = reportID;
		this.reportType = reportType;
		this.parkID = parkID;
		this.month = month;
		this.comment = comment;
	}

	public int getReportID() {
		return reportID;
	}

	public void setReportID(int reportID) {
		this.reportID = reportID;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public int getParkID() {
		return parkID;
	}

	public void setParkID(int parkID) {
		this.parkID = parkID;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	

}
