package util;

import javax.swing.*;

    public class Fehlermeldung {

    // Hilfsmethode zum Anzeigen von Fehlermeldungen
    public static void zeigeFehlermeldung(String nachricht){
        JOptionPane.showMessageDialog(null,nachricht,"Fehler",JOptionPane.ERROR_MESSAGE);
    }
}
