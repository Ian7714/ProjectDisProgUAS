package servertcp;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    public FoodOrder createOrder(int reservationId, List<int[]> items /* [menuId, qty] */) throws SQLException {
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            double total = 0;
            List<OrderItemData> savedItems = new ArrayList<>();

            // Hitung harga tiap item dari tabel menu_items
            String priceSql = "SELECT price FROM menu_items WHERE id = ?";
            try (PreparedStatement psPrice = con.prepareStatement(priceSql)) {
                for (int[] item : items) {
                    psPrice.setInt(1, item[0]);
                    try (ResultSet rs = psPrice.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Menu id " + item[0] + " tidak ditemukan");
                        }
                        double price = rs.getDouble("price");
                        double subtotal = price * item[1];
                        total += subtotal;
                        savedItems.add(new OrderItemData(item[0], item[1], subtotal));
                    }
                }
            }

            int orderId;
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO food_orders (reservation_id, status, total_amount) VALUES (?, 'PENDING', ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, reservationId);
                ps.setDouble(2, total);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    orderId = keys.getInt(1);
                }
            }

            try (PreparedStatement psItem = con.prepareStatement(
                    "INSERT INTO order_items (order_id, menu_id, quantity, subtotal) VALUES (?, ?, ?, ?)")) {
                for (OrderItemData d : savedItems) {
                    psItem.setInt(1, orderId);
                    psItem.setInt(2, d.getMenuId());
                    psItem.setInt(3, d.getQuantity());
                    psItem.setDouble(4, d.getSubtotal());
                    psItem.addBatch();
                }
                psItem.executeBatch();
            }

            con.commit();

            FoodOrder order = new FoodOrder();
            order.setId(orderId);
            order.setReservationId(reservationId);
            order.setStatus("PENDING");
            order.setTotalAmount(total);
            order.setItems(savedItems);
            return order;

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

    public void updateStatus(int orderId, String status) throws SQLException {
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(
                "UPDATE food_orders SET status=? WHERE id=?")) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Order id " + orderId + " tidak ditemukan");
            }
        }
    }

    public List<FoodOrder> getAllActiveOrders() throws SQLException {
        List<FoodOrder> list = new ArrayList<>();
        String sql = "SELECT id, reservation_id, status, total_amount FROM food_orders "
                + "WHERE status != 'SERVED' ORDER BY created_at ASC";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                FoodOrder o = new FoodOrder();
                o.setId(rs.getInt("id"));
                o.setReservationId(rs.getInt("reservation_id"));
                o.setStatus(rs.getString("status"));
                o.setTotalAmount(rs.getDouble("total_amount"));
                list.add(o);
            }
        }
        return list;
    }
}
