package logic;

/**
 * This class is responsible on the system's order status.
 */
public enum OrderStatusName {
	CONFIRMED, CANCELED, PENDING, PENDING_EMAIL_SENT, WAITING, WAITING_HAS_SPOT, ENTERED_THE_PARK,COMPLETED;

	@Override
	public String toString() {
		switch (this) {
		case CONFIRMED:
			return "Confirmed";
		case CANCELED:
			return "Canceled";
		case PENDING:
			return "Pending";
		case WAITING:
			return "Waiting";
		case ENTERED_THE_PARK:
			return "Entered the park";
		case PENDING_EMAIL_SENT:
			return "Pending email sent";
		case WAITING_HAS_SPOT:
			return "Waiting has spot";
		case COMPLETED:
			return "Visit completed";
		default:
			throw new IllegalArgumentException();
		}
	}
}