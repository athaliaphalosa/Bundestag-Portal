package org.texttechnologylab.ppr.blatt3.data.redenportal;


import org.texttechnologylab.ppr.blatt3.data.interfaces.RedeIn;
import org.json.JSONObject;
import org.json.JSONArray;


import java.util.ArrayList;
import java.util.List;


/**
 * Repräsentiert einer Rede im Reden Portal
 * Enthält Redner, Sitzung, Text vom Rede, und Kommentar
 */
public class Rede implements RedeIn {
    private Redner redner;
    private Sitzung sitzung;
    private List<Kommentar> kommentar;
    private String rid;
    private String text;

    //Erstellt neue Rede
    public Rede(String id, Redner redner, Sitzung sitzung) {
        this.rid = id;
        this.redner = redner;
        this.sitzung = sitzung;
        this.kommentar = new ArrayList<>();
    }

    public String getRid() {
        return rid;
    }

    public Redner getRedner() {
        return redner;
    }

    public Sitzung getSitzung() {
        return sitzung;
    }

    public List<Kommentar> getKommentar() {
        return kommentar;
    }


    //get und set für Text der Rede
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }


    // Gesamtlänge aller Reden in Zeichen, wird in Aufgabe 4 benutzen
    public int getLaenge() {
        return text != null ? text.length() : 0;
    }


    // Rede ID
    public String getId() {
        return rid;
    }

    // Addieren Kommentare in einer Rede
    public void addKommentar(Kommentar kommentars) {
        this.kommentar.add(kommentars);
    }

    //toString(), hab ich für Debugging benutzen
    @Override
    public String toString() {
        return "Rede{" +
                "rid='" + rid + '\'' +
                ", redner=" + (redner != null ? redner.getVorname() + " " + redner.getNachname() : "null") +
                ", kommentare=" + kommentar.size() +
                ", textLength=" + (text != null ? text.length() : 0) +
                '}' + "\n";
    }

    // toJSON()
    public String toJSON() {
        JSONObject json = new JSONObject();
        json.put("type", "Rede");
        json.put("id", rid != null ? rid : "");
        json.put("textLength", getLaenge());

        // Redners Information
        if (redner != null) {
            JSONObject rednerJson = new JSONObject();
            rednerJson.put("id", redner.getId());
            rednerJson.put("name", redner.getVorname() + " " + redner.getNachname());
            if (redner.getFraktion() != null) {
                rednerJson.put("fraktion", redner.getFraktion().getName());
            }
            json.put("redner", rednerJson);
        }

        // Sitzungs Information
        if (sitzung != null) {
            JSONObject sitzungJson = new JSONObject();
            sitzungJson.put("wahlperiode", sitzung.getWahlperiode());
            sitzungJson.put("sitzungNr", sitzung.getSitzungNr());
            json.put("sitzung", sitzungJson);
        }

        // Kommentars Information
        JSONArray kommentareArray = new JSONArray();
        for (Kommentar kom : kommentar) {
            if (kom != null) {
                JSONObject komJson = new JSONObject();
                komJson.put("text", kom.getTextk() != null ? kom.getTextk() : "");
                komJson.put("textLength", kom.getTextk() != null ? kom.getTextk().length() : 0);
                kommentareArray.put(komJson);
            }
        }
        json.put("kommentare", kommentareArray);
        json.put("kommentareCount", kommentar.size());

        return json.toString(2);
    }

}