package com.uas.service.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** "Specific Model" untuk tabel menu_items. */
public class MenuItem extends MyModel {

    private int id;
    private String name;
    private String category;
    private double price;
    private String description;
    private boolean available;

    public MenuItem() {}

    // Constructor untuk TAMBAH menu baru (belum ada id)
    public MenuItem(String name, String category, double price, String description) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.description = description;
        this.available = true;
    }

    // Constructor LENGKAP (dari database)
    public MenuItem(int id, String name, String category, double price, String description, boolean available) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.description = description;
        this.available = available;
    }

    // Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    // ================= InsertData =================
    public boolean insertData() {
        String sql = "INSERT INTO menu_items (name, category, price, description, available) VALUES (?, ?, ?, ?, TRUE)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, category);
            ps.setDouble(3, price);
            ps.setString(4, description);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("insertData MenuItem error: " + e.getMessage());
            return false;
        }
    }

    // ================= UpdateData =================
    public boolean updateData() {
        String sql = "UPDATE menu_items SET name=?, category=?, price=?, description=?, available=? WHERE id=?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, category);
            ps.setDouble(3, price);
            ps.setString(4, description);
            ps.setBoolean(5, available);
            ps.setInt(6, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("updateData MenuItem error: " + e.getMessage());
            return false;
        }
    }

    // ================= DeleteData =================
    public boolean deleteData() {
        String sql = "DELETE FROM menu_items WHERE id=?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("deleteData MenuItem error: " + e.getMessage());
            return false;
        }
    }

    // ================= ViewListData =================
    public List<MenuItem> viewListData() {
        List<MenuItem> list = new ArrayList<>();
        String sql = "SELECT id, name, category, price, description, available FROM menu_items ORDER BY category, name";
        try (Connection con = getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.out.println("viewListData MenuItem error: " + e.getMessage());
        }
        return list;
    }

    // Cari menu berdasarkan nama/kategori (dipakai fitur search)
    public List<MenuItem> viewListDataByKeyword(String keyword) {
        List<MenuItem> list = new ArrayList<>();
        String sql = "SELECT id, name, category, price, description, available FROM menu_items " +
                "WHERE name LIKE ? OR category LIKE ? ORDER BY name";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String kw = "%" + keyword + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("viewListDataByKeyword error: " + e.getMessage());
        }
        return list;
    }

    // ================= ViewListDataString =================
    public List<String> viewListDataString() {
        List<String> result = new ArrayList<>();
        for (MenuItem m : viewListData()) {
            result.add(m.getId() + ";" + m.getName() + ";" + m.getCategory() + ";" + m.getPrice()
                    + ";" + m.getDescription() + ";" + m.isAvailable());
        }
        return result;
    }

    private MenuItem mapRow(ResultSet rs) throws SQLException {
        return new MenuItem(rs.getInt("id"), rs.getString("name"), rs.getString("category"),
                rs.getDouble("price"), rs.getString("description"), rs.getBoolean("available"));
    }
}
