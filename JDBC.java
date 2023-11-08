package Main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Establishes a connection with the database, as provided in the sample program from the lab.
 * @author Elton Mannil
 */
public abstract class JDBC {
    private static final String protocol = "jdbc";
    private static final String vendor = ":mysql:";
    private static final String location = "//localhost/";
    private static final String databaseName = "client_schedule";
    private static final String jdbcUrl = protocol + vendor + location + databaseName + "?connectionTimeZone = SERVER"; // LOCAL
    private static final String driver = "com.mysql.cj.jdbc.Driver"; // Driver reference
    private static final String userName = "sqlUser"; // Username
    private static String password = "Passw0rd!"; // Password
    public static Connection connection;  // Connection Interface
    private static PreparedStatement preparedStatement;

    // Establishes a connection to the database and returns the connection object.
    public static Connection openConnection() {
        try {// Load the JDBC driver to establish a connection.
            Class.forName(driver); // Locate Driver
            connection = DriverManager.getConnection(jdbcUrl, userName, password); // Reference Connection object
            System.out.println("Connection successful!");
        } catch (ClassNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }
        return connection;
    }
    // Retrieves the existing connection object.
    public static Connection getConnection(){
        return connection;
    }
    // Closes the existing database connection.
    public static void closeConnection() {
        try {
            connection.close();
            System.out.println("Connection closed!");
        } catch (SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }
    }
    // Sets the prepared statement for database operations.
        public static void setPreparedStatement (Connection conn, String sqlStatement) throws SQLException {
            if (conn != null)
                preparedStatement = conn.prepareStatement(sqlStatement);
            else
                System.out.println("Prepared Statement Creation Failed");
        }
    // Retrieves the prepared statement for database operations.
    public static PreparedStatement getPreparedStatement () {
            if (preparedStatement != null)
                return preparedStatement;
            else
                System.out.println("Null reference to Prepared Statement");
            return null;
        }
    }



