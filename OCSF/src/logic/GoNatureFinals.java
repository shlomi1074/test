package logic;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class is responsible for the system's finals
 */
public final class GoNatureFinals {

	private GoNatureFinals() {
	}
	
	public final static String APP_ICON = "/resources/images/tree.png";
	public final static int FULL_PRICE = 100;
	public final static String GO_NATURE_EMAIL = "G8GoNature@gmail.com";
	public final static String GO_NATURE_EMAIL_PASSWORD = "Aa123456!";


	public final static ArrayList<String> AVAILABLE_HOURS = new ArrayList<>(Arrays.asList("08:00", "09:00", "10:00",
			"11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00"));
	public final static String[] MONTHS = { "Month", "January", "February", "March", "April", "May", "June", "July",
			"August", "September", "October", "November", "December" };
	
}