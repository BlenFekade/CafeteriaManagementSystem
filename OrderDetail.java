package cafeteria;

public class OrderDetail {

    private final int detailId;
    private final MenuItem menuItem;
    private int quantity;
    private double subtotal;
    private double unitPrice;
    private static int nextId = 1;

    // Constructor
    public OrderDetail(MenuItem menuItem, int quantity) {
        this.detailId = nextId++;
        this.menuItem = menuItem;
        this.quantity = quantity;

        this.subtotal = menuItem.getSubtotal(quantity);
        this.unitPrice = this.subtotal / quantity;
    }

    // Getters
    public int getDetailId() {
        return detailId;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getUnitPrice() {
        return unitPrice;
    }


    // Update quantity (no console output → GUI handles feedback)
    public void setQuantity(int quantity) {
        if (quantity > 0) {
            this.quantity = quantity;
            this.subtotal = menuItem.getSubtotal(quantity);
        }
    }

    // Increase quantity
    public void addQuantity(int amount) {
        if (amount > 0) {
            this.quantity += amount;
            this.subtotal = menuItem.getSubtotal(this.quantity);
        }
    }

    @Override
    public String toString() {
        return menuItem.getItemName() + " x" + quantity + " = " + String.format("%.2f", subtotal);
    }
}