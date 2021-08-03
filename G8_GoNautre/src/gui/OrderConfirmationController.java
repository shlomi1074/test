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
import logic.Traveler;

/**
 * This Class is the GUI controller of OrderConfirmation.fxml
 * It handles all the JavaFx nodes events.
 * 
 * This is the order confirmation after the order process ends
 *
 */
public class OrderConfirmationController implements Initializable {

    @FXML
    private Label headerLabel;

    @FXML
    private Label summaryID;

    @FXML
    private Label summaryEmail;

    @FXML
    private Label summaryFullName;

    @FXML
    private Label summaryPhone;

    @FXML
    private Label summaryPark;

    @FXML
    private Label summaryDate;
    
    @FXML
    private Label smsSimLabel;

    @FXML
    private Label summaryTime;

    @FXML
    private Label summaryType;

    @FXML
    private Label summaryVisitors;

    @FXML
    private Label summaryPayment;

    @FXML
    private Label totalPriceLabel;

    @FXML
    private JFXButton finishBtn;

    @FXML
    private Label msgLine1;

    @FXML
    private Label msgLine2;

    @FXML
    private Label orderStatusLabel;

	private Order order;
	private Traveler traveler;
	private String paymentMethod;
	private boolean isWaitingList = false;
	private boolean isOrderFromWeb = false;


	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		setOrderInfo();
	}

	public void setOrderInfo() {
		smsSimLabel.setText("");
		if (isOrderFromWeb) {
			smsSimLabel.setText("This message was sent to your Email and to your phone");
		}
		if (isWaitingList) {
			headerLabel.setText("You Are In The Waiting List");
			msgLine1.setText("If someone will cancel their visit we will let you know");
			msgLine2.setText("You will have 1 hour to confirm your order");
		}
		if (order != null && traveler != null) {
			summaryID.setText(order.getTravelerId());
			summaryFullName.setText(traveler.getFirstName() + " " + traveler.getLastName());
			summaryPhone.setText(traveler.getPhoneNumber());
			summaryEmail.setText(traveler.getEmail());
			summaryPark.setText(ParkControl.getParkName(order.getParkId() + ""));
			summaryDate.setText(order.getOrderDate());
			summaryTime.setText(order.getOrderTime());
			summaryType.setText(order.getOrderType());
			summaryVisitors.setText(order.getNumberOfParticipants() + "");
			summaryPayment.setText(paymentMethod);
			orderStatusLabel.setText(order.getOrderStatus());
			totalPriceLabel.setText(order.getPrice() + "₪");
		}

	}

	/* On OK button click */
	@FXML
	private void closeStage() {
		getStage().close();
	}

	private Stage getStage() {
		return (Stage) totalPriceLabel.getScene().getWindow();
	}

	/**
	 * Setter for class variable order
	 * 
	 * @param recentOrder The recent order the traveler did
	 */
	public void setOrder(Order recentOrder) {
		this.order = recentOrder;
	}

	/**
	 * Setter for class variable traveler
	 * 
	 * @param traveler The traveler who did thye order
	 */
	public void setTraveler(Traveler traveler) {
		this.traveler = traveler;
	}

	/**
	 * Setter for class variable paymentMethod
	 * 
	 * @param paymentMethod The payment method
	 */
	public void setSummaryPayment(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	
	/**
	 * Setter for class variable isWaitingList
	 * 
	 * @param isWaitingList true if this is an order confirmation for waiting list
	 */
	public void setWaitingList(boolean isWaitingList) {
		this.isWaitingList = isWaitingList;
	}
	
	/**
	 * Setter for class variable isOrderFromWeb
 
	 * @param isOrderFromWeb true if order confirmation to order from the web
	 */
	public void setOrderFromWeb(boolean isOrderFromWeb) {
		this.isOrderFromWeb = isOrderFromWeb;
	}

}
