package gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextArea;
import com.sun.javafx.scene.control.skin.DatePickerContent;
import com.sun.javafx.scene.control.skin.DatePickerSkin;
import Controllers.ParkControl;
import Controllers.ReportsControl;
import alerts.CustomAlerts;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import logic.GoNatureFinals;
import logic.Report;

@SuppressWarnings("restriction")
/**
 * This class is the GUI controller of UsageReport.fxml
 * It handles all the JavaFx nodes events.
 * 
 * In the screen we can see the dates that the park was full or not.
 *
 */
public class UsageReportController implements Initializable {

    @FXML
    private StackPane rootPane;
    
	@FXML
	private Label headerLabel;

	@FXML
	private JFXDatePicker datePicker;

	@FXML
	private JFXButton sendToManagerBtn;

	@FXML
	private JFXTextArea commentTextArea;

	@FXML
	private Label monthLabel;

	@FXML
	private AnchorPane root;

	@FXML
	private ProgressIndicator pb;

	private int parkID;
	private int monthNumber;
	private int year = Calendar.getInstance().get(Calendar.YEAR);
	private String comment;
	private boolean isDepManager = false;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		init();
	}

	private void init() {
		Locale.setDefault(Locale.ENGLISH);
		initLabels();
		showDatePicker();

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

	private void showDatePicker() {
		LocalDate date = LocalDate.of(year, monthNumber, 1);

		DatePickerSkin datePickerSkin = new DatePickerSkin(new DatePicker(date));
		Node popupContent = datePickerSkin.getPopupContent();

		popupContent.applyCss();
		popupContent.lookup(".month-year-pane").setVisible(false);

		EventHandler<MouseEvent> handler = MouseEvent::consume;

		popupContent.addEventFilter(MouseEvent.MOUSE_CLICKED, handler);
		popupContent.addEventFilter(MouseEvent.MOUSE_PRESSED, handler);
		popupContent.addEventFilter(MouseEvent.MOUSE_RELEASED, handler);
		root.getChildren().add(popupContent);
		AnchorPane.setTopAnchor(popupContent, 200.0);
		AnchorPane.setRightAnchor(popupContent, 182.0);
		AnchorPane.setLeftAnchor(popupContent, 182.0);
		AnchorPane.setBottomAnchor(popupContent, 410.0);

		DatePickerContent pop = (DatePickerContent) datePickerSkin.getPopupContent();
		List<DateCell> dateCells = getAllDateCells(pop);

		Task<Boolean> task = new Task<Boolean>() {
			@Override
			protected Boolean call() throws Exception {
				setDateCellColor(dateCells);
				return true;
			}
		};
		pb.setVisible(true);
		root.setDisable(true);
		new Thread(task).start();

		task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent t) {
				pb.setVisible(false);
				root.setDisable(false);
			}
		});

	}

	private void setDateCellColor(List<DateCell> dateCells) {
		for (DateCell cell : dateCells) {
			cell.setEditable(false);

			ArrayList<String> result = isParkIsFullAtDate(year, monthNumber, Integer.parseInt(cell.getText()));
			if (result.get(0).equals("notFull"))
				cell.setStyle("-fx-background-color: #8cf55f;");
			else {
				cell.setStyle("-fx-background-color: #ffc0cb;");
				String comment = "";
				SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
				Date prevTime = null;
				try {
					prevTime = parser.parse("07:00");
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
				for (String str : result) {
					String time = str.split(" ")[6];
					String minusHour = String.valueOf(Integer.parseInt(time.split(":")[0]) - 1);
					String min = time.split(":")[1];
					String timeMinusHour = minusHour + ":" + min;
					try {
						Date timePlusHourTime = parser.parse(timeMinusHour);
						if (timePlusHourTime.after(prevTime)) {
							comment += str + "\n";
							prevTime = parser.parse(time);
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				cell.setTooltip(new Tooltip(comment));
			}
		}
	}

	private ArrayList<String> isParkIsFullAtDate(int year, int monthNumber, int day) {
		int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
		if (monthNumber > currentMonth)
			year--;
		
		String date = year + "-" + monthNumber + "-" + day;
		return ParkControl.isParkIsFullAtDate(date, String.valueOf(parkID));
	}
	
	private void saveReportAsPdf() {
		File directory = new File(System.getProperty("user.home") + "/Desktop/reports/");
	    if (! directory.exists()){
	        directory.mkdir();
	    }
	    
		WritableImage nodeshot = rootPane.snapshot(new SnapshotParameters(), null);
		String fileName = "Usage Report - park " + parkID + " - month number " + monthNumber + ".pdf";
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

	/**
	 * Handle 'sendToManagerBtn' button
	 * On click it sends a request to the server to add the report to the database
	 */
	@FXML
	private void sendToManagerBtn() {

		Report r = new Report(0, "Usage", parkID, monthNumber, commentTextArea.getText());
		if (ReportsControl.addReport(r)) {
			new CustomAlerts(AlertType.INFORMATION, "Success", "Success",
					"Usage report has been sent to department manager.").showAndWait();
			getStage().close();
		} else {
			new CustomAlerts(AlertType.ERROR, "Faild", "Faild", "Something went wrong. Please try again late.")
					.showAndWait();
		}

	}

	private void initLabels() {
		monthLabel.setText(GoNatureFinals.MONTHS[monthNumber]); // Set the name of the month
		commentTextArea.setText(comment);
	}

	private static List<DateCell> getAllDateCells(DatePickerContent content) {
		List<DateCell> result = new ArrayList<>();
		int rowNum = 0;
		int flag = 0;
		for (Node n : content.getChildren()) {
			if (n instanceof GridPane) {
				GridPane grid = (GridPane) n;
				for (Node gChild : grid.getChildren()) {
					if (rowNum < 7 || flag == 0) {
						if (((DateCell) gChild).getText().equals("1")) {
							flag = 1;
						}
						rowNum++;
					}
					if (((DateCell) gChild).getText().equals("1") && rowNum >= 25) {
						break;
					}
					if (gChild instanceof DateCell && flag == 1 && rowNum >= 7) {
						result.add((DateCell) gChild);
						rowNum++;
					}
				}
			}
		}

		return result;
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
