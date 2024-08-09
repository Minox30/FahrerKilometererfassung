package gui;

import model.Fahrer;
import model.Fahrt;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class ExterneFahrtenThread extends Thread {
    private static final String EXTERNE_FAHRTEN = "addfahrten.csv";
    private final Path externeFahrten;
    private final Map<String, Fahrer> fahrerMap;
    private final KilometererfassungGUI gui;
    private final DateTimeFormatter dateFormatter;

    public ExterneFahrtenThread(Map<String, Fahrer> fahrerMap, KilometererfassungGUI gui, DateTimeFormatter dateFormatter) {
        // Initialisiert den Pfad zur zu überwachenden Datei
        this.externeFahrten = Paths.get(EXTERNE_FAHRTEN);

        this.fahrerMap = fahrerMap;
        this.gui = gui;
        this.dateFormatter = dateFormatter;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                System.out.println("Prüfe auf externe Datei");
                // Überprüft, ob die Datei existiert
                if (Files.exists(externeFahrten)) {
                    System.out.println("Externe Datei wurde eingelesen.");
                    processFile();
                }
// Wartet 60 Sekunden bis zur nächsten Überprüfung
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processFile() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(externeFahrten.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String personalnummer = parts[0];
                    LocalDate datum = LocalDate.parse(parts[1], dateFormatter);
                    String startort = parts[2];
                    int kilometer = Integer.parseInt(parts[3]);

                    if (kilometer < 0) {
                        throw new IllegalArgumentException("negative Kilometerangabe in der Datei gefunden");
                    }
                    Fahrt neueFahrt = new Fahrt(datum, startort, kilometer);
                    // Sucht oder erstellt einen Fahrer und fügt die neue Fahrt hinzu.
                    Fahrer fahrer = fahrerMap.computeIfAbsent(personalnummer, pnr -> new Fahrer(pnr, "Unbekannt", "Unbekannt"));
                    fahrer.addFahrt(neueFahrt);

                    // Aktualisiert die GUI
                    SwingUtilities.invokeLater(() -> gui.updateFahrerUI(fahrer));
                }
            }

        } catch (IllegalArgumentException e) {
            System.err.println("Fehler beim Verarbeiten der Datei: " + e.getMessage());
        }
        // Löscht die Datei nach der Verarbeitung
        Files.delete(externeFahrten);
        System.out.println("Datei wurde verarbeitet und gelöscht " + EXTERNE_FAHRTEN);
    }
}
