package gui;

import dao.FahrtenVerwaltung;
import dao.FahrtenVerwaltung;
import model.Fahrer;
import model.Fahrt;

import javax.swing.SwingUtilities;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class KilometererfassungGUI extends JFrame {
    private JPanel mainPanel;
    private JComboBox fahrerComboBox;
    private JButton neuerFahrerButton;
    private JTextField datumField;
    private JTextField startortField;
    private JTextField kilometerField;
    private JButton fahrtHinzufuegenButton;
    private JButton beendenButton;
    private JTable fahrtenTable;
    private JScrollPane fahrtenScrollPane;
    private JLabel gesamtkilometerLabel;
    private FahrtenVerwaltung csvHandler;

    private Map<String, Fahrer> fahrerMap;
    private DateTimeFormatter dateformatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public KilometererfassungGUI() {
        System.out.println("Starte GUI-Initialisierung");

        SwingUtilities.invokeLater(() -> {
            createUIComponents();
            System.out.println("UI-Komponenten erstellt");
            fahrerMap = new HashMap<>();
            csvHandler = new FahrtenVerwaltung();
            System.out.println("CSV-Handler erstellt");
            initialisiereFahrerDaten();
            System.out.println("Fahrer-Daten erstellt");
            addListeners();
            System.out.println("Listener hinzugefügt");
            this.setContentPane(mainPanel);
            this.pack();
            System.out.println("GUI-Initialisierung abgeschlossen");

// Startet den ExterneFahrtenThread in einem separaten invokeLater Aufruf, damit der Thread erst nach vollständiger GUI-Initialisierung startet
            SwingUtilities.invokeLater(this::startExterneFahrtenThread);
        });
    }
// Hilfsmethode zum Starten des ExterneFahrtenThreads
    private void startExterneFahrtenThread() {
        ExterneFahrtenVerwaltung.startInstance(fahrerMap, this, dateformatter);
        System.out.println("ExterneFahrtenThread gestartet");

    }

    // Methode initialisiert die Fahrerdaten und lädt sie in die GUI
    private void initialisiereFahrerDaten() {
        fahrerMap = new HashMap<>();
        List<Fahrer> fahrer = csvHandler.loadData();
        fahrerComboBox.removeAllItems();
        fahrerComboBox.addItem("Bitte Fahrer auswählen");
        for (Fahrer f : fahrer) {
            fahrerMap.put(f.getPersonalnummer(), f);
            fahrerComboBox.addItem(f.toString());

            System.out.println("Fahrerdaten in GUI geladen");
        }
    }

    // Methode zum Speichern und Beenden des Programms
    private void programmBeenden() {
        System.out.println("Daten werden gespeichert und das Programm beendet.");
        ExterneFahrtenVerwaltung.startInstance(fahrerMap, this, dateformatter);
        csvHandler.saveData(new ArrayList<>(fahrerMap.values()));
        System.exit(0);
    }

    private void createUIComponents() {

        // Hauptpanel mit Borderlayout
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Oberes Panel für Fahrer-Auswahl
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Komponenten für die Fahrerauswahl
        fahrerComboBox = new JComboBox<>();
        fahrerComboBox.addItem("Bitte Fahrer auswählen");

        // Button für das Hinzufügen eines neuen Fahrers
        neuerFahrerButton = new JButton("Neuer Fahrer");

        // Komponenten werden dem oberen Panel zugeordnet
        topPanel.add(new JLabel("Fahrer:"));
        topPanel.add(fahrerComboBox);
        topPanel.add(neuerFahrerButton);

        // Tabelle für die Fahrten
        fahrtenTable = new JTable(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Datum", "Startort", "Kilometer"}
        ));

        // Tabelle wird in ScrollPane eingebettet um durch diese scrollen zu können
        fahrtenScrollPane = new JScrollPane(fahrtenTable);

        // Label für Gesamtkilometeranzeige
        gesamtkilometerLabel = new JLabel("Gesamtkilometer: 0");

        // Unteres Panel für neue Fahrt und Beenden-Button
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Textfelder für die neue Fahrt
        datumField = new JTextField(10);
        startortField = new JTextField(15);
        kilometerField = new JTextField(5);
        fahrtHinzufuegenButton = new JButton("Fahrt hinzufügen");
        beendenButton = new JButton("Beenden");

        // Komponenten werden dem unteren Panel zugeordnet
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 6;
        bottomPanel.add(new JLabel("Neue Fahrt erfassen:"), gbc);

        // Eingabefeld für das Datum
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        bottomPanel.add(new JLabel("Datum:"), gbc);
        gbc.gridx = 1;
        bottomPanel.add(datumField, gbc);

        // Eingabefeld für den Startort
        gbc.gridx = 2;
        bottomPanel.add(new JLabel("Startort:"), gbc);
        gbc.gridx = 3;
        bottomPanel.add(startortField, gbc);

        // Eingabefeld für die Kilometer
        gbc.gridx = 4;
        bottomPanel.add(new JLabel("Kilometer:"), gbc);
        gbc.gridx = 5;
        bottomPanel.add(kilometerField, gbc);

        // Button zum Hinzufügen der Fahrt
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 6;
        bottomPanel.add(fahrtHinzufuegenButton, gbc);

        // Beenden Button
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        bottomPanel.add(beendenButton, gbc);

        // Komponenten werden dem HauptPanel hinzugefügt
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(fahrtenScrollPane, BorderLayout.CENTER);

        // Unteres Panel für Gesamtkilometer und die Eingabefelder für eine neue Fahrt
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(gesamtkilometerLabel, BorderLayout.NORTH);
        southPanel.add(bottomPanel, BorderLayout.CENTER);

        mainPanel.add(southPanel, BorderLayout.SOUTH);
    }
// Fügt ActionListener zu UI-Komponenten hinzu
    private void addListeners() {
        neuerFahrerButton.addActionListener(e -> SwingUtilities.invokeLater(this::neuerFahrerHinzufuegen));
        fahrtHinzufuegenButton.addActionListener(e -> SwingUtilities.invokeLater(this::neueFahrtHinzufuegen));
        beendenButton.addActionListener(e -> SwingUtilities.invokeLater(this::programmBeenden));
        fahrerComboBox.addActionListener(e -> SwingUtilities.invokeLater(this::fahrerAusgewaehlt));
    }


    private void neuerFahrerHinzufuegen() {

        Frame parentFrame = JOptionPane.getFrameForComponent(mainPanel);
        // Erstellen eines neuen JDialog
        JDialog dialog = new JDialog(parentFrame, "Neuen Fahrer hinzufügen", true);
        dialog.setLayout(new BorderLayout(10, 10));

        // Panel für die Eingabefelder
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        JTextField personalnummerField = new JTextField(10);
        JTextField vornameField = new JTextField(10);
        JTextField nachnameField = new JTextField(10);

        inputPanel.add(new JLabel("Personalnummer"));
        inputPanel.add(personalnummerField);
        inputPanel.add(new JLabel("Vorname"));
        inputPanel.add(vornameField);
        inputPanel.add(new JLabel("Nachname"));
        inputPanel.add(nachnameField);

        // Button-Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("OK");
        JButton abbrechenButton = new JButton("Abbrechen");

        buttonPanel.add(okButton);
        buttonPanel.add(abbrechenButton);

        // Fügt die Panels zum Dialog hinzu
        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Extrahiert die Eingabe und entfernt die Leerzeichen.
        okButton.addActionListener(e -> {
            String personalnummer = personalnummerField.getText().trim();
            String vorname = vornameField.getText().trim();
            String nachname = nachnameField.getText().trim();

            // Überprüft, ob alle Felder gefüllt sind
            if (!personalnummer.isEmpty() && !vorname.isEmpty() && !nachname.isEmpty()) {
                // Überprüft das Format der Personalnummer|
                if (istGueltigePersonalnummer(personalnummer)) {
                    //Erstellt ein neues Fahrer-Objekt mit den eingegebenen Daten
                    Fahrer neuerFahrer = new Fahrer(personalnummer, vorname, nachname);
                    // Überprüft, ob die eingegebene Personalnummer bereits existiert
                    if (!fahrerMap.containsKey(personalnummer)) {
                        // Fügt den neuen Fahrer der Map und der ComboBox hinzu und wählt ihn aus
                        fahrerMap.put(personalnummer, neuerFahrer);
                        fahrerComboBox.addItem(neuerFahrer.toString());
                        fahrerComboBox.setSelectedItem(neuerFahrer.toString());

                        // Erfolgsmeldung
                        JOptionPane.showMessageDialog(dialog, "Neuer Fahrer wurde hinzugefügt.", "Fahrer hinzugefügt", JOptionPane.INFORMATION_MESSAGE);
                        // Dialog wird geschlossen
                        dialog.dispose();
                        //Fehlermeldung bei bereits vorhandener Personalnummer
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Dieser Fahrer ist bereits vorhanden.", "Fehler", JOptionPane.ERROR_MESSAGE);
                    }
                    // Fehlermeldung bei ungültiger Personalnummer
                } else {
                    JOptionPane.showMessageDialog(dialog, "Die Personalnummer muss aus 10 Ziffern bestehen.", "Fehler", JOptionPane.ERROR_MESSAGE);
                }
                // Fehlermeldung bei nicht vollständig ausgefüllten Daten
            } else {
                JOptionPane.showMessageDialog(dialog, "Bitte füllen sie alle Felder aus.", "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        });

        abbrechenButton.addActionListener(e -> dialog.dispose());

        // Dialog anzeigen
        dialog.pack();
        dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(mainPanel));
        dialog.setVisible(true);
    }

    private boolean istGueltigePersonalnummer(String personalnummer) {
        return personalnummer.matches("\\d{10}");
    }


    private void neueFahrtHinzufuegen() {
        // Holt den ausgewählten Fahrer aus der ComboBox
        String ausgewaehlterFahrerString = (String) fahrerComboBox.getSelectedItem();

        // Überprüft, ob ein Fahrer ausgewählt wurde
        if ("Bitte Fahrer auswählen".equals(ausgewaehlterFahrerString)) {
            JOptionPane.showMessageDialog(mainPanel, "Bitte wählen Sie zuerst einen Fahrer aus", "Fehler", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Extrahiere die Personalnummer aus dem ausgewählten Fahrer-String
        String personalnummer = ausgewaehlterFahrerString.split(" ")[0];
        // Holt das Fahrer-Objekt aus der Map
        Fahrer ausgewaehlterFahrer = fahrerMap.get(personalnummer);

        // Extrahiert die Eingaben und entfernt die Leerzeichen
        String datumString = datumField.getText().trim();
        String startort = startortField.getText().trim();
        String kilometer = kilometerField.getText().trim();

        // Überprüft die Vollständigkeit der Eingaben
        if (datumString.isEmpty() || startort.isEmpty() || kilometer.isEmpty()) {
            JOptionPane.showMessageDialog(mainPanel, "Bitte füllen Sie alle Felder aus.", "Fehler", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // konvertiert String-Datum zu LocalDate
            LocalDate datum = LocalDate.parse(datumString, dateformatter);

            // Überprüft, ob das eingegebene Datum in der Zukunft liegt
            if (datum.isAfter(LocalDate.now())) {
                JOptionPane.showMessageDialog(mainPanel, "Das Datum darf nicht in der Zukunft liegen.", "Fehler", JOptionPane.ERROR_MESSAGE);
            }
            // konvertiert die Kilometereingabe in einen Integer
            int km = Integer.parseInt(kilometer);
            // Überprüft, ob die Eingabe der Kilometer positiv ist
            if (km < 0) {
                throw new NumberFormatException();
            }
            // Erstellt ein neues Fahrt-Objekt und fügt es dem Fahrer hinzu
            Fahrt neueFahrt = new Fahrt(datum, startort, km);
            ausgewaehlterFahrer.addFahrt(neueFahrt);

            // aktualisiert das UI
            updateFahrtenTabelle(ausgewaehlterFahrer);
            updateGesamtkilometer(ausgewaehlterFahrer);

            // Setzt die Eingabefelder zurück
            datumField.setText("");
            startortField.setText("");
            kilometerField.setText("");

            JOptionPane.showMessageDialog(mainPanel, "Neue Fahrt wurde hinzugefügt", "Fahrt hinzugefügt", JOptionPane.INFORMATION_MESSAGE);
        } catch (DateTimeParseException e) {
            // Fängt Fehler beim Parsen des Datums ab
            JOptionPane.showMessageDialog(mainPanel, "Bitte geben Sie das Datum im Format TT.MM.JJJJ ein.", "Fehler", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            // Fängt Fehler beim Parsen der Kilometer ab
            JOptionPane.showMessageDialog(mainPanel, "Bitte geben Sie eine gültige Zahl für die Kilometer ein.", "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Aktualisiert die UI-Elemente für den gegebenen Fahrer
    public void updateFahrerUI(Fahrer fahrer) {
        SwingUtilities.invokeLater(() -> {
            updateFahrtenTabelle(fahrer);
            updateGesamtkilometer(fahrer);
        });
    }

    // Aktualisiert die Fahrten-Tabelle für den ausgewählten Fahrer
    private void updateFahrtenTabelle(Fahrer fahrer) {
        DefaultTableModel model = (DefaultTableModel) fahrtenTable.getModel();
        model.setRowCount(0); // Tabelle wird geleert.
        for (Fahrt fahrt : fahrer.getFahrten()) {
            model.addRow(new Object[]{fahrt.getDatum().format(dateformatter), fahrt.getStartort(), fahrt.getKilometer()});
        }
    }

    // Aktualisiert die Anzeige der Gesamtkilometer für den gegebenen Fahrer
    private void updateGesamtkilometer(Fahrer fahrer) {
        int gesamtKilometer = fahrer.getFahrten().stream().mapToInt(Fahrt::getKilometer).sum();
        gesamtkilometerLabel.setText("Gesamtkilometer: " + gesamtKilometer);
    }

    // Aktualisiert die Fahrten-Tabelle und die Gesamtkilometer-Anzeige
    private void fahrerAusgewaehlt() {
        String ausgewaehlterFahrerString = (String) fahrerComboBox.getSelectedItem();
        if (!"Bitte Fahrer auswählen".equals(ausgewaehlterFahrerString)) {
            String personalnummer = ausgewaehlterFahrerString.split(" ")[0];
            Fahrer ausgewaehlterFahrer = fahrerMap.get(personalnummer);
            updateFahrtenTabelle(ausgewaehlterFahrer);
            updateGesamtkilometer(ausgewaehlterFahrer);
        } else {

            // Tabelle leeren und Gesamtkilometer zurücksetzen, wenn kein Fahrer ausgewählt ist
            DefaultTableModel model = (DefaultTableModel) fahrtenTable.getModel();
            while (model.getRowCount() > 0) {
                model.removeRow(0);
            }
            gesamtkilometerLabel.setText("Gesamtkilometer: 0");
        }
    }


}


