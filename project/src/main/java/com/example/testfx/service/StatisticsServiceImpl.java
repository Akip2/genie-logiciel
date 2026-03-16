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
                
                .collect(Collectors.groupingBy(a -> getGroupingKey(a, request.getNafLevel())))
                .entrySet().stream()

                // TKA - 15/03/2026 - vire les keys null
                .filter(entry -> entry.getKey() != null && !entry.getKey().trim().isEmpty())
                .map(entry -> {
                    String codeGroupement = entry.getKey();
                    List<AccidentTravail> listAt = entry.getValue();

                    String label = getGroupingLabel(listAt, request.getNafLevel());
                    double finalInd = computeIndicatorValue(listAt, request.getIndicator());

                    return new SectorStat(codeGroupement, label, finalInd);
                })
                .sorted(Comparator.comparingDouble((SectorStat s) -> s.getValue().doubleValue()).reversed())
                .collect(Collectors.toList());
    }

    private String getGroupingKey(AccidentTravail a, String nafLevel) {
        if (nafLevel == null) return a.getCodeCTN(); 
        return switch (nafLevel) {
            case "NAF38" -> a.getCodeNAF38();
            case "NAF2" -> a.getCodeNAF2();
            default -> a.getCodeCTN();
        };
    }

    private String getGroupingLabel(List<AccidentTravail> groupe, String nafLevel) {
        if (groupe == null || groupe.isEmpty()) return "Inconnu";
        AccidentTravail ref = groupe.get(0);

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

        // TKA - 16/03 - group by ctn et annee pr evo
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

    @Override
    public List<SectorShare> getShareByCTN(int year) {
        List<AccidentTravail> listAttr = dataRepository.parAnnee(year);
        
        if (listAttr.isEmpty()) {
            return Collections.emptyList();
        }

        double totalAt = listAttr.stream()
                .mapToDouble(AccidentTravail::getAtPremierReglement)
                .sum();

        if (totalAt == 0) {
            return Collections.emptyList();
        }

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
        List<AccidentTravail> selectedData = dataRepository.parAnnee(year);

        if (selectedData.isEmpty() || ctns == null || ctns.isEmpty()) {
            return Collections.emptyList();
        }

        return selectedData.stream()
                .filter(at -> ctns.contains(at.getCodeCTN()))
                .collect(Collectors.groupingBy(
                        at -> at.getCodeCTN() + "|" + at.getLibelleCTN(),
                        
                        // TKA - 16/03 - merge les enummap 
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
        List<AccidentTravail> listData = dataRepository.parCTNetAnnee(ctn, year);

        if (listData.isEmpty()) {
            return Collections.emptyList();
        }

        return listData.stream()
                .collect(Collectors.groupingBy(
                        at -> {
                            if ("NAF2".equals(nafLevel)) return at.getCodeNAF2() + "|" + at.getLibelleNAF2();
                            if ("NAF38".equals(nafLevel)) return at.getCodeNAF38() + "|" + at.getLibelleNAF38();
                            return at.getCodeNAF() + "|" + at.getLibelleNAF();
                        },
                        Collectors.summingDouble(at -> getIndicatorValue(at, indicator))
                ))
                .entrySet().stream()
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
        
        // TKA - 16/03 - groupby sect et calcul freq / gravite
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

                    double totalAt = lst.stream().mapToDouble(AccidentTravail::getAtPremierReglement).sum();
                    double totalSal = lst.stream().mapToDouble(AccidentTravail::getNombreSalaries).sum();
                    double totalIt = lst.stream().mapToDouble(AccidentTravail::getJourneesIT).sum();
                    double totalH = lst.stream().mapToDouble(AccidentTravail::getHeuresTravaillees).sum();

                    double freq = (totalSal > 0) ? (totalAt / totalSal) * 1000.0 : 0.0;
                    double grav = (totalH > 0) ? (totalIt / totalH) * 1000.0 : 0.0;

                    return new SectorRisk(codeCTN, libelleCTN, freq, grav);
                })
                .collect(Collectors.toList());
    }
}
