package servertcp;

import java.io.Serializable;

public class OrderItemData implements Serializable {

    private int menuId;
    private int quantity;
    private double subtotal;

    public OrderItemData() {
    }

    public OrderItemData(int menuId, int quantity, double subtotal) {
        this.menuId = menuId;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
}
