package gui;

import java.net.URL;
import java.util.ResourceBundle;
import com.jfoenix.controls.JFXButton;
import Controllers.AutenticationControl;
import client.ClientUI;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import util.FxmlUtil;

/**
 * This Class is the GUI controller of TravelerScreen.fxml
 * It handles all the JavaFx nodes events.
 * 
 * This is the main screen of the traveler
 *
 */
public class TravelerScreenController implements Initializable {

	@FXML
	private BorderPane borderPane;

	@FXML
	private AnchorPane topPane;

	@FXML
	private Label travelerLabel;

	@FXML
	private VBox vbox;

	@FXML
	private JFXButton profileTravelerButton;

	@FXML
	private JFXButton orderTravelerButton;

	@FXML
	private JFXButton viewOrdersButton;

	@FXML
	private JFXButton viewMessagesButton;

	@FXML
	private Pane midPane;

	private Stage stage;
	private Stage mainScreenStage;

	FxmlUtil loader = new FxmlUtil();

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		init();

	}

	private void init() {
		getStage().setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent we) {
				AutenticationControl.userLogout(
						TravelerLoginController.traveler != null ? TravelerLoginController.traveler.getTravelerId()
								: TravelerLoginController.subscriber.getTravelerId());
				mainScreenStage.close();
				ClientUI.chat.getClient().quit();
			}
		});
		loadProfile();
	}

	private Stage getStage() {
		return stage;
	}

	/**
	 * Setter for the class variable stage
	 * 
	 * @param stage The current stage
	 */
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	/**
	 * Setter for the class variable mainScreenStage
	 * 
	 * @param stage The main stage
	 */
	public void setMainScreenStage(Stage stage) {
		this.mainScreenStage = stage;
	}

	@FXML
	private void loadOrderVisit() {
		Pane view = loader.loadPaneToBorderPaneWithController("/gui/OrderVisit.fxml", "orderVisit");
		borderPane.setCenter(view);
	}

	@FXML
	private void loadProfile() {
		loader.setWorker(false);
		Pane view = loader.loadPaneToBorderPaneWithController("/gui/Profile.fxml", "profile");
		borderPane.setCenter(view);
	}

	@FXML
	private void loadOrders() {
		Pane view = loader.loadPaneToBorderPaneWithController("/gui/TravelerViewOrders.fxml", "travelerOrders");
		borderPane.setCenter(view);
	}

	@FXML
	private void loadMessages() {
		Pane view = loader.loadPaneToBorderPaneWithController("/gui/ViewMessages.fxml", "travelerMessages");
		borderPane.setCenter(view);
	}

	@FXML
	private void logOut() {
		AutenticationControl
				.userLogout(TravelerLoginController.traveler != null ? TravelerLoginController.traveler.getTravelerId()
						: TravelerLoginController.subscriber.getTravelerId());
		getStage().close();
		mainScreenStage.show();
	}

}
