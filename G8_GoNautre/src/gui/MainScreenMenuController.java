package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import logic.GoNatureFinals;

/**
 * This Class is the GUI controller of MainScreenMenu.fxml
 * It handles all the JavaFx nodes events.
 * 
 * This is the menu in the the main screen
 *
 */
public class MainScreenMenuController implements Initializable {

	@FXML
	private JFXButton orderVisit;

	@FXML
	private JFXButton travelerLogin;

	@FXML
	private JFXButton memberLogin;

	@FXML
	private JFXButton showPrices;

	@FXML
	private JFXButton contactUs;
	

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initButtons();
	}

	private void initButtons() {
		orderVisit.setFocusTraversable(false);
		travelerLogin.setFocusTraversable(false);
		memberLogin.setFocusTraversable(false);
		showPrices.setFocusTraversable(false);
		contactUs.setFocusTraversable(false);
	}

	@FXML
	private void loadContactUs() {
		switchScence("ContactUs.fxml", "GoNature8 - Contact Us");
	}

	@FXML
	private void loadMemberLogin() {
		switchScenceWithController("MemberLogin.fxml", "GoNature8 - Member Login", 2);
	}

	@FXML
	private void loadTravelerLogin() {
		switchScenceWithController("TravelerLogin.fxml", "GoNature8 - Traveler Login", 1);
	}

	@FXML
	private void loadOrderVisit() {
		switchScenceWithController("OrderVisit.fxml", "GoNature8 - Order A Visit", 3);
	}

	@FXML
	private void loadPrices() {// Alon
		switchScenceWithController("Prices.fxml", "GoNature8 - Prices", 4);
	}

	private void switchScence(String fxmlName, String screenTitle) {
		try {
			Stage thisStage = getStage();
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlName));
			loader.load();
			Parent p = loader.getRoot();
			Stage newStage = new Stage();

			/* Block parent stage until child stage closes */
			newStage.initModality(Modality.WINDOW_MODAL);
			newStage.initOwner(thisStage);

			newStage.setTitle(screenTitle);
			newStage.getIcons().add(new Image(GoNatureFinals.APP_ICON));
			newStage.setScene(new Scene(p));
			newStage.setResizable(false);
			newStage.show();
		} catch (IOException e) {
			System.out.println("faild to load form");
			e.printStackTrace();
		}

	}

	private void switchScenceWithController(String fxmlName, String screenTitle, int id) {
		try {
			Stage thisStage = getStage();
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlName));
			if (id == 1) {
				TravelerLoginController controller = new TravelerLoginController(thisStage);
				loader.setController(controller);
			} else if (id == 2) {
				MemberLoginController controller = new MemberLoginController(thisStage);
				loader.setController(controller);
			} else if (id == 3) {
				OrderVisitController controller = new OrderVisitController();
				controller.setOrderFromMain(true);
				loader.setController(controller);
			} else if (id == 4) { // Alon
				PricesController controller = new PricesController();
				loader.setController(controller);
			}
			loader.load();
			Parent p = loader.getRoot();
			Stage newStage = new Stage();

			/* Block parent stage until child stage closes */
			newStage.initModality(Modality.WINDOW_MODAL);
			newStage.initOwner(thisStage);

			newStage.setTitle(screenTitle);
			newStage.getIcons().add(new Image(GoNatureFinals.APP_ICON));
			newStage.setScene(new Scene(p));
			newStage.setResizable(false);
			newStage.show();
		} catch (IOException e) {
			System.out.println("faild to load form");
			e.printStackTrace();
		}

	}

	private Stage getStage() {
		return (Stage) orderVisit.getScene().getWindow();
	}
}
