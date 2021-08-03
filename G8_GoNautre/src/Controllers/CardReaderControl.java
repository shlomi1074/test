package Controllers;

import java.util.ArrayList;
import client.ChatClient;
import client.ClientUI;
import gui.CardReaderController;
import logic.ClientToServerRequest;
import logic.ClientToServerRequest.Request;
import logic.Order;
import logic.OrderStatusName;
import logic.Park;

/**
 * CardReaderControl class is the controller of every thing that is related to card reader simulator.
 * It's provides the functionalities that the card reader needs to have.
 * The Gui interacts with this controller to active those functionalities.
 *
 */
public class CardReaderControl {

	public static Order order = null;

	/**
	 * This function starts the card reader simulation.
	 * It's getting the travelers id's from the database and executes enter and exit sequences.
	 * 
	 * @param cardReaderController The GUI controller to update.
	 */
	@SuppressWarnings("unchecked")
	public static void startSimulator(CardReaderController cardReaderController) {
		ClientToServerRequest<String> request = new ClientToServerRequest<>(Request.GET_SIMULATOR_TRAVELERS_IDS);
		ClientUI.chat.accept(request);
		ArrayList<String> travelersID = ChatClient.responseFromServer.getResultSet();
		
		for (String string : travelersID) {
			String id = string.split(" ")[0];
			String cardReaderLocation = string.split(" ")[1];
			order = null;
			if (cardReaderLocation.equals("Exit")) {
				executeExitSequence(id,"", cardReaderController);
			} else {
				executeEntranceSequence(id, cardReaderController);
			}
		}
	}

	/**
	 * This function runs all the actions that needs to happened when exiting the park.
	 * Update exit time in the database.
	 * Update park's current visitors.
	 * 
	 * @param id The id of the exiting traveler.
	 * @param exitTime The traveler exit time
	 * @param cardReaderController  The GUI controller to update.
	 */
	public static void executeExitSequence(String id, String exitTime, CardReaderController cardReaderController) {
		order = OrderControl.getRelevantOrder_ParkExit(id);
		if (order != null) {
			updateVisitExitTimeSimulator(order, exitTime);
			OrderControl.changeOrderStatus(String.valueOf(order.getOrderId()), OrderStatusName.COMPLETED);
			Park park = ParkControl.getParkById(String.valueOf(order.getParkId()));
			ParkControl.updateCurrentVisitors(order.getParkId(),
					park.getCurrentVisitors() - order.getNumberOfParticipants());
		}
		cardReaderController.generateMsg(order, true, id);
	}

	/**
	 * This function runs all the actions that needs to happened when entering the park.
	 * Add visit to the visit table in the database.
	 * Update order status in the database to completed.
	 * Update park's current visitors.
	 * 
	 * @param id The id of the entering traveler.
	 * @param cardReaderController  The GUI controller to update.
	 */
	public static void executeEntranceSequence(String id, CardReaderController cardReaderController) {
		order = OrderControl.getRelevantOrderByTravelerID_ParkEntrance(id);
		if (order != null) {
			OrderControl.addVisit(order);
			OrderControl.changeOrderStatus(String.valueOf(order.getOrderId()), OrderStatusName.ENTERED_THE_PARK);
			Park park = ParkControl.getParkById(String.valueOf(order.getParkId()));
			ParkControl.updateCurrentVisitors(order.getParkId(),
					park.getCurrentVisitors() + order.getNumberOfParticipants());
		}
		cardReaderController.generateMsg(order, false, id);
	}

	/**
	 * This function update visit exit time.
	 * This function is only to update exit visit time using the card reader simulator
	 * It's update the exit time artificially.
	 * 
	 * @param order the order to update.
	 */
	private static void updateVisitExitTimeSimulator(Order order, String exitTime) {
		ClientToServerRequest<Order> request = new ClientToServerRequest<>(Request.UPDATE_EXIT_TIME_SIMULATOR);
		request.setObj(order);
		request.setInput(exitTime);
		ClientUI.chat.accept(request);
	}
	
}
