package servertcp;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    private final TableDAO tableDAO = new TableDAO();

    public Reservation createReservation(int userId, String date, String time, int guestCount)
            throws SQLException {

        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);
            con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

            // 1. Cari meja yang tersedia (row-locked dengan FOR UPDATE)
            RestaurantTable table = tableDAO.findAvailableTable(con, guestCount, date, time);
            if (table == null) {
                con.rollback();
                throw new SQLException("Tidak ada meja tersedia untuk " + guestCount
                        + " tamu pada " + date + " " + time);
            }

            // 2. Insert reservasi
            String sql = "INSERT INTO reservations (user_id, table_id, reservation_date, "
                    + "reservation_time, guest_count, status) VALUES (?, ?, ?, ?, ?, 'CONFIRMED')";
            int reservationId;
            try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, userId);
                ps.setInt(2, table.getId());
                ps.setString(3, date);
                ps.setString(4, time);
                ps.setInt(5, guestCount);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    reservationId = keys.getInt(1);
                }
            }

            // 3. Update status meja menjadi RESERVED
            try (PreparedStatement ps2 = con.prepareStatement(
                    "UPDATE restaurant_tables SET status='RESERVED' WHERE id=?")) {
                ps2.setInt(1, table.getId());
                ps2.executeUpdate();
            }

            con.commit();

            return new Reservation(reservationId, userId, table.getId(), date, time,
                    guestCount, "CONFIRMED");

        } catch (SQLException e) {
            if (con != null) {
                con.rollback();
            }
            throw e;
        } finally {
            if (con != null) {
                con.setAutoCommit(true);
                con.close();
            }
        }
    }

    public void cancelReservation(int reservationId) throws SQLException {
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            int tableId = -1;
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT table_id FROM reservations WHERE id=? FOR UPDATE")) {
                ps.setInt(1, reservationId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("Reservasi tidak ditemukan");
                    }
                    tableId = rs.getInt("table_id");
                }
            }

            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE reservations SET status='CANCELLED' WHERE id=?")) {
                ps.setInt(1, reservationId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE restaurant_tables SET status='AVAILABLE' WHERE id=?")) {
                ps.setInt(1, tableId);
                ps.executeUpdate();
            }

            con.commit();
        } catch (SQLException e) {
            if (con != null) {
                con.rollback();
            }
            throw e;
        } finally {
            if (con != null) {
                con.setAutoCommit(true);
                con.close();
            }
        }
    }

    public List<Reservation> getReservationsByUser(int userId) throws SQLException {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT id, user_id, table_id, reservation_date, reservation_time, "
                + "guest_count, status FROM reservations WHERE user_id = ? ORDER BY reservation_date DESC, reservation_time DESC";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Reservation(
                            rs.getInt("id"), rs.getInt("user_id"), rs.getInt("table_id"),
                            rs.getString("reservation_date"), rs.getString("reservation_time"),
                            rs.getInt("guest_count"), rs.getString("status")));
                }
            }
        }
        return list;
    }
}
