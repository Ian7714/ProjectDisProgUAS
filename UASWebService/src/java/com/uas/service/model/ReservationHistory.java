package com.uas.service.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationHistory extends MyModel {

    private int reservationId;
    private String tableNumber;
    private String reservationDate;
    private String reservationTime;
    private int guestCount;
    private String status;
    private double totalOrderAmount;

    public ReservationHistory() {
        super();
    }

    public ReservationHistory(int reservationId, String tableNumber, String reservationDate,
            String reservationTime, int guestCount, String status, double totalOrderAmount) {
        super();
        this.reservationId = reservationId;
        this.tableNumber = tableNumber;
        this.reservationDate = reservationDate;
        this.reservationTime = reservationTime;
        this.guestCount = guestCount;
        this.status = status;
        this.totalOrderAmount = totalOrderAmount;
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
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

    public double getTotalOrderAmount() {
        return totalOrderAmount;
    }

    public void setTotalOrderAmount(double totalOrderAmount) {
        this.totalOrderAmount = totalOrderAmount;
    }

    // ================= ViewListData (per user) =================
    public List<ReservationHistory> viewListDataByUser(int userId) {
        List<ReservationHistory> list = new ArrayList<>();
        try {
            if (!connect.isClosed()) {
                PreparedStatement sql = connect.prepareStatement(
                        "SELECT r.id, t.table_number, r.reservation_date, r.reservation_time, "
                        + "r.guest_count, r.status, COALESCE(fo.total_amount,0) AS total "
                        + "FROM reservations r "
                        + "JOIN restaurant_tables t ON r.table_id = t.id "
                        + "LEFT JOIN food_orders fo ON fo.reservation_id = r.id "
                        + "WHERE r.user_id = ? "
                        + "ORDER BY r.reservation_date DESC, r.reservation_time DESC"
                );
                sql.setInt(1, userId);
                ResultSet rs = sql.executeQuery();
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
                rs.close();
                sql.close();
            } else {
                System.out.println("Koneksi Hilang");
            }
        } catch (SQLException ex) {
            System.out.println("Error di viewListDataByUser: " + ex);
        }
        return list;
    }

    // ================= ViewListData (rentang tanggal, untuk admin) =================
    public List<ReservationHistory> viewListDataByPeriod(String startDate, String endDate) {
        List<ReservationHistory> list = new ArrayList<>();
        try {
            if (!connect.isClosed()) {
                PreparedStatement sql = connect.prepareStatement(
                        "SELECT r.id, t.table_number, r.reservation_date, r.reservation_time, "
                        + "r.guest_count, r.status, COALESCE(fo.total_amount,0) AS total "
                        + "FROM reservations r "
                        + "JOIN restaurant_tables t ON r.table_id = t.id "
                        + "LEFT JOIN food_orders fo ON fo.reservation_id = r.id "
                        + "WHERE r.reservation_date BETWEEN ? AND ? "
                        + "ORDER BY r.reservation_date DESC, r.reservation_time DESC"
                );
                sql.setString(1, startDate);
                sql.setString(2, endDate);
                ResultSet rs = sql.executeQuery();
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
                rs.close();
                sql.close();
            } else {
                System.out.println("Koneksi Hilang");
            }
        } catch (SQLException ex) {
            System.out.println("Error di viewListDataByPeriod: " + ex);
        }
        return list;
    }

    private ReservationHistory mapRow(ResultSet rs) throws SQLException {
        return new ReservationHistory(rs.getInt("id"), rs.getString("table_number"),
                rs.getString("reservation_date"), rs.getString("reservation_time"),
                rs.getInt("guest_count"), rs.getString("status"), rs.getDouble("total"));
    }
}
