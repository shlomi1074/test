package gui;

import java.net.URL;
import java.util.ResourceBundle;
import Controllers.ParkControl;
import Controllers.TravelerControl;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.shape.Line;
import logic.Subscriber;
import logic.WorkerType;

/**
 * Gets user's data and displays it on the screen
 */
public class ProfileController implements Initializable {

	@FXML
	private Label headerLabel;

	@FXML
	private Label profileNameLabel;

	@FXML
	private Label profileLastNameLabel;

	@FXML
	private Label profileIDLabel;

	@FXML
	private Label ProfileEmailLabel;

	@FXML
	private Label profileAccountTypeLabel;

	@FXML
	private Label profileParkLabel;

	@FXML
	private Label parkLabel;
	
	@FXML
	private Line line;
	
	private boolean isWorker;
	

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		init();
	}

	private void init() {
		loadInfo();
	}
	
	/**
	 * Setter for class variable isWorker
	 * @param isWorker true if this is a worker screen
	 */
	public void setWorker(boolean isWorker) {
		this.isWorker = isWorker;
	}
	

	/*
	 * This function loads profile of current entity (Traveler/Subscriber/Employee)
	 */
	private void loadInfo() {
		//Employee.
		if(isWorker) { 
			WorkerType role = MemberLoginController.member.getRole();
			profileNameLabel.setText(MemberLoginController.member.getFirstName());
			profileLastNameLabel.setText(MemberLoginController.member.getLastName());
			profileIDLabel.setText(String.valueOf(MemberLoginController.member.getEmployeeId()));
			ProfileEmailLabel.setText(MemberLoginController.member.getEmail());
			String parkId = String.valueOf(MemberLoginController.member.getParkId());
			profileParkLabel.setText(ParkControl.getParkName(parkId));
			profileAccountTypeLabel.setText(role.getStr());
			if(role.equals(WorkerType.DEPARTMENT_MANAGER)||role.equals(WorkerType.SERVICE)) { //<-- ALON NEW
				profileParkLabel.setVisible(false);
				parkLabel.setVisible(false);
				line.setVisible(false);
			}
		}
		//Subscriber
		else if(TravelerLoginController.traveler==null) {
			profileNameLabel.setText(TravelerLoginController.subscriber.getFirstName());
			profileLastNameLabel.setText(TravelerLoginController.subscriber.getLastName());
			profileIDLabel.setText(TravelerLoginController.subscriber.getTravelerId());
			ProfileEmailLabel.setText(TravelerLoginController.subscriber.getEmail());
			profileParkLabel.setVisible(false);
			parkLabel.setVisible(false);
			line.setVisible(false);
			profileAccountTypeLabel.setText(TravelerLoginController.subscriber.getSubscriberType());
		}
		//Traveler
		else {
			profileNameLabel.setText(TravelerLoginController.traveler.getFirstName());
			profileLastNameLabel.setText(TravelerLoginController.traveler.getLastName());
			profileIDLabel.setText(TravelerLoginController.traveler.getTravelerId());
			ProfileEmailLabel.setText(TravelerLoginController.traveler.getEmail());
			profileParkLabel.setVisible(false);
			parkLabel.setVisible(false);
			line.setVisible(false);
			Subscriber subscriber = TravelerControl.getSubscriber(TravelerLoginController.traveler.getTravelerId());
			if(subscriber==null)
			profileAccountTypeLabel.setText("Guest");
			else
				profileAccountTypeLabel.setText(subscriber.getSubscriberType());
		}
	}

}