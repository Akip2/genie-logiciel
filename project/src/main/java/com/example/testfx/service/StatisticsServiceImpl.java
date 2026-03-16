package com.example.testfx.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.testfx.data.DataRepository;
import com.example.testfx.dto.FilterRequest;
import com.example.testfx.dto.SectorCauses;
import com.example.testfx.dto.SectorRisk;
import com.example.testfx.dto.SectorShare;
import com.example.testfx.dto.SectorStat;
import com.example.testfx.dto.SubSectorStat;
import com.example.testfx.dto.YearValue;
import com.example.testfx.model.AccidentTravail;
import com.example.testfx.model.CauseAccident;

public class StatisticsServiceImpl implements IStatisticsService {

    private final DataRepository dataRepository;

    public StatisticsServiceImpl(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    @Override
    public List<SectorStat> getStatsByCTN(FilterRequest request, List<AccidentTravail> accidents) {

        return accidents.stream()
                
                // TKA - 16/03/2026 - Groupement des données selon la granularité (CTN, NAF2, NAF38)
                // Permet de consolider toutes les sous-lignes associées à cet agrégat
                .collect(Collectors.groupingBy(a -> getGroupingKey(a, request.getNafLevel())))
                .entrySet().stream()

                // TKA - 16/03/2026 - Sécurité pour ignorer les groupes sans clé valide
                // Parfois le code NAF2/NAF38 est manquant dans les données brutes
                .filter(entry -> entry.getKey() != null && !entry.getKey().trim().isEmpty())
                .map(entry -> {
                    String codeGroupement = entry.getKey();
                    List<AccidentTravail> listAt = entry.getValue();

                    // TKA - 16/03/2026 - Calcul de la valeur finale de l'indicateur sélectionné (F4)
                    // Utilisation de la méthode générique pour éviter la duplication de switch
                    String label = getGroupingLabel(listAt, request.getNafLevel());
                    double finalInd = computeIndicatorValue(listAt, request.getIndicator());

                    return new SectorStat(codeGroupement, label, finalInd);
                })
                // TKA - 16/03/2026 - Tri par ordre décroissant de la valeur finale
                // Assure de toujours avoir les plus gros volumes en premier dans l'UI
                .sorted(Comparator.comparingDouble((SectorStat s) -> s.getValue().doubleValue()).reversed())
                .collect(Collectors.toList());
    }
    // TKA - 16/03/2026 - Récupération de la clé de groupement
    private String getGroupingKey(AccidentTravail a, String nafLevel) {
        if (nafLevel == null) return a.getCodeCTN(); 
        return switch (nafLevel) {
            case "NAF38" -> a.getCodeNAF38();
            case "NAF2" -> a.getCodeNAF2();
            default -> a.getCodeCTN();
        };
    }
    
    // TKA - 16/03/2026 - Récupération du libellé du groupe
    private String getGroupingLabel(List<AccidentTravail> groupe, String nafLevel) {
        if (groupe == null || groupe.isEmpty()) return "Inconnu";
        AccidentTravail ref = groupe.get(0);
        // TKA - 16/03/2026 - Retourne le libellé correspondant au niveau de granularité
        if (nafLevel == null) return ref.getLibelleCTN();
        return switch (nafLevel) {
            case "NAF38" -> ref.getLibelleNAF38();
            case "NAF2" -> ref.getLibelleNAF2();
            default -> ref.getLibelleCTN();
        };
    }

    private double computeIndicatorValue(List<AccidentTravail> groupe, String indicator) {
        if (groupe == null || groupe.isEmpty()) {
            return 0.0;
        }
        // TKA - 16/03/2026 - Calcul de la valeur finale de l'indicateur sélectionné
        return switch (indicator) {
            case "atPremierReglement" -> groupe.stream().mapToDouble(AccidentTravail::getAtPremierReglement).sum();
            case "nombreSalaries" -> groupe.stream().mapToDouble(AccidentTravail::getNombreSalaries).sum();
            case "heuresTravaillees" -> groupe.stream().mapToDouble(AccidentTravail::getHeuresTravaillees).sum();
            case "journeesIT" -> groupe.stream().mapToDouble(AccidentTravail::getJourneesIT).sum();
            case "deces" -> groupe.stream().mapToDouble(AccidentTravail::getDeces).sum();
            case "nouvellesIP" -> groupe.stream().mapToDouble(AccidentTravail::getNouvellesIP).sum();

            case "indiceFrequence" -> {
                double totalAt = groupe.stream().mapToDouble(AccidentTravail::getAtPremierReglement).sum();
                double totalSal = groupe.stream().mapToDouble(AccidentTravail::getNombreSalaries).sum();

                yield (totalSal == 0) ? 0.0 : (totalAt / totalSal) * 1000.0;
            }

            case "tauxGravite" -> {
                double totalIt = groupe.stream().mapToDouble(AccidentTravail::getJourneesIT).sum();
                double totalH = groupe.stream().mapToDouble(AccidentTravail::getHeuresTravaillees).sum();

                yield (totalH == 0) ? 0.0 : (totalIt / totalH) * 1000.0;
            }
            default -> 0.0;
        };
    }
        @Override
    public Map<String, List<YearValue>> getEvolution(List<String> ctns, String indicator) {
        List<AccidentTravail> allData = dataRepository.getTout();

        if (allData.isEmpty() || ctns == null || ctns.isEmpty()) {
            return Collections.emptyMap();
        }

        // TKA - 16/03/2026 - Groupement par code CTN puis sous-groupement par année
        // Permet de construire l'historique de l'indicateur ciblé pour générer les courbes
        return allData.stream()
                .filter(at -> ctns.contains(at.getCodeCTN()))
                .collect(Collectors.groupingBy(
                        AccidentTravail::getCodeCTN,
                        Collectors.groupingBy(
                                AccidentTravail::getAnnee,
                                Collectors.summingDouble(at -> getIndicatorValue(at, indicator))
                        )
                ))
                .entrySet().stream()
                // TKA - 16/03/2026 - Transformation de la sous-map en liste de YearValue triée
                // JFreeChart attend une distribution ordonnée chronologiquement
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().entrySet().stream()
                                .map(e -> new YearValue(e.getKey(), e.getValue()))
                                .sorted(Comparator.comparingInt(YearValue::annee))
                                .collect(Collectors.toList())
                ));
    }

    private double getIndicatorValue(AccidentTravail at, String indicator) {
        return switch (indicator) {
            case "atPremierReglement" -> at.getAtPremierReglement();
            case "nombreSalaries" -> at.getNombreSalaries();
            case "heuresTravaillees" -> at.getHeuresTravaillees();
            case "journeesIT" -> at.getJourneesIT();
            case "deces" -> at.getDeces();
            case "nouvellesIP" -> at.getNouvellesIP();
            default -> 0.0;
        };
    }

    // TKA - 16/03/2026 - Calcul de la part de chaque secteur (CTN) par rapport au total
    // Utilisé pour générer un graphique en camembert sur une année donnée
    @Override
    public List<SectorShare> getShareByCTN(int year) {
        List<AccidentTravail> listAttr = dataRepository.parAnnee(year);
        
        if (listAttr.isEmpty()) {
            return Collections.emptyList();
        }

        // TKA - 16/03/2026 - Calcul du volume global de l'année pour le dénominateur
        // Requis pour évaluer la part relative (pourcentage) de chaque secteur
        double totalAt = listAttr.stream()
                .mapToDouble(AccidentTravail::getAtPremierReglement)
                .sum();

        if (totalAt == 0) {
            return Collections.emptyList();
        }

        // TKA - 16/03/2026 - Somme des accidents par secteur et calcul du pourcentage de part
        // Restitution d'une liste triée pour faciliter le mapping
        return listAttr.stream()
                .collect(Collectors.groupingBy(
                        at -> at.getCodeCTN() + "|" + at.getLibelleCTN(),
                        Collectors.summingInt(AccidentTravail::getAtPremierReglement)
                ))
                .entrySet().stream()
                .map(entry -> {
                    String[] keys = entry.getKey().split("\\|");
                    String codeCtn = keys[0];
                    String libelleCtn = keys.length > 1 ? keys[1] : "";
                    
                    int sectorAcc = entry.getValue();
                    double pct = (sectorAcc / totalAt) * 100.0;
                    
                    return new SectorShare(codeCtn, libelleCtn, sectorAcc, pct);
                })
                .sorted(Comparator.comparingDouble(SectorShare::pourcentage).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<SectorCauses> getCausesByCTN(int year, List<String> ctns) {
        // TKA - 16/03/2026 - Récupération et filtrage temporel initial (filtre F1)
        List<AccidentTravail> selectedData = dataRepository.parAnnee(year);

        if (selectedData.isEmpty() || ctns == null || ctns.isEmpty()) {
            return Collections.emptyList();
        }

        return selectedData.stream()
                // TKA - 16/03/2026 - Conservation exclusive des secteurs requis (filtre F2)
                .filter(at -> ctns.contains(at.getCodeCTN()))
                .collect(Collectors.groupingBy(
                        at -> at.getCodeCTN() + "|" + at.getLibelleCTN(),
                        
                        // TKA - 16/03/2026 - Fusion des Map<Cause, Integer> par secteur
                        // Utilisation de flatMapping pour concaténer toutes les entrées et toMap pour faire le total par cause
                        Collectors.flatMapping(
                                at -> at.getCauses().entrySet().stream(),
                                Collectors.toMap(
                                        Map.Entry::getKey,
                                        Map.Entry::getValue,
                                        Integer::sum,
                                        () -> new EnumMap<>(CauseAccident.class)
                                )
                        )
                ))
                .entrySet().stream()
                .map(entry -> {
                    String[] keys = entry.getKey().split("\\|");
                    String codeCTN = keys[0];
                    String libelleCTN = keys.length > 1 ? keys[1] : "";
                    
                    return new SectorCauses(codeCTN, libelleCTN, entry.getValue());
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<SubSectorStat> getTopNAF(int year, String ctn, String nafLevel, String indicator, int limit) {
        // TKA - 16/03/2026 - Filtrage via la méthode du repository
        List<AccidentTravail> listData = dataRepository.parCTNetAnnee(ctn, year);

        if (listData.isEmpty()) {
            return Collections.emptyList();
        }

        // TKA - 16/03/2026 - Agrégation selon le sous-niveau hierarchique NAF choisi
        return listData.stream()
                .collect(Collectors.groupingBy(
                        at -> {
                            // TKA - 16/03/2026 - Clé composé (Code|Libellé) dynamique
                            if ("NAF2".equals(nafLevel)) return at.getCodeNAF2() + "|" + at.getLibelleNAF2();
                            if ("NAF38".equals(nafLevel)) return at.getCodeNAF38() + "|" + at.getLibelleNAF38();
                            return at.getCodeNAF() + "|" + at.getLibelleNAF();
                        },
                        // TKA - 16/03/2026 - Calcul de l'indicateur choisi
                        Collectors.summingDouble(at -> getIndicatorValue(at, indicator))
                ))
                .entrySet().stream()
                // TKA - 16/03/2026 - Classement des N sous-secteurs les plus accidentogènes
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> {
                    String[] keys = entry.getKey().split("\\|");
                    String codeNAF = keys[0];
                    String lib = keys.length > 1 ? keys[1] : "";
                    
                    return new SubSectorStat(codeNAF, lib, entry.getValue());
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<SectorRisk> getRiskProfile(int year) {
        List<AccidentTravail> listData = dataRepository.parAnnee(year);

        if (listData.isEmpty()) {
            return Collections.emptyList();
        }
        
        // TKA - 16/03/2026 - Groupement par branche (CTN) pour l'analyse croisée
        // Requis pour générer le nuage de points Fréquence / Gravité
        return listData.stream()
                .collect(Collectors.groupingBy(
                        at -> at.getCodeCTN() + "|" + at.getLibelleCTN()
                ))
                .entrySet().stream()
                .map(entry -> {
                    String[] keys = entry.getKey().split("\\|");
                    String codeCTN = keys[0];
                    String libelleCTN = keys.length > 1 ? keys[1] : "";
                    List<AccidentTravail> lst = entry.getValue();

                    // TKA - 16/03/2026 - Sous-agrégations locales pour la formule métier
                    double totalAt = lst.stream().mapToDouble(AccidentTravail::getAtPremierReglement).sum();
                    double totalSal = lst.stream().mapToDouble(AccidentTravail::getNombreSalaries).sum();
                    double totalIt = lst.stream().mapToDouble(AccidentTravail::getJourneesIT).sum();
                    double totalH = lst.stream().mapToDouble(AccidentTravail::getHeuresTravaillees).sum();

                    // TKA - 16/03/2026 - Calcul des indices avec contrôle de gestion
                    // Le x 1000 exprime le taux "pour mille salariés/heures"
                    double freq = (totalSal > 0) ? (totalAt / totalSal) * 1000.0 : 0.0;
                    double grav = (totalH > 0) ? (totalIt / totalH) * 1000.0 : 0.0;

                    return new SectorRisk(codeCTN, libelleCTN, freq, grav);
                })
                .collect(Collectors.toList());
    }
}
