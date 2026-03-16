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
}
