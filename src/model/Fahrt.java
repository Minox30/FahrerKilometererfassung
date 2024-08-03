package model;

import java.time.LocalDate;

public class Fahrt implements Comparable<Fahrt>{
    private LocalDate datum;
    private String startort;
    private int kilometer;

    public Fahrt(LocalDate datum, String startort, int kilometer) {
        this.datum = datum;
        this.startort = startort;
        this.kilometer = kilometer;
    }

    // Getter
    public LocalDate getDatum() {
        return datum;
    }
    public String getStartort() {
        return startort;
    }
    public int getKilometer() {
        return kilometer;
    }

    @Override
    public int compareTo(Fahrt other) {
        return other.datum.compareTo(this.datum); // Absteigende Sortierung
    }

    @Override
    public String toString() {
        return datum + " - " + startort + " - " + kilometer + " km";
    }
}
