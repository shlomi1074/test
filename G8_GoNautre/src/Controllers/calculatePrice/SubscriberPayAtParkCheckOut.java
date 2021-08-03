package Controllers.calculatePrice;

/**
 * SubscriberPayAtParkCheckOut extends CheckOutDecorator
 * SubscriberPayAtParkCheckOut calculates a subscriber casual order's price
 *
 */
public class SubscriberPayAtParkCheckOut extends CheckOutDecorator {

	private final double subscriberDiscount = 0.80;

	public SubscriberPayAtParkCheckOut(CheckOut tempCheckOut) {
		super(tempCheckOut);
	}

	/**
	 * Overrite getPrice from CheckOutDecorator
	 * 
	 * @return return the subscriber casual order's price
	 */
	public double getPrice() {
		return regularCheckOut.getPrice() * subscriberDiscount;
	}
}
