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

// Thread zur Verarbeitung von externen Fahrerdaten aus der "addfahrten.csv Datei
public class ExterneFahrtenVerwaltung extends Thread {
    // Name der Datei
    private static final String EXTERNE_FAHRTEN = "addfahrten.csv";
    // Pfad zur Datei
    private final Path externeFahrten;
    // Map zur Speicherung der Fahrerobjekte
    private final Map<String, Fahrer> fahrerMap;
    // Referenz zur GUI
    private final KilometererfassungGUI gui;
    // Formatter für die Datumskonvertierung
    private final DateTimeFormatter dateFormatter;
    // Flag zur Kontrolle der Ausführung des Threads
    private volatile boolean running = true;
    // Singleton-Instanz des Threads
    private static ExterneFahrtenVerwaltung instance;

    // Konstruktor für den ExterneFahrtenThread
    private ExterneFahrtenVerwaltung(Map<String, Fahrer> fahrerMap, KilometererfassungGUI gui, DateTimeFormatter dateFormatter) {
        this.externeFahrten = Paths.get(EXTERNE_FAHRTEN);
        this.fahrerMap = fahrerMap;
        this.gui = gui;
        this.dateFormatter = dateFormatter;
    }
    // Gibt die ExterneFahrtenThread-Instanz zurück oder erstellt diese.
public static ExterneFahrtenVerwaltung getInstance(Map<String, Fahrer> fahrerMap, KilometererfassungGUI gui, DateTimeFormatter dateFormatter) {
    if (instance == null) {
        instance = new ExterneFahrtenVerwaltung(fahrerMap, gui, dateFormatter);
    }
    return instance;
}
// Startet die Instanz
public static void startInstance(Map<String, Fahrer> fahrerMap, KilometererfassungGUI gui, DateTimeFormatter dateFormatter){
        getInstance(fahrerMap, gui, dateFormatter).start();
}
// Beendet die Instanz
public static void shutdownInstance() {
        if (instance != null) {
            instance.shutdown();
            try{
                instance.join(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            instance = null;
            System.out.println("ExterneFahrtenThread beendet");
        }
}
// Methode überwacht und verarbeitet die Datei, wenn diese existiert
    @Override
    public void run() {
        while (running && !isInterrupted()) {
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
                Thread.currentThread().interrupt();
                break;
            } catch (IOException e) {
                System.out.println("Fehler beim lesen der Datei: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
// Liest die Datei aus und fügt die neuen Fahrten hinzu
    private void processFile() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(externeFahrten.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    try {
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
                } catch(IllegalArgumentException e){
                    System.err.println("Fehler beim bearbeiten der Zeile: " + line + " -" + e.getMessage());
                }
            } else{
                System.err.println("Ungültiges Zeilenformat: " + line);
            }
        }
    }
        // Löscht die Datei nach der Verarbeitung
        Files.delete(externeFahrten);
        System.out.println("Datei wurde verarbeitet und gelöscht " + EXTERNE_FAHRTEN);
    }
        public void shutdown() {
            running = false;
            interrupt();
        }
}
