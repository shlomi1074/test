package gui;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.ResourceBundle;
import Controllers.NotificationControl;
import Controllers.OrderControl;
import Controllers.ParkControl;
import alerts.CustomAlerts;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import logic.Messages;
import logic.Order;
import logic.OrderTb;
import logic.Park;
import logic.Subscriber;
import logic.Traveler;
import resources.MsgTemplates;
import logic.OrderStatusName;

/**
 * This Class is the GUI controller of TravelerViewOrders.fxml
 * It handles all the JavaFx nodes events.
 * 
 * In this screen the traveler can confirm or cancel an order.
 *
 */
public class TravelerViewOrders implements Initializable {

	ObservableList<OrderTb> ov = FXCollections.observableArrayList();

	@FXML
	private TableView<OrderTb> ordersTableView;

	@FXML
	private TableColumn<OrderTb, Integer> orderIdCol;

	@FXML
	private TableColumn<OrderTb, String> visitDateCol;

	@FXML
	private TableColumn<OrderTb, String> visitTimeCol;

	@FXML
	private TableColumn<OrderTb, String> orderStatusCol;

	@FXML
	private Button confirmOrderBtn;

	@FXML
	private Button cancelOrderBtn;

	@FXML
	private Label headerLabel;

	@FXML
	private Label orderIdTxt;

	@FXML
	private Label visitDateTxt;

	@FXML
	private Label visitTimeTxt;

	@FXML
	private Label orderStatusTxt;

	/*
	 * Search "Question" for questions to dev team.
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		loadTableView();
		confirmOrderBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				confirmButton();
				clearLabals();
			}
		});
		cancelOrderBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				cancelButton();
				clearLabals();
			}
		});
	}

	/**
	 * This function load the data from the DB to the Table view.
	 */
	@FXML
	public void loadTableView() {
		String id;
		Traveler trv = TravelerLoginController.traveler;
		Subscriber sbc = TravelerLoginController.subscriber;
		if (trv == null)
			id = String.valueOf(sbc.getTravelerId());
		else
			id = String.valueOf(trv.getTravelerId());

		ArrayList<Order> ordersArrayList = OrderControl.getOrders(id);
		ArrayList<OrderTb> tbOrdersArrayList = OrderControl.convertOrderToOrderTb(ordersArrayList);
		init(tbOrdersArrayList);
		ordersTableView.setItems(getOrders(tbOrdersArrayList));
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
	 * This function init the table view.
	 */
	private void init(ArrayList<OrderTb> orders) {
		orderIdCol.setCellValueFactory(new PropertyValueFactory<OrderTb, Integer>("orderId"));
		visitDateCol.setCellValueFactory(new PropertyValueFactory<OrderTb, String>("orderDate"));
		visitTimeCol.setCellValueFactory(new PropertyValueFactory<OrderTb, String>("orderTime"));
		orderStatusCol.setCellValueFactory(new PropertyValueFactory<OrderTb, String>("orderStatus"));

		ordersTableView.setRowFactory(tv -> {
			TableRow<OrderTb> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
					OrderTb clickedRow = row.getItem();
					orderIdTxt.setText(String.valueOf(clickedRow.getOrderId()));
					visitDateTxt.setText(clickedRow.getOrderDate());
					visitTimeTxt.setText(clickedRow.getOrderTime());
					orderStatusTxt.setText(clickedRow.getOrderStatus());
				}
			});
			return row;
		});
	}

	/*
	 * This function check if the user choose an order
	 */
	private boolean orderChose() {
		if (orderIdTxt.getText().isEmpty()) {
			new CustomAlerts(AlertType.ERROR, "Input Error", "Input Error", "Please select one of the orders")
					.showAndWait();
			return false;
		}
		return true;
	}

	/*
	 * This function handle the confirm button when pressed.
	 */
	private void confirmButton() {
		// Did the user choose order
		if (!orderChose())
			return;

		/*
		 * User can Confirm order if order is PENDING_EMAIL_SENT WAITING_HAS_SPOT
		 * Otherwise, can not confirm order.
		 */

		if (orderStatusTxt.getText().equals(OrderStatusName.WAITING_HAS_SPOT.toString())
				|| orderStatusTxt.getText().equals(OrderStatusName.PENDING_EMAIL_SENT.toString())) {
			boolean orderControlResult = OrderControl.changeOrderStatus(orderIdTxt.getText(),
					OrderStatusName.CONFIRMED);
			// Status Changed
			if (orderControlResult) {
				loadTableView();
				new CustomAlerts(AlertType.INFORMATION, "Changes were made", "Changes were made", "Order confirmed")
						.showAndWait();
				return;
			} else {
				new CustomAlerts(AlertType.ERROR, "System Error", "System Error",
						"Could not confirm this order, please try again later.").showAndWait();
				return;
			}
		} else {
			new CustomAlerts(AlertType.ERROR, "Input Error", "Order Error",
					"Make sure the order is in status Waiting has spot or Pending email sent").showAndWait();
			return;
		}
	}

	/*
	 * This function handle the cancel button when pressed.
	 */
	private void cancelButton() {

		// Did the user choose order
		if (!orderChose())
			return;

		/*
		 * User can Cancel order if order is PENDING, PENDING_EMAIL_SENT, WAITING, WAITING_HAS_SPOT, CONFIRMED
		 * Otherwise, can not confirm order.
		 * Question - Confirmed can be canceled after the date?
		 */
		if (orderStatusTxt.getText().equals(OrderStatusName.PENDING.toString())
				|| orderStatusTxt.getText().equals(OrderStatusName.PENDING_EMAIL_SENT.toString())
				|| orderStatusTxt.getText().equals(OrderStatusName.WAITING.toString())
				|| orderStatusTxt.getText().equals(OrderStatusName.WAITING_HAS_SPOT.toString())
				|| orderStatusTxt.getText().equals(OrderStatusName.CONFIRMED.toString())) {
			boolean orderControlResult = OrderControl.changeOrderStatus(orderIdTxt.getText(), OrderStatusName.CANCELED);
			// Status changed
			if (orderControlResult) {
				Order order = new Order(ordersTableView.getSelectionModel().getSelectedItem());
				loadTableView();
				new CustomAlerts(AlertType.INFORMATION, "Changes were made", "Changes were made", "Order canceled")
						.showAndWait();

				// Check the waiting list.
				OrderControl.checkWaitingList(Integer.parseInt(orderIdTxt.getText()));
				
				
				
				messageTravelerIfCancel(order);
				return;
			} else {
				// Status did not changed, system error.
				new CustomAlerts(AlertType.ERROR, "System Error", "System Error",
						"Could not cancel this order,please try again later.").showAndWait();
				return;
			}
		} else {
			// Order is not in right status.
			new CustomAlerts(AlertType.ERROR, "Input Error", "Input Error", "Order cannot be canceled").showAndWait();
			return;
		}
	}

	/*
	 * This function clears the labels
	 */
	private void messageTravelerIfCancel(Order order) {
		Park park = ParkControl.getParkById(String.valueOf(order.getParkId()));
		String subject = MsgTemplates.orderCancel[0];
		String content = String.format(MsgTemplates.orderCancel[1].toString(), park.getParkName(), order.getOrderDate(),
				order.getOrderTime());

		String travelerId = order.getTravelerId();
		String date = LocalDate.now().toString();
		String time = LocalTime.now().toString();
		int orderId = Integer.parseInt(orderIdTxt.getText());

		NotificationControl.sendMessageToTraveler(travelerId, date, time, subject, content, String.valueOf(orderId));
		Messages msg = new Messages(0, travelerId, date, time, subject, content, orderId);
		NotificationControl.sendMailInBackgeound(msg, order.getEmail());
	}

	/*
	 * This function clears the labels
	 */
	private void clearLabals() {
		orderIdTxt.setText("");
		visitDateTxt.setText("");
		visitTimeTxt.setText("");
		orderStatusTxt.setText("");
	}
}