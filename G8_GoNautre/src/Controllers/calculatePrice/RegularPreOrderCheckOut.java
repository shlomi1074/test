package Controllers.calculatePrice;

/**
 * RegularPreOrderCheckOut extends CheckOutDecorator
 * RegularPreOrderCheckOut calculates a non subscriber pre order's price
 *
 */
public class RegularPreOrderCheckOut extends CheckOutDecorator {

	private final double baseDiscount = 0.85;

	public RegularPreOrderCheckOut(CheckOut tempCheckOut) {
		super(tempCheckOut);
	}

	/**
	 * Overrite getPrice from CheckOutDecorator
	 * 
	 * @return return the non subscriber pre order's price
	 */
	public double getPrice() {
		return regularCheckOut.getPrice() * baseDiscount;
	}
}
