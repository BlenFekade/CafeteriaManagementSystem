package cafeteria;
import java.util.List;
public interface OrderProcessor {
	void addItem(MenuItem item, int quantity);
    void removeItem(String itemCode);
    void updateQuantity(String itemCode, int newQuantity);
    void calculateTotal();
    void checkout();
    void printReceipt();
    List<OrderDetail> getOrderDetails();

}
