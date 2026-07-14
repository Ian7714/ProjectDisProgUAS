/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clienttcp.model;

/**
 *
 * @author raiha
 */
public class RestaurantTable {
    public int id;
    public String tableNumber;
    public int capacity;
    public String status;

    public static RestaurantTable parse(String csv) {
        String[] p = csv.split(";");
        RestaurantTable t = new RestaurantTable();
        t.id = Integer.parseInt(p[0]);
        t.tableNumber = p[1];
        t.capacity = Integer.parseInt(p[2]);
        t.status = p[3];
        return t;
    }

    @Override
    public String toString() {
        return tableNumber + " (kapasitas " + capacity + ") - " + status;
    }
}
