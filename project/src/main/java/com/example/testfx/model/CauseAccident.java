package com.example.testfx.model;

/**
 * Enumération des causes d'accidents du travail.
 * <p>
 * Résout le problème de l'ordre des colonnes qui change entre les années :
 * on mappe par NOM de colonne et non par position.
 */
public enum CauseAccident {

    MANUTENTION_MANUELLE("Manutention manuelle"),
    CHUTES_PLAIN_PIED("Chutes de plain-pied"),
    CHUTES_HAUTEUR("Chutes de hauteur"),
    OUTILLAGE_MAIN("Outillage à main"),
    RISQUE_MACHINES("Risque machines"),
    RISQUE_CHIMIQUE("Risque chimique"),
    RISQUE_PHYSIQUE("Risque physique dont risque électrique"),
    RISQUE_ROUTIER("Risque routier"),
    AGRESSIONS("Agressions (y compris par animaux)"),
    MANUTENTION_MECANIQUE("Manutention mécanique"),
    AUTRES_RISQUES("Autres risques"),
    AUTRES_VEHICULES("Autres véhicules de transport");

    private final String libelleExcel;

    CauseAccident(String libelleExcel) {
        this.libelleExcel = libelleExcel;
    }

    public String getLibelleExcel() {
        return libelleExcel;
    }

    /**
     * Trouve la CauseAccident correspondant à un libellé de colonne Excel.
     * Comparaison insensible à la casse et tolérante aux espaces.
     *
     * @return la cause correspondante, ou null si non reconnue
     */
    public static CauseAccident fromLibelleExcel(String libelle) {
        if (libelle == null) return null;
        String normalise = libelle.trim().toLowerCase();
        for (CauseAccident cause : values()) {
            if (cause.libelleExcel.toLowerCase().equals(normalise)) {
                return cause;
            }
        }
        return null;
    }
}