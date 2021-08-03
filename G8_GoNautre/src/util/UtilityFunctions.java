package util;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 * UtilityFunctions is a utility class that provide function that are used frequently
 *
 */
public class UtilityFunctions {

	/**
	 * This function gets an email as String and return true if it is a valid email.
	 * Valid Email - xxxx@xxxxx.
	 * 
	 * @param email The email to check
	 * @return true if the email is valid, false otherwise
	 */
	public static boolean isValidEmailAddress(String email) {
		boolean result = true;
		try {
			InternetAddress emailAddr = new InternetAddress(email);
			emailAddr.validate();
		} catch (AddressException ex) {
			result = false;
		}
		return result;
	}

	/**
	 * This function check whether a string consists of only numbers.
	 * 
	 * @param str the string to check
	 * @return true if string consists of only numbers
	 * @return false otherwise
	 */
	public static boolean isNumeric(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
