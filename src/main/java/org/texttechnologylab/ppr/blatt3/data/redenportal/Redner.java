package org.texttechnologylab.ppr.blatt3.data.redenportal;

import org.json.JSONObject;
import org.texttechnologylab.ppr.blatt3.data.interfaces.RednerIn;


/**
 * Repräsentiert einer Rede im Reden Portal
 * Enthält personliche Daten der Redner und Fraktionsugehörigkeit
 */
public class Redner implements RednerIn {
    private String titel;
    private String vorname;
    private String nachname;
    private Fraktion fraktion;
    private String id;

    //Erstellt neue Redner
    public Redner(String titel, String id, String vorname, String nachname, Fraktion fraktion) {
        this.titel = titel;
        this.id = id;
        this.vorname = vorname;
        this.nachname = nachname;
        this.fraktion = fraktion;
    }

    public String getTitel() {
        return titel;
    }

    public String getId() {
        return id;
    }

    public String getVorname() {
        return vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public Fraktion getFraktion() {
        return fraktion;
    }

    public Fraktion getFraktion(String fraktionName) {
        if (fraktionName != null && fraktion != null && fraktion.getName().equals(fraktionName)) {
            return fraktion;
        }
        return null;
    }

    // Vollständiger Name auch mit Titel
    public String getFullName() {
        return (titel != null && !titel.isEmpty() ? titel + " " : "") + vorname + " " + nachname;
    }

}