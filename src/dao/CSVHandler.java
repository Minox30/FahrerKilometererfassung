package dao;

import model.Fahrt;
import model.Fahrer;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CSVHandler {

    private static final String CSV_File = "kilometer.csv";
    private static final DateTimeFormatter Date_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Methode zum Laden der Daten aus der CSV-Datei
    public List<Fahrer> loadData() {
        List<Fahrer> fahrer = new ArrayList<>(); // Liste zur Speicherung der Fahrer
        // Nutzung des BufferedReader zum Lesen der Datei.
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_File))) {
            String line;
            // Zeilen werden so lange gelesen bis null zurückgegeben wird und somit das Ende der Datei erreicht ist.
            while ((line = br.readLine()) != null) {
                // Der String line wird an jedem Komma geteilt und das Ergebnis in values gespeichert.
                String[] values = line.split(",");
                // Bei 3 enthaltenen Elementen handelt es sich um einen Fahrer.
                if (values.length == 3) {
                    // Erstellt ein neues Fahrer-Objekt mit den in values enthaltenen Werten und fügt es der Liste fahrer hinzu.
                    Fahrer f = new Fahrer(values[0], values[1], values[2]);
                    fahrer.add(f);
                    // Bei 4 enthaltenen Elementen handelt es sich um eine Fahrt.
                } else if (values.length == 4) {
                    // Erstellt ein neues Fahrt-Objekt.
                    Fahrt fahrt = new Fahrt(LocalDate.parse(values[1], Date_FORMATTER), values[2], Integer.parseInt(values[3]));
                    // fügt die Fahrt dem letzten Fahrer in der Liste hinzu.
                    fahrer.get(fahrer.size() -1).addFahrt(fahrt);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fahrer;
    }

    // Methode zum Speichern der Daten in der CSV-Datei
    public void saveData(List<Fahrer> fahrer) {
        // Öffnet einen BufferedWriter für die CSV-Datei.
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CSV_File))) {
            for (Fahrer f : fahrer) {
                // Schreibt die Fahrer-Informationen in die Datei.
                bw.write(String.format("%s,%s,%s%n", f.getPersonalnummer(),f.getVorname(), f.getNachname()));
                for (Fahrt fahrt : f.getFahrten()){
                    // Schreibt die Fahrt-Informationen in die Datei.
                    bw.write(String.format("%s,%s,%s%n",f.getPersonalnummer(),fahrt.getDatum().format(Date_FORMATTER), fahrt.getStartort(), fahrt.getKilometer()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
