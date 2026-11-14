package cafeteria;

import javax.swing.*;
import java.util.*;

/**
 * GUI Version of Order Class
 */
public class Order implements OrderProcessor {

    private static int nextOrderId = 1000;

    private final int orderId;
    private final Date orderDate;
    private double totalBill;
    private boolean isCompleted;

    private final User cashier;

    private final List<OrderDetail> orderDetails;

    // CONSTRUCTOR

    public Order(User cashier) {

        this.orderId = nextOrderId++;

        this.orderDate = new Date();

        this.totalBill = 0;

        this.isCompleted = false;

        this.cashier = cashier;

        this.orderDetails = new ArrayList<>();
    }

    // GETTERS & SETTERS

    public int getOrderId() {
        return orderId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public double getTotalBill() {
        return totalBill;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public User getCashier() {
        return cashier;
    }

    @Override
    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    // GET SPECIFIC ORDER DETAIL

    public OrderDetail getOrderDetail(String itemCode) {

        for (OrderDetail od : orderDetails) {

            if (od.getMenuItem()
                    .getItemCode()
                    .equalsIgnoreCase(itemCode)) {

                return od;
            }
        }

        return null;
    }


    // ADD ITEM

    @Override
    public void addItem(MenuItem item, int quantity) {

        if (!item.isAvailable()) {

            JOptionPane.showMessageDialog(
                    null,
                    "Item not available:\n" +
                            item.getItemName(),
                    "Unavailable Item",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        if (quantity <= 0) {

            JOptionPane.showMessageDialog(
                    null,
                    "Quantity must be positive!",
                    "Invalid Quantity",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        // Check existing item
        OrderDetail existing =
                getOrderDetail(item.getItemCode());

        if (existing != null) {

            existing.addQuantity(quantity);

            JOptionPane.showMessageDialog(
                    null,
                    quantity + " more " +
                            item.getItemName() +
                            " added.\nTotal Quantity: " +
                            existing.getQuantity(),
                    "Item Updated",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } else {

            OrderDetail newDetail =
                    new OrderDetail(item, quantity);

            orderDetails.add(newDetail);

            JOptionPane.showMessageDialog(
                    null,
                    "Added:\n" +
                            quantity +
                            " x " +
                            item.getItemName(),
                    "Item Added",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }

        calculateTotal();
    }

    // REMOVE ITEM


    @Override
    public void removeItem(String itemCode) {

        OrderDetail toRemove =
                getOrderDetail(itemCode);

        if (toRemove != null) {

            orderDetails.remove(toRemove);

            calculateTotal();

            JOptionPane.showMessageDialog(
                    null,
                    "Removed:\n" +
                            toRemove.getMenuItem()
                                    .getItemName(),
                    "Item Removed",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } else {

            JOptionPane.showMessageDialog(
                    null,
                    "Item not found in order!",
                    "Remove Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // UPDATE QUANTITY

    @Override
    public void updateQuantity(String itemCode,
                               int newQuantity) {

        if (newQuantity <= 0) {

            removeItem(itemCode);

            return;
        }

        OrderDetail detail =
                getOrderDetail(itemCode);

        if (detail != null) {

            detail.setQuantity(newQuantity);

            calculateTotal();

            JOptionPane.showMessageDialog(
                    null,
                    "Updated:\n" +
                            detail.getMenuItem()
                                    .getItemName() +
                            "\nNew Quantity: " +
                            newQuantity,
                    "Quantity Updated",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } else {

            JOptionPane.showMessageDialog(
                    null,
                    "Item not found in order!",
                    "Update Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // CALCULATE TOTAL

    @Override
    public void calculateTotal() {

        totalBill = 0;

        for (OrderDetail detail : orderDetails) {

            totalBill += detail.getSubtotal();
        }

    }

    // CHECKOUT

    @Override
    public void checkout() {

        if (orderDetails.isEmpty()) {

            JOptionPane.showMessageDialog(
                    null,
                    "Cannot checkout empty order!",
                    "Checkout Error",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        calculateTotal();

        JOptionPane.showMessageDialog(
                null,
                "========== CHECKOUT ==========\n\n" +
                        "Order #: " + orderId +
                        "\nTotal Items: " +
                        orderDetails.size() +
                        "\nTotal Bill: ETB " +
                        String.format("%.2f", totalBill),
                "Checkout",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // PRINT RECEIPT

    @Override
    public void printReceipt() {

        StringBuilder receipt =
                new StringBuilder();

        receipt.append("================================\n");
        receipt.append("       CAFETERIA RECEIPT\n");
        receipt.append("================================\n");

        receipt.append("Order #: ")
                .append(orderId)
                .append("\n");

        receipt.append("Date: ")
                .append(orderDate)
                .append("\n");

        receipt.append("Cashier: ")
                .append(cashier.getUsername())
                .append("\n");

        receipt.append("--------------------------------\n");

        for (OrderDetail detail : orderDetails) {

            MenuItem item =
                    detail.getMenuItem();

            receipt.append(item.getItemName())
                    .append(" x")
                    .append(detail.getQuantity())
                    .append(" = ETB ")
                    .append(String.format("%.2f",
                            detail.getSubtotal()))
                    .append("\n");
        }

        receipt.append("--------------------------------\n");

        receipt.append("TOTAL: ETB ")
                .append(String.format("%.2f",
                        totalBill))
                .append("\n");

        receipt.append("================================");

        JTextArea textArea =
                new JTextArea(receipt.toString());

        textArea.setEditable(false);

        JOptionPane.showMessageDialog(
                null,
                new JScrollPane(textArea),
                "Receipt",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // =====================================================
    // SHOW ORDER SUMMARY
    // =====================================================

    public void showOrderSummary() {

        if (orderDetails.isEmpty()) {

            JOptionPane.showMessageDialog(
                    null,
                    "No items in order.",
                    "Order Summary",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }

        StringBuilder summary =
                new StringBuilder();

        summary.append("ORDER #")
                .append(orderId)
                .append("\n\n");

        for (OrderDetail detail : orderDetails) {

            summary.append(detail)
                    .append("\n");
        }

        summary.append("\n----------------------\n");

        summary.append("Total: ETB ")
                .append(String.format("%.2f",
                        totalBill));

        JTextArea textArea =
                new JTextArea(summary.toString());

        textArea.setEditable(false);

        JOptionPane.showMessageDialog(
                null,
                new JScrollPane(textArea),
                "Order Summary",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // =====================================================
    // GET ITEM COUNT
    // =====================================================

    public int getItemCount() {

        return orderDetails.size();
    }

    // =====================================================
    // CLEAR ORDER
    // =====================================================

    public void clearOrder() {

        orderDetails.clear();

        totalBill = 0;

        JOptionPane.showMessageDialog(
                null,
                "Order Cleared Successfully!",
                "Clear Order",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}