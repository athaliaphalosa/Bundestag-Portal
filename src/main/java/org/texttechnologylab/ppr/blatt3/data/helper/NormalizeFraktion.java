package org.texttechnologylab.ppr.blatt3.data.helper;

/**
 * zu Normalize der Fraktion
 */
public class NormalizeFraktion {

    // Standardized fraktion names dengan encoding yang benar
    public static final String FRAKTIONSLOS = "Fraktionslos";
    public static final String CDU_CSU = "CDU/CSU";
    public static final String SPD = "SPD";
    public static final String GRUENE = "BÜNDNIS 90/DIE GRÜNEN";  // UTF-8 characters
    public static final String AFD = "AfD";
    public static final String FDP = "FDP";
    public static final String LINKE = "DIE LINKE";
    public static final String BSW = "BSW";

    /**
     * Helper Method zu Normalize der Fraktion
     */
    public static String normalizeFraktion(String oriName) {
        if (oriName == null || oriName.trim().isEmpty()) {
            return FRAKTIONSLOS;
        }

        String normalized = oriName.trim();

        // Handle fraktionslos cases
        if (normalized.equalsIgnoreCase("Fraktionslos") ||
                normalized.equalsIgnoreCase("Fractionless") ||
                normalized.equalsIgnoreCase("Ohne Fraktion")) {
            return FRAKTIONSLOS;
        }

        // Handle CDU/CSU
        if (normalized.toUpperCase().contains("CDU") ||
                normalized.toUpperCase().contains("CSU") ||
                normalized.contains("Christlich") ||
                normalized.contains("Union")) {
            return CDU_CSU;
        }

        // Handle SPD
        if (normalized.toUpperCase().contains("SPD") ||
                normalized.contains("Sozialdemokrat")) {
            return SPD;
        }

        // Handle Grüne
        if (normalized.toUpperCase().contains("GRÜN") ||
                normalized.toUpperCase().contains("GRUEN") ||
                normalized.toUpperCase().contains("BÜNDNIS") ||
                normalized.toUpperCase().contains("BUENDNIS") ||
                normalized.toUpperCase().contains("B90") ||
                normalized.contains("Grüne") ||
                normalized.contains("GREEN")) {
            return GRUENE;
        }

        // Handle AfD
        if (normalized.toUpperCase().contains("AFD") ||
                normalized.toUpperCase().contains("ALTERNATIVE") ||
                normalized.contains("Alternative für Deutschland")) {
            return AFD;
        }

        // Handle FDP
        if (normalized.toUpperCase().contains("FDP") ||
                normalized.contains("Freie Demokrat") ||
                normalized.contains("Liberale")) {
            return FDP;
        }

        // Handle LINKE
        if (normalized.toUpperCase().contains("LINKE") ||
                normalized.contains("DIE LINKE") ||
                normalized.contains("Linkspartei")) {
            return LINKE;
        }

        // Handle BSW
        if (normalized.toUpperCase().contains("BSW") ||
                normalized.contains("Bündnis Sahra Wagenknecht") ||
                normalized.contains("Wagenknecht")) {
            return BSW;
        }

        // Fallback: return original name if not match
        return normalized;
    }

}