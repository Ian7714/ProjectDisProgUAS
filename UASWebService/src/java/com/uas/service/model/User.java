package com.uas.service.model;

import java.security.MessageDigest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class User extends MyModel {

    private int id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String role;

    public User() {
        super(); // buka connect
    }

    // Constructor untuk DAFTAR AKUN BARU (belum ada id, role default CUSTOMER)
    public User(String username, String password, String fullName, String email) {
        super();
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.role = "CUSTOMER";
    }

    // Constructor LENGKAP (dari database)
    public User(int id, String username, String password, String fullName, String email, String role) {
        super();
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
        try {
            if (!connect.isClosed()) {
                PreparedStatement sql = connect.prepareStatement(
                        "INSERT INTO users (username, password, full_name, email, role) VALUES (?, ?, ?, ?, ?)"
                );
                sql.setString(1, username);
                sql.setString(2, hashPassword(password));
                sql.setString(3, fullName);
                sql.setString(4, email);
                sql.setString(5, role);
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
                        "UPDATE users SET full_name=?, email=? WHERE id=?"
                );
                sql.setString(1, fullName);
                sql.setString(2, email);
                sql.setInt(3, id);
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

    // ================= ViewListData =================
    public List<User> viewListData() {
        List<User> list = new ArrayList<>();
        try {
            if (!connect.isClosed()) {
                stat = connect.createStatement();
                ResultSet rs = stat.executeQuery(
                        "SELECT id, username, password, full_name, email, role FROM users");
                while (rs.next()) {
                    User u = new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"),
                            rs.getString("full_name"), rs.getString("email"), rs.getString("role"));
                    list.add(u);
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

    // ================= ViewListDataString =================
    public List<String> viewListDataString() {
        List<String> result = new ArrayList<>();
        for (User u : viewListData()) {
            result.add(u.getId() + ";" + u.getUsername() + ";" + u.getFullName()
                    + ";" + u.getEmail() + ";" + u.getRole());
        }
        return result;
    }

    // ================= CheckLogin =================
    public boolean checkLogin(String usernameInput, String passwordInput) {
        try {
            if (!connect.isClosed()) {
                PreparedStatement sql = connect.prepareStatement(
                        "SELECT id FROM users WHERE username=? AND password=?"
                );
                sql.setString(1, usernameInput);
                sql.setString(2, hashPassword(passwordInput));
                ResultSet rs = sql.executeQuery();
                boolean found = rs.next();
                rs.close();
                sql.close();
                return found;
            } else {
                System.out.println("Koneksi Hilang");
                return false;
            }
        } catch (SQLException ex) {
            System.out.println("Error di checkLogin: " + ex);
            return false;
        }
    }

    // Ambil 1 data user lengkap berdasarkan username (dipanggil setelah checkLogin sukses)
    public User getByUsername(String usernameInput) {
        try {
            if (!connect.isClosed()) {
                PreparedStatement sql = connect.prepareStatement(
                        "SELECT id, username, password, full_name, email, role FROM users WHERE username=?"
                );
                sql.setString(1, usernameInput);
                ResultSet rs = sql.executeQuery();
                User result = null;
                if (rs.next()) {
                    result = new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"),
                            rs.getString("full_name"), rs.getString("email"), rs.getString("role"));
                }
                rs.close();
                sql.close();
                return result;
            } else {
                System.out.println("Koneksi Hilang");
                return null;
            }
        } catch (SQLException ex) {
            System.out.println("Error di getByUsername: " + ex);
            return null;
        }
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
