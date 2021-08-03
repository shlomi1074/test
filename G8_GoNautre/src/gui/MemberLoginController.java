package gui;

import java.net.URL;
import java.util.ResourceBundle;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import Controllers.AutenticationControl;
import alerts.CustomAlerts;
import client.ChatClient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import logic.Employees;
import logic.GoNatureFinals;

public class MemberLoginController implements Initializable {

	@FXML
	private AnchorPane loginContainer;

	@FXML
	private Rectangle rectangle;

	@FXML
	private Label forgotPasswordLable;

	@FXML
	private AnchorPane personImageContainer;

	@FXML
	private ImageView userImageView;

	@FXML
	private AnchorPane lockImageContainer;

	@FXML
	private ImageView lockImageView;

	@FXML
	private Label createAccountLabel;

	@FXML
	private JFXTextField idTextField;

	@FXML
	private JFXPasswordField passwordTextField;

	@FXML
	private JFXButton loginButton;

	private Stage parentStage;
	public static Employees member; // Alon 12.12.20

	public MemberLoginController(Stage parentStage) {
		this.parentStage = parentStage;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

	}

	@FXML
	private void recoverPassword() {
		try {
			Stage newStage = new Stage();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("RecoverPassword.fxml"));
			loader.load();
			Parent p = loader.getRoot();
			/* Block parent stage until child stage closes */
			newStage.initModality(Modality.WINDOW_MODAL);
			newStage.initOwner((Stage) loginButton.getScene().getWindow());
			newStage.setTitle("GoNature8 - Recover Password");
			newStage.getIcons().add(new Image(GoNatureFinals.APP_ICON));
			newStage.setScene(new Scene(p));
			newStage.setResizable(false);
			newStage.show();

		} catch (Exception e) {
			System.out.println("faild to load form");
			e.printStackTrace();
		}
	}

	@FXML
	private void loginButton() {
		/* Alon 12.12.20 */
		String id = idTextField.getText();
		String pass = passwordTextField.getText();
		if (id.isEmpty() || pass.isEmpty())
			new CustomAlerts(AlertType.ERROR, "Input Error", "Input Error", "Please fill all the fields").showAndWait();
		else {
			int res = AutenticationControl.memberLoginHandler(id, pass);
			if (res == 0) {
				member = (Employees) ChatClient.responseFromServer.getResultSet().get(0);
				String member_type = member.getRole().getStr();
				String fxmlName = member_type.replaceAll("\\s+", ""); // Trimming all white spaces.
				switch (fxmlName) {
				case "DepartmentManager":
					switchScene("DepartmentManagerScreen.fxml", "GoNature8 - Department Manager", member_type);
					break;
				case "ParkManager":
					switchScene("ParkManager.fxml", "GoNature8 - Park Manager", member_type);
					break;
				case "Entrance":
					switchScene("EntranceWorker.fxml", "GoNature8 - Entrance Worker", member_type);
					break;
				case "Service":
					switchScene("ServiceWorker.fxml", "GoNature8 - Service Worker", member_type);
					break;
				default:
					break;
				}
			} else if (res == 1) {
				new CustomAlerts(AlertType.ERROR, "Login Error", "Login Error", "You are already connected!")
						.showAndWait();
			} else {
				new CustomAlerts(AlertType.ERROR, "Login Error", "Login Error", "Incorrect id or password!")
						.showAndWait();
			}
		}

	}

	private void switchScene(String fxmlName, String title, String type) {
		try {
			Stage thisStage = getStage();
			Stage newStage = new Stage();
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlName));
			if (type.equals("Service")) {
				ServiceWorkerController controller = new ServiceWorkerController();
				controller.setStage(newStage);
				controller.setMainScreenStage(parentStage);
				loader.setController(controller);
			} else if (type.equals("Park Manager")) {
				ParkManagerController controller = new ParkManagerController();
				controller.setStage(newStage);
				controller.setMainScreenStage(parentStage);
				loader.setController(controller);
			} else if (type.equals("Entrance")) {
				EntranceWorkerController controller = new EntranceWorkerController();
				controller.setStage(newStage);
				controller.setMainScreenStage(parentStage);
				loader.setController(controller);
			} else if (type.equals("Department Manager")) {
				DepartmentManagerController controller = new DepartmentManagerController();
				controller.setStage(newStage);
				controller.setMainScreenStage(parentStage);
				loader.setController(controller);
			}
			loader.load();
			Parent p = loader.getRoot();
			newStage.setTitle(title);
			newStage.setScene(new Scene(p));
			newStage.setResizable(false);
			newStage.getIcons().add(new Image(GoNatureFinals.APP_ICON));
			newStage.show();
			thisStage.close();
			parentStage.hide();
		} catch (Exception e) {
			System.out.println("faild to load form");
			e.printStackTrace();
		}
	}

	private Stage getStage() {
		return (Stage) loginButton.getScene().getWindow();
	}

}