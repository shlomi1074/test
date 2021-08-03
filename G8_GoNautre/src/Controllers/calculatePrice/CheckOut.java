package Controllers.calculatePrice;

/**
 * This interface implements the method used to get order's price
 *
 */
public interface CheckOut {

	/**
	 * Method that when overriden is used to get order's price
	 * 
	 * @return the order price
	 */
	public double getPrice();

}
