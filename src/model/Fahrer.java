package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Fahrer {
    private String personalnummer;
    private String vorname;
    private String nachname;
    private List<Fahrt> fahrten;

    public Fahrer(String personalnummer, String vorname, String nachname) {
        this.personalnummer = personalnummer;
        this.vorname = vorname;
        this.nachname = nachname;
        this.fahrten = new ArrayList<>();
    }

    // Getter und Setter
    public String getPersonalnummer() {
        return personalnummer;
    }

    public String getVorname() {
        return vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    public List<Fahrt> getFahrten() {
        return fahrten;
    }

    // Methode zum Hinzufügen einer Fahrt
    public void addFahrt(Fahrt fahrt) {
        fahrten.add(fahrt);
        Collections.sort(fahrten);
    }

    // Methode zur Berechnung der Gesamtkilometer
    public int berechneGesamtKilometer() {
        return fahrten.stream()
                .mapToInt(Fahrt::getKilometer)
                .sum();
    }

    @Override
    public String toString() {
        return personalnummer + " " + vorname + " " + nachname;
    }
}
