package Controllers.calculatePrice;

/**
 * GroupCasualCheckOut extends CheckOutDecorator
 * GroupCasualCheckOut calculates casual group order's price
 *
 */
public class GroupCasualCheckOut extends CheckOutDecorator {

	private final double subscriberDiscount = 0.90;

	public GroupCasualCheckOut(CheckOut tempCheckOut) {
		super(tempCheckOut);
	}

	/**
	 * Overrite getPrice from CheckOutDecorator
	 * 
	 * @return return the casual group order's price
	 */
	public double getPrice() {
		return regularCheckOut.getPrice() * subscriberDiscount;
	}
}
