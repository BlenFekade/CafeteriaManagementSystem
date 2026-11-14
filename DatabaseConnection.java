package cafeteria;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

/**
 * GUI Version of DatabaseConnection
 * Shows database connection status visually using Swing
 */
public class DatabaseConnection {

    // SQL Server JDBC URL
    private static final String URL =
      "jdbc:sqlserver://localhost; databaseName=CafeteriaDB;encrypt=true;trustServerCertificate=true";


    private static final String DB_USER = "sa";
    private static final String DB_PASS = "11264321";

    private static DatabaseConnection instance;

    private Connection connection;

    /**
     * Private constructor (Singleton)
     */
    private DatabaseConnection() {

        try {

            // Load SQL Server JDBC Driver
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            // Connect to database
            connection = DriverManager.getConnection(URL, DB_USER, DB_PASS);

            // Success GUI Message
            showMessage(
                    "Database Connected Successfully!",
                    "Connected to CafeteriaDB",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (ClassNotFoundException e) {

            showMessage(
                    "JDBC Driver Not Found!\n\n" + e.getMessage(),
                    "Driver Error",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (SQLException e) {

            showMessage(
                    "Database Connection Failed!\n\n" + e.getMessage(),
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Singleton accessor
     */
    public static DatabaseConnection getInstance() {

        if (instance == null) {
            instance = new DatabaseConnection();
        }

        return instance;
    }

    /**
     * Get database connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Check if database is connected
     */
    public boolean isConnected() {

        try {

            return connection != null && !connection.isClosed();

        } catch (SQLException e) {

            return false;
        }
    }

    /**
     * Close database connection
     */
    public void close() {

        try {

            if (connection != null && !connection.isClosed()) {

                connection.close();

                showMessage(
                        "Database Connection Closed Successfully.",
                        "Disconnected",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

        } catch (SQLException e) {

            showMessage(
                    "Error Closing Database!\n\n" + e.getMessage(),
                    "Close Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Helper method for GUI popups
     */
    private void showMessage(String message,
                             String title,
                             int messageType) {

        JTextArea textArea = new JTextArea(message);

        textArea.setEditable(false);
        textArea.setFont(new Font("Arial", Font.PLAIN, 14));
        textArea.setBackground(null);

        JOptionPane.showMessageDialog(
                null,
                textArea,
                title,
                messageType
        );
    }

    /**
     * Optional Test Main Method
     */
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            DatabaseConnection db =
                    DatabaseConnection.getInstance();

            if (db.isConnected()) {

                JOptionPane.showMessageDialog(
                        null,
                        "Database is ACTIVE",
                        "Status",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } else {

                JOptionPane.showMessageDialog(
                        null,
                        "Database is NOT connected",
                        "Status",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}