package Controllers.calculatePrice;

/**
 * GuidePrePayCheckOut extends CheckOutDecorator
 * GuidePrePayCheckOut calculates organized group pre pay order's price
 *
 */
public class GuidePrePayCheckOut extends CheckOutDecorator{
	
	private final double baseDiscount = 0.75;
	private final double prePayDiscount = 0.88;
	public GuidePrePayCheckOut(CheckOut tempCheckOut) {
		super(tempCheckOut);
	}

	/**
	 * Overrite getPrice from CheckOutDecorator
	 * 
	 * @return return the organized group pre pay order's price
	 */
	public double getPrice() {
		return regularCheckOut.getPrice() * baseDiscount * prePayDiscount;	
	}
}
