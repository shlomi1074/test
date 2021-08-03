package Controllers.calculatePrice;

/**
 * GuidePayAtParkCheckOut extends CheckOutDecorator
 * GuidePayAtParkCheckOut calculates organized group pay at the park order's price
 *
 */
public class GuidePayAtParkCheckOut extends CheckOutDecorator {

	private final double discountForGuidesPayAtPark = 0.75;

	public GuidePayAtParkCheckOut(CheckOut tempCheckOut) {
		super(tempCheckOut);
	}

	/**
	 * Overrite getPrice from CheckOutDecorator
	 * 
	 * @return return the organized group pay at the park order's price
	 */
	public double getPrice() {
		return regularCheckOut.getPrice() * discountForGuidesPayAtPark;
	}

}
