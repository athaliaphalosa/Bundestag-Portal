package org.texttechnologylab.ppr.blatt3.data.redenportal;

import org.json.JSONObject;
import org.texttechnologylab.ppr.blatt3.data.interfaces.SitzungIn;

import java.time.LocalTime;
import java.util.Date;



/**
 * Repräsentiert Sitzung im Reden Portal
 * Enthält der Ort, Datum, Zeitbeginn und Zeitende, Sitzungnummer, und WP(Wahlperiode)
 */
public class Sitzung implements SitzungIn {
    private String sitzungOrt;
    private Date sitzungDatum;
    private LocalTime startZeit;
    private LocalTime endZeit;
    private String wahlperiode;
    private String sitzungNr;

    public Sitzung(String wahlperiode, String sitzungNr, String sitzungOrt, Date sitzungDatum, LocalTime startZeit, LocalTime endZeit) {
        this.wahlperiode = wahlperiode;
        this.sitzungNr = sitzungNr;
        this.sitzungOrt = sitzungOrt;
        this.sitzungDatum = sitzungDatum;
        this.startZeit = startZeit;
        this.endZeit = endZeit;
    }

    // Method zu generate sitzungId
    private String genSitzungId() {
        String dateStr = (sitzungDatum != null) ? sitzungDatum.toString().replace("-", "").replace(":", "") : "unknown";
        return wahlperiode + "_" + sitzungNr + "_" + dateStr;
    }

    // Getter methods
    public String getWahlperiode() { return wahlperiode; }
    public String getSitzungNr() { return sitzungNr; }
    public String getSitzungOrt() { return sitzungOrt; }
    public Date getSitzungDatum() { return sitzungDatum; }
    public LocalTime getStartZeit() { return startZeit; }
    public LocalTime getEndZeit() { return endZeit; }

}