package cafeteria;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main extends JFrame {

    // DAO objects
    private final UserDAO userDAO = new UserDAO();
    private final MenuItemDAO menuItemDAO = new MenuItemDAO();
    private final OrderDAO orderDAO = new OrderDAO();

    // Logged-in user
    private User loggedInUser;

    // Menu items
    private List<MenuItem> menu = new ArrayList<>();

    // Current order
    private Order currentOrder;

    // GUI Components
    private JTable menuTable;
    private JTable orderTable;

    private DefaultTableModel menuModel;
    private DefaultTableModel orderModel;

    private JLabel totalLabel;

    public Main() {

        // Load menu from database
        menu = menuItemDAO.getAllMenuItems();

        // Start login screen
        showLoginScreen();
    }

    // LOGIN SCREEN
    private void showLoginScreen() {

        JFrame loginFrame = new JFrame("Cafeteria Login");
        loginFrame.setSize(400, 250);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel title = new JLabel("MINI CAFETERIA SYSTEM");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setBounds(70, 20, 300, 30);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(50, 80, 100, 25);

        JTextField userField = new JTextField();
        userField.setBounds(150, 80, 180, 25);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(50, 120, 100, 25);

        JPasswordField passField = new JPasswordField();
        passField.setBounds(150, 120, 180, 25);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(140, 170, 100, 35);

        panel.add(title);
        panel.add(userLabel);
        panel.add(userField);
        panel.add(passLabel);
        panel.add(passField);
        panel.add(loginButton);

        loginFrame.add(panel);
        loginFrame.setVisible(true);

        // Login Action
        loginButton.addActionListener(e -> {

            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();

            loggedInUser = userDAO.login(username, password);

            if (loggedInUser != null) {

                JOptionPane.showMessageDialog(loginFrame,
                        "Welcome " + loggedInUser.getUsername());

                loginFrame.dispose();

                if (loggedInUser instanceof Cashier) {
                    showCashierDashboard((Cashier) loggedInUser);
                } else if (loggedInUser instanceof Manager) {
                    showManagerDashboard((Manager) loggedInUser);
                }

            } else {
                JOptionPane.showMessageDialog(loginFrame,
                        "Invalid Username or Password");
            }
        });
    }

    // MANAGER DASHBOARD
    private void showManagerDashboard(Manager manager) {

        JFrame frame = new JFrame("Manager Dashboard");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();

        JButton viewMenuBtn = new JButton("View Menu");
        JButton addItemBtn = new JButton("Add Menu Item");
        JButton updatePriceBtn = new JButton("Update Price");
        JButton removeItemBtn = new JButton("Remove Menu Item");
        JButton salesBtn = new JButton("Sales Report");

        panel.add(viewMenuBtn);
        panel.add(addItemBtn);
        panel.add(updatePriceBtn);
        panel.add(removeItemBtn);
        panel.add(salesBtn);

        frame.add(panel);
        frame.setVisible(true);

        // View Menu
        viewMenuBtn.addActionListener(e -> showMenuTable());

        // Add Menu Item
        addItemBtn.addActionListener(e -> {

            JTextField codeField = new JTextField();
            JTextField nameField = new JTextField();
            JTextField priceField = new JTextField();

            String[] categories = {"Food", "Drink", "Snack"};
            JComboBox<String> categoryBox = new JComboBox<>(categories);

            Object[] fields = {
                    "Item Code:", codeField,
                    "Item Name:", nameField,
                    "Price:", priceField,
                    "Category:", categoryBox
            };

            int result = JOptionPane.showConfirmDialog(
                    null,
                    fields,
                    "Add Menu Item",
                    JOptionPane.OK_CANCEL_OPTION
            );

            if (result == JOptionPane.OK_OPTION) {

                try {

                    String code = codeField.getText().trim();
                    String name = nameField.getText().trim();
                    double price = Double.parseDouble(priceField.getText());

                    String category = Objects.requireNonNull(categoryBox.getSelectedItem()).toString();

                    int catId = switch (category) {
                        case "Food" -> 1;
                        case "Drink" -> 2;
                        default -> 3;
                    };

                    MenuItem item = new MenuItem(code, name, price, category);

                    manager.addMenuItem(item);
                    menuItemDAO.insertMenuItem(item, catId);

                    menu.add(item);

                    // Refresh menu table if cashier dashboard is open
                    if (menuModel != null) {
                        menuModel.addRow(new Object[]{
                                item.getItemCode(),
                                item.getItemName(),
                                item.getPrice(),
                                item.getCategory()
                        });
                    }

                    JOptionPane.showMessageDialog(null,
                            "Menu item added successfully!");

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null,
                            "Invalid input!");
                }
            }
        });

        // Update Price
        updatePriceBtn.addActionListener(e -> {

            if (menu.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No menu items loaded.");
                return;
            }

            String[] itemLabels = menu.stream()
                    .map(i -> i.getItemCode() + " - " + i.getItemName() + " (ETB " + i.getPrice() + ")")
                    .toArray(String[]::new);

            JComboBox<String> itemBox = new JComboBox<>(itemLabels);
            JTextField newPriceField = new JTextField();

            Object[] fields = {
                    "Select Item:", itemBox,
                    "New Price (ETB):", newPriceField
            };

            int result = JOptionPane.showConfirmDialog(
                    null, fields, "Update Menu Item Price",
                    JOptionPane.OK_CANCEL_OPTION
            );

            if (result == JOptionPane.OK_OPTION) {
                try {
                    int selectedIndex = itemBox.getSelectedIndex();
                    MenuItem selectedItem = menu.get(selectedIndex);
                    double newPrice = Double.parseDouble(newPriceField.getText().trim());

                    if (newPrice < 0) {
                        JOptionPane.showMessageDialog(null, "Price cannot be negative!");
                        return;
                    }

                    boolean success = menuItemDAO.updateMenuItemPrice(
                            selectedItem.getItemCode(), newPrice);

                    if (success) {
                        manager.updatePrice(selectedItem, newPrice);

                        // FIX 1: Update the in-memory object so it holds the new price
                        selectedItem.setPrice(newPrice);

                        // FIX 2: Reload full menu list from DB
                        menu = menuItemDAO.getAllMenuItems();

                        // FIX 3: Refresh the cashier menu table if it is open
                        if (menuModel != null) {
                            menuModel.setRowCount(0);
                            for (MenuItem m : menu) {
                                menuModel.addRow(new Object[]{
                                        m.getItemCode(),
                                        m.getItemName(),
                                        m.getPrice(),
                                        m.getCategory()
                                });
                            }
                        }
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid price entered.");
                }
            }
        });

        // Remove Menu Item
        removeItemBtn.addActionListener(e -> {

            if (menu.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No menu items loaded.");
                return;
            }

            String[] itemLabels = menu.stream()
                    .map(i -> i.getItemCode() + " - " + i.getItemName() + " (ETB " + i.getPrice() + ")")
                    .toArray(String[]::new);

            JComboBox<String> itemBox = new JComboBox<>(itemLabels);

            Object[] fields = { "Select Item to Remove:", itemBox };

            int result = JOptionPane.showConfirmDialog(
                    null, fields, "Remove Menu Item",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (result == JOptionPane.OK_OPTION) {

                int selectedIndex = itemBox.getSelectedIndex();
                MenuItem selectedItem = menu.get(selectedIndex);

                int confirm = JOptionPane.showConfirmDialog(
                        null,
                        "Are you sure you want to remove:\n" +
                                selectedItem.getItemCode() + " - " + selectedItem.getItemName() + "?",
                        "Confirm Removal",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    boolean success = menuItemDAO.deleteMenuItem(selectedItem.getItemCode());
                    if (success) {
                        manager.removeMenuItem(selectedItem);

                        // Reload menu list from DB
                        menu = menuItemDAO.getAllMenuItems();

                        // Refresh cashier menu table if open
                        if (menuModel != null) {
                            menuModel.setRowCount(0);
                            for (MenuItem m : menu) {
                                menuModel.addRow(new Object[]{
                                        m.getItemCode(),
                                        m.getItemName(),
                                        m.getPrice(),
                                        m.getCategory()
                                });
                            }
                        }
                    }
                }
            }
        });

        // Sales Report
        salesBtn.addActionListener(e ->
                manager.viewSalesReport());
    }

    // CASHIER DASHBOARD
    private void showCashierDashboard(Cashier cashier) {

        currentOrder = new Order(cashier);

        JFrame frame = new JFrame("Cashier Dashboard");
        frame.setSize(1000, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // LEFT SIDE MENU TABLE
        String[] menuColumns = {"Code", "Item", "Price", "Category"};

        menuModel = new DefaultTableModel(menuColumns, 0);

        for (MenuItem item : menu) {
            menuModel.addRow(new Object[]{
                    item.getItemCode(),
                    item.getItemName(),
                    item.getPrice(),
                    item.getCategory()
            });
        }

        menuTable = new JTable(menuModel);

        JScrollPane menuScroll = new JScrollPane(menuTable);

        // RIGHT SIDE ORDER TABLE
        String[] orderColumns = {"Code", "Item", "Qty", "Price"};

        orderModel = new DefaultTableModel(orderColumns, 0);

        orderTable = new JTable(orderModel);

        JScrollPane orderScroll = new JScrollPane(orderTable);

        // BUTTONS
        JButton addBtn = new JButton("Add Item");
        JButton removeBtn = new JButton("Remove Item");
        JButton checkoutBtn = new JButton("Checkout");

        totalLabel = new JLabel("Total: ETB 0.00");

        JPanel buttonPanel = new JPanel();

        buttonPanel.add(addBtn);
        buttonPanel.add(removeBtn);
        buttonPanel.add(checkoutBtn);
        buttonPanel.add(totalLabel);

        // MAIN LAYOUT
        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                menuScroll,
                orderScroll
        );

        splitPane.setDividerLocation(500);

        frame.add(splitPane, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);

        // ADD ITEM
        addBtn.addActionListener(e -> {

            int selectedRow = menuTable.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(frame,
                        "Select an item first.");
                return;
            }

            String code = menuModel.getValueAt(selectedRow, 0).toString();

            MenuItem item = findMenuItem(code);

            String qtyInput = JOptionPane.showInputDialog(
                    frame,
                    "Enter Quantity:"
            );

            try {

                int qty = Integer.parseInt(qtyInput);

                // Reject zero or negative quantities before touching the order or table
                if (qty <= 0) {
                    JOptionPane.showMessageDialog(frame,
                            "❌ Invalid Quantity!\n\nQuantity must be a positive number.\nEntered: " + qty,
                            "Invalid Quantity",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                assert item != null;
                currentOrder.addItem(item, qty);

                orderModel.addRow(new Object[]{
                        item.getItemCode(),
                        item.getItemName(),
                        qty,
                        item.getPrice() * qty
                });

                updateTotal();

            } catch (NumberFormatException ex) {

                JOptionPane.showMessageDialog(frame,
                        "Invalid quantity. Please enter a whole number.",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // REMOVE ITEM
        removeBtn.addActionListener(e -> {

            int selectedRow = orderTable.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(frame,
                        "Select order item to remove.");
                return;
            }

            // Get the item code BEFORE removing the row
            String itemCode = orderModel.getValueAt(selectedRow, 0).toString();

            // Remove from the underlying Order object so getTotalBill() stays accurate
            currentOrder.removeItem(itemCode);

            // Remove from the UI table
            orderModel.removeRow(selectedRow);

            updateTotal();
        });

        // CHECKOUT
        checkoutBtn.addActionListener(e -> {

            if (currentOrder.getOrderDetails().isEmpty()) {

                JOptionPane.showMessageDialog(frame,
                        "Order is empty!");

                return;
            }

            String paymentInput = JOptionPane.showInputDialog(
                    frame,
                    "Enter Payment Amount:"
            );

            try {

                double payment = Double.parseDouble(paymentInput);

                cashier.processPayment(currentOrder, payment);

                if (currentOrder.isCompleted()) {

                    int savedId = orderDAO.saveOrder(currentOrder);

                    JOptionPane.showMessageDialog(frame,
                            "Payment Successful!\nOrder ID: " + savedId);

                    orderModel.setRowCount(0);

                    totalLabel.setText("Total: ETB 0.00");

                    currentOrder = new Order(cashier);
                }

            } catch (NumberFormatException ex) {

                JOptionPane.showMessageDialog(frame,
                        "Invalid payment amount.");
            }
        });
    }

    // HELPER METHODS
    private void updateTotal() {

        double total = 0;

        for (int i = 0; i < orderModel.getRowCount(); i++) {
            total += Double.parseDouble(
                    orderModel.getValueAt(i, 3).toString()
            );
        }

        totalLabel.setText(String.format("Total: ETB %.2f", total));
    }

    private MenuItem findMenuItem(String code) {

        for (MenuItem item : menu) {

            if (item.getItemCode().equalsIgnoreCase(code)) {
                return item;
            }
        }

        return null;
    }

    private void showMenuTable() {

        JFrame frame = new JFrame("Menu");

        frame.setSize(600, 400);

        String[] columns = {"Code", "Item", "Price", "Category"};

        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (MenuItem item : menu) {

            model.addRow(new Object[]{
                    item.getItemCode(),
                    item.getItemName(),
                    item.getPrice(),
                    item.getCategory()
            });
        }

        JTable table = new JTable(model);

        frame.add(new JScrollPane(table));

        frame.setVisible(true);
    }

    // MAIN METHOD

    public static void main(String[] args) {

        SwingUtilities.invokeLater(Main::new);
    }
}
