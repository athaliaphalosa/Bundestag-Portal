package org.texttechnologylab.ppr.blatt3.data.interfaces;

import org.texttechnologylab.ppr.blatt3.data.redenportal.Kommentar;
import org.texttechnologylab.ppr.blatt3.data.redenportal.Redner;
import org.texttechnologylab.ppr.blatt3.data.redenportal.Sitzung;

import java.util.List;


/**
 * Interface für Rede in Redenportal in Redeportal
 */
public interface RedeIn {

    /**
     * Liefert Id von Rede
     * @return die Id von Rede
     */
    String getRid();


    /**
     * Liefert den Redner der Rede
     * @return der Redner
     */
    Redner getRedner();


    /**
     * Liefert die Sitzung wo die Rede gehalten würde
     * @return die Sitzung
     */
    Sitzung getSitzung();

    /**
     * Liefert die Liste der Kommentar in jeder Rede
     * @return Liste der Kommentar
     */
    List<Kommentar> getKommentar();

    /**
     * Hinzufügen neue Kommentar zu Rede
     * @param kommentar
     */
    void addKommentar(Kommentar kommentar);

}
