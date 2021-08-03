package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import Controllers.ReportsControl;
import alerts.CustomAlerts;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;
import logic.GoNatureFinals;
import logic.Report;
import logic.ReportTb;

/**
 * Gets all reports from reports table
 * Load all received reports to table for department manager to view
 * Department manager can get report on cancelled orders when clicking on Cancels Report button
 * Department manager can get visit report when clicking on visit Report button
 */
public class DepartmentManagerReportsController implements Initializable {

	ObservableList<ReportTb> observable = FXCollections.observableArrayList(); /* Lior */

	@FXML
	private Label headerLabel;

	@FXML
	private TableView<ReportTb> ReportsTableView;// Lior added id to table in fxml

	@FXML
	private TableColumn<ReportTb, Integer> reportIDCol;

	@FXML
	private TableColumn<ReportTb, String> reportTypeCol;

	@FXML
	private TableColumn<ReportTb, Integer> parkIDCol;

	@FXML
	private TableColumn<ReportTb, Integer> monthCol;

	@FXML
	private TableColumn<ReportTb, String> commentCol;

	@FXML
	private JFXButton visitReportBtn;

	@FXML
	private JFXButton CancelsReportBtn;

	@FXML
	private JFXComboBox<String> monthCB;

	private String fxmlName;
	private String screenTitle;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		loadTabelView();
		initComboBox();
		initTabelView();
	}

	private void initTabelView() {
		ReportsTableView.setTooltip(new Tooltip("Double click on a row to open the report"));
		ReportsTableView.setRowFactory(tv -> {
			TableRow<ReportTb> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
					ReportTb clickedRow = row.getItem();
					String name = clickedRow.getReportType();
					int month = clickedRow.getMonth();
					int parkID = clickedRow.getParkID();
					String comment = clickedRow.getComment();
					loadReport(name, month, parkID, comment);
				}
			});
			return row;
		});

	}

	private void loadReport(String name, int month, int parkID, String comment) {
		try {
			Stage thisStage = getStage();
			FXMLLoader loader = null;
			Stage newStage = new Stage();

			if (name.equals("Usage")) {
				screenTitle = "Usage Report";
				loader = new FXMLLoader(getClass().getResource("/gui/UsageReport.fxml"));
				UsageReportController controller = new UsageReportController();
				controller.setComment(comment);
				controller.setMonthNumber(month);
				controller.setParkID(parkID);
				controller.setIsDepManager(true);
				loader.setController(controller);
			} else if (name.equals("Income")) {
				screenTitle = "Income Report";
				loader = new FXMLLoader(getClass().getResource("/gui/IncomeReport.fxml"));
				IncomeReportController controller = new IncomeReportController();
				controller.setComment(comment);
				controller.setMonthNumber(month);
				controller.setParkID(parkID);
				controller.setIsDepManager(true);
				loader.setController(controller);
			} else if (name.equals("Total Visitors")) {
				screenTitle = "Total Visitors Report";
				loader = new FXMLLoader(getClass().getResource("/gui/TotalVisitorsReport.fxml"));
				TotalVisitorsReportController controller = new TotalVisitorsReportController();
				controller.setComment(comment);
				controller.setMonthNumber(month);
				controller.setParkID(parkID);
				controller.setIsDepManager(true);
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

	@FXML
	private void visitReportBtn() {
		fxmlName = "/gui/VisitsReports.fxml";
		screenTitle = "Visits Report";
		if (monthCB.getSelectionModel().getSelectedIndex() != 0) {
			switchScenceWithController();
		} else {
			new CustomAlerts(AlertType.ERROR, "Error", "Month Error", "Plesae choose month.").showAndWait();
		}
	}

	@FXML
	private void cancelReportBtn() {
		fxmlName = "/gui/CancelsReport.fxml";
		screenTitle = "Cancels Report";
		if (monthCB.getSelectionModel().getSelectedIndex() != 0) {
			switchScenceWithController();
		} else {
			new CustomAlerts(AlertType.ERROR, "Error", "Month Error", "Plesae choose month.").showAndWait();
		}
	}

	/* Here we need to fill the table view from database */
	private void loadTabelView() {
		ArrayList<Report> reports = ReportsControl.getReports();
		ArrayList<ReportTb> tbReports = convertreportsToTeportTb(reports);
		init(tbReports);
		ReportsTableView.setItems(getReports(tbReports));
	}

	/* Here we convert reports from database To TeportTb */
	private static ArrayList<ReportTb> convertreportsToTeportTb(ArrayList<Report> reports) {
		ArrayList<ReportTb> tbReports = new ArrayList<ReportTb>();
		for (Report report : reports) {
			ReportTb tbReport = new ReportTb(report);
			tbReports.add(tbReport);
		}
		return tbReports;
	}

	private void init(ArrayList<ReportTb> tbReports) {
		reportIDCol.setCellValueFactory(new PropertyValueFactory<ReportTb, Integer>("reportID"));
		reportTypeCol.setCellValueFactory(new PropertyValueFactory<ReportTb, String>("reportType"));
		parkIDCol.setCellValueFactory(new PropertyValueFactory<ReportTb, Integer>("parkID"));
		monthCol.setCellValueFactory(new PropertyValueFactory<ReportTb, Integer>("month"));
		commentCol.setCellValueFactory(new PropertyValueFactory<ReportTb, String>("comment"));
	}

	private ObservableList<ReportTb> getReports(ArrayList<ReportTb> tbReports) {
		ReportsTableView.getItems().clear();
		for (ReportTb report : tbReports) {
			observable.add(report);
		}
		return observable;
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
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlName));
			if (screenTitle.equals("Cancels Report")) {
				CancelsReportController controller = new CancelsReportController();
				controller.setMonthNumber(monthCB.getSelectionModel().getSelectedIndex());
				loader.setController(controller);
			} else {
				VisitsReportController controller = new VisitsReportController();
				controller.setMonthNumber(monthCB.getSelectionModel().getSelectedIndex());
				loader.setController(controller);

			}
			loader.load();
			Parent p = loader.getRoot();
			Stage newStage = new Stage();

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
