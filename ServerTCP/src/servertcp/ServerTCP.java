package servertcp;

import javax.swing.SwingUtilities;

// Main class biasa (Java Class) - cuma buka jendela FormServer.
public class ServerTCP {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FormServer().setVisible(true));
    }
}
