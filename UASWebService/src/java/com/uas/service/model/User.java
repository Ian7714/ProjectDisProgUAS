package com.uas.service.model;

import java.security.MessageDigest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * "Specific Model" untuk tabel users. Fokus ke data & proses bisnis
 * (login, register, dst) - persis pola yang diajarkan (Model = data + query,
 * WebService cuma jadi Controller yang manggil method di sini).
 */
public class User extends MyModel {

    private int id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String role;

    // Constructor kosong (wajib ada, dipakai JAX-WS)
    public User() {}

    // Constructor untuk DAFTAR AKUN BARU (belum ada id, role default CUSTOMER)
    public User(String username, String password, String fullName, String email) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.role = "CUSTOMER";
    }

    // Constructor LENGKAP (biasanya dipakai waktu ambil data dari database)
    public User(int id, String username, String password, String fullName, String email, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    // ================= Getter & Setter =================
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    // ================= InsertData =================
    // Dipakai waktu Register akun baru
    public boolean insertData() {
        String sql = "INSERT INTO users (username, password, full_name, email, role) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, hashPassword(password));
            ps.setString(3, fullName);
            ps.setString(4, email);
            ps.setString(5, role);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("insertData User error: " + e.getMessage());
            return false;
        }
    }

    // ================= UpdateData =================
    public boolean updateData() {
        String sql = "UPDATE users SET full_name=?, email=? WHERE id=?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("updateData User error: " + e.getMessage());
            return false;
        }
    }

    // ================= ViewListData =================
    // Ambil semua data user, dibungkus jadi List<User>
    public List<User> viewListData() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT id, username, password, full_name, email, role FROM users";
        try (Connection con = getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                User u = new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"),
                        rs.getString("full_name"), rs.getString("email"), rs.getString("role"));
                list.add(u);
            }
        } catch (SQLException e) {
            System.out.println("viewListData User error: " + e.getMessage());
        }
        return list;
    }

    // ================= ViewListDataString =================
    // Sama seperti viewListData, tapi hasilnya dijadiin kumpulan String
    // (format: id;username;fullName;email;role)
    public List<String> viewListDataString() {
        List<String> result = new ArrayList<>();
        for (User u : viewListData()) {
            result.add(u.getId() + ";" + u.getUsername() + ";" + u.getFullName()
                    + ";" + u.getEmail() + ";" + u.getRole());
        }
        return result;
    }

    // ================= CheckLogin =================
    // TRUE kalau username & password ditemukan (cocok)
    public boolean checkLogin(String usernameInput, String passwordInput) {
        String sql = "SELECT id FROM users WHERE username=? AND password=?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usernameInput);
            ps.setString(2, hashPassword(passwordInput));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("checkLogin User error: " + e.getMessage());
            return false;
        }
    }

    // Ambil 1 data user lengkap berdasarkan username (dipanggil setelah checkLogin sukses)
    public User getByUsername(String usernameInput) {
        String sql = "SELECT id, username, password, full_name, email, role FROM users WHERE username=?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usernameInput);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"),
                            rs.getString("full_name"), rs.getString("email"), rs.getString("role"));
                }
            }
        } catch (SQLException e) {
            System.out.println("getByUsername error: " + e.getMessage());
        }
        return null;
    }

    // Hash password sederhana (SHA-256) supaya password tidak disimpan polos di database
    private static String hashPassword(String plain) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(plain.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
