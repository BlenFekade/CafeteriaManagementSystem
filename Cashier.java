package cafeteria;

import javax.swing.*;

public class Cashier extends User {

    public Cashier(int userId, String username, String password) {
        super(userId, username, password);
    }

    @Override
    public String getRole() {
        return "Cashier";
    }

    @Override
    public boolean canEditMenu() {
        return false;
    }

    @Override
    public boolean canViewReports() {
        return false;
    }

    public void processPayment(Order order, double amount) {

        double total = order.getTotalBill();

        double change = amount - total;

        // Payment successful
        if (change >= 0) {

            order.setCompleted(true);

            JOptionPane.showMessageDialog(
                    null,
                    "✅ Payment Accepted!\n\n" +
                            "Total Bill: ETB " + total +
                            "\nPaid: ETB " + amount +
                            "\nChange: ETB " + String.format("%.2f", change),
                    "Payment Successful",
                    JOptionPane.INFORMATION_MESSAGE
            );

        }

        // Payment failed
        else {

            JOptionPane.showMessageDialog(
                    null,
                    "❌ Insufficient Payment!\n\n" +
                            "Total Bill: ETB " + total +
                            "\nPaid: ETB " + amount +
                            "\nStill Needed: ETB " + String.format("%.2f", -change),
                    "Payment Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}