package cafeteria;

import javax.swing.*;

/**
 * GUI Version of MenuItem Class
 */
public class MenuItem {

    private final String itemCode;
    private final String itemName;
    private double price;
    private final String category;
    private boolean available;

    /**
     * Constructor
     */
    public MenuItem(String itemCode,
                    String itemName,
                    double price,
                    String category) {

        this.itemCode = itemCode;
        this.itemName = itemName;
        this.price = price;
        this.category = category;
        this.available = true;
    }

    // GETTERS

    public String getItemCode() {
        return itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public double getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public boolean isAvailable() {
        return available;
    }

    // SETTERS

    /**
     * Update item price with GUI validation
     */
    public void setPrice(double price) {

        if (price >= 0) {

            this.price = price;

            JOptionPane.showMessageDialog(
                    null,
                    "Price Updated Successfully!\n\n" +
                            "Item: " + itemName +
                            "\nNew Price: ETB " + price,
                    "Price Update",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } else {

            JOptionPane.showMessageDialog(
                    null,
                    "Price cannot be negative!",
                    "Invalid Price",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Change availability
     */
    public void setAvailable(boolean available) {

        this.available = available;

        String status;

        if (available) {
            status = "Available";
        } else {
            status = "Unavailable";
        }

        JOptionPane.showMessageDialog(
                null,
                itemName + " is now " + status,
                "Availability Changed",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // BUSINESS METHODS

    /**
     * Calculate subtotal
     */
    public double getSubtotal(int quantity) {

        return this.price * quantity;
    }

    /**
     * GUI display for item details
     */
    public void showItemDetails() {

        JOptionPane.showMessageDialog(
                null,
                "========== MENU ITEM ==========\n\n" +
                        "Code: " + itemCode +
                        "\nName: " + itemName +
                        "\nPrice: ETB " + price +
                        "\nCategory: " + category +
                        "\nAvailable: " + available,
                "Menu Item Details",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Convert object to string
     */
    @Override
    public String toString() {

        return itemCode +
                " - " +
                itemName +
                " : ETB " +
                price +
                " (" + category + ")";
    }


    // TEST MAIN METHOD

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            MenuItem item = new MenuItem(
                    "F01",
                    "Burger",
                    150,
                    "Food"
            );

            item.showItemDetails();

            item.setPrice(200);

            item.setAvailable(false);
        });
    }
}