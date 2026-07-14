package com.uas.service.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuItem extends MyModel {

    private int id;
    private String name;
    private String category;
    private double price;
    private String description;
    private boolean available;

    public MenuItem() {
        super();
    }

    // Constructor untuk TAMBAH menu baru (belum ada id)
    public MenuItem(String name, String category, double price, String description) {
        super();
        this.name = name;
        this.category = category;
        this.price = price;
        this.description = description;
        this.available = true;
    }

    // Constructor LENGKAP (dari database)
    public MenuItem(int id, String name, String category, double price, String description, boolean available) {
        super();
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.description = description;
        this.available = available;
    }

    // Getter & Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    // ================= InsertData =================
    public boolean insertData() {
        try {
            if (!connect.isClosed()) {
                PreparedStatement sql = connect.prepareStatement(
                        "INSERT INTO menu_items (name, category, price, description, available) VALUES (?, ?, ?, ?, TRUE)"
                );
                sql.setString(1, name);
                sql.setString(2, category);
                sql.setDouble(3, price);
                sql.setString(4, description);
                sql.executeUpdate();
                sql.close();
                return true;
            } else {
                System.out.println("Koneksi Hilang");
                return false;
            }
        } catch (SQLException ex) {
            System.out.println("Error di insert: " + ex);
            return false;
        }
    }

    // ================= UpdateData =================
    public boolean updateData() {
        try {
            if (!connect.isClosed()) {
                PreparedStatement sql = connect.prepareStatement(
                        "UPDATE menu_items SET name=?, category=?, price=?, description=?, available=? WHERE id=?"
                );
                sql.setString(1, name);
                sql.setString(2, category);
                sql.setDouble(3, price);
                sql.setString(4, description);
                sql.setBoolean(5, available);
                sql.setInt(6, id);
                int rows = sql.executeUpdate();
                sql.close();
                return rows > 0;
            } else {
                System.out.println("Koneksi Hilang");
                return false;
            }
        } catch (SQLException ex) {
            System.out.println("Error di update: " + ex);
            return false;
        }
    }

    // ================= DeleteData =================
    public boolean deleteData() {
        try {
            if (!connect.isClosed()) {
                PreparedStatement sql = connect.prepareStatement("DELETE FROM menu_items WHERE id=?");
                sql.setInt(1, id);
                int rows = sql.executeUpdate();
                sql.close();
                return rows > 0;
            } else {
                System.out.println("Koneksi Hilang");
                return false;
            }
        } catch (SQLException ex) {
            System.out.println("Error di delete: " + ex);
            return false;
        }
    }

    // ================= ViewListData =================
    public List<MenuItem> viewListData() {
        List<MenuItem> list = new ArrayList<>();
        try {
            if (!connect.isClosed()) {
                stat = connect.createStatement();
                ResultSet rs = stat.executeQuery(
                        "SELECT id, name, category, price, description, available FROM menu_items ORDER BY category, name");
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
                rs.close();
                stat.close();
            } else {
                System.out.println("Koneksi Hilang");
            }
        } catch (SQLException ex) {
            System.out.println("Error di viewListData: " + ex);
        }
        return list;
    }

    // Cari menu berdasarkan nama/kategori (dipakai fitur search)
    public List<MenuItem> viewListDataByKeyword(String keyword) {
        List<MenuItem> list = new ArrayList<>();
        try {
            if (!connect.isClosed()) {
                PreparedStatement sql = connect.prepareStatement(
                        "SELECT id, name, category, price, description, available FROM menu_items "
                        + "WHERE name LIKE ? OR category LIKE ? ORDER BY name"
                );
                String kw = "%" + keyword + "%";
                sql.setString(1, kw);
                sql.setString(2, kw);
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
            System.out.println("Error di viewListDataByKeyword: " + ex);
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
