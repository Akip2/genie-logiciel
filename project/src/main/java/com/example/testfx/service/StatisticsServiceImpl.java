package com.example.testfx.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.example.testfx.dto.FilterRequest;
import com.example.testfx.dto.SectorStat;
import com.example.testfx.model.AccidentTravail;

public class StatisticsServiceImpl implements IStatisticsService {

    @Override
    public List<SectorStat> getStatsByCTN(FilterRequest request, List<AccidentTravail> accidents) {

        // Note du Tech Lead: Moteur V2 optimisé pour gérer les indicateurs complexes (F4) et les niveaux de granularité (F3).
        // Au lieu de calculer l'indicateur "à la volée", on regroupe d'abord.

        return accidents.stream()
                // 1. Groupement basique O(N) : Le critère de groupement dépend de "nafLevel" (F3)
                .collect(Collectors.groupingBy(a -> getGroupingKey(a, request.getNafLevel())))

                // 2. Transformer la Map (Clé(String) -> Valeur(List<AccidentTravail>)) en un flux
                .entrySet().stream()

                // Filtre de sécurité : on ignore les groupes sans clé valide ("" ou null souvent dû à des données NAF2/NAF38 manquantes)
                .filter(entry -> entry.getKey() != null && !entry.getKey().trim().isEmpty())

                // 3. Mapping Magique O(K) où K = nb de secteurs distincts :
                //    Pour chaque liste de données sectorielles, on calcule l'indicateur consolidé.
                .map(entry -> {
                    String codeGroupement = entry.getKey();
                    List<AccidentTravail> accidentsSecteur = entry.getValue();

                    // On pioche le libellé en fonction du type de groupement
                    String libelleGroupement = getGroupingLabel(accidentsSecteur, request.getNafLevel());

                    // Calcul complexe de l'indicateur F4 demandé par l'UI sur la liste de données agrégées
                    double indicateurFinal = computeIndicatorValue(accidentsSecteur, request.getIndicator());

                    return new SectorStat(codeGroupement, libelleGroupement, indicateurFinal);
                })

                // 4. Tri descendant : Les plus gros résultats en premier O(K log K)
                .sorted(Comparator.comparingDouble((SectorStat s) -> s.getValue().doubleValue()).reversed())

                // 5. Rendre la copie à l'UI
                .collect(Collectors.toList());
    }

    /**
     * Détermine la clé de groupement en fonction du niveau de granularité (F3).
     */
    private String getGroupingKey(AccidentTravail a, String nafLevel) {
        if (nafLevel == null) return a.getCodeCTN(); // Valeur par défaut
        switch (nafLevel) {
            case "NAF38": return a.getCodeNAF38();
            case "NAF2": return a.getCodeNAF2();
            case "CTN":
            default: return a.getCodeCTN();
        }
    }

    /**
     * Récupère le libellé correspondant au niveau de groupement (F3).
     */
    private String getGroupingLabel(List<AccidentTravail> groupe, String nafLevel) {
        if (groupe == null || groupe.isEmpty()) return "Inconnu";
        AccidentTravail ref = groupe.get(0);

        if (nafLevel == null) return ref.getLibelleCTN();
        switch (nafLevel) {
            case "NAF38": return ref.getLibelleNAF38();
            case "NAF2": return ref.getLibelleNAF2();
            case "CTN":
            default: return ref.getLibelleCTN();
        }}

    /**
     * Moteur de calcul interne (F4).
     * Distingue les valeurs additives simples (comme les Décès) des calculs de taux (comme l'Indice de fréquence).
     */
    private double computeIndicatorValue(List<AccidentTravail> groupe, String indicator) {
        if (groupe == null || groupe.isEmpty()) {
            return 0.0;
        }

        switch (indicator) {
            case "atPremierReglement":
                return groupe.stream().mapToDouble(AccidentTravail::getAtPremierReglement).sum();
            case "nombreSalaries":
                return groupe.stream().mapToDouble(AccidentTravail::getNombreSalaries).sum();
            case "heuresTravaillees":
                return groupe.stream().mapToDouble(AccidentTravail::getHeuresTravaillees).sum();
            case "journeesIT":
                return groupe.stream().mapToDouble(AccidentTravail::getJourneesIT).sum();
            case "deces":
                return groupe.stream().mapToDouble(AccidentTravail::getDeces).sum();
            case "nouvellesIP":
                return groupe.stream().mapToDouble(AccidentTravail::getNouvellesIP).sum();

            // === CALCULS DE TAUX COMPLEXES (F4) === //

            case "indiceFrequence":
                // Total AT / Total Salariés * 1000
                double totalAT = groupe.stream().mapToDouble(AccidentTravail::getAtPremierReglement).sum();
                double totalSalaries = groupe.stream().mapToDouble(AccidentTravail::getNombreSalaries).sum();

                if (totalSalaries == 0) return 0.0; // Éviter division par zéro + NaN
                return (totalAT / totalSalaries) * 1000.0;

            case "tauxGravite":
                // Total Journées IT / Total heures * 1000
                double totalIT = groupe.stream().mapToDouble(AccidentTravail::getJourneesIT).sum();
                double totalHeures = groupe.stream().mapToDouble(AccidentTravail::getHeuresTravaillees).sum();

                if (totalHeures == 0) return 0.0;
                return (totalIT / totalHeures) * 1000.0;

            default:
                throw new IllegalArgumentException("Indicateur F4 non supporté : " + indicator);
        }
    }
}
