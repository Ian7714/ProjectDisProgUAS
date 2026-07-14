package servertcp;

import java.io.Serializable;
import java.util.List;

public class FoodOrder implements Serializable {

    private int id;
    private int reservationId;
    private String status; // PENDING, PREPARING, READY, SERVED
    private double totalAmount;
    private List<OrderItemData> items;

    public FoodOrder() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<OrderItemData> getItems() {
        return items;
    }

    public void setItems(List<OrderItemData> items) {
        this.items = items;
    }
}
