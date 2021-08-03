package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import com.jfoenix.controls.JFXComboBox;
import Controllers.ParkControl;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import logic.Park;

/**
 * This Class is the GUI controller of ParkParameters.fxml
 * It handles all the JavaFx nodes events.
 * 
 * This screen shows the parameters of a certain park
 *
 */
public class ParkParametersController implements Initializable {

	@FXML
	private Label headerLabel;

	@FXML
	private Label currentLabel;

	@FXML
	private Label maxLabel;

	@FXML
	private Label allowedLabel;

	@FXML
	private Label actualLabel;

	@FXML
	private Label chooseParkLabel;

	@FXML
	private JFXComboBox<String> parkComboBox;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		if (MemberLoginController.member.getParkId() == 0) {
			initComboBoxs();
			chooseParkLabel.setVisible(true);
			parkComboBox.setVisible(true);
			parkComboBox.setDisable(false);
		} else {
			chooseParkLabel.setVisible(false);
			parkComboBox.setVisible(false);
			parkComboBox.setDisable(true);
			loadParameters(ParkControl.getParkById(String.valueOf(MemberLoginController.member.getParkId())));
		}
	}

	private void initComboBoxs() {
		/* Set parks combo box to load dynamically from database */
		ArrayList<String> parksNames = ParkControl.getParksNames();
		if (parksNames != null) {
			parkComboBox.getItems().addAll(parksNames);
		}

		/* Listener - activate on every item selected */
		parkComboBox.valueProperty().addListener((obs, oldItem, newItem) -> {
			if (newItem != null) {
				loadParameters(ParkControl.getParkByName(newItem));
			}
		});

	}

	private void loadParameters(Park park) {
		if (park != null)
			setLabels(park);
	}

	private void setLabels(Park park) {
		currentLabel.setText(park.getCurrentVisitors() + "");
		maxLabel.setText(park.getMaxVisitors() + "");
		allowedLabel.setText(park.getGapBetweenMaxAndCapacity() + "");
		int temp = park.getMaxVisitors() - park.getCurrentVisitors();
		temp = temp < 0 ? 0 : temp;
		actualLabel.setText(temp + "");
	}

}
