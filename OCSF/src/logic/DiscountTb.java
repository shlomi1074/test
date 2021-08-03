package logic;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * DiscountTb represent a discount in the park.
 * This class suited for table view
 */
public class DiscountTb {
	private SimpleStringProperty discountId;
	private SimpleDoubleProperty amount;
	private SimpleStringProperty startDate;
	private SimpleStringProperty endDate;
	private SimpleIntegerProperty parkId;
	private SimpleStringProperty status;

	public DiscountTb(String discountId, Double amount, String startDate,
			String endDate, int parkId, String status) {
		this.discountId = new SimpleStringProperty(discountId);
		this.amount = new SimpleDoubleProperty(amount);
		this.startDate = new SimpleStringProperty(startDate);
		this.endDate = new SimpleStringProperty(endDate);
		this.parkId = new SimpleIntegerProperty(parkId);
		this.status = new SimpleStringProperty(status);
	}

	public String getDiscountId() {
		return discountId.get();
	}

	public void setDiscountId(SimpleStringProperty discountId) {
		this.discountId = discountId;
	}

	public String getStatus() {
		return status.get();
	}

	public void setStatus(SimpleStringProperty status) {
		this.status = status;
	}

	public double getAmount() {
		return amount.get();
	}

	public void setAmount(SimpleDoubleProperty amount) {
		this.amount = amount;
	}

	public String getStartDate() {
		return startDate.get();
	}

	public void setStartDate(SimpleStringProperty startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate.get();
	}

	public void setEndDate(SimpleStringProperty endDate) {
		this.endDate = endDate;
	}

	public int getParkId() {
		return parkId.get();
	}

	public void setParkId(SimpleIntegerProperty parkId) {
		this.parkId = parkId;
	}

}
