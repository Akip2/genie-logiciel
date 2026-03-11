package com.example.testfx.parser;

import com.example.testfx.model.AccidentTravail;
import com.example.testfx.model.CauseAccident;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parser des fichiers Excel (.xlsx) de l'Assurance Maladie.
 *
 * Lit les fichiers "AT par CTN x NAF" (2020, 2021, 2023) au format XLSX.
 * L'ordre des colonnes change entre les années : le parser mappe les valeurs
 * par NOM d'en-tête, pas par position.
 *
 * Format attendu :
 * - Ligne 1-3 : titre et métadonnées (ignorées)
 * - Ligne 4 : en-tête avec les noms de colonnes
 * - Ligne 5+ : données (un croisement CTN × Code NAF par ligne)
 */
public class ExcelParser {

    // Ligne d'en-tête et début des données
    private static final int HEADER_ROW = 4;
    private static final int DATA_START_ROW = 5;

    /**
     * Parse un fichier Excel et retourne la liste des accidents du travail.
     *
     * @param cheminFichier chemin vers le fichier .xlsx
     * @return liste des enregistrements
     * @throws IOException en cas d'erreur de lecture
     */
    public List<AccidentTravail> parse(String cheminFichier) throws IOException {
        int annee = extractAnnee(cheminFichier);
        List<AccidentTravail> resultats = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(cheminFichier);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            // Lecture de l'en-tête et construction de la map nom -> index
            Row headerRow = sheet.getRow(HEADER_ROW - 1);
            Map<String, Integer> colMap = buildColumnMap(headerRow);

            int numCols = headerRow.getLastCellNum();

            // Parcours des lignes de données
            for (int i = DATA_START_ROW - 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Object[] rowValues = extractRowValues(row, numCols);

                // Filtrage des lignes sans code CTN (lignes de totaux ou vides)
                String ctn = getString(rowValues, colMap.get("CTN"));
                if (ctn.isEmpty()) continue;

                try {
                    AccidentTravail at = mapRow(rowValues, colMap, annee);
                    resultats.add(at);
                } catch (Exception e) {
                    System.err.printf("[ExcelParser] Erreur ligne %d de %s : %s%n",
                            i + 1, cheminFichier, e.getMessage());
                }
            }
        }

        System.out.printf("[ExcelParser] %s : %d enregistrements chargés%n",
                cheminFichier, resultats.size());
        return resultats;
    }

    /**
     * Convertit une ligne Excel en objet AccidentTravail.
     * Accède aux valeurs PAR NOM de colonne, jamais par position.
     */
    private AccidentTravail mapRow(Object[] row, Map<String, Integer> colMap, int annee) {
        AccidentTravail at = new AccidentTravail();
        at.setAnnee(annee);

        // Identification
        at.setCodeCTN(getString(row, colMap.get("CTN")));
        at.setLibelleCTN(getString(row, colMap.get("Libellé du CTN")));
        at.setCodeNAF(getString(row, colMap.get("Code NAF")));
        at.setLibelleNAF(getString(row, colMap.get("Libellé du code NAF")));
        at.setCodeNAF2(getString(row, colMap.get("Code NAF2")));
        at.setLibelleNAF2(getString(row, colMap.get("Libellé du code NAF2")));
        at.setCodeNAF38(getString(row, colMap.get("Code NAF38")));
        at.setLibelleNAF38(getString(row, colMap.get("Libellé du code NAF38")));

        // Indicateurs principaux (avec variantes de noms)
        at.setNombreSalaries(getInt(row, findCol(colMap,
                "Nombre de salariés",
                "Nombre de salariés en activités ou au chômage partiel")));
        at.setHeuresTravaillees(getLong(row, colMap.get("Nombre d'heures travaillées")));
        at.setNombreSIRET(getInt(row, colMap.get("Nombre de SIRET")));
        at.setAtPremierReglement(getInt(row, colMap.get("AT en 1er règlement")));
        at.setAtAvec4JoursArret(getInt(row, colMap.get("dont AT avec 4 jours d'arrêt ou plus sur l'année")));
        at.setNouvellesIP(getInt(row, colMap.get("Nouvelles IP")));
        at.setIpTauxInferieur10(getInt(row, colMap.get("dont IP avec taux < 10%")));
        at.setIpTauxSuperieur10(getInt(row, findCol(colMap,
                "dont IP avec taux ≥ 10%",
                "dont IP avec taux >= 10%")));
        at.setDeces(getInt(row, colMap.get("Décès")));
        at.setJourneesIT(getInt(row, colMap.get("Journées d'IT")));
        at.setSommeTauxIPInf10(getDouble(row, colMap.get("Somme des taux d'IP <10%")));
        at.setSommeTauxIP(getDouble(row, colMap.get("Somme des taux d'IP")));

        // Causes d'accidents — mappées dynamiquement par nom de colonne
        for (CauseAccident cause : CauseAccident.values()) {
            Integer idx = colMap.get(cause.getLibelleExcel());
            if (idx != null) {
                at.setCause(cause, getInt(row, idx));
            }
        }

        return at;
    }

    /**
     * Construit la map nom_colonne -> index depuis la ligne d'en-tête.
     */
    private Map<String, Integer> buildColumnMap(Row headerRow) {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell != null && cell.getCellType() == CellType.STRING) {
                String name = cell.getStringCellValue().trim();
                if (!name.isEmpty()) {
                    map.put(name, i);
                }
            }
        }
        return map;
    }

    /**
     * Cherche une colonne en essayant plusieurs noms possibles.
     * Utile quand le libellé varie entre les années.
     */
    private Integer findCol(Map<String, Integer> colMap, String... noms) {
        for (String nom : noms) {
            Integer idx = colMap.get(nom);
            if (idx != null) return idx;
        }
        return null;
    }

    /**
     * Extrait l'année depuis le nom du fichier.
     */
    private int extractAnnee(String cheminFichier) {
        String nom = cheminFichier.replaceAll(".*[/\\\\]", "").replaceAll("\\.[^.]+$", "");
        try {
            return Integer.parseInt(nom);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Impossible d'extraire l'année du fichier : " + cheminFichier +
                    ". Le fichier doit être nommé ANNEE.xlsx (ex: 2023.xlsx)");
        }
    }

    private Object[] extractRowValues(Row row, int numCols) {
        Object[] values = new Object[numCols];
        for (int i = 0; i < numCols; i++) {
            Cell cell = row.getCell(i);
            if (cell == null) {
                values[i] = null;
                continue;
            }
            values[i] = switch (cell.getCellType()) {
                case NUMERIC -> cell.getNumericCellValue();
                case STRING -> cell.getStringCellValue().trim();
                case BOOLEAN -> cell.getBooleanCellValue();
                default -> null;
            };
        }
        return values;
    }


    // Utilitaires de conversion avec gestion de cas particuliers (cellules vides, formats inattendus, etc.)

    private String getString(Object[] row, Integer colIndex) {
        if (colIndex == null || colIndex < 0 || colIndex >= row.length) return "";
        Object val = row[colIndex];
        if (val == null) return "";
        return val.toString().trim();
    }

    private int getInt(Object[] row, Integer colIndex) {
        if (colIndex == null || colIndex < 0 || colIndex >= row.length) return 0;
        Object val = row[colIndex];
        if (val == null) return 0;
        if (val instanceof Number n) return n.intValue();
        try { return (int) Double.parseDouble(val.toString().trim()); }
        catch (NumberFormatException e) { return 0; }
    }

    private long getLong(Object[] row, Integer colIndex) {
        if (colIndex == null || colIndex < 0 || colIndex >= row.length) return 0;
        Object val = row[colIndex];
        if (val == null) return 0;
        if (val instanceof Number n) return n.longValue();
        try { return (long) Double.parseDouble(val.toString().trim()); }
        catch (NumberFormatException e) { return 0; }
    }

    private double getDouble(Object[] row, Integer colIndex) {
        if (colIndex == null || colIndex < 0 || colIndex >= row.length) return 0;
        Object val = row[colIndex];
        if (val == null) return 0;
        if (val instanceof Number n) return n.doubleValue();
        try { return Double.parseDouble(val.toString().trim()); }
        catch (NumberFormatException e) { return 0; }
    }
}