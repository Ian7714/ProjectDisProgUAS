package com.uas.service;

import com.uas.service.model.MenuItem;
import com.uas.service.model.ReservationHistory;
import com.uas.service.model.User;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

@WebService(serviceName = "UASWebService")
public class UASWebService {

    @WebMethod(operationName = "hello")
    public String hello(@WebParam(name = "name") String name) {
        return "Hello " + name + " !";
    }

    // ================= USER MANAGEMENT =================
    @WebMethod(operationName = "login")
    public User login(@WebParam(name = "username") String username,
            @WebParam(name = "password") String password) {
        User model = new User();
        if (model.checkLogin(username, password)) {
            return model.getByUsername(username);
        }
        return null; // login gagal
    }

    @WebMethod(operationName = "registerUser")
    public String registerUser(@WebParam(name = "username") String username,
            @WebParam(name = "password") String password,
            @WebParam(name = "fullName") String fullName,
            @WebParam(name = "email") String email) {
        User model = new User(username, password, fullName, email);
        boolean ok = model.insertData();
        return ok ? "OK" : "ERR|Registrasi gagal, username/email mungkin sudah dipakai";
    }

    @WebMethod(operationName = "updateProfile")
    public String updateProfile(@WebParam(name = "userId") int userId,
            @WebParam(name = "fullName") String fullName,
            @WebParam(name = "email") String email) {
        User model = new User();
        model.setId(userId);
        model.setFullName(fullName);
        model.setEmail(email);
        boolean ok = model.updateData();
        return ok ? "OK" : "ERR|Update gagal";
    }

    // ================= MENU MANAGEMENT =================
    @WebMethod(operationName = "getAllMenu")
    public List<MenuItem> getAllMenu() {
        return new MenuItem().viewListData();
    }

    @WebMethod(operationName = "searchMenu")
    public List<MenuItem> searchMenu(@WebParam(name = "keyword") String keyword) {
        return new MenuItem().viewListDataByKeyword(keyword);
    }

    @WebMethod(operationName = "addMenu")
    public String addMenu(@WebParam(name = "name") String name,
            @WebParam(name = "category") String category,
            @WebParam(name = "price") double price,
            @WebParam(name = "description") String description) {
        MenuItem model = new MenuItem(name, category, price, description);
        boolean ok = model.insertData();
        return ok ? "OK" : "ERR|Gagal menambah menu";
    }

    @WebMethod(operationName = "updateMenu")
    public String updateMenu(@WebParam(name = "id") int id,
            @WebParam(name = "name") String name,
            @WebParam(name = "category") String category,
            @WebParam(name = "price") double price,
            @WebParam(name = "description") String description,
            @WebParam(name = "available") boolean available) {
        MenuItem model = new MenuItem(id, name, category, price, description, available);
        boolean ok = model.updateData();
        return ok ? "OK" : "ERR|Menu tidak ditemukan";
    }

    @WebMethod(operationName = "deleteMenu")
    public String deleteMenu(@WebParam(name = "id") int id) {
        MenuItem model = new MenuItem();
        model.setId(id);
        boolean ok = model.deleteData();
        return ok ? "OK" : "ERR|Menu tidak ditemukan";
    }

    // ================= RESERVATION HISTORY =================
    @WebMethod(operationName = "getHistoryByUser")
    public List<ReservationHistory> getHistoryByUser(@WebParam(name = "userId") int userId) {
        return new ReservationHistory().viewListDataByUser(userId);
    }

    @WebMethod(operationName = "getHistoryByPeriod")
    public List<ReservationHistory> getHistoryByPeriod(@WebParam(name = "startDate") String startDate,
            @WebParam(name = "endDate") String endDate) {
        return new ReservationHistory().viewListDataByPeriod(startDate, endDate);
    }
}
