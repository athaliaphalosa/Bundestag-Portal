package org.texttechnologylab.ppr.blatt3.data.redenportal;


import org.texttechnologylab.ppr.blatt3.data.interfaces.FraktionIn;



/**
 * Implementierung einer Fraktion
 */
public class Fraktion implements FraktionIn {
    private String namef;

    /**
     * Erstellt eine neue Fraktion
     * @param namef
     */
    public Fraktion(String namef) {
        this.namef = namef;
    }

    // Name der Fraktion
    @Override
    public String getName() {
        return namef;
    }


}
