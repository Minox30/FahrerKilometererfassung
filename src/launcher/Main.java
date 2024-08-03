package launcher;

import gui.KilometererfassungGUI;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->{
            try {
                KilometererfassungGUI gui = new KilometererfassungGUI();
                gui.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Fehler beim Starten der Anwendung" + e.getMessage());
            }
        });
    }
}
