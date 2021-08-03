package client;

import client.ClientController;
import gui.ClientConfigurationController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import logic.GoNatureFinals;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;

public class ClientUI extends Application {
	public static ClientController chat;
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/clientConfiguration.fxml"));
			ClientConfigurationController controller = new ClientConfigurationController();
			loader.setController(controller);
			controller.setStage(primaryStage);
			loader.load();
			Parent p = loader.getRoot();
			primaryStage.setTitle("GoNature Client Set UP");
			primaryStage.getIcons().add(new Image(GoNatureFinals.APP_ICON));
			primaryStage.setScene(new Scene(p));
			primaryStage.setResizable(false);
			primaryStage.show();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}