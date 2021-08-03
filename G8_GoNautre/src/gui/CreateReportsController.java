package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextArea;
import alerts.CustomAlerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import logic.GoNatureFinals;

/**
 * This Class is the GUI controller of CreateReport.fxml
 * It handles all the JavaFx nodes events.
 * 
 * In this screen the park manager create the reports and can send them to
 * the department manager
 * 
 */
public class CreateReportsController implements Initializable {

	@FXML
	private AnchorPane createReportsRootPane;

	@FXML
	private Label headerLabel;

	@FXML
	private TitledPane monthTP;

	@FXML
	private AnchorPane chooseMonthAP;

	@FXML
	private JFXComboBox<String> monthCB;

	@FXML
	private TitledPane reportTP;

	@FXML
	private AnchorPane chooseReportAP;

	@FXML
	private JFXRadioButton totalVisitorsRB;

	@FXML
	private JFXRadioButton useageRB;

	@FXML
	private JFXRadioButton IncomeRB;

	@FXML
	private TitledPane commentTP;

	@FXML
	private AnchorPane addCommentAP;

	@FXML
	private JFXTextArea commentTextArea;

	@FXML
	private JFXButton createButton;

	@FXML
	private Accordion accordion;

	private String fxmlName;
	private String screenTitle;
	protected static int month;
	protected ArrayList<String> newReportList;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		init();
	}

	private void init() {
		accordion.setExpandedPane(monthTP);
		initComboBox();
		/* Default settings */
		totalVisitorsRB.setSelected(true);
		this.fxmlName = "/gui/TotalVisitorsReport.fxml";
		screenTitle = "Total Visitors Report";
	}

	@FXML
	private void createReportButton() {
		if (monthCB.getSelectionModel().getSelectedIndex() == 0) {
			new CustomAlerts(AlertType.ERROR, "Error", "Month Error", "Plesae choose month.").showAndWait();
		} else {
			switchScenceWithController();
		}
	}

	@FXML
	private void turnON_totalVisitorsRB() {
		totalVisitorsRB.setSelected(true);
		useageRB.setSelected(false);
		IncomeRB.setSelected(false);
		this.fxmlName = "/gui/TotalVisitorsReport.fxml";
		screenTitle = "Total Visitors Report";
	}

	@FXML
	private void turnON_useageRB() {
		useageRB.setSelected(true);
		totalVisitorsRB.setSelected(false);
		IncomeRB.setSelected(false);
		this.fxmlName = "/gui/UsageReport.fxml";
		screenTitle = "Usage Report";
	}

	@FXML
	private void turnON_IncomeRB() {
		IncomeRB.setSelected(true);
		totalVisitorsRB.setSelected(false);
		useageRB.setSelected(false);
		this.fxmlName = "/gui/IncomeReport.fxml";
		screenTitle = "Income Report";
	}

	private void initComboBox() {
		monthCB.getItems().addAll(GoNatureFinals.MONTHS);
		monthCB.getSelectionModel().select(0);
	}

	private Stage getStage() {
		return (Stage) monthCB.getScene().getWindow();
	}

	private void switchScenceWithController() {
		try {
			Stage thisStage = getStage();
			Stage newStage = new Stage();
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlName));
			if (totalVisitorsRB.isSelected()) {
				TotalVisitorsReportController controller = new TotalVisitorsReportController();
				controller.setComment(commentTextArea.getText());
				controller.setMonthNumber(monthCB.getSelectionModel().getSelectedIndex());
				controller.setParkID(MemberLoginController.member.getParkId());
				loader.setController(controller);
			} else if (useageRB.isSelected()) {
				UsageReportController controller = new UsageReportController();
				controller.setComment(commentTextArea.getText());
				controller.setMonthNumber(monthCB.getSelectionModel().getSelectedIndex());
				controller.setParkID(MemberLoginController.member.getParkId());
				loader.setController(controller);

			} else if (IncomeRB.isSelected()) {
				IncomeReportController controller = new IncomeReportController();
				controller.setComment(commentTextArea.getText());
				controller.setMonthNumber(monthCB.getSelectionModel().getSelectedIndex());
				controller.setParkID(MemberLoginController.member.getParkId());
				loader.setController(controller);
			}
			loader.load();
			Parent p = loader.getRoot();

			/* Block parent stage until child stage closes */
			newStage.initModality(Modality.WINDOW_MODAL);
			newStage.initOwner(thisStage);
			newStage.getIcons().add(new Image(GoNatureFinals.APP_ICON));
			newStage.setTitle(screenTitle);
			newStage.setScene(new Scene(p));
			newStage.setResizable(false);
			newStage.show();
		} catch (IOException e) {
			System.out.println("faild to load form");
			e.printStackTrace();
		}

	}

}