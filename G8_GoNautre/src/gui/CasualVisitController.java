package gui;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.ResourceBundle;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import Controllers.OrderControl;
import Controllers.ParkControl;
import Controllers.TravelerControl;
import Controllers.calculatePrice.CheckOut;
import Controllers.calculatePrice.GroupCasualCheckOut;
import Controllers.calculatePrice.RegularCheckOut;
import Controllers.calculatePrice.SubscriberPayAtParkCheckOut;
import alerts.CustomAlerts;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import logic.GoNatureFinals;
import logic.Order;
import logic.OrderStatusName;
import logic.OrderTb;
import logic.OrderType;
import logic.Park;
import logic.Subscriber;
import util.UtilityFunctions;

/**
 * This Class is the GUI controller of CasualTravelerVisit.fxml
 * It handles all the JavaFx nodes events.
 * 
 * In this screen the entrance worker makes a casual visit.
 *
 */
public class CasualVisitController implements Initializable {

	@FXML
	private JFXTextField idInputCasualVisit;

	@FXML
	private JFXTextField emailInputCasualVisit;

	@FXML
	private JFXComboBox<OrderType> typeComboBox;

	@FXML
	private JFXTextField numOfVisitorsCasualVisit;

	@FXML
	private Label headerLabel;

	@FXML
	private JFXButton placeOrderBtn;

	@FXML
	private Label totalPriceLabel;

	@FXML
	private JFXButton checkPriceBtn;

	@FXML
	private Label permissionLabel;

	private Subscriber subscriber;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initComboBoxOrderType();
		initListeners();

		// In order to check price - all info has to be valid
		checkPriceBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (validInfo())
					checkPricebtnAction();
			}
		});

		// In order to place order - info and price has to be valid.
		placeOrderBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (validInfo() && validPrice())
					placeOrderAction();
			}
		});
	}


	private void initComboBoxOrderType() {

		typeComboBox.getItems().clear();
		typeComboBox.getItems().addAll(Arrays.asList(OrderType.values()));
		if (!permissionLabel.getText().equals("Family")) {
			typeComboBox.getItems().remove(1);
		}

		/* Listener to order type ComboBox. activate on every item selected */
		typeComboBox.valueProperty().addListener((obs, oldItem, newItem) -> {
			if (newItem == null) {
			} else {
				if (newItem.toString().equals("Solo Visit")) {
					numOfVisitorsCasualVisit.setText("1");
					numOfVisitorsCasualVisit.setEditable(false);

				} else {
					numOfVisitorsCasualVisit.setText("");
					numOfVisitorsCasualVisit.setPromptText("Visitor's Number");
					numOfVisitorsCasualVisit.setEditable(true);
				}
			}
		});
	}

	/*
	 * This function init the listeners for permission label.
	 */
	private void initListeners() {
		idInputCasualVisit.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				if (arg2.length() == 9) {
					subscriber = TravelerControl.getSubscriber(arg2);
					if (subscriber == null)
						permissionLabel.setText("Guest");
					else
						permissionLabel.setText(subscriber.getSubscriberType());
				} else {
					permissionLabel.setText("Guest");
				}
				initComboBoxOrderType();
			}
		});
	}

	/*
	 * This function return the current OrderType in a string format
	 * if the user did not choose, it returns null.
	 */
	private String currentOrderType() {
		if (typeComboBox.getValue() == null)
			return "null";
		return typeComboBox.getValue().toString();
	}

	/*
	 * This function return if order details are valid, otherwise it shows a pop up screen with relevent message.
	 */
	private boolean validInfo() {

		// Recive the data from the text fields.
		String idOfTraveler = idInputCasualVisit.getText();
		String orderType = currentOrderType();
		String email = emailInputCasualVisit.getText();
		String numOfCasualVisitors = numOfVisitorsCasualVisit.getText();

		// Check if one of the text fields is empty.
		if (idOfTraveler.isEmpty() || orderType.equals("null") || email.isEmpty() || numOfCasualVisitors.isEmpty()) {
			popNotification(AlertType.ERROR, "Input Error", "Please fill all of the fields correctly ");
			return false;
		}

		// Input validation for id and email.
		if (!UtilityFunctions.isNumeric(idOfTraveler) || idOfTraveler.length() != 9
				|| !UtilityFunctions.isValidEmailAddress(email)) {
			popNotification(AlertType.ERROR, "Input Error", "Please fill the form correctly");
			return false;
		}

		// Recive the data after it has been validated, in order to avoid exceptions.
		int numOfVisitors = Integer.parseInt(numOfCasualVisitors);
		Subscriber sub = TravelerControl.getSubscriber(idOfTraveler);

		// Input validation for number of visitors.
		if (numOfVisitors > 15 || numOfVisitors <= 0) {
			popNotification(AlertType.ERROR, "Input Error",
					"Please fill the form correctly and then press check price");
			return false;
		}

		// Input validation - group with one participant.
		if (orderType.equals(OrderType.GROUP.toString()) && numOfVisitors < 2) {
			popNotification(AlertType.ERROR, "Input Error", "Can't order for group of 1");
			return false;
		}

		if (sub == null)
			return true;

		// Input validation - Number of visitors cant be larger than number of participants in family subscription
		if (orderType.equals(OrderType.FAMILY.toString()) && numOfVisitors > sub.getNumberOfParticipants()) {
			popNotification(AlertType.ERROR, "Input Error", "Check number of participants");
			return false;
		}

		// Input validation - Number of visitors cant be larger than number of participants in subsriber subscription
		if (orderType.equals(OrderType.GROUP.toString()) && sub.getSubscriberType().equals("Guide")
				&& numOfVisitors > sub.getNumberOfParticipants()) {
			popNotification(AlertType.ERROR, "Input Error", "Check number of participants");
			return false;
		}
		return true;
	}

	/*
	 * This function calculate the order price.
	 */
	private double calculatePriceForVisit() {
		double price = 0;
		// Recive the data from the text fields.
		String idOfTraveler = idInputCasualVisit.getText();
		int numberOfVisitors = Integer.parseInt(numOfVisitorsCasualVisit.getText());
		String orderType = currentOrderType();

		// Setting up vars
		Subscriber sub = null;
		boolean existTraveler = TravelerControl.isTravelerExist(idOfTraveler);
		LocalDate today = LocalDate.now();
		int parkId = MemberLoginController.member.getParkId();

		// Setting up price class.
		CheckOut chk = new RegularCheckOut(numberOfVisitors, parkId, today.toString());

		// Order for group has no discount.
		if (orderType.equals(OrderType.GROUP.toString())) {
			price = (new GroupCasualCheckOut(chk)).getPrice();
			return price;
		}
		// If the traveler is subscriber.
		if (existTraveler)
			sub = TravelerControl.getSubscriber(idOfTraveler);
		if (sub != null)
			price = (new SubscriberPayAtParkCheckOut(chk)).getPrice();
		else
			price = chk.getPrice();
		return price;
	}

	/*
	 * This function set the label for price label.
	 */
	private void checkPricebtnAction() {
		double price = calculatePriceForVisit();
		totalPriceLabel.setText(String.valueOf(price));
	}

	/*
	 * This function check if the price that appears in the form is valid
	 */
	private boolean validPrice() {
		if (totalPriceLabel.getText().equals("")) {
			popNotification(AlertType.ERROR, "Price error", "Please check the price in order to continue");
			return false;
		}

		double priceInForm = Double.parseDouble(totalPriceLabel.getText());
		double realPrice = calculatePriceForVisit();
		if (priceInForm == realPrice)
			return true;
		popNotification(AlertType.ERROR, "Price error", "Please check the price in order to continue");
		return false;
	}

	/*
	 * This function is a wrapper for alerts.
	 */
	private void popNotification(AlertType type, String header, String content) {
		new CustomAlerts(type, header, header, content).showAndWait();
	}

	/*
	 * This function handle the place order button when pressed.
	 */
	private void placeOrderAction() {

		// Recive the data from the text fields.
		String idOfTraveler = idInputCasualVisit.getText();
		int numberOfVisitors = Integer.parseInt(numOfVisitorsCasualVisit.getText());
		String orderType = currentOrderType();
		String email = emailInputCasualVisit.getText();
		int parkId = MemberLoginController.member.getParkId();

		// Checking if the user is already a subscriber.
		Subscriber sub = TravelerControl.getSubscriber(idOfTraveler);
		if (sub != null)
			email = sub.getEmail();

		// Creating new order with relevent details.
		Order order = new Order(idOfTraveler, parkId, LocalDate.now().toString(), LocalTime.now().toString(), orderType,
				numberOfVisitors, email, Double.parseDouble(totalPriceLabel.getText()),
				OrderStatusName.ENTERED_THE_PARK.toString());

		// Since addVisit Uses orderTb from previous controllers,we need to convert it with builder.
		// Adding casual Order is the same as adding order
		// Adding visit adds the visit to the DB

		OrderTb orderTb = new OrderTb(order);
		if (OrderControl.addCasualOrder(order)) {
			OrderControl.addVisit(orderTb);

			// Updated number = the number of visitors after the entrance of the casual visit.
			int updateNumber = ParkControl.getParkById(String.valueOf(parkId)).getCurrentVisitors() + numberOfVisitors;

			// Updating the number of visitors in the park
			ParkControl.updateCurrentVisitors(parkId, updateNumber);
			Park park = ParkControl.getParkById(String.valueOf(MemberLoginController.member.getParkId()));

			ParkControl.updateIfParkFull(park);

			// Notifying the visit is approved.

			// Need to get orderId from DB
			order = OrderControl.getTravelerRecentOrder(idOfTraveler);

			// Closing the scene and updating the table for entrance worker.
			Stage stage = (Stage) idInputCasualVisit.getScene().getWindow();
			ManageTravelerController manageTravelerController = (ManageTravelerController) stage.getUserData();
			manageTravelerController.loadTableView();
			stage.close();
			
			// Setting the receipt window.
			loadOrderConfirmation(order);
		} else {
			popNotification(AlertType.ERROR, "System Error", "An error has occurred, please try again");
		}
	}
	
	/*
	 * This function handle the receipt.
	 */
	private void loadOrderConfirmation(Order order) {
		try {
			Stage newStage = new Stage();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/CasualVisitReceipt.fxml"));
			CasualVisitReceiptController controller = new CasualVisitReceiptController();
			controller.setOrder(order);

			loader.setController(controller);
			loader.load();
			Parent p = loader.getRoot();

			newStage.setTitle("Order receipt");
			newStage.getIcons().add(new Image(GoNatureFinals.APP_ICON));
			newStage.setScene(new Scene(p));
			newStage.setResizable(false);
			newStage.show();
		} catch (IOException e) {
			System.out.println("faild to load form");
			e.printStackTrace();
		}
	}

}