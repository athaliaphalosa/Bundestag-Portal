package org.texttechnologylab.ppr.blatt3.data.interfaces;

import org.texttechnologylab.ppr.blatt3.data.redenportal.Fraktion;


/**
 * Interface für Redner in Redeportal
 */
public interface RednerIn {

    /**
     * Get Titel des Redners
     * @return der Titel
     */
    String getTitel();

    /**
     * Get Id des Redners
     * @return die Id
     */
    String getId();

    /**
     * Get Vorname des Redners
     * @return der Vorname
     */
    String getVorname();

    /**
     * Get Nachanme des Redners
     * @return der Nachame
     */
    String getNachname();


}
