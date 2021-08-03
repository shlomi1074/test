package gui;

import java.net.URL;
import java.util.ResourceBundle;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import Controllers.NotificationControl;
import alerts.CustomAlerts;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import logic.GoNatureFinals;
import logic.Messages;

/**
 * This Class is the GUI controller of ContactUs.fxml
 * It handles all the JavaFx nodes events.
 * 
 * In this screen the user can send us an email.
 *
 */
public class ContactUsController implements Initializable {
	@FXML
	private AnchorPane ourConcactsPane;

	@FXML
	private AnchorPane emailPane;

	@FXML
	private JFXTextArea textArea;
	
    @FXML
    private Label emailLabel;

	@FXML
	private JFXButton sendButton;

	@FXML
	private JFXTextField nameTextField;

	@FXML
	private JFXTextField phoneTextField;

	@FXML
	private JFXTextField emailTextField;

	@FXML
	private JFXTextField subjectLabel;
	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		emailLabel.setText(GoNatureFinals.GO_NATURE_EMAIL);
	}

	@FXML
	private void sendEmailBtn() {
		if (isValidInput()) {
			String content = "Mail from: " + nameTextField.getText() + ", " + emailTextField.getText() + ", "
					+ phoneTextField.getText() + ".\n" + textArea.getText();

			Messages msg = new Messages(0, null, null, null, subjectLabel.getText(), content, -1);
			NotificationControl.sendMailInBackgeound(msg, GoNatureFinals.GO_NATURE_EMAIL);
			CustomAlerts alert = new CustomAlerts(AlertType.INFORMATION, "Email Sent", "Email Sent",
					"Thank you for reaching out.\n" + "We will be in touch as soon as possiable.");
			alert.showAndWait();
			getStage().close();

		}

	}

	private boolean isValidInput() {
		if (emailTextField.getText().isEmpty() || subjectLabel.getText().isEmpty() || textArea.getText().isEmpty()) {
			new CustomAlerts(AlertType.ERROR, "Bad Input", "Bad Input", "Email, Subject, and message are must!")
					.showAndWait();
			return false;
		}
		return true;
	}

	private Stage getStage() {
		return (Stage) subjectLabel.getScene().getWindow();
	}

}
