package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import Controllers.CardReaderControl;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;
import logic.Order;

/**
 * This Class is the GUI controller of CardReader.fxml
 * It handles all the JavaFx nodes events.
 * 
 * In this screen we simulate travelers entering and exiting the park using the card reader.
 *
 */
public class CardReaderController implements Initializable {

	@FXML
	private Label headerLabel;

	@FXML
	private JFXButton startBtn;

	@FXML
	private TextFlow CardReaderLog;

	@FXML
	private JFXButton closeBtn;

	@FXML
	private JFXTextField idTextField;

	@FXML
	private Button enterBtn;

	@FXML
	private Label idLabel;

	@FXML
	private Button exitBtn;

	@FXML
	private JFXTextField exitTimeTextField;

	private String Msg = "Traveler ID: %s, Order ID: %s with %s visitors %s the park at %s";
	private final String EXIT = "EXIT";
	private final String ENTER = "ENTER";
	private ArrayList<String> messages = new ArrayList<>();
	private ArrayList<String> ids = new ArrayList<>();
	String exitTime = null;
	Order currentOrder = null;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		startBtn.setFocusTraversable(false);
		closeBtn.setFocusTraversable(false);
		idLabel.setText("xxxxxxxxx");
	}

	@FXML
	private void startSimulationBtn() {
		messages.clear();
		ids.clear();
		CardReaderLog.getChildren().clear();
		updateTextFlow("\n\n Simulation has started:", Color.WHITE);
		/* THE MAGIC HAPPENS HERE */
		CardReaderControl.startSimulator(this);
		updateUI();
	}

	private void updateUI() {
		Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1.5), new EventHandler<ActionEvent>() {
			private int i = 0;

			@Override
			public void handle(ActionEvent event) {
				idLabel.setText(ids.get(i));
				if (messages.get(i).contains(EXIT))
					updateTextFlow(messages.get(i), Color.DEEPPINK);
				else if (messages.get(i).contains(ENTER))
					updateTextFlow(messages.get(i), Color.LIGHTGREEN);
				else
					updateTextFlow(messages.get(i), Color.INDIANRED);
				i++;
			}
		}));
		timeline.setCycleCount(messages.size());
		timeline.play();
	}

	@FXML
	private void enterBtn() {
		messages.clear();
		ids.clear();
		if (!idTextField.getText().isEmpty()) {
			CardReaderControl.executeEntranceSequence(idTextField.getText(), this);
		}
		updateUI();
	}

	@FXML
	private void exitBtn() {
		messages.clear();
		ids.clear();
		if (!idTextField.getText().isEmpty())
			CardReaderControl.executeExitSequence(idTextField.getText(),exitTimeTextField.getText() , this);
		updateUI();
	}

	/**
	 * This method generate message base on the parameters she gets.
	 * each message is added to 'messages' arrayList
	 * 
	 * @param order     Order object
	 * @param isExiting true if leaving the park
	 * @param id        the traveler id
	 */
	public void generateMsg(Order order,  boolean isExiting, String id) {
		String msgText = "";
		currentOrder = order;
		if (order == null) {
			msgText = "Could not find relevant order for traveler id: " + id + ". Please go to the Entrance worker.";
		} else if (!isExiting) {
			msgText = String.format(this.Msg.toString(), order.getTravelerId(), order.getOrderId(),
					order.getNumberOfParticipants(), ENTER, order.getOrderTime());
		} else {
			updateExitTime();

			msgText = String.format(this.Msg.toString(), order.getTravelerId(), order.getOrderId(),
					order.getNumberOfParticipants(), EXIT, exitTime);
		}
		messages.add(msgText);
		ids.add(id);
	}
	
	private void updateExitTime() {
		String entranceTime = currentOrder.getOrderTime();
		String hour = entranceTime.split(":")[0];
		String minutes = entranceTime.split(":")[1];
		int timeToAdd = 3;
		if (!exitTimeTextField.getText().isEmpty())
			exitTime = exitTimeTextField.getText();
		else {
			timeToAdd += Integer.parseInt(hour);
			exitTime = timeToAdd + ":" + minutes;
		}
	}

	private void updateTextFlow(String msg, Color c) {
		Text t = new Text("  " + msg + "\n\n");
		t.setFont(Font.font("Helvetica", FontWeight.NORMAL, 12));
		t.setFill(c);
		CardReaderLog.getChildren().add(t);
	}

	/**
	 * Close the Screen (stage)
	 */
	@FXML
	private void closeSimulationBtn() {
		Stage stage = (Stage) closeBtn.getScene().getWindow();
		stage.close();
	}

}
