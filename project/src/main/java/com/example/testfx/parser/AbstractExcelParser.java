package com.example.testfx.parser;

import com.example.testfx.model.AccidentTravail;
import com.example.testfx.model.CauseAccident;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe abstraite qui définit le squelette de l'algorithme de parsing Excel.
 * Les sous-classes (XLS, XLSX) n'implémentent que les étapes spécifiques
 * à leur format, tandis que le mapping colonnes → objet est mutualisé ici.
 *
 * Cela résout un problème central : l'ordre des colonnes change entre les années.
 * On lit d'abord la ligne d'en-tête pour construire une MAP (nom_colonne -> index),
 * puis on utilise cette map pour extraire les valeurs par NOM et non par position.
 */
public abstract class AbstractExcelParser implements DataParser {

    // Ligne d'en-tête dans les fichiers Assurance Maladie
    protected static final int HEADER_ROW = 4;

    // Ligne de début des données
    protected static final int DATA_START_ROW = 5;

    // Noms de colonnes normalisés
    // Colonnes d'identification
    protected static final String COL_CTN = "CTN";
    protected static final String COL_LIBELLE_CTN = "Libellé du CTN";
    protected static final String COL_CODE_NAF = "Code NAF";
    protected static final String COL_LIBELLE_NAF = "Libellé du code NAF";
    protected static final String COL_CODE_NAF2 = "Code NAF2";
    protected static final String COL_LIBELLE_NAF2 = "Libellé du code NAF2";
    protected static final String COL_CODE_NAF38 = "Code NAF38";
    protected static final String COL_LIBELLE_NAF38 = "Libellé du code NAF38";

    /**
     * Colonnes numériques principales (les noms peuvent varier)
     * À adapter en cas de modifications pour les futures années
     */
    protected static final String[] COL_SALARIES = {
            "Nombre de salariés",
            "Nombre de salariés en activités ou au chômage partiel"
    };
    protected static final String COL_HEURES = "Nombre d'heures travaillées";
    protected static final String COL_SIRET = "Nombre de SIRET";
    protected static final String COL_AT = "AT en 1er règlement";
    protected static final String COL_AT_4JOURS = "dont AT avec 4 jours d'arrêt ou plus sur l'année";
    protected static final String COL_IP = "Nouvelles IP";
    protected static final String COL_IP_INF10 = "dont IP avec taux < 10%";
    protected static final String[] COL_IP_SUP10 = {
            "dont IP avec taux ≥ 10%",
            "dont IP avec taux >= 10%"
    };
    protected static final String COL_DECES = "Décès";
    protected static final String COL_JOURNEES_IT = "Journées d'IT";
    protected static final String COL_SOMME_IP_INF10 = "Somme des taux d'IP <10%";
    protected static final String COL_SOMME_IP = "Somme des taux d'IP";

    /**
     * Construit la map nom_colonne -> index à partir de la ligne d'en-tête.
     * Normalise les noms (trim) pour une correspondance robuste.
     */
    protected Map<String, Integer> buildColumnMap(String[] headerValues) {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < headerValues.length; i++) {
            if (headerValues[i] != null && !headerValues[i].isBlank()) {
                map.put(headerValues[i].trim(), i);
            }
        }
        return map;
    }

    /**
     * Trouve l'index d'une colonne en essayant plusieurs noms possibles.
     * Utile quand le nom varie entre les années (ex: "Nombre de salariés" vs
     * "Nombre de salariés en activités ou au chômage partiel").
     *
     * @return l'index de la colonne, ou -1 si non trouvée
     */
    protected int findColumn(Map<String, Integer> colMap, String... possibleNames) {
        for (String name : possibleNames) {
            Integer idx = colMap.get(name);
            if (idx != null) return idx;
        }
        return -1;
    }

    /**
     * Peuple un objet AccidentTravail à partir des valeurs d'une ligne
     * et de la map de colonnes. On accède aux valeurs PAR NOM et non PAR POSITION.
     */
    protected AccidentTravail mapRowToAccident(Object[] rowValues, Map<String, Integer> colMap, int annee) {
        AccidentTravail at = new AccidentTravail();
        at.setAnnee(annee);

        // Identification
        at.setCodeCTN(getString(rowValues, colMap.get(COL_CTN)));
        at.setLibelleCTN(getString(rowValues, colMap.get(COL_LIBELLE_CTN)));
        at.setCodeNAF(getString(rowValues, colMap.get(COL_CODE_NAF)));
        at.setLibelleNAF(getString(rowValues, colMap.get(COL_LIBELLE_NAF)));
        at.setCodeNAF2(getString(rowValues, colMap.get(COL_CODE_NAF2)));
        at.setLibelleNAF2(getString(rowValues, colMap.get(COL_LIBELLE_NAF2)));
        at.setCodeNAF38(getString(rowValues, colMap.get(COL_CODE_NAF38)));
        at.setLibelleNAF38(getString(rowValues, colMap.get(COL_LIBELLE_NAF38)));

        // Indicateurs principaux
        at.setNombreSalaries(getInt(rowValues, findColumn(colMap, COL_SALARIES)));
        at.setHeuresTravaillees(getLong(rowValues, colMap.get(COL_HEURES)));
        at.setNombreSIRET(getInt(rowValues, colMap.get(COL_SIRET)));
        at.setAtPremierReglement(getInt(rowValues, colMap.get(COL_AT)));
        at.setAtAvec4JoursArret(getInt(rowValues, colMap.get(COL_AT_4JOURS)));
        at.setNouvellesIP(getInt(rowValues, colMap.get(COL_IP)));
        at.setIpTauxInferieur10(getInt(rowValues, colMap.get(COL_IP_INF10)));
        at.setIpTauxSuperieur10(getInt(rowValues, findColumn(colMap, COL_IP_SUP10)));
        at.setDeces(getInt(rowValues, colMap.get(COL_DECES)));
        at.setJourneesIT(getInt(rowValues, colMap.get(COL_JOURNEES_IT)));
        at.setSommeTauxIPInf10(getDouble(rowValues, colMap.get(COL_SOMME_IP_INF10)));
        at.setSommeTauxIP(getDouble(rowValues, colMap.get(COL_SOMME_IP)));

        // Causes d'accidents — mappées dynamiquement par nom de colonne
        for (CauseAccident cause : CauseAccident.values()) {
            Integer idx = colMap.get(cause.getLibelleExcel());
            if (idx != null) {
                at.setCause(cause, getInt(rowValues, idx));
            }
        }

        return at;
    }

    /**
     * Extrait l'année depuis le nom du fichier (ex: "data/2023.xlsx" -> 2023).
     */
    protected int extractAnnee(String cheminFichier) {
        String nomFichier = cheminFichier.replaceAll(".*[/\\\\]", "")  // garder le nom
                                          .replaceAll("\\.[^.]+$", ""); // enlever l'extension
        try {
            return Integer.parseInt(nomFichier);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Impossible d'extraire l'année du fichier : " + cheminFichier +
                    ". Le fichier doit être nommé ANNEE.xlsx (ex: 2023.xlsx)");
        }
    }

    // Utilitaires de conversion

    protected String getString(Object[] row, Integer colIndex) {
        if (colIndex == null || colIndex < 0 || colIndex >= row.length) return "";
        Object val = row[colIndex];
        if (val == null) return "";
        return val.toString().trim();
    }

    protected int getInt(Object[] row, Integer colIndex) {
        if (colIndex == null || colIndex < 0 || colIndex >= row.length) return 0;
        Object val = row[colIndex];
        if (val == null) return 0;
        if (val instanceof Number n) return n.intValue();
        try {
            return (int) Double.parseDouble(val.toString().trim());
        } catch (NumberFormatException e) {
            return 0; // Cases vides = 0 (dit dans le fichier)
        }
    }

    protected long getLong(Object[] row, Integer colIndex) {
        if (colIndex == null || colIndex < 0 || colIndex >= row.length) return 0;
        Object val = row[colIndex];
        if (val == null) return 0;
        if (val instanceof Number n) return n.longValue();
        try {
            return (long) Double.parseDouble(val.toString().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    protected double getDouble(Object[] row, Integer colIndex) {
        if (colIndex == null || colIndex < 0 || colIndex >= row.length) return 0;
        Object val = row[colIndex];
        if (val == null) return 0;
        if (val instanceof Number n) return n.doubleValue();
        try {
            return Double.parseDouble(val.toString().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}