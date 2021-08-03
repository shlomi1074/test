package util;

import java.io.IOException;

import gui.AddSubscriberController;
import gui.CreateReportsController;
import gui.DepartmentManagerReportsController;
import gui.ManageTravelerController;
import gui.OrderVisitController;
import gui.ParkParametersController;
import gui.ProfileController;
import gui.TravelerViewOrders;
import gui.UpdateParametersController;
import gui.ViewMessagesController;
import gui.ViewRequestsForChangesController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

/**
 * FxmlUtil is a utility class to load scenes
 *
 */
public class FxmlUtil {
	private Pane view;
	private boolean isWorker; // true if it is member (park worker)

	/**
	 * This function loads fxml and it's controller
	 * 
	 * @param fxmlUrl        - fxml to load
	 * @param controllerName - the fxml's controller
	 * @return Pane
	 */
	public Pane loadPaneToBorderPaneWithController(String fxmlUrl, String controllerName) {

		FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlUrl));
		if (controllerName.equals("orderVisit")) {
			OrderVisitController controller = new OrderVisitController();
			loader.setController(controller);
		} else if (controllerName.equals("profile")) {
			ProfileController controller = new ProfileController();
			controller.setWorker(isWorker);//Alon 12.13.20
			loader.setController(controller);
		} else if (controllerName.equals("manageTraveler")) {
			ManageTravelerController controller = new ManageTravelerController();
			loader.setController(controller);
		} else if (controllerName.equals("parkParameters")) {
			ParkParametersController controller = new ParkParametersController();
			loader.setController(controller);
		} else if (controllerName.equals("travelerOrders")) {
			TravelerViewOrders controller = new TravelerViewOrders();
			loader.setController(controller);
		} else if (controllerName.equals("travelerMessages")) {
			ViewMessagesController controller = new ViewMessagesController();
			loader.setController(controller);
		} else if (controllerName.equals("viewRequests")) {
			ViewRequestsForChangesController controller = new ViewRequestsForChangesController();
			loader.setController(controller);
		} else if (controllerName.equals("addSubSubscriber")) {
			AddSubscriberController controller = new AddSubscriberController();
			loader.setController(controller);
		} else if (controllerName.equals("updateParameters")) {
			UpdateParametersController controller = new UpdateParametersController();
			loader.setController(controller);
		} else if (controllerName.equals("createReport")) {
			CreateReportsController controller = new CreateReportsController();
			loader.setController(controller);
		}
		 else if (controllerName.equals("reports")) {
			 DepartmentManagerReportsController controller = new DepartmentManagerReportsController();
				loader.setController(controller);
			}
		try {
			loader.load();
			view = loader.getRoot();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return view;

	}

	/**
	 * Setter for the class variable isWorker
	 * 
	 * @param isWorker True if this screen is a worker screen
	 */
	public void setWorker(boolean isWorker) {
		this.isWorker = isWorker;
	}

}
