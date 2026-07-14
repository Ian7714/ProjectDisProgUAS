/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package clienttcp;

import javax.swing.SwingUtilities;
import clienttcp.ui.LoginFrame;

/**
 *
 * @author Ian
 */
public class ClientTCP {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
    
}
