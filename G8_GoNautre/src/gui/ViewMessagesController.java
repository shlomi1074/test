package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import com.jfoenix.controls.JFXTextArea;
import Controllers.NotificationControl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import logic.Messages;
import logic.MessagesTb;
import logic.Subscriber;
import logic.Traveler;

/**
 * gets traveler's / subscriber's messages from DB
 * Loads them into table for view 
 * when user double clicks on a message, its content shows under the table
 */
public class ViewMessagesController implements Initializable {
	
	ObservableList<MessagesTb> observable = FXCollections.observableArrayList(); 
	
    @FXML
    private TableView<MessagesTb> messagesTableView;

    @FXML
    private TableColumn<MessagesTb, Integer> messageIdCol;

    @FXML
    private TableColumn<MessagesTb, String> SubjectCol;

    @FXML
    private TableColumn<MessagesTb, String> DateCol;

    @FXML
    private Label headerLabel;

    @FXML
    private JFXTextArea messageTextArea;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		loadTableView();
	}

	private void loadTableView() {
		String id;
		/*Loading traveler/subscriber's id*/
		Traveler traveler = TravelerLoginController.traveler;
		/*if subscriber*/
		if (traveler == null) {
			Subscriber subscriber = TravelerLoginController.subscriber;
			id = String.valueOf(subscriber.getTravelerId());
		/*if traveler*/
		} else {
			id = String.valueOf(traveler.getTravelerId());
		}
		/*getting all messages by id*/
		ArrayList<Messages> messagess= NotificationControl.getMessages(id);
		ArrayList<MessagesTb> tbMessages= convertMessagesToMessagesTb(messagess);
		init(tbMessages);
		messagesTableView.setItems(getMessages(tbMessages));
	}
	
	private ArrayList<MessagesTb> convertMessagesToMessagesTb(ArrayList<Messages> messages) {
		ArrayList<MessagesTb> messagesTb = new ArrayList<MessagesTb>();
		for (Messages message : messages) {
			MessagesTb messageTb = new MessagesTb(message);
			messagesTb.add(messageTb);
		}
		return messagesTb;
	}
	
	private void init(ArrayList<MessagesTb> messagess) {
		messageIdCol.setCellValueFactory(new PropertyValueFactory<MessagesTb, Integer>("messageId"));
		SubjectCol.setCellValueFactory(new PropertyValueFactory<MessagesTb, String>("subject"));
		DateCol.setCellValueFactory(new PropertyValueFactory<MessagesTb, String>("sendDate"));
		/*if user doubleclickes on a message, it's content will appear in messageTextArea*/
		messagesTableView.setTooltip(new Tooltip("Double click on a row to open it"));
		messagesTableView.setRowFactory(tv -> {
			TableRow<MessagesTb> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
					MessagesTb clickedRow = row.getItem();
					messageTextArea.setText(String.valueOf(clickedRow.getContent()));
				}
			});
			return row;
		});
	}
	
	private ObservableList<MessagesTb> getMessages(ArrayList<MessagesTb> tbMessages) {
		messagesTableView.getItems().clear();
		for (MessagesTb message : tbMessages) {
			observable.add(message);
		}
		return observable;
	}
	
	
}