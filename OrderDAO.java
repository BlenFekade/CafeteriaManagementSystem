package cafeteria;

import java.sql.*;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * OrderDAO GUI Version
 */
public class OrderDAO {

    private final Connection conn;

    public OrderDAO() {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Saves completed order to database
     */
    public int saveOrder(Order order) {

        if (conn == null) {
            JOptionPane.showMessageDialog(
                    null,
                    "No database connection.\nOrder not saved.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return -1;
        }

        int newOrderId = -1;

        try {

            conn.setAutoCommit(false);

            // ==============================
            // INSERT INTO Orders
            // ==============================

            String insertOrder =
                   "INSERT INTO Orders (IsCompleted, UserID) VALUES (?, ?)";

            PreparedStatement ps = conn.prepareStatement(
                    insertOrder,
                   Statement.RETURN_GENERATED_KEYS
            );

            ps.setBoolean(1, order.isCompleted());
            ps.setInt(2, order.getCashier().getUserId());

            ps.executeUpdate();

            ResultSet ResultSet; ResultSet keys = ps.getGeneratedKeys();

            if (keys.next()) {
                newOrderId = keys.getInt(1);
            }

            // If failed
            if (newOrderId == -1) {
                conn.rollback();

                JOptionPane.showMessageDialog(
                        null,
                        "Failed to generate Order ID.",
                        "Order Error",
                        JOptionPane.ERROR_MESSAGE
                );

                return -1;
            }

            // ==============================
            // INSERT INTO OrderDetails
            // ==============================
            String insertDetail =
                    "INSERT INTO OrderDetails (OrderID, ItemCode, Quantity, UnitPrice) VALUES (?, ?, ?,?)";

            PreparedStatement detailPS =
                    conn.prepareStatement(insertDetail);

            List<OrderDetail> details = order.getOrderDetails();

            for (OrderDetail detail : details) {

                detailPS.setInt(1, newOrderId);

                detailPS.setString(
                        2,
                        detail.getMenuItem().getItemCode()
                );

                detailPS.setInt(
                        3,
                        detail.getQuantity()
                );

                detailPS.setDouble(
                        4,
                        detail.getUnitPrice()
                );


                detailPS.addBatch();
            }

            detailPS.executeBatch();

            // ==============================
            // CALL STORED PROCEDURE
            // ==============================
            CallableStatement cs =
                    conn.prepareCall("{CALL UpdateOrderTotal(?)}");

            cs.setInt(1, newOrderId);

            cs.execute();

            // ==============================
            // COMMIT
            // ==============================
            conn.commit();

            JOptionPane.showMessageDialog(
                    null,
                    "Order #" + newOrderId + " saved successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

        }

        catch (SQLException e) {

            try {
                conn.rollback();
            } catch (SQLException ignored) {}

            JOptionPane.showMessageDialog(
                    null,
                    "Error saving order:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            return -1;
        }

        finally {

            try {
                conn.setAutoCommit(true);
            } catch (SQLException ignored) {}
        }

        return newOrderId;
    }
}