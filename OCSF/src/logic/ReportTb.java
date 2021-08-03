package logic;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Report class represent a report in the park system.
 * This class suited for table view.
 */
public class ReportTb {
	
	private SimpleIntegerProperty reportID;
	private SimpleStringProperty reportType;
	private SimpleIntegerProperty parkID;
	private SimpleIntegerProperty month;
	private SimpleStringProperty comment;
	
	public ReportTb(int reportID, String reportType, int parkID, int month, String comment) {
		
		this.reportID = new SimpleIntegerProperty(reportID);
		this.reportType = new SimpleStringProperty(reportType);
		this.parkID = new SimpleIntegerProperty(parkID);
		this.month = new SimpleIntegerProperty(month);
		this.comment = new SimpleStringProperty(comment);
		
	}
	
	/*Lior*/
	public ReportTb(Report report) {
		this.reportID = new SimpleIntegerProperty(report.getReportID());
		this.reportType = new SimpleStringProperty(report.getReportType());
		this.parkID = new SimpleIntegerProperty(report.getParkID());
		this.month = new SimpleIntegerProperty(report.getMonth());
		this.comment = new SimpleStringProperty(report.getComment());
	}
	
	public int getReportID() {
		return reportID.get();
	}

	public void setReportID(SimpleIntegerProperty reportID) {
		this.reportID = reportID;
	}
	
	public String getReportType() {
		return reportType.get();
	}

	public void setReportType(SimpleStringProperty reportType) {
		this.reportType = reportType;
	}

	public int getParkID() {
		return parkID.get();
	}

	public void setParkID(SimpleIntegerProperty parkID) {
		this.parkID = parkID;
	}

	public int getMonth() {
		return month.get();
	}

	public void setMonth(SimpleIntegerProperty month) {
		this.month = month;
	}

	public String getComment() {
		return comment.get();
	}

	public void setComment(SimpleStringProperty comment) {
		this.comment = comment;
	}
}
