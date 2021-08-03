package controllers.sqlHandlers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * MysqlConnection class holds a connection to the DB.
 * MysqlConnection implements the Singleton design pattern
 * 
 */
public class MysqlConnection {

	private Connection connection = null;
	private static MysqlConnection instance = null;

	/**
	 * private constructor
	 * 
	 * @throws SQLException	If got SQL error
	 * @throws ClassNotFoundException If failed to create jdbc driver
	 * @throws InstantiationException if failed to connect to the database
	 * @throws IllegalAccessException if failed to connect to the database
	 */
	private MysqlConnection()
			throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			System.out.println("Driver definition succeed");
		} catch (ClassNotFoundException ex) {
			/* handle the error */
			System.out.println("Driver definition failed");
			throw ex;
		}
		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/g8gonature?serverTimezone=UTC",
					"root", "root");

			/* How to handle multiple requests to the database */
			connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

			System.out.println("SQL connection succeed");
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			throw ex;
		}
	}

	/**
	 * This function returns an MysqlConnection instance
	 * 
	 * @return MysqlConnection object
	 * 
	 * @throws SQLException	If got SQL error
	 * @throws ClassNotFoundException If failed to create JDBC driver
	 * @throws InstantiationException if failed to connect to the database
	 * @throws IllegalAccessException if failed to connect to the database
	 */
	public static MysqlConnection getInstance()
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
		if (instance == null)
			instance = new MysqlConnection();
		return instance;
	}

	/**
	 * This function returns a connection to the DB
	 * 
	 * @return Connection object
	 */
	public Connection getConnection() {
		return this.connection;
	}

}
