package gui;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import Controllers.RequestControl;
import alerts.CustomAlerts;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.DateCell;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;

/**
 * This class control the request sending, that is used by Park Manager.
 */
public class UpdateParametersController implements Initializable {

	@FXML
	private AnchorPane updateParametersRootPane;

	@FXML
	private Accordion accordion;

	@FXML
	private TitledPane maxVisitorsTP;

	@FXML
	private AnchorPane identificationAP;

	@FXML
	private JFXTextField newMaxVisitorsTextField;

	@FXML
	private TitledPane estimatedTimeTP;

	@FXML
	private AnchorPane informationAP;

	@FXML
	private JFXTextField newEsitimatedTIme;

	@FXML
	private TitledPane gapTP;

	@FXML
	private AnchorPane paymentAP;

	@FXML
	private JFXTextField gapTextField;

	@FXML
	private TitledPane discountTP;

	@FXML
	private AnchorPane discountAP;

	@FXML
	private JFXTextField discountPercentage;

	@FXML
	private JFXDatePicker discountStartDate;

	@FXML
	private JFXDatePicker discountEndDate;

	@FXML
	private Label headerLabel;

	@FXML
	private JFXButton sendForApprovealButton;

	@FXML
	private void sendForApprovealButton() {

		ArrayList<String> arrayOfTextRequests = new ArrayList<>();

		arrayOfTextRequests.add(newMaxVisitorsTextField.getText());
		arrayOfTextRequests.add(newEsitimatedTIme.getText());
		arrayOfTextRequests.add(gapTextField.getText());

		if (discountStartDate.getValue() != null)
			arrayOfTextRequests.add(discountStartDate.getValue().toString());

		else
			arrayOfTextRequests.add("NULL"); //

		if (discountEndDate.getValue() != null)
			arrayOfTextRequests.add(discountEndDate.getValue().toString());
		else
			arrayOfTextRequests.add("NULL"); //

		arrayOfTextRequests.add(discountPercentage.getText());

		Integer prakID = MemberLoginController.member.getParkId();
		arrayOfTextRequests.add(prakID.toString());

		RequestControl.addNewRequest(arrayOfTextRequests);

		new CustomAlerts(AlertType.INFORMATION, "Sent", "Sent", "New requests were sent to Department Manager")
				.showAndWait();

	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		accordion.setExpandedPane(maxVisitorsTP);
		initDatePicker();
		initTextFields();
	}

	private void initTextFields() {
		discountPercentage.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				discountPercentage.setText(arg2.replaceAll("[^\\d]", ""));
			}
		});
		gapTextField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				gapTextField.setText(arg2.replaceAll("[^\\d]", ""));
			}
		});
		newEsitimatedTIme.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				newEsitimatedTIme.setText(arg2.replaceAll("[^\\d]", ""));
			}
		});
		newMaxVisitorsTextField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				newMaxVisitorsTextField.setText(arg2.replaceAll("[^\\d]", ""));
			}
		});
	}

	private void initDatePicker() {
		discountStartDate.setDayCellFactory(picker -> new DateCell() {
			@Override
			public void updateItem(LocalDate date, boolean empty) {
				super.updateItem(date, empty);
				LocalDate today = LocalDate.now();
				setDisable(empty || date.compareTo(today) < 0);
			}
		});

		discountEndDate.setDayCellFactory(picker -> new DateCell() {
			@Override
			public void updateItem(LocalDate date, boolean empty) {
				super.updateItem(date, empty);
				LocalDate today = LocalDate.now();
				setDisable(empty || date.compareTo(today) < 0);
			}
		});

	}

}