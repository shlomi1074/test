package gui;

import java.net.URL;
import java.util.ResourceBundle;
import com.jfoenix.controls.JFXButton;
import Controllers.ParkControl;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import logic.Order;

/**
 * This Class is the GUI controller of CasualVisitReceipt.fxml
 * It handles all the JavaFx nodes events.
 * 
 * This is the visit receipt for casual visits
 *
 */
public class CasualVisitReceiptController implements Initializable {

	@FXML
	private Label summaryPark;

	@FXML
	private Label summaryDate;

	@FXML
	private Label summaryTime;

	@FXML
	private Label summaryType;

	@FXML
	private Label summaryVisitors;

	@FXML
	private Label totalPriceLabel;

	@FXML
	private JFXButton finishBtn;

	private Order order;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		setOrderInfo();
	}

	private void setOrderInfo() {
		if (order != null) {
			summaryPark.setText(ParkControl.getParkName(order.getParkId() + ""));
			summaryDate.setText(order.getOrderDate());
			summaryTime.setText(order.getOrderTime());
			summaryType.setText(order.getOrderType());
			summaryVisitors.setText(order.getNumberOfParticipants() + "");
			totalPriceLabel.setText(order.getPrice() + "₪");
		}
	}

	/* On OK button click */
	@FXML
	private void closeStage() {
		getStage().close();
	}

	/**
	 * Getter for the current Stage
	 * 
	 * @return Current stage
	 */
	private Stage getStage() {
		return (Stage) totalPriceLabel.getScene().getWindow();
	}

	/**
	 * Setter for the class variable order
	 * 
	 * @param recentOrder The last order the traveler did
	 */
	public void setOrder(Order recentOrder) {
		this.order = recentOrder;
	}
}