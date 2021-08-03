package gui;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import com.jfoenix.controls.*;
import Controllers.OrderControl;
import Controllers.ParkControl;
import Controllers.TravelerControl;
import Controllers.calculatePrice.*;
import alerts.CustomAlerts;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import logic.GoNatureFinals;
import logic.Order;
import logic.OrderStatusName;
import logic.OrderType;
import logic.Park;
import logic.Subscriber;
import logic.Traveler;
import util.UtilityFunctions;
import javafx.fxml.Initializable;
import javafx.scene.control.DateCell;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * This Class is the GUI controller of OrderVisit.fxml
 * It handles all the JavaFx nodes events.
 * 
 * In this screen the traveler makes a new order
 *
 */
public class OrderVisitController implements Initializable {

	@FXML
	private AnchorPane orderVisitRootPane;
	@FXML
	private Accordion accordion;

	@FXML
	private TitledPane identificationTP;

	@FXML
	private AnchorPane identificationAP;

	@FXML
	private JFXTextField idInputOrderVisit;

	@FXML
	private AnchorPane informationAP;

	@FXML
	private JFXComboBox<String> parksComboBox;

	@FXML
	private JFXDatePicker datePicker;

	@FXML
	private JFXTextField emailInputOrderVisit;

	@FXML
	private JFXComboBox<OrderType> typeComboBox;

	@FXML
	private Label requiredFieldsLabel;

	@FXML
	private AnchorPane paymentAP;

	@FXML
	private JFXRadioButton payNowRadioBox;

	@FXML
	private JFXRadioButton payLaterRadioBox;

	@FXML
	private AnchorPane paymentPane;

	@FXML
	private JFXComboBox<String> timeComboBox;

	@FXML
	private JFXTextField fullNameInput;

	@FXML
	private JFXTextField phoneInput;

	@FXML
	private JFXTextField cardHolderName;

	@FXML
	private JFXTextField cardHolderLastName;

	@FXML
	private JFXTextField CardNumber;

	@FXML
	private JFXTextField CCV;

	@FXML
	private JFXTextField numOfVisitorsOrderVisit;

	@FXML
	private JFXDatePicker CardExpiryDate;

	@FXML
	private Label permissionLabel;

	@FXML
	private Label orderVisitHeaderLabel;

	@FXML
	private Label summaryID;

	@FXML
	private Label summaryPark;

	@FXML
	private Label summaryDate;

	@FXML
	private Label summaryPayment;

	@FXML
	private Label summaryType;

	@FXML
	private Label summaryVisitors;

	@FXML
	private Label summaryEmail;

	@FXML
	private Label summaryTotalPrice;

	@FXML
	private Label summaryFullName;

	@FXML
	private ProgressIndicator pb;

	@FXML
	private Label summaryPhone;
	@FXML
	private Label summaryTime;

	DecimalFormat df = new DecimalFormat("####0.00");
	private Subscriber subscriber;
	private Traveler traveler;
	private Order order;
	private Order recentOrder;
	private boolean isOrderFromMain = false;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		Locale.setDefault(Locale.ENGLISH);
		accordion.setExpandedPane(identificationTP);
		initComboBoxes();
		initRadioBoxes();
		initDatePicker();
		initLabels();
	}

	@FXML
	private void placeOrderButton() {

		if (isValidInput()) {

			Task<Boolean> task = new Task<Boolean>() {
				@Override
				protected Boolean call() throws Exception {

					order = new Order(0, summaryID.getText(), getSelectedParkId(), summaryDate.getText(),
							summaryTime.getText(), summaryType.getText(), Integer.parseInt(summaryVisitors.getText()),
							summaryEmail.getText(), CalculatePrice(), OrderStatusName.PENDING.toString());

					String[] travelerName = summaryFullName.getText().split(" ");
					String travelerFirstName = travelerName[0];
					String travelerLastName = travelerName.length == 1 ? "" : travelerName[1];

					traveler = new Traveler(summaryID.getText(), travelerFirstName, travelerLastName,
							summaryEmail.getText(), summaryPhone.getText());
					recentOrder = OrderControl.addOrderAndNotify(order, traveler);
					if (recentOrder != null)
						return true;
					return false;
				}
			};

			pb.setVisible(true);
			orderVisitRootPane.setDisable(true);
			new Thread(task).start();
			task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent t) {
					boolean res = false;
					res = task.getValue();
					pb.setVisible(false);
					orderVisitRootPane.setDisable(false);
					if (res) {
						loadOrderConfirmation();
					} else {
						loadRescheduleScreen(order);
					}
				}
			});

			task.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent t) {
					pb.setVisible(false);
					orderVisitRootPane.setDisable(false);
				}
			});

		}

	}

	/* This function returns the name of the selected park */
	private int getSelectedParkId() {
		Park park = ParkControl.getParkByName(summaryPark.getText());
		if (park != null)
			return park.getParkId();
		else
			return -1;
	}

	/* This function check if All the input is valid */
	private boolean isValidInput() {
		if (!checkIfFilledAllFields())
			new CustomAlerts(AlertType.ERROR, "Bad Input", "Bad Input", "Please fill all the fields").showAndWait();
		else if (summaryID.getText().length() != 9)
			new CustomAlerts(AlertType.ERROR, "Bad Input", "Bad ID Input", "Id length must be 9").showAndWait();
		else if (!checkIfOrderTimeIs24HouesFromNow()) {
			new CustomAlerts(AlertType.ERROR, "Bad Input", "Invalid Visit Time",
					"Visit time must be atleast 24 hours from now").showAndWait();
		} else if (Integer.parseInt(summaryVisitors.getText()) > 15
				&& summaryType.getText().equals(OrderType.GROUP.toString())) {
			new CustomAlerts(AlertType.ERROR, "Bad Input", "Invalid Visitor's Number",
					"Group order can be up to 15 travelers").showAndWait();
		} else if (Integer.parseInt(summaryVisitors.getText()) < 2
				&& summaryType.getText().equals(OrderType.GROUP.toString())) {
			new CustomAlerts(AlertType.ERROR, "Bad Input", "Invalid Visitor's Number",
					"Group order must have atleast 2 visitors").showAndWait();
		} else if (subscriber != null && subscriber.getSubscriberType().equals("Family")
				&& Integer.parseInt(summaryVisitors.getText()) > subscriber.getNumberOfParticipants()
				&& summaryType.getText().equals(OrderType.FAMILY.toString())) {
			new CustomAlerts(AlertType.ERROR, "Bad Input", "Invalid Visitor's Number",
					"Your family account has " + subscriber.getNumberOfParticipants()
							+ " members.\nThe number of visitors can not be higher than "
							+ subscriber.getNumberOfParticipants()).showAndWait();
		} else if (Integer.parseInt(summaryVisitors.getText()) < 1) {
			new CustomAlerts(AlertType.ERROR, "Bad Input", "Invalid Visitor's Number",
					"Visitor's number must be positive number and atleast 1. ").showAndWait();
		} else if (!UtilityFunctions.isValidEmailAddress(summaryEmail.getText())) {
			new CustomAlerts(AlertType.ERROR, "Bad Input", "Invalid Email", "Please insert valid email. ")
					.showAndWait();
		} else if (!UtilityFunctions.isNumeric(summaryVisitors.getText())) {
			new CustomAlerts(AlertType.ERROR, "Bad Input", "Invalid Visitor's Number",
					"Visitor's number must be a positive number and atleast 1. ").showAndWait();
		} else {
			return true;
		}
		return false;

	}

	private boolean checkIfFilledAllFields() {
		if (summaryID.getText().isEmpty() || summaryPark.getText().isEmpty() || summaryDate.getText().isEmpty()
				|| summaryType.getText().isEmpty() || summaryVisitors.getText().isEmpty()
				|| summaryEmail.getText().isEmpty() || summaryTime.getText().isEmpty()
				|| summaryFullName.getText().isEmpty() || summaryPhone.getText().isEmpty()
				|| summaryType.getText().equals("null") || summaryPark.getText().equals("null")
				|| summaryDate.getText().equals("null") || summaryTime.getText().equals("null")) {
			return false;
		} else if (summaryPayment.getText().equals("At The Park"))
			return true;
		else {
			if (cardHolderName.getText().isEmpty() || cardHolderLastName.getText().isEmpty() || CCV.getText().isEmpty()
					|| CardNumber.getText().isEmpty() || CardExpiryDate.valueProperty().getValue() == null)
				return false;
			else
				return true;
		}
	}

	private Double CalculatePrice() {
		if (!summaryVisitors.getText().isEmpty() && !idInputOrderVisit.getText().isEmpty()
				&& !summaryVisitors.getText().isEmpty() && !summaryDate.getText().isEmpty()
				&& !summaryDate.getText().equals("null")) {

			int visitorsNumber = 0;
			if (!UtilityFunctions.isNumeric(summaryVisitors.getText())) {
				return 0.0;
			} else {
				visitorsNumber = Integer.parseInt(summaryVisitors.getText());
				if (visitorsNumber <= 0) {
					return 0.0;
				}
			}

			CheckOut basic = new RegularCheckOut(visitorsNumber, 1, summaryDate.getText());
			/* subscriber - Solo/family order */
			if ((permissionLabel.getText().equals("Solo") || permissionLabel.getText().equals("Family"))
					&& summaryType.getText().equals(OrderType.SOLO.toString())
					|| summaryType.getText().equals(OrderType.FAMILY.toString())) {

				SubscriberPreOrderCheckOut checkOut = new SubscriberPreOrderCheckOut(basic);
				return checkOut.getPrice();

				/* guest order */
			} else if (permissionLabel.getText().equals("Guest")) {
				RegularPreOrderCheckOut checkOut = new RegularPreOrderCheckOut(basic);
				return checkOut.getPrice();

				/* group order - pay online */
			} else if (summaryPayment.getText().equals("Online")
					&& summaryType.getText().equals(OrderType.GROUP.toString())) {
				GuidePrePayCheckOut checkOut = new GuidePrePayCheckOut(basic);
				return checkOut.getPrice();

			}
			/* group order - pay at the park */
			else if (summaryPayment.getText().equals("At The Park")
					&& summaryType.getText().equals(OrderType.GROUP.toString())) {
				GuidePayAtParkCheckOut checkOut = new GuidePayAtParkCheckOut(basic);
				return checkOut.getPrice();

			} else {
				RegularPreOrderCheckOut checkOut = new RegularPreOrderCheckOut(basic);
				return checkOut.getPrice();
			}

		}
		return (double) GoNatureFinals.FULL_PRICE;
	}

	private boolean checkIfOrderTimeIs24HouesFromNow() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		LocalDateTime now = LocalDateTime.now();
		String currentDateAndTime = dtf.format(now);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(sdf.parse(currentDateAndTime));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Number of Days to add
		c.add(Calendar.DAY_OF_MONTH, 1);
		c.add(Calendar.HOUR_OF_DAY, -1); // new
		currentDateAndTime = sdf.format(c.getTime());
		Date orderDate = null;
		Date dateOfTommorow = null;
		try {
			orderDate = new SimpleDateFormat("yyyy-MM-dd HH:mm")
					.parse(summaryDate.getText() + " " + summaryTime.getText());
			dateOfTommorow = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(currentDateAndTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		if (dateOfTommorow.after(orderDate)) {
			return false;
		}
		return true;
	}

	private void initLabels() {

		if (!isOrderFromMain) {
			String id = "";
			if (TravelerLoginController.traveler != null) {
				id = TravelerLoginController.traveler.getTravelerId();
				fullNameInput.setText(TravelerLoginController.traveler.getFirstName() + " "
						+ TravelerLoginController.traveler.getLastName());
				emailInputOrderVisit.setText(TravelerLoginController.traveler.getEmail());
				phoneInput.setText(TravelerLoginController.traveler.getPhoneNumber());

			} else {
				id = TravelerLoginController.subscriber.getTravelerId();
				fullNameInput.setText(TravelerLoginController.subscriber.getFirstName() + " "
						+ TravelerLoginController.subscriber.getLastName());
				emailInputOrderVisit.setText(TravelerLoginController.subscriber.getEmail());
				phoneInput.setText(TravelerLoginController.subscriber.getPhoneNumber());
			}
			idInputOrderVisit.setText(id);
			idInputOrderVisit.setDisable(true);
			fullNameInput.setDisable(true);
			emailInputOrderVisit.setDisable(true);
			phoneInput.setDisable(true);

			subscriber = TravelerControl.getSubscriber(id);
			if (subscriber == null) {
				permissionLabel.setText("Guest");
			} else
				permissionLabel.setText(subscriber.getSubscriberType());

			initComboBoxes();

		}
		summaryPark.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				if (arg2.equals("null"))
					summaryPark.setVisible(false);
				else
					summaryPark.setVisible(true);
			}
		});
		summaryDate.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				if (arg2.equals("null"))
					summaryDate.setVisible(false);
				else
					summaryDate.setVisible(true);
			}
		});
		summaryTime.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				if (arg2.equals("null"))
					summaryTime.setVisible(false);
				else
					summaryTime.setVisible(true);
			}
		});

		summaryType.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				if (arg2.equals("null"))
					summaryType.setVisible(false);
				else {
					summaryType.setVisible(true);
					if (!summaryID.getText().isEmpty() && !summaryDate.getText().isEmpty()
							&& !summaryVisitors.getText().isEmpty() && !summaryType.getText().isEmpty()) {
						summaryTotalPrice.setText(df.format(CalculatePrice()) + "₪");
					}
				}
			}
		});

		phoneInput.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				phoneInput.setText(arg2.replaceAll("[^\\d]", ""));
			}
		});

		idInputOrderVisit.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				idInputOrderVisit.setText(arg2.replaceAll("[^\\d]", ""));
				if (arg2.length() == 9) {
					subscriber = TravelerControl.getSubscriber(arg2);
					if (subscriber == null)
						permissionLabel.setText("Guest");
					else
						permissionLabel.setText(subscriber.getSubscriberType());

					if (!summaryVisitors.getText().isEmpty())
						summaryTotalPrice.setText(df.format(CalculatePrice()) + "₪");
				} else {
					permissionLabel.setText("Guest");
					summaryTotalPrice.setText("");
				}

				initComboBoxes();
			}

		});

		summaryVisitors.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				if (!idInputOrderVisit.getText().isEmpty())
					summaryTotalPrice.setText(df.format(CalculatePrice()) + "₪");
				else {
					summaryTotalPrice.setText("");
				}
			}
		});

		datePicker.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				summaryTotalPrice.setText(df.format(CalculatePrice()) + "₪");
			}
		});

		summaryID.textProperty().bind(Bindings.convert(idInputOrderVisit.textProperty()));
		summaryPark.textProperty().bind(Bindings.convert(parksComboBox.valueProperty()));
		summaryDate.textProperty().bind(Bindings.convert(datePicker.valueProperty()));
		summaryTime.textProperty().bind(Bindings.convert(timeComboBox.valueProperty()));
		summaryType.textProperty().bind(Bindings.convert(typeComboBox.valueProperty()));
		summaryVisitors.textProperty().bind(Bindings.convert(numOfVisitorsOrderVisit.textProperty()));
		summaryEmail.textProperty().bind(Bindings.convert(emailInputOrderVisit.textProperty()));
		summaryFullName.textProperty().bind(Bindings.convert(fullNameInput.textProperty()));
		summaryPhone.textProperty().bind(Bindings.convert(phoneInput.textProperty()));
	}

	/* Setup the date picker */
	private void initDatePicker() {
		datePicker.setConverter(new StringConverter<LocalDate>()
		{
		    private DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("MM/dd/yyyy");

		    @Override
		    public String toString(LocalDate localDate)
		    {
		        if(localDate==null)
		            return "";
		        return dateTimeFormatter.format(localDate);
		    }

		    @Override
		    public LocalDate fromString(String dateString)
		    {
		        if(dateString==null || dateString.trim().isEmpty())
		        {
		            return null;
		        }
		        return LocalDate.parse(dateString,dateTimeFormatter);
		    }
		});
		CardExpiryDate.setConverter(new StringConverter<LocalDate>()
		{
		    private DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("MM/dd/yyyy");

		    @Override
		    public String toString(LocalDate localDate)
		    {
		        if(localDate==null)
		            return "";
		        return dateTimeFormatter.format(localDate);
		    }

		    @Override
		    public LocalDate fromString(String dateString)
		    {
		        if(dateString==null || dateString.trim().isEmpty())
		        {
		            return null;
		        }
		        return LocalDate.parse(dateString,dateTimeFormatter);
		    }
		});
		/* Disable the user from picking past dates */
		datePicker.setDayCellFactory(picker -> new DateCell() {
			@Override
			public void updateItem(LocalDate date, boolean empty) {
				super.updateItem(date, empty);
				LocalDate today = LocalDate.now();
				setDisable(empty || date.compareTo(today) < 0 || date.compareTo(today) == 0);
			}
		});
	}

	private void initRadioBoxes() {
		summaryPayment.setText("Online");
		payNowRadioBox.setSelected(true);
		payLaterRadioBox.setSelected(false);
		paymentPane.setVisible(true);
		paymentPane.setDisable(false);
	}

	@FXML
	private void turnOffPayNow() {
		if (!payNowRadioBox.isSelected())
			payLaterRadioBox.setSelected(true);
		else {
			payNowRadioBox.setSelected(false);
			paymentPane.setVisible(false);
			paymentPane.setDisable(true);
			summaryPayment.setText("At The Park");
		}
		summaryTotalPrice.setText(df.format(CalculatePrice()) + "₪");
	}

	@FXML
	private void turnOffPayLater() {
		if (!payLaterRadioBox.isSelected())
			payNowRadioBox.setSelected(true);
		else {
			payLaterRadioBox.setSelected(false);
			paymentPane.setVisible(true);
			paymentPane.setDisable(false);
			summaryPayment.setText("Online");
		}
		summaryTotalPrice.setText(df.format(CalculatePrice()) + "₪");
	}

	private void initComboBoxes() {
		parksComboBox.getItems().clear();
		typeComboBox.getItems().clear();
		timeComboBox.getItems().clear();

		/* Set parks combobox to load dynamically from database */
		ArrayList<String> parksNames = ParkControl.getParksNames();
		if (parksNames != null) {
			parksComboBox.getItems().addAll(parksNames);
		}
		/* Set up order type from OrderType Enum */
		typeComboBox.getItems().addAll(Arrays.asList(OrderType.values()));
		if (!permissionLabel.getText().equals("Family")) {
			typeComboBox.getItems().remove(1);
		}

		/* Listener to order type ComboBox. activate on every item selected */
		typeComboBox.valueProperty().addListener((obs, oldItem, newItem) -> {
			if (newItem == null) {
			} else {
				if (newItem.toString().equals(OrderType.SOLO.toString())) {
					numOfVisitorsOrderVisit.setText("1");
					numOfVisitorsOrderVisit.setDisable(true);

				} else {
					numOfVisitorsOrderVisit.setText("");
					numOfVisitorsOrderVisit.setPromptText("Visitor's Number");
					numOfVisitorsOrderVisit.setDisable(false);
				}
			}
		});

		timeComboBox.getItems().addAll(GoNatureFinals.AVAILABLE_HOURS);
	}

	private void loadRescheduleScreen(Order order) {
		try {
			Stage newStage = new Stage();
			Stage thisStage = getStage();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/Reschedule.fxml"));
			RescheduleController controller = new RescheduleController();
			System.out.println("order " + order.getOrderDate());
			controller.setOrder(order);
			loader.setController(controller);
			loader.load();
			controller.setSelectedTimeLabel(summaryDate.getText() + ", " + summaryTime.getText());
			controller.setRescheduleStage(newStage);
			if (isOrderFromMain) {
				controller.setOrderFromMain(true);

			}
			controller.setOrderStage(thisStage);
			controller.setTraveler(traveler);
			Parent p = loader.getRoot();

			/* Block parent stage until child stage closes */
			newStage.initModality(Modality.WINDOW_MODAL);
			newStage.initOwner(thisStage);
			newStage.getIcons().add(new Image(GoNatureFinals.APP_ICON));
			newStage.setTitle("Reschedule");
			newStage.setScene(new Scene(p));
			newStage.setResizable(false);
			newStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void loadOrderConfirmation() {
		try {
			Stage thisStage = getStage();
			Stage newStage = new Stage();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/OrderConfirmation.fxml"));
			OrderConfirmationController controller = new OrderConfirmationController();
			controller.setOrder(recentOrder);
			controller.setTraveler(traveler);
			controller.setSummaryPayment(summaryPayment.getText());
			controller.setOrderFromWeb(true);
			loader.setController(controller);
			loader.load();
			Parent p = loader.getRoot();

			newStage.setTitle("Order Confirmation");
			newStage.getIcons().add(new Image(GoNatureFinals.APP_ICON));
			newStage.setScene(new Scene(p));
			newStage.setResizable(false);
			if (isOrderFromMain)
				thisStage.close();
			newStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Setter for class variable isOrderFromMain
	 * 
	 * @param isOrderFromMain true if we opened this screen from the main screen
	 */
	public void setOrderFromMain(boolean isOrderFromMain) {
		this.isOrderFromMain = isOrderFromMain;
	}

	private Stage getStage() {
		return (Stage) summaryID.getScene().getWindow();
	}
}
