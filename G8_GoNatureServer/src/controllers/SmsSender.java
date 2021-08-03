package controllers;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class SmsSender {

	public static final String ACCOUNT_SID = "ACa0bb543943a4dc407680ec8041f98cae";
	public static final String AUTH_TOKEN = "d5c28d27e702e247379a94724d9bd082";

	public static boolean sendSms(String phoneNumber, String msg) {

		try {
			Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
			Message message = Message.creator(new PhoneNumber("+972" + phoneNumber), // to
					new PhoneNumber("+15046031953"), // from
					"\n\n" + msg).create();

			System.out.println(message.getSid());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
