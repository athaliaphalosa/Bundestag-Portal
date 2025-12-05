package org.texttechnologylab.ppr.blatt3.data.redenportal;

import org.json.JSONObject;
import org.texttechnologylab.ppr.blatt3.data.interfaces.KommentarIn;

/**
 * Representation Kommentars für jede Rede
 * Jeder Kommentar besitzt Redner, Fraktion, zugehörige Rede, Kommentartext
 */
public class Kommentar implements KommentarIn {
    private Redner redner;
    private Rede rede;
    private Fraktion fraktion;
    private String textk;

    public Kommentar(Redner redner, Rede rede, Fraktion fraktion, String textk) {
        this.redner = redner;
        this.fraktion = fraktion;
        this.textk = textk;
        this.rede = rede;
    }

    public Redner getRedner() {
        return redner;
    }

    public Fraktion getFraktion() {
        return fraktion;
    }

    public String getTextk() {
        return textk;
    }

    @Override
    public Fraktion fraktion() {
        return this.fraktion;
    }

    public Rede getRede() {
        return rede;
    }

    //toString()
    @Override
    public String toString() {
        // Wenn Redner vorhanden, nehmen Nachname; sonst Not found
        String rednerName = (redner != null) ?
                redner.getVorname() + " " + redner.getNachname() : "Not found";

        // Analog wie in Redner
        String fraktionName = (fraktion != null) ? fraktion.getName() : "Not found";

        // Format for kommentar with Redner and Fraktion
        return String.format("Kommentar von %s (%s): \"%s\"",
                rednerName, fraktionName);
    }

    // toJSON()
    public String toJSON() {
        JSONObject json = new JSONObject();
        json.put("type", "Kommentar");
        json.put("text", textk != null ? textk : "");
        json.put("textLength", textk != null ? textk.length() : 0);

        // Info Redner
        if (redner != null) {
            JSONObject rednerJson = new JSONObject();
            rednerJson.put("id", redner.getId());
            rednerJson.put("name", redner.getVorname() + " " + redner.getNachname());
            json.put("redner", rednerJson);
        }

        // Info Fraktion
        if (fraktion != null) {
            json.put("fraktion", fraktion.getName());
        }

        // Info Rede
        if (rede != null) {
            json.put("redeId", rede.getRid());
        }

        return json.toString(2); // indentasi 2 spasi untuk format rapi
    }

}