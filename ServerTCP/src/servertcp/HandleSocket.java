package servertcp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Sama seperti contoh di materi Week 13: HandleSocket punya reference ke
 * FormServer (parent) supaya bisa nampilin log ke jendela server.
 * Bedanya, di sini "chat" diganti jadi command reservasi/order yang
 * diproses lewat TableDAO / ReservationDAO / OrderDAO.
 */
public class HandleSocket extends Thread {

    FormServer parent;
    Socket client;
    DataOutputStream out;
    BufferedReader in;

    TableDAO tableDAO = new TableDAO();
    ReservationDAO reservationDAO = new ReservationDAO();
    OrderDAO orderDAO = new OrderDAO();

    // Lock khusus supaya 2 client TIDAK BISA reservasi meja yang sama di
    // waktu bersamaan (mencegah double booking). 'static' -> dipakai bareng
    // semua HandleSocket / semua thread client.
    private static final Object RESERVATION_LOCK = new Object();

    public HandleSocket(FormServer _parent, Socket _client) {
        this.parent = _parent;
        this.client = _client;

        try {
            this.out = new DataOutputStream(client.getOutputStream());
            this.in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void sendReply(String tmp) {
        try {
            out.writeBytes(tmp + "\n");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public void run() {
        parent.log("[+] Client terhubung: " + client.getInetAddress());

        while (true) {
            try {
                String msg = in.readLine();
                if (msg == null) break; // client putus koneksi

                parent.log(">> Diterima: " + msg);
                String response = handleCommand(msg);
                sendReply(response);
                parent.log("<< Dibalas : " + response);

            } catch (Exception ex) {
                parent.log("error hs run: " + ex);
                break;
            }
        }

        parent.log("[-] Client terputus: " + client.getInetAddress());
    }

    // Semua command dari Client TCP diproses di sini.
    // Format: COMMAND|param1|param2|...
    private String handleCommand(String request) {
        String[] parts = request.split("\\|", -1);
        String command = parts[0];

        try {
            switch (command) {

                case "LIST_TABLES":
                    return listTables();

                case "CREATE_RESERVATION":
                    // userId|date|time|guestCount
                    return createReservation(Integer.parseInt(parts[1]), parts[2], parts[3],
                            Integer.parseInt(parts[4]));

                case "CANCEL_RESERVATION":
                    // reservationId
                    return cancelReservation(Integer.parseInt(parts[1]));

                case "LIST_MY_RESERVATIONS":
                    // userId
                    return listMyReservations(Integer.parseInt(parts[1]));

                case "CREATE_ORDER":
                    // reservationId|menuId:qty,menuId:qty,...
                    return createOrder(Integer.parseInt(parts[1]), parts[2]);

                case "UPDATE_ORDER_STATUS":
                    // orderId|status
                    return updateOrderStatus(Integer.parseInt(parts[1]), parts[2]);

                case "LIST_ORDERS":
                    return listOrders();

                default:
                    return "ERR|Command tidak dikenali: " + command;
            }
        } catch (Exception e) {
            return "ERR|" + e.getMessage();
        }
    }

    private String listTables() throws java.sql.SQLException {
        List<RestaurantTable> tables = tableDAO.getAllTables();
        StringBuilder sb = new StringBuilder("OK");
        for (RestaurantTable t : tables) sb.append("|").append(t.toString());
        return sb.toString();
    }

    private String createReservation(int userId, String date, String time, int guestCount) {
        synchronized (RESERVATION_LOCK) {
            try {
                Reservation r = reservationDAO.createReservation(userId, date, time, guestCount);
                return "OK|" + r.toString();
            } catch (java.sql.SQLException e) {
                return "ERR|" + e.getMessage();
            }
        }
    }

    private String cancelReservation(int reservationId) {
        try {
            reservationDAO.cancelReservation(reservationId);
            return "OK|Reservasi " + reservationId + " dibatalkan";
        } catch (java.sql.SQLException e) {
            return "ERR|" + e.getMessage();
        }
    }

    private String listMyReservations(int userId) throws java.sql.SQLException {
        List<Reservation> list = reservationDAO.getReservationsByUser(userId);
        StringBuilder sb = new StringBuilder("OK");
        for (Reservation r : list) sb.append("|").append(r.toString());
        return sb.toString();
    }

    private String createOrder(int reservationId, String itemsRaw) {
        try {
            List<int[]> items = new ArrayList<>();
            for (String itemStr : itemsRaw.split(",")) {
                String[] pair = itemStr.split(":");
                items.add(new int[]{Integer.parseInt(pair[0]), Integer.parseInt(pair[1])});
            }
            FoodOrder order = orderDAO.createOrder(reservationId, items);
            return "OK|" + order.getId() + "|" + order.getStatus() + "|" + order.getTotalAmount();
        } catch (java.sql.SQLException e) {
            return "ERR|" + e.getMessage();
        }
    }

    private String updateOrderStatus(int orderId, String status) {
        try {
            orderDAO.updateStatus(orderId, status);
            return "OK|Order " + orderId + " -> " + status;
        } catch (java.sql.SQLException e) {
            return "ERR|" + e.getMessage();
        }
    }

    private String listOrders() throws java.sql.SQLException {
        List<FoodOrder> orders = orderDAO.getAllActiveOrders();
        StringBuilder sb = new StringBuilder("OK");
        for (FoodOrder o : orders) {
            sb.append("|").append(o.getId()).append(":").append(o.getReservationId())
                    .append(":").append(o.getStatus()).append(":").append(o.getTotalAmount());
        }
        return sb.toString();
    }
}
