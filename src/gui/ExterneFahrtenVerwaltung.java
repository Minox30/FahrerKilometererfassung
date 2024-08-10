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
import java.time.format.DateTimeParseException;
import java.util.Map;

import static util.Fehlermeldung.zeigeFehlermeldung;

// Thread zur Verarbeitung von externen Fahrerdaten aus der "addfahrten.csv Datei
public class ExterneFahrtenVerwaltung extends Thread {
    // Name der Datei
    private static final String EXTERNE_FAHRTEN = "addfahrten.csv";
    // Formatter für die Datumskonvertierung
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    // Pfad zur Datei
    private final Path externeFahrten;
    // Map zur Speicherung der Fahrerobjekte
    private final Map<String, Fahrer> fahrerMap;
    // Referenz zur GUI
    private final KilometererfassungGUI gui;
    // Flag zur Kontrolle der Ausführung des Threads
    private volatile boolean running = true;
    // Singleton-Instanz des Threads
    private static ExterneFahrtenVerwaltung instance;

    // Konstruktor für die ExterneFahrtenVerwaltung
    private ExterneFahrtenVerwaltung(Map<String, Fahrer> fahrerMap, KilometererfassungGUI gui, DateTimeFormatter dateFormatter) {
        this.externeFahrten = Paths.get(EXTERNE_FAHRTEN);
        this.fahrerMap = fahrerMap;
        this.gui = gui;
    }

    // Gibt die ExterneFahrtenVerwaltung-Instanz zurück oder erstellt diese.
    public static ExterneFahrtenVerwaltung getInstance(Map<String, Fahrer> fahrerMap, KilometererfassungGUI gui, DateTimeFormatter dateFormatter) {
        if (instance == null) {
            instance = new ExterneFahrtenVerwaltung(fahrerMap, gui, dateFormatter);
        }
        return instance;
    }

    // Startet die Instanz
    public static void startInstance(Map<String, Fahrer> fahrerMap, KilometererfassungGUI gui, DateTimeFormatter dateFormatter) {
        getInstance(fahrerMap, gui, dateFormatter).start();
    }

    // Beendet die Instanz
    public static void shutdownInstance() {
        if (instance != null) {
            instance.shutdown();
            try {
                instance.join(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            instance = null;
        }
    }

    // Methode überwacht und verarbeitet die Datei, wenn diese existiert
    @Override
    public void run() {
        while (running && !isInterrupted()) {
            try {
                // Überprüft, ob die Datei existiert
                if (Files.exists(externeFahrten)) {
                    processFile();
                }
// Wartet 60 Sekunden bis zur nächsten Überprüfung
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (IOException e) {
                zeigeFehlermeldung("Fehler beim lesen der Datei: ");
            }
        }
    }

    // Liest die Datei aus und fügt die neuen Fahrten hinzu
    private void processFile() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(externeFahrten.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                verarbeiteZeilen(line);
            }
        }
        Files.delete(externeFahrten);
    }

    private void verarbeiteZeilen(String line) {
        String[] parts = line.split(",");
        if (parts.length != 4) {
            zeigeFehlermeldung("Ungültiges Zeilenformat");
            return;
        }
        try {
            String personalnummer = parts[0];
            LocalDate datum = LocalDate.parse(parts[1], dateFormatter);
            String startort = parts[2];
            int kilometer = Integer.parseInt(parts[3]);

            if (kilometer < 0) {
                zeigeFehlermeldung("Negative Kilometerangabe in Zeile: " + line);
                return;
            }
            Fahrer fahrer = fahrerMap.get(personalnummer);
            if (fahrer == null) {
                zeigeFehlermeldung("Fahrer mit der Personalnummer " + personalnummer + " nicht gefunden.");
                return;
            }
            Fahrt neueFahrt = new Fahrt(datum, startort, kilometer);
            fahrer.addFahrt(neueFahrt);

            SwingUtilities.invokeLater(() -> gui.updateFahrerUI(fahrer));
        } catch (DateTimeParseException e) {
            zeigeFehlermeldung("Ungültiges Datumsformat in Zeile: " + line);
        } catch (NumberFormatException e) {
            zeigeFehlermeldung("Ungültige Kilometerangabe in der Zeile: " + line);
        } catch (IllegalArgumentException e) {
            zeigeFehlermeldung("Fehler beim Bearbeiten der Zeile: " + line + " - " + e.getMessage());
        }
    }

    public void shutdown() {
        running = false;
        interrupt();
    }
}
