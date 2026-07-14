package servertcp;

import java.io.Serializable;

public class Reservation implements Serializable {

    private int id;
    private int userId;
    private int tableId;
    private String reservationDate; // yyyy-MM-dd
    private String reservationTime; // HH:mm
    private int guestCount;
    private String status;

    public Reservation() {
    }

    public Reservation(int id, int userId, int tableId, String reservationDate,
            String reservationTime, int guestCount, String status) {
        this.id = id;
        this.userId = userId;
        this.tableId = tableId;
        this.reservationDate = reservationDate;
        this.reservationTime = reservationTime;
        this.guestCount = guestCount;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public String getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(String reservationDate) {
        this.reservationDate = reservationDate;
    }

    public String getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(String reservationTime) {
        this.reservationTime = reservationTime;
    }

    public int getGuestCount() {
        return guestCount;
    }

    public void setGuestCount(int guestCount) {
        this.guestCount = guestCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return id + ";" + userId + ";" + tableId + ";" + reservationDate + ";"
                + reservationTime + ";" + guestCount + ";" + status;
    }
}
