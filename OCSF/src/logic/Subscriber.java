package logic;

/**
 * This class represents a subscriber
 */
@SuppressWarnings("serial")
public class Subscriber extends Traveler {
	private int subscriberNumber;
	private String creditCard;
	private String subscriberType;
	private int numberOfParticipants;

	public Subscriber(int subscriberNumber, String travelerId, String firstName, String lastName, String email,
			String phoneNumber, String creditCard, String subscriberType, int numberOfParticipants) {
		super(travelerId, firstName, lastName, email, phoneNumber);
		this.subscriberNumber = subscriberNumber;
		this.creditCard = creditCard;
		this.subscriberType = subscriberType;
		this.numberOfParticipants = numberOfParticipants;
	}

	public int getSubscriberNumber() {
		return subscriberNumber;
	}

	public void setSubscriberNumber(int subscriberNumber) {
		this.subscriberNumber = subscriberNumber;
	}

	public String getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(String creditCard) {
		this.creditCard = creditCard;
	}

	public String getSubscriberType() {
		return subscriberType;
	}

	public void setSubscriberType(String subscriberType) {
		this.subscriberType = subscriberType;
	}

	public int getNumberOfParticipants() {
		return numberOfParticipants;
	}

	public void setNumberOfParticipants(int numberOfParticipants) {
		this.numberOfParticipants = numberOfParticipants;
	}

}
