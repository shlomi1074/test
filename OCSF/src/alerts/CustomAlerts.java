package alerts;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * CustomAlerts is a customize Alerts
 *
 */
public class CustomAlerts extends Alert {

	final String errorIcon = "/resources/images/errorIcon.png";
	final String warningIcon = "/resources/images/warningIcon.png";
	final String informationIcon = "/resources/images/infoIcon.png";

	public CustomAlerts(AlertType alertType, String title, String header, String content) {
		super(alertType);
		this.setTitle(title);
		this.setHeaderText(header);
		this.setContentText(content);
		setAlertStyle();
	}

	private void setAlertStyle() {
		String alertType = this.getAlertType().toString();
		if (alertType.equals("ERROR"))
			setupErrorIcon();
		else if (alertType.equals("WARNING"))
			setupWarningIcon();
		else if (alertType.equals("INFORMATION"))
			setupInfoIcon();
		

	}

	private void setupErrorIcon() {
		DialogPane pane = this.getDialogPane();
		((Stage) pane.getScene().getWindow()).getIcons().add(new Image(errorIcon));
		pane.getStylesheets().add(
				   getClass().getResource("errorAlert.css").toExternalForm());
	}

	private void setupWarningIcon() {
		DialogPane pane = this.getDialogPane();
		((Stage) pane.getScene().getWindow()).getIcons().add(new Image(warningIcon));
		pane.getStylesheets().add(
				   getClass().getResource("warningAlert.css").toExternalForm());
	}

	private void setupInfoIcon() {
		DialogPane pane = this.getDialogPane();
		((Stage) pane.getScene().getWindow()).getIcons().add(new Image(informationIcon));
		((Stage) pane.getScene().getWindow()).getIcons().add(new Image(warningIcon));
		pane.getStylesheets().add(
				   getClass().getResource("infoAlert.css").toExternalForm());
	}

}
