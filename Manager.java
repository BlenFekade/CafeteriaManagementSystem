package cafeteria;

import javax.swing.*;

public class Manager extends User {

    public Manager(int userId, String username, String password) {
        super(userId, username, password);
    }

    @Override
    public String getRole() {
        return "Manager";
    }

    @Override
    public boolean canEditMenu() {
        return true;
    }

    @Override
    public boolean canViewReports() {
        return true;
    }

    /**
     * GUI Message when manager adds menu item
     */
    public void addMenuItem(MenuItem item) {

        JOptionPane.showMessageDialog(
                null,
                "✅ Menu Item Added Successfully!\n\n" +
                        "Manager: " + getUsername() +
                        "\nItem Code: " + item.getItemCode() +
                        "\nItem Name: " + item.getItemName() +
                        "\nPrice: ETB " + item.getPrice() +
                        "\nCategory: " + item.getCategory(),
                "Menu Update",
                JOptionPane.INFORMATION_MESSAGE
        );
    }


    /**
     * GUI confirmation when manager updates a price
     */
    public void updatePrice(MenuItem item, double newPrice) {

        JOptionPane.showMessageDialog(
                null,
                "✅ Price Updated Successfully!\n\n" +
                        "Manager: " + getUsername() +
                        "\nItem: " + item.getItemName() +
                        "\nOld Price: ETB " + item.getPrice() +
                        "\nNew Price: ETB " + newPrice,
                "Price Update",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * GUI confirmation when manager removes a menu item
     */
    public void removeMenuItem(MenuItem item) {

        JOptionPane.showMessageDialog(
                null,
                "🗑️ Menu Item Removed!\n\n" +
                        "Manager: " + getUsername() +
                        "\nItem Code: " + item.getItemCode() +
                        "\nItem Name: " + item.getItemName(),
                "Item Removed",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public void viewSalesReport() {

        // Example report window
        JFrame reportFrame = new JFrame("Sales Report");

        reportFrame.setSize(500, 300);
        reportFrame.setLocationRelativeTo(null);

        JTextArea reportArea = new JTextArea();

        reportArea.setEditable(false);

        reportArea.setText(
                "========== SALES REPORT ==========\n\n" +
                        "Manager: " + getUsername() + "\n\n" +
                        "• Total Orders: 25\n" +
                        "• Total Revenue: ETB 15,500\n" +
                        "• Most Sold Item: Macchiato\n" +
                        "• Pending Orders: 2\n\n" +
                        "=================================="
        );

        JScrollPane scrollPane = new JScrollPane(reportArea);

        reportFrame.add(scrollPane);

        reportFrame.setVisible(true);
    }
}