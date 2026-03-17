package com.example.testfx.chart;

import com.example.testfx.data.DataRepository;
import com.example.testfx.dto.FilterRequest;
import com.example.testfx.service.DataFilterService;
import com.example.testfx.service.IStatisticsService;
import com.example.testfx.service.StatisticsServiceImpl;

import java.util.List;

public class ChartManager {

    private final BarChartSecteur barChartSecteur;
    private final BarChartEvolution BarChartEvolution;
    private final PieChartRepartition pieChartRepartition;
    private final StackedBarChartCauses stackedBarChartCauses;
    private final BarChartTopNaf barChartTopNaf;
    private final ScatterChartRisque scatterChartRisque;

    private final IStatisticsService statsService;
    private final DataFilterService filterService;

    // tous les CTN pour le cas ou aucun filtre n'est applique
    private static final List<String> TOUS_CTN = List.of(
            "AA", "BB", "CC", "DD", "EE", "FF", "GG", "HH", "II"
    );

    public ChartManager(DataRepository repository) {
        this.statsService = new StatisticsServiceImpl(repository);
        this.filterService = new DataFilterService(repository);

        // MSD - 16/03 - init des 6 graphiques
        this.barChartSecteur = new BarChartSecteur(statsService, filterService);
        this.BarChartEvolution = new BarChartEvolution(statsService);
        this.pieChartRepartition = new PieChartRepartition(statsService);
        this.stackedBarChartCauses = new StackedBarChartCauses(statsService);
        this.barChartTopNaf = new BarChartTopNaf(statsService);
        this.scatterChartRisque = new ScatterChartRisque(statsService);
    }

    /**
     * Rafraichit TOUS les graphiques d'un coup.
     * Utile au chargement initial ou quand l'annee change.
     */
    public void updateAll(FilterRequest request) {
        List<String> ctns = request.getSelectedCTNs();
        List<String> ctnsEffectifs = (ctns != null && !ctns.isEmpty()) ? ctns : TOUS_CTN;

        barChartSecteur.update(request);
        BarChartEvolution.update(ctnsEffectifs, request.getIndicator());
        pieChartRepartition.update(request.getYear());
        stackedBarChartCauses.update(request.getYear(), ctnsEffectifs);
        scatterChartRisque.update(request.getYear());

        // top NAF : on prend le premier CTN selectionne
        if (!ctnsEffectifs.isEmpty()) {
            barChartTopNaf.update(
                    request.getYear(),
                    ctnsEffectifs.get(0),
                    request.getNafLevel(),
                    request.getIndicator()
            );
        }
    }

    // --- Getters pour Nathan ---

    public BarChartSecteur getBarChartSecteur() { return barChartSecteur; }
    public BarChartEvolution getLineChartEvolution() { return BarChartEvolution; }
    public PieChartRepartition getPieChartRepartition() { return pieChartRepartition; }
    public StackedBarChartCauses getStackedBarChartCauses() { return stackedBarChartCauses; }
    public BarChartTopNaf getBarChartTopNaf() { return barChartTopNaf; }
    public ScatterChartRisque getScatterChartRisque() { return scatterChartRisque; }
}