package com.uas.service.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * "Specific Model" read-only untuk laporan riwayat reservasi.
 * Menggabungkan tabel reservations + restaurant_tables + food_orders.
 */
public class ReservationHistory extends MyModel {

    private int reservationId;
    private String tableNumber;
    private String reservationDate;
    private String reservationTime;
    private int guestCount;
    private String status;
    private double totalOrderAmount;

    public ReservationHistory() {}

    public ReservationHistory(int reservationId, String tableNumber, String reservationDate,
                               String reservationTime, int guestCount, String status, double totalOrderAmount) {
        this.reservationId = reservationId;
        this.tableNumber = tableNumber;
        this.reservationDate = reservationDate;
        this.reservationTime = reservationTime;
        this.guestCount = guestCount;
        this.status = status;
        this.totalOrderAmount = totalOrderAmount;
    }

    public int getReservationId() { return reservationId; }
    public void setReservationId(int reservationId) { this.reservationId = reservationId; }
    public String getTableNumber() { return tableNumber; }
    public void setTableNumber(String tableNumber) { this.tableNumber = tableNumber; }
    public String getReservationDate() { return reservationDate; }
    public void setReservationDate(String reservationDate) { this.reservationDate = reservationDate; }
    public String getReservationTime() { return reservationTime; }
    public void setReservationTime(String reservationTime) { this.reservationTime = reservationTime; }
    public int getGuestCount() { return guestCount; }
    public void setGuestCount(int guestCount) { this.guestCount = guestCount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getTotalOrderAmount() { return totalOrderAmount; }
    public void setTotalOrderAmount(double totalOrderAmount) { this.totalOrderAmount = totalOrderAmount; }

    // ================= ViewListData (per user) =================
    public List<ReservationHistory> viewListDataByUser(int userId) {
        List<ReservationHistory> list = new ArrayList<>();
        String sql = "SELECT r.id, t.table_number, r.reservation_date, r.reservation_time, " +
                "r.guest_count, r.status, COALESCE(fo.total_amount,0) AS total " +
                "FROM reservations r " +
                "JOIN restaurant_tables t ON r.table_id = t.id " +
                "LEFT JOIN food_orders fo ON fo.reservation_id = r.id " +
                "WHERE r.user_id = ? " +
                "ORDER BY r.reservation_date DESC, r.reservation_time DESC";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("viewListDataByUser error: " + e.getMessage());
        }
        return list;
    }

    // ================= ViewListData (rentang tanggal, untuk admin) =================
    public List<ReservationHistory> viewListDataByPeriod(String startDate, String endDate) {
        List<ReservationHistory> list = new ArrayList<>();
        String sql = "SELECT r.id, t.table_number, r.reservation_date, r.reservation_time, " +
                "r.guest_count, r.status, COALESCE(fo.total_amount,0) AS total " +
                "FROM reservations r " +
                "JOIN restaurant_tables t ON r.table_id = t.id " +
                "LEFT JOIN food_orders fo ON fo.reservation_id = r.id " +
                "WHERE r.reservation_date BETWEEN ? AND ? " +
                "ORDER BY r.reservation_date DESC, r.reservation_time DESC";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, startDate);
            ps.setString(2, endDate);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("viewListDataByPeriod error: " + e.getMessage());
        }
        return list;
    }

    private ReservationHistory mapRow(ResultSet rs) throws SQLException {
        return new ReservationHistory(rs.getInt("id"), rs.getString("table_number"),
                rs.getString("reservation_date"), rs.getString("reservation_time"),
                rs.getInt("guest_count"), rs.getString("status"), rs.getDouble("total"));
    }
}
