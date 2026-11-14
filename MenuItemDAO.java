package cafeteria;

import java.sql.*;
import java.util.*;
import javax.swing.JOptionPane;

/**
 * MenuItemDAO - GUI version
 */
public class MenuItemDAO {

    private Connection conn;

    public MenuItemDAO() {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    /** Fetch all AVAILABLE menu items only */
    public List<MenuItem> getAllMenuItems() {
        List<MenuItem> items = new ArrayList<>();

        if (conn == null) {
            return getOfflineMenu();
        }

        String sql =
                "SELECT m.ItemCode, m.ItemName, m.Price, c.CategoryName " +
                        "FROM MenuItems m JOIN Categories c ON m.CategoryID = c.CategoryID " +
                        "WHERE m.IsAvailable = 1";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                items.add(new MenuItem(
                        rs.getString("ItemCode"),
                        rs.getString("ItemName"),
                        rs.getDouble("Price"),
                        rs.getString("CategoryName")
                ));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Error loading menu:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        return items;
    }

    private List<MenuItem> getOfflineMenu() {
        return List.of();
    }

    /** Insert menu item */
    public boolean insertMenuItem(MenuItem item, int categoryId) {

        if (conn == null) {
            JOptionPane.showMessageDialog(
                    null,
                    "No database connection.\nItem not saved: " + item.getItemName(),
                    "Warning",
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        }

        String sql =
                "INSERT INTO MenuItems (ItemCode, ItemName, Price, CategoryID) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, item.getItemCode());
            ps.setString(2, item.getItemName());
            ps.setDouble(3, item.getPrice());
            ps.setInt(4, categoryId);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(
                    null,
                    "Menu item added: " + item.getItemName(),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return true;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Insert failed:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
    }

    /** Update price of an existing menu item */
    public boolean updateMenuItemPrice(String itemCode, double newPrice) {

        if (conn == null) {
            JOptionPane.showMessageDialog(null,
                    "No database connection.\nPrice not updated.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        String sql = "UPDATE MenuItems SET Price = ? WHERE ItemCode = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, newPrice);
            ps.setString(2, itemCode);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(null,
                        "✅ Price updated successfully!\n" +
                                "Item: " + itemCode +
                                "\nNew Price: ETB " + newPrice,
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                return true;
            } else {
                JOptionPane.showMessageDialog(null,
                        "Item code not found: " + itemCode,
                        "Not Found", JOptionPane.WARNING_MESSAGE);
                return false;
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Update failed:\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Soft-delete: sets IsAvailable = 0 instead of physically deleting.
     * This avoids the FK constraint error from OrderDetails referencing ItemCode.
     * Past order history is fully preserved.
     */
    public boolean deleteMenuItem(String itemCode) {

        if (conn == null) {
            JOptionPane.showMessageDialog(null,
                    "No database connection.\nItem not removed.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        String sql = "UPDATE MenuItems SET IsAvailable = 0 WHERE ItemCode = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, itemCode);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(null,
                        "✅ Menu item removed from menu!\n" +
                                "Item Code: " + itemCode +
                                "\n(Historical order data is preserved.)",
                        "Removed", JOptionPane.INFORMATION_MESSAGE);
                return true;
            } else {
                JOptionPane.showMessageDialog(null,
                        "Item code not found: " + itemCode,
                        "Not Found", JOptionPane.WARNING_MESSAGE);
                return false;
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Remove failed:\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
   