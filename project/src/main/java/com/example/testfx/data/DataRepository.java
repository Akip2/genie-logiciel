package com.example.testfx.data;

import com.example.testfx.model.AccidentTravail;
import com.example.testfx.parser.ExcelParser;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Point d'accès unique et simplifié aux données d'accidents du travail.
 * Cache la complexité du chargement multi-fichiers et offre une API de filtrage.
 *
 * Périmètre : chargement, filtrage et accès aux données brutes.
 * Les calculs statistiques et la logique métier sont dans la couche service.
 */
public class DataRepository {

    private final List<AccidentTravail> donnees;
    private final Set<Integer> anneesChargees;
    private final ExcelParser parser;

    public DataRepository() {
        this.donnees = new ArrayList<>();
        this.anneesChargees = new TreeSet<>();
        this.parser = new ExcelParser();
    }

    // Chargement des données

    // Charge TOUS les fichiers .xlsx d'un dossier
    public void chargerDossier(String cheminDossier) throws IOException {
        Path dossier = Path.of(cheminDossier);
        if (!Files.isDirectory(dossier)) {
            throw new IOException("Le chemin n'est pas un dossier : " + cheminDossier);
        }

        int fichierCharges = 0;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dossier, "*.xlsx")) {
            for (Path fichier : stream) {
                chargerFichier(fichier.toString());
                fichierCharges++;
            }
        }

        System.out.printf("[DataRepository] %d fichier(s) chargé(s), %d enregistrements au total, " +
                        "années : %s%n", fichierCharges, donnees.size(), anneesChargees);
    }

    // Charge un fichier .xlsx unique
    public void chargerFichier(String cheminFichier) throws IOException {
        List<AccidentTravail> resultats = parser.parse(cheminFichier);
        donnees.addAll(resultats);
        resultats.stream().map(AccidentTravail::getAnnee).forEach(anneesChargees::add);
    }

    // État des données

    public boolean estCharge() {
        return !donnees.isEmpty();
    }

    public int getNombreEnregistrements() {
        return donnees.size();
    }

    /**
     * Retourne les années disponibles (triées)
     * Note : 2022 est absente (données manquantes côté Assurance Maladie).
     */
    public List<Integer> getAnneesDisponibles() {
        return List.copyOf(anneesChargees);
    }

    public boolean anneeDisponible(int annee) {
        return anneesChargees.contains(annee);
    }

    // Filtrage et accès aux données

    // Toutes les données brutes
    public List<AccidentTravail> getTout() {
        return List.copyOf(donnees);
    }

    // Filtre par année
    public List<AccidentTravail> parAnnee(int annee) {
        return donnees.stream()
                .filter(at -> at.getAnnee() == annee)
                .collect(Collectors.toList());
    }

    // Filtre par code CTN (secteur d'activité)
    public List<AccidentTravail> parCTN(String codeCTN) {
        return donnees.stream()
                .filter(at -> at.getCodeCTN().equalsIgnoreCase(codeCTN))
                .collect(Collectors.toList());
    }

    // Filtre par CTN et année
    public List<AccidentTravail> parCTNetAnnee(String codeCTN, int annee) {
        return donnees.stream()
                .filter(at -> at.getCodeCTN().equalsIgnoreCase(codeCTN) && at.getAnnee() == annee)
                .collect(Collectors.toList());
    }

    // Filtre par code NAF (5 caractères)
    public List<AccidentTravail> parCodeNAF(String codeNAF) {
        return donnees.stream()
                .filter(at -> at.getCodeNAF().equalsIgnoreCase(codeNAF))
                .collect(Collectors.toList());
    }

    // Filtre par code NAF2 (2 caractères, niveau agrégé)
    public List<AccidentTravail> parCodeNAF2(String codeNAF2) {
        return donnees.stream()
                .filter(at -> at.getCodeNAF2().equalsIgnoreCase(codeNAF2))
                .collect(Collectors.toList());
    }

    // Recherche par texte dans le libellé NAF (insensible à la casse)
    public List<AccidentTravail> rechercherActivite(String texte) {
        String recherche = texte.toUpperCase();
        return donnees.stream()
                .filter(at -> at.getLibelleNAF().toUpperCase().contains(recherche))
                .collect(Collectors.toList());
    }

    // Référentiels

    // Liste des codes CTN distincts chargés
    public List<String> getListeCTN() {
        return donnees.stream()
                .map(AccidentTravail::getCodeCTN)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    // Map code CTN -> libellé CTN
    public Map<String, String> getReferentielCTN() {
        return donnees.stream()
                .collect(Collectors.toMap(
                        AccidentTravail::getCodeCTN,
                        AccidentTravail::getLibelleCTN,
                        (a, b) -> a,
                        TreeMap::new
                ));
    }

    // Map code NAF2 -> libellé NAF2
    public Map<String, String> getReferentielNAF2() {
        return donnees.stream()
                .filter(at -> !at.getCodeNAF2().isEmpty())
                .collect(Collectors.toMap(
                        AccidentTravail::getCodeNAF2,
                        AccidentTravail::getLibelleNAF2,
                        (a, b) -> a,
                        TreeMap::new
                ));
    }
}