package client;

import common.*;
import logic.ClientToServerRequest;
import logic.Request;
import logic.ServerToClientResponse;
import ocsf.client.*;
import java.io.*;
import java.util.ArrayList;

/**
 * This class overrides some of the methods defined in the abstract superclass
 * in order to give more functionality to the client.
 *
 * @author Shlomi Amar
 * @author Alon Ivshin
 * @author Ofir Vaknin
 * @author Lior Keren
 * @author Ofir Newman
 * 
 * @version January 2021
 */
@SuppressWarnings("rawtypes")
public class ChatClient extends AbstractClient {
	// Instance variables **********************************************

	/**
	 * The interface type variable. It allows the implementation of the display
	 * method in the client.
	 */
	ChatIF clientUI;
	public static boolean awaitResponse = false;
	public static ServerToClientResponse responseFromServer;
	public static ArrayList<Request> requestsWaitingForApproval=new ArrayList<>();

	// Constructors ****************************************************

	/**
	 * Constructs an instance of the chat client.
	 *
	 * @param host     The server to connect to.
	 * @param port     The port number to connect on.
	 * @param clientUI The interface type variable.
	 * 
	 * @throws IOException If failed to connect to server
	 */
	public ChatClient(String host, int port, ChatIF clientUI) throws IOException {
		super(host, port); // Call the superclass constructor
		this.clientUI = clientUI;
		openConnection();
	}

	// Instance methods ************************************************

	/**
	 * This method handles all data that comes in from the server.
	 *
	 * @param msg The message from the server.
	 */
	public void handleMessageFromServer(Object msg) {
		awaitResponse = false;
		if (msg instanceof ServerToClientResponse) {
			ServerToClientResponse response = (ServerToClientResponse) msg;
			responseFromServer = response;	
		}
		
		else if (msg instanceof String) {
			String serverMsg = (String) msg;
			if (serverMsg.equals("Finished")) {
				//System.out.println("Finished handle client request");
			}
			if (serverMsg.equals("Server closing")) {
			}
				
		}
	}

	/**
	 * This method handles all data coming from the UI
	 *
	 * @param message The message from the UI.
	 */
	public void handleMessageFromClientUI(String message) {
		try {
			openConnection();// in order to send more than one message
			awaitResponse = true;

			sendToServer(message);
			// wait for response
			while (awaitResponse) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			clientUI.display("Could not send message to server: Terminating client." + e);
			quit();
		}
	}

	/**
	 * This method handles all data coming from the UI
	 *
	 * @param message The message from the UI.
	 */
	public void handleMessageFromClientUI(ClientToServerRequest<?> message) {
		try {
			openConnection();// in order to send more than one message
			awaitResponse = true;

			sendToServer(message);
			// wait for response
			while (awaitResponse) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			clientUI.display("Could not send message to server: Terminating client." + e);
			quit();
		}
	}

	/**
	 * This method terminates the client.
	 */
	public void quit() {
		try {
			this.closeConnection();
		} catch (IOException e) {
		}
		System.exit(0);
	}
}
//End of ChatClient class
