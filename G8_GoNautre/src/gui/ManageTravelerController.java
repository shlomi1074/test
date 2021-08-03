package gui;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.ResourceBundle;
import com.jfoenix.controls.JFXTextField;
import Controllers.OrderControl;
import Controllers.ParkControl;
import Controllers.TravelerControl;
import Controllers.calculatePrice.CheckOut;
import Controllers.calculatePrice.GuidePrePayCheckOut;
import Controllers.calculatePrice.RegularCheckOut;
import Controllers.calculatePrice.RegularPreOrderCheckOut;
import Controllers.calculatePrice.SubscriberPreOrderCheckOut;
import alerts.CustomAlerts;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;
import logic.GoNatureFinals;
import logic.Order;
import logic.OrderStatusName;
import logic.OrderTb;
import logic.OrderType;
import logic.Park;
import logic.Subscriber;
import logic.Traveler;

/**
 * This Class is the GUI controller of ManageTraveler.fxml
 * It handles all the JavaFx nodes events.
 * 
 * In this screen the entrance worker makes can confirm a visit
 * or open a window to create a new casual visit.
 *
 */
public class ManageTravelerController implements Initializable {

	ObservableList<OrderTb> ov = FXCollections.observableArrayList();

	@FXML
	private JFXTextField idTextField;

	@FXML
	private TableView<OrderTb> ordersTableView;

	@FXML
	private TableColumn<OrderTb, String> travelerIDCol;

	@FXML
	private TableColumn<OrderTb, Integer> orderIDCol;

	@FXML
	private TableColumn<OrderTb, String> dateCol;

	@FXML
	private TableColumn<OrderTb, String> timeCol;

	@FXML
	private TableColumn<OrderTb, String> statusCol;

	@FXML
	private Button occVisitBtn;

	@FXML
	private Button confirmBtn;

	@FXML
	private Button searchBtn;

	@FXML
	private Label headerLabel;

	@FXML
	private Label orderIdTxt;

	@FXML
	private JFXTextField visitorsTextField;

	private OrderTb clickedRow;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		loadTableView();
		confirmBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				confirmButton();
				clickedRow = null;
				clearLabels();
			}
		});
		searchBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				searchTraveler();
				clickedRow = null;
				clearLabels();
			}
		});

	}

	/*
	 * This function handle the load casual button when pressed.
	 */
	@FXML
	private void loadCasualVisit() {
		try {
			// Setting the next stage.
			Stage thisStage = getStage();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("CasualTravelerVisit.fxml"));
			CasualVisitController controller = new CasualVisitController();
			loader.setController(controller);
			loader.load();
			Parent p = loader.getRoot();
			Stage newStage = new Stage();

			/* Block parent stage until child stage closes */
			newStage.initModality(Modality.WINDOW_MODAL);
			newStage.initOwner(thisStage);
			newStage.getIcons().add(new Image(GoNatureFinals.APP_ICON));
			newStage.setTitle("Casual Visit");
			newStage.setScene(new Scene(p));
			newStage.setResizable(false);

			// Set the current controller to the next stage.
			newStage.setUserData(this);

			newStage.show();

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("faild to load form");
		}
	}

	/*
	 * This function return the stage of casual visit.
	 */
	private Stage getStage() {
		return (Stage) occVisitBtn.getScene().getWindow();
	}

	/**
	 * This function load the data from the DB to the Table view.
	 */
	@FXML
	public void loadTableView() {
		ArrayList<Order> ordersArrayList = OrderControl.getAllOrdersForParkId(MemberLoginController.member.getParkId());
		ArrayList<OrderTb> tbOrdersArrayList = OrderControl.convertOrderToOrderTb(ordersArrayList);
		init(tbOrdersArrayList);
		ordersTableView.setItems(getOrders(tbOrdersArrayList));
	}

	/*
	 * This function init the table view.
	 */
	private void init(ArrayList<OrderTb> orders) {

		travelerIDCol.setCellValueFactory(new PropertyValueFactory<OrderTb, String>("travelerId"));
		orderIDCol.setCellValueFactory(new PropertyValueFactory<OrderTb, Integer>("orderId"));
		dateCol.setCellValueFactory(new PropertyValueFactory<OrderTb, String>("orderDate"));
		timeCol.setCellValueFactory(new PropertyValueFactory<OrderTb, String>("orderTime"));
		statusCol.setCellValueFactory(new PropertyValueFactory<OrderTb, String>("orderStatus"));

		ordersTableView.setRowFactory(tv -> {
			TableRow<OrderTb> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
					clickedRow = row.getItem();
					orderIdTxt.setText(String.valueOf(clickedRow.getOrderId()));
					visitorsTextField.setText(String.valueOf(clickedRow.getNumberOfParticipants()));
				}
			});
			return row;
		});

	}

	/*
	 * This function init the orders ObservableList
	 */
	private ObservableList<OrderTb> getOrders(ArrayList<OrderTb> orderArray) {
		ordersTableView.getItems().clear();
		for (OrderTb order : orderArray) {
			ov.add(order);
		}
		return ov;
	}

	/*
	 * This function clears the labels
	 */
	private void clearLabels() {
		orderIdTxt.setText("");
		visitorsTextField.setText("");
	}

	/*
	 * This function handle the confirm button when pressed.
	 */
	private void confirmButton() {
		// Did the user choose order
		if (clickedRow == null) {
			new CustomAlerts(AlertType.ERROR, "Input Error", "Input Error", "Please choose order to confirm")
					.showAndWait();
			return;
		}

		// Can traveler enter the park?
		if (!canTravelerEnter()) {
			new CustomAlerts(AlertType.ERROR, "Input Error", "Order Error",
					"Make sure the order is in the right time/date and that the order is confirmed").showAndWait();
			return;
		}

		// Calculate how many people can enter
		double price = 0;
		int numberOfParticipantsInOriginalOrder = clickedRow.getNumberOfParticipants();
		int numberOfParticipantsInCurrentOrder = Integer.parseInt(visitorsTextField.getText());
		if (numberOfParticipantsInOriginalOrder < numberOfParticipantsInCurrentOrder) {
			new CustomAlerts(AlertType.ERROR, "Input Error", "Order Error",
					"You can't list more people than the order mentioned").showAndWait();
			return;
		}
		// at least one person has to enter.
		if (numberOfParticipantsInCurrentOrder <= 0) {
			new CustomAlerts(AlertType.ERROR, "Input Error", "Order Error", "Order must have more than 0 participants")
					.showAndWait();
			return;
		}

		// Order is good to confirm

		// Change order status
		OrderControl.changeOrderStatus(String.valueOf(clickedRow.getOrderId()), OrderStatusName.ENTERED_THE_PARK);

		// Setting parameters for price.
		String id = clickedRow.getTravelerId();
		String type = clickedRow.getOrderType().toString();
		price = clickedRow.getPrice();

		// Change number of participants in the order
		if (numberOfParticipantsInCurrentOrder != numberOfParticipantsInOriginalOrder) {
			// Update the number of visitors in DB
			OrderControl.changeNumberOfVisitorsInExisitingOrder(String.valueOf(clickedRow.getOrderId()),
					numberOfParticipantsInCurrentOrder);

			String orderId = String.valueOf(clickedRow.getOrderId());
			price = calculatePriceForVisit(id, numberOfParticipantsInCurrentOrder, type);
			// Update the price in DB
			OrderControl.updateOrderPrice(orderId, price);
		}

		// Changing the number of participants
		Order tempOrder = new Order(clickedRow);
		// Update the number of visitors and price in current order in GUI.
		tempOrder.setNumberOfParticipants(numberOfParticipantsInCurrentOrder);
		tempOrder.setPrice(price);
		clickedRow = new OrderTb(tempOrder);

		// Add the visit
		OrderControl.addVisit(clickedRow);

		// Update the current visitors number
		Park p = ParkControl.getParkById(String.valueOf(clickedRow.getParkId()));
		int updateNumber = p.getCurrentVisitors() + numberOfParticipantsInCurrentOrder;
		ParkControl.updateCurrentVisitors(clickedRow.getParkId(), updateNumber);

		ParkControl.updateIfParkFull(p);

		// Printing receipt
		loadOrderConfirmation();

		// Reloading the table view
		loadTableView();

	}

	/*
	 * This function calculate the order price.
	 */
	private double calculatePriceForVisit(String id, int visitorsNumber, String type) {
		double price = 0;
		// Recive the data from the text fields.
		String idOfTraveler = id;
		int numberOfVisitors = visitorsNumber;
		String orderType = type;

		// Setting up vars
		Subscriber sub = TravelerControl.getSubscriber(idOfTraveler);
		LocalDate today = LocalDate.now();
		int parkId = MemberLoginController.member.getParkId();

		// Regular Price
		CheckOut chk = new RegularCheckOut(numberOfVisitors, parkId, today.toString());

		// Order is for solo / family and the visit is pre-ordered,
		// in addition the ordering person is not a guide
		if (orderType.equals(OrderType.SOLO.toString()) || orderType.equals(OrderType.FAMILY.toString())) {
			if (sub != null && !sub.getSubscriberType().equals("Guide"))
				price = new SubscriberPreOrderCheckOut(chk).getPrice();
			else
				price = new RegularPreOrderCheckOut(chk).getPrice();
			return price;
		} else {
			// The group is a group visit.
			if (sub == null) {
				price = new RegularPreOrderCheckOut(chk).getPrice();
			} else if (sub.getSubscriberType().equals("Guide")) {
				price = new GuidePrePayCheckOut(chk).getPrice();
			} else {
				price = new SubscriberPreOrderCheckOut(chk).getPrice();
			}
			return price;
		}

	}

	/*
	 * This function handle the confirm button when pressed.
	 */
	private void loadOrderConfirmation() {
		try {
			Stage newStage = new Stage();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/OrderConfirmation.fxml"));
			OrderConfirmationController controller = new OrderConfirmationController();
			controller.setOrder(new Order(clickedRow));
			String id = clickedRow.getTravelerId();
			Subscriber subscriber = TravelerControl.getSubscriber(id);
			Traveler traveler;
			if (subscriber == null) {
				traveler = TravelerControl.getTraveler(id);
				controller.setTraveler(traveler);
			} else
				controller.setTraveler(subscriber);

			// The traveler pays for how many visitors that ordered.
			controller.setSummaryPayment(String.valueOf(clickedRow.getPrice()));
			loader.setController(controller);
			loader.load();
			Parent p = loader.getRoot();

			newStage.setTitle("Order Confirmation");
			newStage.setScene(new Scene(p));
			newStage.setResizable(false);
			newStage.show();
		} catch (IOException e) {
			System.out.println("faild to load form");
			e.printStackTrace();
		}
	}

	/*
	 * This function return if the traveler can enter the park when approching the entrance
	 */
	private boolean canTravelerEnter() {

		// Var set up
		LocalTime orderTime = LocalTime.parse(clickedRow.getOrderTime());
		LocalDate orderDate = LocalDate.parse(clickedRow.getOrderDate());

		// Order date is today?
		if (!orderDate.equals(LocalDate.now()))
			return false;

		// Check if the person came early at no more than 15 mins
		// Or came late by at most 1 hour

		// Order status is not valid
		if (!clickedRow.getOrderStatus().equals(OrderStatusName.CONFIRMED.toString()))
			return false;

		// Order is within range
		if (LocalTime.now().isAfter(orderTime.minusMinutes(15)) && orderTime.isBefore(orderTime.plusHours(1)))
			return true;
		return false;
	}

	/*
	 * This function return all of the orders for traveler in a specific park
	 */
	private void searchTraveler() {

		// Did not choose traveler Id, load whole table view.
		String id = idTextField.getText();
		if (id.isEmpty()) {
			loadTableView();
			return;
		}

		// Return all of the orders for specific
		String parkId = String.valueOf(MemberLoginController.member.getParkId());
		ArrayList<Order> ordersArrayList = OrderControl.getOrdersForTravelerInPark(parkId, id);
		ArrayList<OrderTb> tbOrdersArrayList = OrderControl.convertOrderToOrderTb(ordersArrayList);

		// No orders returned
		if (ordersArrayList.isEmpty()) {
			new CustomAlerts(AlertType.ERROR, "Input error", "ID error", "No orders found for " + id).showAndWait();
			return;
		}

		// Orders returned.
		init(tbOrdersArrayList);
		ordersTableView.setItems(getOrders(tbOrdersArrayList));
	}

}