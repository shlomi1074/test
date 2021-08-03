package gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import Controllers.ReportsControl;
import alerts.CustomAlerts;
import client.ChatClient;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import logic.GoNatureFinals;
import logic.Order;
import logic.Report;

@SuppressWarnings("unchecked")
public class TotalVisitorsReportController implements Initializable {

    @FXML
    private AnchorPane rootPane;
    
	@FXML
	private Label headerLabel;

	@FXML
	private Label monthLabel;

	@FXML
	private Label individualLabel;

	@FXML
	private Label groupsLabel;

	@FXML
	private Label subscribersLabel;

	@FXML
	private Label totalLabel;

	@FXML
	private JFXButton sendToManagerBtn;

	@FXML
	private JFXTextArea commentTextArea;

	@FXML
	private BarChart<String, Number> barChart;

	@FXML
	private CategoryAxis xAxis;

	@FXML
	private NumberAxis yAxis;

	private ArrayList<String> newReportList;
	private static ArrayList<Integer> reportList;
	private int parkID;
	private int monthNumber;
	private String comment;
	private boolean isDepManager = false;

	/* Orders distributed by order type */
	private ArrayList<Order> solosOrdersUnClean = new ArrayList<Order>();
	private ArrayList<Order> subscribesrOrdersUnClean = new ArrayList<Order>();
	private ArrayList<Order> groupsOrdersUnClean = new ArrayList<Order>();

	/*
	 * Numbers of visitors distributed by days
	 * 0 - Sunday
	 * 6 - Saturday
	 */
	private int[] daysSolosClean = new int[7];
	private int[] daysSubscribersClean = new int[7];
	private int[] daysGroupClean = new int[7];
	private int[] totalClean = new int[7];

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		init();
		getData();
		cleanData();
		
		Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.3), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				loadBarChat();
			}
		}));
		timeline.setCycleCount(1);
		timeline.play();

	}

	private void init() {
		initLabels();
		commentTextArea.setText(comment);

		if (isDepManager) {
			commentTextArea.setEditable(false);
			commentTextArea.setPromptText("Park manager comment:");
			sendToManagerBtn.setText("         Save Report Locally         ");

			sendToManagerBtn.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					saveReportAsPdf();
					getStage().close();
				}

			});
		} else {
			sendToManagerBtn.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					sendToManagerBtn();
					getStage().close();
				}
			});
		}
	}

	private void loadBarChat() {
		yAxis.setAutoRanging(false);
		yAxis.setLowerBound(0);
		yAxis.setUpperBound(Arrays.stream(totalClean).max().getAsInt() + 2);
		yAxis.setTickUnit(1);
		xAxis.setCategories(FXCollections.<String>observableArrayList(
				Arrays.asList("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")));

		barChart.setBarGap(2);

		loadDataToChart(daysSolosClean, "Solos      ");
		loadDataToChart(daysSubscribersClean, "Subscribers");
		loadDataToChart(daysGroupClean, "Groups     ");
		loadDataToChart(totalClean, "Total      ");
		setToolTip();
	}

	private void loadDataToChart(int[] data, String name) {
		XYChart.Series<String, Number> series = new XYChart.Series<>();
		series.setName(name);
		series.getData().add(new XYChart.Data<>("Sunday", data[0]));
		series.getData().add(new XYChart.Data<>("Monday", data[1]));
		series.getData().add(new XYChart.Data<>("Tuesday", data[2]));
		series.getData().add(new XYChart.Data<>("Wednesday", data[3]));
		series.getData().add(new XYChart.Data<>("Thursday", data[4]));
		series.getData().add(new XYChart.Data<>("Friday", data[5]));
		series.getData().add(new XYChart.Data<>("Saturday", data[6]));
		barChart.getData().add(series);
	}

	private void setToolTip() {
		for (XYChart.Series<String, Number> s : barChart.getData()) {
			for (XYChart.Data<String, Number> d : s.getData()) {

				Tooltip.install(d.getNode(), new Tooltip(
						"Number of " + s.getName().trim() + " visitors in " + d.getXValue() + ": " + d.getYValue()));
				
				d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
				d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
			}
		}
	}
	
	private void saveReportAsPdf() {
		File directory = new File(System.getProperty("user.home") + "/Desktop/reports/");
	    if (! directory.exists()){
	        directory.mkdir();
	    }
	    
		WritableImage nodeshot = rootPane.snapshot(new SnapshotParameters(), null);
		String fileName = "Total Visitors Report - park " + parkID + " - month number " + monthNumber + ".pdf";
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
			doc.save(System.getProperty("user.home") + "/Desktop/reports/" +fileName);
			doc.close();
			file.delete();
			new CustomAlerts(AlertType.INFORMATION, "Success", "Success",
					"The report was saved in your desktop under reports folder").showAndWait();
		} catch (IOException ex) {
			System.out.println("faild to create pdf");
			ex.printStackTrace();
		}

	}

	@FXML
	private void sendToManagerBtn() {

		Report r = new Report(0, "Total Visitors", parkID, monthNumber, commentTextArea.getText());
		if (ReportsControl.addReport(r)) {
			new CustomAlerts(AlertType.INFORMATION, "Success", "Success",
					"Total Visitors report has been sent to department manager.").showAndWait();
		} else {
			new CustomAlerts(AlertType.ERROR, "Faild", "Faild", "Something went wrong. Please try again late.")
					.showAndWait();
		}

		getStage().close();
	}

	private void initLabels() {
		int year = Calendar.getInstance().get(Calendar.YEAR);
		monthLabel.setText(GoNatureFinals.MONTHS[monthNumber] + " - " + year);

		newReportList = new ArrayList<>();

		newReportList.add(String.valueOf(monthNumber));
		newReportList.add("Total Visitors");
		newReportList.add(String.valueOf(parkID));

		ReportsControl.showReport(newReportList);
		reportList = ChatClient.responseFromServer.getResultSet();

		individualLabel.setText(String.valueOf(reportList.get(0)));
		groupsLabel.setText(String.valueOf(reportList.get(2)));
		subscribersLabel.setText(String.valueOf(reportList.get(1)));
		totalLabel.setText(String.valueOf(reportList.get(0) + reportList.get(1) + reportList.get(2)));

	}

	private void getData() {
		solosOrdersUnClean = ReportsControl.getSolosOrdersVisitorsReport(monthNumber, parkID);
		subscribesrOrdersUnClean = ReportsControl.getSubscribersOrdersVisitorsReport(monthNumber, parkID);
		groupsOrdersUnClean = ReportsControl.getGroupOrdersVisitorsReport(monthNumber, parkID);
	}

	private void cleanData() {
		for (Order order : solosOrdersUnClean) {
			String date = order.getOrderDate();
			int dayInWeek = getNumberInWeek(date);
			daysSolosClean[dayInWeek] += order.getNumberOfParticipants();
			totalClean[dayInWeek] += order.getNumberOfParticipants();
		}

		for (Order order : subscribesrOrdersUnClean) {
			String date = order.getOrderDate();
			int dayInWeek = getNumberInWeek(date);
			daysSubscribersClean[dayInWeek] += order.getNumberOfParticipants();
			totalClean[dayInWeek] += order.getNumberOfParticipants();
		}

		for (Order order : groupsOrdersUnClean) {
			String date = order.getOrderDate();
			int dayInWeek = getNumberInWeek(date);
			daysGroupClean[dayInWeek] += order.getNumberOfParticipants();
			totalClean[dayInWeek] += order.getNumberOfParticipants();
		}
	}

	private int getNumberInWeek(String dateStr) {
		int dayOfWeek = 0;
		try {
			Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
			return (dayOfWeek - 1);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;

	}

	/**
	 * Setter for class variable monthNumber
	 * 
	 * @param month The month number
	 */
	public void setMonthNumber(int month) {
		this.monthNumber = month;
	}

	/**
	 * Setter for class variable comment
	 * 
	 * @param comment The park manager comment
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	private Stage getStage() {
		return (Stage) monthLabel.getScene().getWindow();
	}

	/**
	 * Setter for class variable parkID
	 * 
	 * @param parkID The park id
	 */
	public void setParkID(int parkID) {
		this.parkID = parkID;
	}

	/**
	 * Setter for class variable isDepManager
	 * 
	 * @param b true if opened from department manager screen
	 */
	public void setIsDepManager(boolean b) {
		this.isDepManager = b;

	}

}