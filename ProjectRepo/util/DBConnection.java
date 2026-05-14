package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * FILE: util/DBConnection.java
 * ROLE: Singleton Database Connection Manager
 *
 * Provides ONE shared Connection for the entire application.
 * All DAO classes call DBConnection.getConnection() — nothing
 * else should call DriverManager directly.
 *
 * SETUP CHECKLIST:
 *   1. XAMPP Control Panel: MySQL is RUNNING (green light)
 *   2. Run database.sql in phpMyAdmin first
 *   3. mysql-connector-j-9.7.0.jar is in /lib AND added to:
 *      File > Project Structure > Modules > Dependencies > + > JARs
 */
public class DBConnection {

    private static final String URL      = "jdbc:mysql://localhost:3306/coffee_shop_inventory"
            + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER     = "root";
    private static final String PASSWORD = "";      // XAMPP default: blank password

    private static Connection connection = null;

    private DBConnection() {}   // Prevent instantiation

    /**
     * Returns the shared Connection. Opens it on first call.
     * Reconnects automatically if MySQL was restarted.
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("[DBConnection] Connected to coffee_shop_inventory.");
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                    "[DBConnection] MySQL driver not found.\n" +
                            "Add mysql-connector-j-9.7.0.jar to File > Project Structure > Modules > Dependencies.", e
            );
        } catch (SQLException e) {
            throw new RuntimeException(
                    "[DBConnection] Cannot connect to MySQL.\n" +
                            "Check: (1) XAMPP MySQL is running (2) database.sql was executed in phpMyAdmin.", e
            );
        }
        return connection;
    }

    /**
     * Closes the connection cleanly. Call once when the app exits.
     *
     * Add to your main JFrame constructor:
     *   addWindowListener(new java.awt.event.WindowAdapter() {
     *       public void windowClosing(java.awt.event.WindowEvent e) {
     *           DBConnection.closeConnection();
     *       }
     *   });
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("[DBConnection] Connection closed.");
            } catch (SQLException e) {
                System.err.println("[DBConnection] Error closing connection: " + e.getMessage());
            }
        }
    }
}

