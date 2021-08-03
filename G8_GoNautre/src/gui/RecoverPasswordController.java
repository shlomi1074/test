package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXTextField;

import Controllers.NotificationControl;
import Controllers.WorkerControl;
import alerts.CustomAlerts;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import logic.Messages;
import resources.MsgTemplates;
import javafx.scene.control.Alert.AlertType;

/**
 * This Class is the GUI controller of RecoverPassword.fxml
 * It handles all the JavaFx nodes events.
 * 
 * In this screen the user can ask to recover his password
 *
 */
public class RecoverPasswordController implements Initializable {

	@FXML
	private Button recoverBtn;

	@FXML
	private JFXTextField idTextField;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	}

	@FXML
	private void recoverPasswordBtn() {

		String id = idTextField.getText();
		if (id.isEmpty()) {
			new CustomAlerts(AlertType.ERROR, "Bad Input", "Bad Input", "Please enter your id.").showAndWait();
		} else {

			/* NEED TO GET THE EMAIL FROM THE DATABASE */
			ArrayList<String> info = WorkerControl.getEmployeeEmailAndPassword(id);
			String email = info.get(0);
			String password = info.get(1);
			if (email.equals("") || password == null || password.equals("")) {
				System.out.println(email + "da");
				System.out.println(password);
				System.out.println("there is no such id");
			}
			else {
				String emailContent = String.format(MsgTemplates.passwordRecovery[1].toString(), id, password);
				Messages msg = new Messages(0, null, null, null, MsgTemplates.passwordRecovery[0],
						emailContent, -2);
				NotificationControl.sendMailInBackgeound(msg, email);
			}

			new CustomAlerts(AlertType.INFORMATION, "Password Recovery", "Password Recovery",
					"Check your email.\nWe sent your password to your email.").showAndWait();
			getStage().close();
		}

	}

	private Stage getStage() {
		return (Stage) recoverBtn.getScene().getWindow();
	}

}
