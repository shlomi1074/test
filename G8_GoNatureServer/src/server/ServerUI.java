package server;

import javafx.application.Application;
import javafx.stage.Stage;
import gui.ServerGUIController;

/**
 * This class loads the server GUI
 */
public class ServerUI extends Application {
	final public static int DEFAULT_PORT = 5555;
	public static ServerGUIController aFrame;

	public static void main(String args[]) throws Exception {
		launch(args);
	} // end main

	/**
	 * load The GUI
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		aFrame = new ServerGUIController();
		aFrame.start(primaryStage);

	}

	/**
	 * Start the server
	 * @param sv GoNatureServer object
	 */
	public static void runServer(GoNatureServer sv) {
		try {
			sv.listen(); // Start listening for connections
		} catch (Exception ex) {
			System.out.println("ERROR - Could not listen for clients!");
		}
	}

}
