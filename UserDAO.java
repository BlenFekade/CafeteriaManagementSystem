package cafeteria;

import java.sql.*;
import javax.swing.JOptionPane;

/**
 * UserDAO - GUI version (no console output)
 */
public class UserDAO {

    private final Connection conn;

    public UserDAO() {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Login authentication
     */

    public User login(String username, String password) {

        if (conn == null) {
            JOptionPane.showMessageDialog(
                    null,
                    "No database connection.\nSwitching to offline mode.",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE
            );
            return offlineLogin(username, password);
        }

        String sql =
                "SELECT u.UserID, u.Username, u.PasswordHash, r.RoleName " +
                        "FROM Users u JOIN Roles r ON u.RoleID = r.RoleID " +
                        "WHERE u.Username = ? AND u.PasswordHash = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                int userId = rs.getInt("UserID");
                String uname = rs.getString("Username");
                String pass = rs.getString("PasswordHash");
                String role = rs.getString("RoleName");

                JOptionPane.showMessageDialog(
                        null,
                        "Login successful!\nWelcome " + uname,
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                if ("Manager".equalsIgnoreCase(role)) {
                    return new Manager(userId, uname, pass);
                } else {
                    return new Cashier(userId, uname, pass);
                }
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    null,
                    "Login error:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        JOptionPane.showMessageDialog(
                null,
                "Invalid username or password!",
                "Login Failed",
                JOptionPane.ERROR_MESSAGE
        );

        return null;
    }

    /**
     * Offline login (demo mode)
     */
    private User offlineLogin(String username, String password) {

        if ("admin_user".equals(username) && "admin123".equals(password)) {
            return new Manager(1, username, password);
        }

        if ("cashier_abebe".equals(username) && "pay123".equals(password)) {
            return new Cashier(2, username, password);
        }

        JOptionPane.showMessageDialog(
                null,
                "Offline login failed!",
                "Error",
                JOptionPane.ERROR_MESSAGE
        );

        return null;
    }
}