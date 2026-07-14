package servertcp;

import javax.swing.SwingUtilities;

public class ServerTCP {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FormServer().setVisible(true));
    }
}
