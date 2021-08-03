package gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import com.jfoenix.controls.JFXComboBox;

import Controllers.ReportsControl;
import alerts.CustomAlerts;
import client.ChatClient;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import logic.GoNatureFinals;
import logic.VisitReport;

/**
 * Gets month that picked from previous page.
 * Loads all visitors stay time and entrance time into a line chart.
 */
public class VisitsReportController implements Initializable {

	@FXML
	private AnchorPane rootPane;

	@FXML
	private Label headerLabel;

	@FXML
	private LineChart<Number, Number> stayTime_chart;

	@FXML
	private NumberAxis stayX2;

	@FXML
	private NumberAxis stayY;

	@FXML
	public LineChart<Number, Number> entranceTime_chart;

	@FXML
	private NumberAxis enterX2;

	@FXML
	private NumberAxis enterY;

	@FXML
	private Label lblMonth;

	@FXML
	private JFXComboBox<String> comboBox;

	@FXML
	private JFXComboBox<String> dataComboBox;

	private int monthNumber; // the month number
	
	private XYChart.Series<Number, Number> series3; // Refactor
	private XYChart.Series<Number, Number> series2; // Refactor
	private XYChart.Series<Number, Number> series1; // Refactor
	private XYChart.Series<Number, Number> series4; // Refactor
	private XYChart.Series<Number, Number> series5; // Refactor
	private XYChart.Series<Number, Number> series6; // Refactor

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		init();
	}

	private void init() {
		lblMonth.setText(GoNatureFinals.MONTHS[monthNumber]); // set the name of the month
		initGraphs();
		Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.3), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				initComboBox();

				entranceTime_chart.getData().clear();
				stayTime_chart.getData().clear();
				loadEntranceSolosData(comboBox.getSelectionModel().getSelectedItem());
				loadEntranceSubscribersData(comboBox.getSelectionModel().getSelectedItem());
				loadEntranceGroupData(comboBox.getSelectionModel().getSelectedItem());
				loadStayTimeSolosData(comboBox.getSelectionModel().getSelectedItem());
				loadStayTimeSubscribersData(comboBox.getSelectionModel().getSelectedItem());
				loadStayTimeGroupData(comboBox.getSelectionModel().getSelectedItem());
				entranceTime_chart.getData().add(series1);
				entranceTime_chart.getData().add(series3);
				entranceTime_chart.getData().add(series5);

				stayTime_chart.getData().add(series2);
				stayTime_chart.getData().add(series4);
				stayTime_chart.getData().add(series6);
				setToolTip();
			}

		}));
		timeline.setCycleCount(1);
		timeline.play();
	}

	/**
	 * Setter for class variable monthNumber
	 * 
	 * @param month The current month
	 */
	public void setMonthNumber(int month) {
		this.monthNumber = month;
	}

	@SuppressWarnings("unused")
	private void loadEntranceGroupData(String option) {
		ArrayList<VisitReport> rep3 = new ArrayList<VisitReport>();
		if (!option.equals("Show whole month"))
			rep3 = ReportsControl.reportsManager.countGroupsEnterTimeWithDays(monthNumber, option);
		else
			rep3 = ReportsControl.reportsManager.countGroupsEnterTime(monthNumber);
		// rep3 = (ArrayList<VisitReport>) ChatClient.responseFromServer.getResultSet(); // Refactor
		series5 = new Series<Number, Number>();
		double hour, min, time;
		int maxNumOfVisitors = 0, sum;
		for (int i = 0; i < rep3.size(); i++) {
			sum = rep3.get(i).getSum();
			if (maxNumOfVisitors < sum) {
				maxNumOfVisitors = sum;
			}
			hour = Double.parseDouble(rep3.get(i).getData().substring(0, 2));
			min = Double.parseDouble(rep3.get(i).getData().substring(3, 5)) / 60;
			time = hour + min;
			series5.getData().add(new Data<Number, Number>(time, sum));
		}

		series5.setName("Groups");

		maxNumOfVisitors++;
		if (maxNumOfVisitors % 2 != 0)
			maxNumOfVisitors++;
	}

	@SuppressWarnings("unchecked")
	private void loadStayTimeGroupData(String option) {
		ArrayList<VisitReport> rep3 = new ArrayList<VisitReport>();

		double hour, min, time;
		int maxNumOfVisitors = 0, sum;
		rep3 = new ArrayList<VisitReport>();

		if (!option.equals("Show whole month"))
			ReportsControl.countGroupsVisitTimeWithDay(monthNumber, option);
		else
			ReportsControl.countGroupsVisitTime(monthNumber);
		rep3 = (ArrayList<VisitReport>) ChatClient.responseFromServer.getResultSet();
		double totalNumOfVisitors = 0;
		/* Sum total visitors at this date */
		for (int i = 0; i < rep3.size(); i++)
			totalNumOfVisitors += rep3.get(i).getSum();

		series6 = new Series<Number, Number>();
		hour = 0;
		min = 0;
		time = 0;
		maxNumOfVisitors = 0;
		sum = 0;
		for (int i = 0; i < rep3.size(); i++) {
			sum = rep3.get(i).getSum();
			if (maxNumOfVisitors < sum) {
				maxNumOfVisitors = sum;
			}
			hour = Double.parseDouble(rep3.get(i).getData().substring(0, 2));
			min = Double.parseDouble(rep3.get(i).getData().substring(3, 5)) / 60;
			time = hour + min;

			series6.getData().add(new Data<Number, Number>(time, sum / totalNumOfVisitors * 100));
		}

		series6.setName("Groups");
	}

	private void loadEntranceSubscribersData(String option) {
		ArrayList<VisitReport> rep2 = new ArrayList<VisitReport>();
		if (!option.equals("Show whole month"))
			rep2 = ReportsControl.reportsManager.countSubsEnterTimeWithDays(monthNumber, option);
		else
			rep2 = ReportsControl.reportsManager.countSubsEnterTime(monthNumber);
		// rep2 = (ArrayList<VisitReport>) ChatClient.responseFromServer.getResultSet(); // Refactor

		series3 = new Series<Number, Number>();
		double hour, min, time;
		int maxNumOfVisitors = 0, sum;
		for (int i = 0; i < rep2.size(); i++) {
			sum = rep2.get(i).getSum();
			if (maxNumOfVisitors < sum) {
				maxNumOfVisitors = sum;
			}
			hour = Double.parseDouble(rep2.get(i).getData().substring(0, 2));
			min = Double.parseDouble(rep2.get(i).getData().substring(3, 5)) / 60;
			time = hour + min;
			series3.getData().add(new Data<Number, Number>(time, sum));
		}

		series3.setName("Subscribers");
	}

	@SuppressWarnings("unchecked")
	private void loadStayTimeSubscribersData(String option) {
		ArrayList<VisitReport> rep2 = new ArrayList<VisitReport>();
		double hour, min, time;
		int maxNumOfVisitors = 0, sum;
		rep2 = new ArrayList<VisitReport>();
		if (!option.equals("Show whole month"))
			ReportsControl.countSubsVisitTimeWithDay(monthNumber, option);
		else
			ReportsControl.countSubsVisitTime(monthNumber);
		rep2 = (ArrayList<VisitReport>) ChatClient.responseFromServer.getResultSet();

		double totalNumOfVisitors = 0;
		/* Sum total visitors at this date */
		for (int i = 0; i < rep2.size(); i++)
			totalNumOfVisitors += rep2.get(i).getSum();

		series4 = new Series<Number, Number>();
		hour = 0;
		min = 0;
		time = 0;
		maxNumOfVisitors = 0;
		sum = 0;
		for (int i = 0; i < rep2.size(); i++) {
			sum = rep2.get(i).getSum();
			if (maxNumOfVisitors < sum) {
				maxNumOfVisitors = sum;
			}
			hour = Double.parseDouble(rep2.get(i).getData().substring(0, 2));
			min = Double.parseDouble(rep2.get(i).getData().substring(3, 5)) / 60;
			time = hour + min;
			series4.getData().add(new Data<Number, Number>(time, sum / totalNumOfVisitors * 100));
		}

		series4.setName("Subscribers");
	}

	private void loadEntranceSolosData(String option) {
		ArrayList<VisitReport> rep = new ArrayList<VisitReport>();
		if (!option.equals("Show whole month"))
			rep = ReportsControl.reportsManager.countSolosEnterTimeWithDays(monthNumber, option);
		else
			rep = ReportsControl.reportsManager.countSolosEnterTime(monthNumber);
		// rep = (ArrayList<VisitReport>) ChatClient.responseFromServer.getResultSet(); // Refactor
		series1 = new Series<Number, Number>();

		double hour, min, time;
		int maxNumOfVisitors = 0, sum;
		for (int i = 0; i < rep.size(); i++) {
			sum = rep.get(i).getSum();
			if (maxNumOfVisitors < sum) {
				maxNumOfVisitors = sum;
			}
			hour = Double.parseDouble(rep.get(i).getData().substring(0, 2));
			min = Double.parseDouble(rep.get(i).getData().substring(3, 5)) / 60;
			time = hour + min;
			series1.getData().add(new Data<Number, Number>(time, sum));
		}
		series1.setName("Solos      ");
	}

	@SuppressWarnings("unchecked")
	private void loadStayTimeSolosData(String option) {
		ArrayList<VisitReport> rep = new ArrayList<VisitReport>();

		double hour, min, time;
		int maxNumOfVisitors = 0, sum;
		if (!option.equals("Show whole month"))
			ReportsControl.countSolosVisitTimeWithDay(monthNumber, option);
		else
			ReportsControl.countSolosVisitTime(monthNumber);
		rep = new ArrayList<VisitReport>();
		rep = (ArrayList<VisitReport>) ChatClient.responseFromServer.getResultSet();

		double totalNumOfVisitors = 0;
		/* Sum total visitors at this date */
		for (int i = 0; i < rep.size(); i++)
			totalNumOfVisitors += rep.get(i).getSum();

		series2 = new Series<Number, Number>();
		hour = 0;
		min = 0;
		time = 0;
		maxNumOfVisitors = 0;
		sum = 0;
		for (int i = 0; i < rep.size(); i++) {
			sum = rep.get(i).getSum();
			if (maxNumOfVisitors < sum) {
				maxNumOfVisitors = sum;
			}
			hour = Double.parseDouble(rep.get(i).getData().substring(0, 2));
			min = Double.parseDouble(rep.get(i).getData().substring(3, 5)) / 60;
			time = hour + min;
			series2.getData().add(new Data<Number, Number>(time, sum / totalNumOfVisitors * 100));
		}

		series2.setName("Solos      ");


	}

	private void initGraphs() {
		enterX2.setAutoRanging(false);
		enterX2.setLowerBound(7.5);
		enterX2.setUpperBound(19);
		enterX2.setMinorTickVisible(false);
		enterX2.setTickUnit(0.5);

		enterY.setAutoRanging(true);
		enterY.setLowerBound(0);
		enterY.setMinorTickVisible(false);

		stayX2.setAutoRanging(false);
		stayX2.setLowerBound(0.0);
		stayX2.setUpperBound(10.0);
		stayX2.setMinorTickVisible(false);
		stayX2.setTickUnit(0.5);

		stayY.setAutoRanging(true);
		stayY.setLowerBound(0);
		stayY.setTickUnit(1);
		stayY.setMinorTickVisible(false);

	}

	private void setToolTip() {
		for (XYChart.Series<Number, Number> s : stayTime_chart.getData()) {
			for (XYChart.Data<Number, Number> d : s.getData()) {

				Tooltip.install(d.getNode(), new Tooltip(d.getYValue() + "%"));

				d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
				d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
			}
		}
		for (XYChart.Series<Number, Number> s : entranceTime_chart.getData()) {
			for (XYChart.Data<Number, Number> d : s.getData()) {

				Tooltip.install(d.getNode(), new Tooltip(d.getYValue() + " Visitors"));

				d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
				d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
			}
		}
	}

	private void initComboBox() {
		int days = findNumOfDays();
		ObservableList<String> month_days = FXCollections.observableArrayList();
		month_days.add("Show whole month");
		for (int i = 1; i <= days; i++) {
			month_days.add(String.valueOf(i));
		}
		comboBox.getItems().addAll(month_days);
		comboBox.getSelectionModel().select(0);
		comboBox.valueProperty().addListener((obs, oldItem, newItem) -> {
			if (newItem == null) {
			} else {
				if (dataComboBox.getSelectionModel().getSelectedItem().equals("Show All")) {
					entranceTime_chart.getData().clear();
					stayTime_chart.getData().clear();
					loadEntranceSolosData(comboBox.getSelectionModel().getSelectedItem());
					loadEntranceSubscribersData(comboBox.getSelectionModel().getSelectedItem());
					loadEntranceGroupData(comboBox.getSelectionModel().getSelectedItem());
					loadStayTimeSolosData(comboBox.getSelectionModel().getSelectedItem());
					loadStayTimeSubscribersData(comboBox.getSelectionModel().getSelectedItem());
					loadStayTimeGroupData(comboBox.getSelectionModel().getSelectedItem());
					entranceTime_chart.getData().add(series1);
					entranceTime_chart.getData().add(series3);
					entranceTime_chart.getData().add(series5);

					stayTime_chart.getData().add(series2);
					stayTime_chart.getData().add(series4);
					stayTime_chart.getData().add(series6);
					setToolTip();
				} else if (dataComboBox.getSelectionModel().getSelectedItem().equals("Solo Visits")) {
					entranceTime_chart.getData().clear();
					stayTime_chart.getData().clear();
					loadEntranceSolosData(comboBox.getSelectionModel().getSelectedItem());
					loadStayTimeSolosData(comboBox.getSelectionModel().getSelectedItem());
					entranceTime_chart.getData().add(series1);
					stayTime_chart.getData().add(series2);
					setToolTip();
				} else if (dataComboBox.getSelectionModel().getSelectedItem().equals("Subscribers Visits")) {
					entranceTime_chart.getData().clear();
					stayTime_chart.getData().clear();
					loadEntranceSubscribersData(comboBox.getSelectionModel().getSelectedItem());
					loadStayTimeSubscribersData(comboBox.getSelectionModel().getSelectedItem());
					entranceTime_chart.getData().add(series3);
					stayTime_chart.getData().add(series4);
					setToolTip();
				} else if (dataComboBox.getSelectionModel().getSelectedItem().equals("Group Visits")) {
					entranceTime_chart.getData().clear();
					stayTime_chart.getData().clear();
					loadEntranceGroupData(comboBox.getSelectionModel().getSelectedItem());
					loadStayTimeGroupData(comboBox.getSelectionModel().getSelectedItem());
					entranceTime_chart.getData().add(series5);
					stayTime_chart.getData().add(series6);
					setToolTip();
				}
			}
		});

		dataComboBox.getItems().addAll("Show All", "Solo Visits", "Subscribers Visits", "Group Visits");
		dataComboBox.getSelectionModel().select(0);
		dataComboBox.valueProperty().addListener((obs, oldItem, newItem) -> {
			if (newItem == null) {
			} else {
				if (newItem.equals("Show All")) {
					entranceTime_chart.getData().clear();
					stayTime_chart.getData().clear();
					loadEntranceSolosData(comboBox.getSelectionModel().getSelectedItem());
					loadEntranceSubscribersData(comboBox.getSelectionModel().getSelectedItem());
					loadEntranceGroupData(comboBox.getSelectionModel().getSelectedItem());
					loadStayTimeSolosData(comboBox.getSelectionModel().getSelectedItem());
					loadStayTimeSubscribersData(comboBox.getSelectionModel().getSelectedItem());
					loadStayTimeGroupData(comboBox.getSelectionModel().getSelectedItem());
					entranceTime_chart.getData().add(series1);
					entranceTime_chart.getData().add(series3);
					entranceTime_chart.getData().add(series5);

					stayTime_chart.getData().add(series2);
					stayTime_chart.getData().add(series4);
					stayTime_chart.getData().add(series6);
					setToolTip();
				} else if (newItem.equals("Solo Visits")) {
					entranceTime_chart.getData().clear();
					stayTime_chart.getData().clear();
					loadEntranceSolosData(comboBox.getSelectionModel().getSelectedItem());
					loadStayTimeSolosData(comboBox.getSelectionModel().getSelectedItem());
					entranceTime_chart.getData().add(series1);
					stayTime_chart.getData().add(series2);
					setToolTip();
				} else if (newItem.equals("Subscribers Visits")) {
					entranceTime_chart.getData().clear();
					stayTime_chart.getData().clear();
					loadEntranceSubscribersData(comboBox.getSelectionModel().getSelectedItem());
					loadStayTimeSubscribersData(comboBox.getSelectionModel().getSelectedItem());
					entranceTime_chart.getData().add(series3);
					stayTime_chart.getData().add(series4);
					setToolTip();
				} else if (newItem.equals("Group Visits")) {
					entranceTime_chart.getData().clear();
					stayTime_chart.getData().clear();
					loadEntranceGroupData(comboBox.getSelectionModel().getSelectedItem());
					loadStayTimeGroupData(comboBox.getSelectionModel().getSelectedItem());
					entranceTime_chart.getData().add(series5);
					stayTime_chart.getData().add(series6);
					setToolTip();

				}
			}
		});

	}

	@FXML
	private void saveReportAsPdf() {
		File directory = new File(System.getProperty("user.home") + "/Desktop/reports/");
		if (!directory.exists()) {
			directory.mkdir();
		}

		WritableImage nodeshot = rootPane.snapshot(new SnapshotParameters(), null);
		String fileName = "Visits Report - month number " + monthNumber + ".pdf";
		File file = new File("test.png");

		try {
			ImageIO.write(SwingFXUtils.fromFXImage(nodeshot, null), "png", file);
		} catch (IOException e) {
			e.printStackTrace();
		}

		PDDocument doc = new PDDocument();
		PDPage page = new PDPage();
		PDImageXObject pdimage;
		PDPageContentStream content;
		try {
			pdimage = PDImageXObject.createFromFile("test.png", doc);
			content = new PDPageContentStream(doc, page);
			content.drawImage(pdimage, 50, 100, 500, 600);
			content.close();
			doc.addPage(page);
			doc.save(System.getProperty("user.home") + "/Desktop/reports/" + fileName);
			doc.close();
			file.delete();
			new CustomAlerts(AlertType.INFORMATION, "Success", "Success",
					"The report was saved in your desktop under reports folder").showAndWait();
		} catch (IOException ex) {
			System.out.println("faild to create pdf");
			ex.printStackTrace();
		}

		Stage stage = (Stage) rootPane.getScene().getWindow();
		stage.close();

	}

	private int findNumOfDays() {
		String month = null;
		if (monthNumber < 10)
			month = "0" + monthNumber;
		else
			month = String.valueOf(monthNumber);
		YearMonth ym = YearMonth.parse(Calendar.getInstance().get(Calendar.YEAR) + "-" + month);
		return ym.lengthOfMonth();
	}
}