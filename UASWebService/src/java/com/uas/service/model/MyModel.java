package com.uas.service.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MyModel {

    protected static final String HOST = "localhost";
    protected static final String PORT = "3306";
    protected static final String DB_NAME = "db_food_reservation";
    protected static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB_NAME
            + "?useSSL=false&serverTimezone=Asia/Jakarta&allowPublicKeyRetrieval=true";
    protected static final String DB_USER = "root";
    protected static final String DB_PASSWORD = ""; // isi sesuai password MySQL kamu

    protected Connection connect;
    protected Statement stat;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public MyModel() {
        try {
            connect = DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
        } catch (SQLException ex) {
            System.out.println("Error di koneksi: " + ex);
        }
    }
}
