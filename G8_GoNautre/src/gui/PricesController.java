package gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import logic.GoNatureFinals;

/**
 * Show's table of park prices and discounts.
 */
public class PricesController implements Initializable {
	  @FXML
	    private Label lblPrice;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		init();
	}
	
	private void init() {
		lblPrice.setText(String.valueOf(GoNatureFinals.FULL_PRICE));
	}
}

