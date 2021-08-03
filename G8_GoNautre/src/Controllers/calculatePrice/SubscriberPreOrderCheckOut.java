package Controllers.calculatePrice;

/**
 * SubscriberPreOrderCheckOut extends CheckOutDecorator
 * SubscriberPreOrderCheckOut calculates a subscriber pre order's price
 *
 */
public class SubscriberPreOrderCheckOut extends CheckOutDecorator {

	private final double baseDiscount = 0.85;
	private final double discountForSubscribers = 0.8;

	public SubscriberPreOrderCheckOut(CheckOut tempCheckOut) {
		super(tempCheckOut);
	}

	/**
	 * Overrite getPrice from CheckOutDecorator
	 * 
	 * @return return the subscriber pre order's price
	 */
	public double getPrice() {
		return regularCheckOut.getPrice() * baseDiscount * discountForSubscribers;
	}

}
