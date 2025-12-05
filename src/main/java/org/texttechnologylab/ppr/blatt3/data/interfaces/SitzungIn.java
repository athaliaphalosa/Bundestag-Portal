package org.texttechnologylab.ppr.blatt3.data.interfaces;


import java.time.LocalTime;
import java.util.Date;


/**
 * Interface für Sitzung in Redeportal
 */
public interface SitzungIn {

    /**
     * Get den Ort der Sitzung
     * @return der Sitzungort
     */
    String getSitzungOrt();

    /**
     * Get dieNummer der Sitzung
     * @return die Sitzungnummer
     */
    String getSitzungNr();

    /**
     * Get die Wahlperiode
     * @return Get die Wahlperiode
     */
    String getWahlperiode();

    /**
     * Get das Datum der Sitzung
     * @return Sitzungdatum
     */
    Date getSitzungDatum();



}
