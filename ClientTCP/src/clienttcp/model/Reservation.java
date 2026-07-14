/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clienttcp.model;

/**
 *
 * @author raiha
 */
public class Reservation {
    public int id;
    public int userId;
    public int tableId;
    public String date;
    public String time;
    public int guestCount;
    public String status;

    public static Reservation parse(String csv) {
        String[] p = csv.split(";");
        Reservation r = new Reservation();
        r.id = Integer.parseInt(p[0]);
        r.userId = Integer.parseInt(p[1]);
        r.tableId = Integer.parseInt(p[2]);
        r.date = p[3];
        r.time = p[4];
        r.guestCount = Integer.parseInt(p[5]);
        r.status = p[6];
        return r;
    }
}
