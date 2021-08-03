package gui;

import java.net.URL;
import java.util.ResourceBundle;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import Controllers.AutenticationControl;
import alerts.CustomAlerts;
import client.ChatClient;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import logic.GoNatureFinals;
import logic.Subscriber;
import logic.Traveler;

/**
 * This Class is the GUI controller of TravelerLogin.fxml
 * It handles all the JavaFx nodes events.
 * 
 * This is the login screen for the travelers
 *
 */
public class TravelerLoginController implements Initializable {

	@FXML
	private AnchorPane loginContainer;

	@FXML
	private Rectangle rectangle;

	@FXML
	private Label forgotPasswordLabel1;

	@FXML
	private JFXTextField idTextField;

	@FXML
	private JFXTextField subscriberIDTextField;

	@FXML
	private JFXButton loginButton;

	private Stage parentStage;
	public static Traveler traveler = null;
	public static Subscriber subscriber = null;

	public TravelerLoginController(Stage parentStage) {
		this.parentStage = parentStage;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		init();

	}
	
	private void init() {
		TravelerLoginController.subscriber = null;
		TravelerLoginController.traveler = null;
		loginButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				loginButton();
			}
		});
	}

	private void loginButton() {
		String id = idTextField.getText();
		String subId = subscriberIDTextField.getText();
		
		if (id.isEmpty() && subId.isEmpty())
			new CustomAlerts(AlertType.ERROR, "Input Error", "Input Error", "Please fill one of the fields")
					.showAndWait();
		else if (!id.isEmpty() && !subId.isEmpty())
			new CustomAlerts(AlertType.ERROR, "Input Error", "Input Error", "Please fill only one of the fields")
					.showAndWait();
		else if (!id.isEmpty()) {
			int res = AutenticationControl.loginById(id);
			if (res == 0) {
				traveler = (Traveler) ChatClient.responseFromServer.getResultSet().get(0);
				switchScene();
			} else if (res == 2)
				new CustomAlerts(AlertType.ERROR, "Login Error", "Login Error",
						"This Id has no orders yet.\nPlease make an order before login").showAndWait();
			else
				new CustomAlerts(AlertType.ERROR, "Login Error", "Login Error", "You are already connected")
						.showAndWait();
		} else {
			int res = AutenticationControl.loginBySubId(subId);
			if (res == 0) {
				subscriber = (Subscriber) ChatClient.responseFromServer.getResultSet().get(0);
				switchScene();
			} else if (res == 2)
				new CustomAlerts(AlertType.ERROR, "Login Error", "Login Error", "There is no such subscriber id")
						.showAndWait();
			else
				new CustomAlerts(AlertType.ERROR, "Login Error", "Login Error", "You are already connected")
						.showAndWait();

		}
	}

	private void switchScene() {
		try {
			Stage thisStage = getStage();
			Stage newStage = new Stage();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("TravelerScreen.fxml"));

			TravelerScreenController controller = new TravelerScreenController();
			loader.setController(controller);
			controller.setStage(newStage);
			controller.setMainScreenStage(parentStage);
			loader.load();
			Parent p = loader.getRoot();
			newStage.setTitle("user screen");
			newStage.setScene(new Scene(p));
			newStage.getIcons().add(new Image(GoNatureFinals.APP_ICON));
			newStage.setResizable(false);
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
