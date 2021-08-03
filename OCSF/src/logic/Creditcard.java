package logic;

/**
 * Creditcard class represents a credit card of a traveler
 */
public class Creditcard {
	private String subscriberId;
	private String cardNumber;
	private String cardExpiryDate;
	private int cvc;

	public Creditcard(String subscriberId, String cardNumber, String cardExpiryDate, int cvc) {
		this.subscriberId = subscriberId;
		this.cardNumber = cardNumber;
		this.cardExpiryDate = cardExpiryDate;
		this.cvc = cvc;
	}

	public String getSubscriberId() {
		return subscriberId;
	}

	public void setSubscriberId(String subscriberId) {
		this.subscriberId = subscriberId;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getCardExpiryDate() {
		return cardExpiryDate;
	}

	public void setCardExpiryDate(String cardExpiryDate) {
		this.cardExpiryDate = cardExpiryDate;
	}

	public int getCvc() {
		return cvc;
	}

	public void setCvc(int cvc) {
		this.cvc = cvc;
	}

}
