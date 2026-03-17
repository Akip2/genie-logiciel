package com.example.testfx.chart;

import com.example.testfx.model.CauseAccident;
import java.awt.Color;
import java.util.EnumMap;
import java.util.Map;

/**
 * Palette de couleurs pour les graphiques.
 * 12 couleurs pour les causes d'accidents, 9 pour les secteurs CTN.
 */
public class CouleursCauses {

    private static final Map<CauseAccident, Color> COULEURS = new EnumMap<>(CauseAccident.class);

    static {
        COULEURS.put(CauseAccident.MANUTENTION_MANUELLE,   new Color(231, 76, 60));   // rouge
        COULEURS.put(CauseAccident.CHUTES_PLAIN_PIED,      new Color(52, 152, 219));  // bleu
        COULEURS.put(CauseAccident.CHUTES_HAUTEUR,          new Color(243, 156, 18));  // orange
        COULEURS.put(CauseAccident.OUTILLAGE_MAIN,          new Color(46, 204, 113));  // vert
        COULEURS.put(CauseAccident.RISQUE_MACHINES,         new Color(155, 89, 182));  // violet
        COULEURS.put(CauseAccident.RISQUE_ROUTIER,          new Color(26, 188, 156));  // turquoise
        COULEURS.put(CauseAccident.AGRESSIONS,              new Color(230, 126, 34));  // orange fonce
        COULEURS.put(CauseAccident.RISQUE_CHIMIQUE,         new Color(241, 196, 15));  // jaune
        COULEURS.put(CauseAccident.RISQUE_PHYSIQUE,         new Color(52, 73, 94));    // gris fonce
        COULEURS.put(CauseAccident.MANUTENTION_MECANIQUE,   new Color(149, 165, 166)); // gris
        COULEURS.put(CauseAccident.AUTRES_VEHICULES,        new Color(189, 195, 199)); // gris clair
        COULEURS.put(CauseAccident.AUTRES_RISQUES,          new Color(127, 140, 141)); // gris moyen
    }

    public static Color getCouleur(CauseAccident cause) {
        return COULEURS.getOrDefault(cause, Color.DARK_GRAY);
    }

    // couleurs secteurs CTN
    private static final Color[] COULEURS_CTN = {
            new Color(231, 76, 60),   // AA
            new Color(52, 152, 219),  // BB
            new Color(46, 204, 113),  // CC
            new Color(243, 156, 18),  // DD
            new Color(155, 89, 182),  // EE
            new Color(26, 188, 156),  // FF
            new Color(230, 126, 34),  // GG
            new Color(52, 73, 94),    // HH
            new Color(22, 160, 133)   // II
    };

    public static Color getCouleurCTN(int index) {
        return COULEURS_CTN[index % COULEURS_CTN.length];
    }
}
