package Controllers.calculatePrice;

/**
 * CheckOutDecorator implements CheckOut interface
 * This class implements the Decorator design pattern
 */
public class CheckOutDecorator implements CheckOut {

	protected CheckOut regularCheckOut;

	public CheckOutDecorator(CheckOut tempCheckOut) {
		this.regularCheckOut = tempCheckOut;
	}

	public double getPrice() {
		return regularCheckOut.getPrice();
	}

}
