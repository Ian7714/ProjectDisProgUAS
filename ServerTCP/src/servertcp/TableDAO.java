package servertcp;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TableDAO {

    public List<RestaurantTable> getAllTables() throws SQLException {
        List<RestaurantTable> list = new ArrayList<>();
        String sql = "SELECT id, table_number, capacity, status FROM restaurant_tables ORDER BY table_number";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new RestaurantTable(
                        rs.getInt("id"),
                        rs.getString("table_number"),
                        rs.getInt("capacity"),
                        rs.getString("status")));
            }
        }
        return list;
    }

    /**
     * Mencari meja dengan kapasitas cukup yang BELUM terpakai
     * pada tanggal & jam tertentu. Dipanggil di dalam transaksi
     * ReservationDAO agar aman dari race condition.
     */
    public RestaurantTable findAvailableTable(Connection con, int guestCount,
                                               String date, String time) throws SQLException {
        String sql = "SELECT t.id, t.table_number, t.capacity, t.status " +
                "FROM restaurant_tables t " +
                "WHERE t.capacity >= ? " +
                "AND t.id NOT IN ( " +
                "   SELECT r.table_id FROM reservations r " +
                "   WHERE r.reservation_date = ? AND r.reservation_time = ? " +
                "   AND r.status IN ('PENDING','CONFIRMED') " +
                ") " +
                "ORDER BY t.capacity ASC LIMIT 1 FOR UPDATE";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, guestCount);
            ps.setString(2, date);
            ps.setString(3, time);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new RestaurantTable(rs.getInt("id"), rs.getString("table_number"),
                            rs.getInt("capacity"), rs.getString("status"));
                }
            }
        }
        return null;
    }

    public void updateStatus(int tableId, String status) throws SQLException {
        String sql = "UPDATE restaurant_tables SET status = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, tableId);
            ps.executeUpdate();
        }
    }
}
