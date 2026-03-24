package com.example.testfx.chart;

import com.example.testfx.dto.FilterRequest;
import com.example.testfx.dto.SectorStat;
import com.example.testfx.model.AccidentTravail;
import com.example.testfx.service.DataFilterService;
import com.example.testfx.service.IStatisticsService;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * G7 - Comparaison multi-criteres des secteurs CTN.
 * Affiche 4 indicateurs normalises (0-100) cote a cote pour chaque CTN.
 * Permet de voir d'un coup quel secteur est le pire en volume, en frequence, en gravite et en deces.
 * La normalisation ramene chaque indicateur sur une echelle commune :
 * 100 = le max parmi les secteurs affiches pour cet indicateur.
 * Les tooltips montrent la valeur reelle.
 */
public class BarChartComparaison {

    private final JFreeChart jfreeChart;
    private final ChartViewer chartViewer;
    private final DefaultCategoryDataset dataset;

    private final IStatisticsService statsService;
    private final DataFilterService filterService;

    // les 4 indicateurs compares
    private static final String[] INDICATEURS = {
            "atPremierReglement", "indiceFrequence", "tauxGravite", "deces"
    };
    private static final String[] LABELS_INDICATEURS = {
            "Nb AT", "Fréquence", "Gravité", "Décès"
    };

    // stocke les valeurs reelles pour les tooltips
    private final Map<String, Double> valeursReelles = new HashMap<>();

    public BarChartComparaison(IStatisticsService statsService, DataFilterService filterService) {
        this.statsService = statsService;
        this.filterService = filterService;

        this.dataset = new DefaultCategoryDataset();

        this.jfreeChart = ChartFactory.createBarChart(
                "Comparaison multi-critères des secteurs",
                "Indicateur",
                "Score relatif (0-100)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        styliserChart();
        this.chartViewer = new ChartViewer(jfreeChart);
    }

    /**
     * Met a jour avec l'annee et les CTN selectionnes.
     * Appelle getStatsByCTN 4 fois (un par indicateur) puis normalise.
     */
    public void update(int year, List<String> ctns) {
        dataset.clear();
        valeursReelles.clear();

        if (ctns == null || ctns.isEmpty()) return;

        jfreeChart.setTitle("Comparaison multi-critères (" + year + ")");

        // recuperer les stats pour chaque indicateur
        // structure : indicateur -> (codeCTN -> valeur)
        Map<String, Map<String, Double>> statsParIndicateur = new LinkedHashMap<>();

        for (String indicateur : INDICATEURS) {
            FilterRequest req = new FilterRequest.Builder()
                    .year(year)
                    .selectedCTNs(ctns)
                    .nafLevel("CTN")
                    .indicator(indicateur)
                    .build();

            List<AccidentTravail> donnees = filterService.applyBaseFilters(req);
            List<SectorStat> stats = statsService.getStatsByCTN(req, donnees);

            Map<String, Double> parCTN = new LinkedHashMap<>();
            for (SectorStat s : stats) {
                parCTN.put(s.getCodeCTN(), s.getValue().doubleValue());
            }
            statsParIndicateur.put(indicateur, parCTN);
        }

        // normaliser et remplir le dataset
        for (int i = 0; i < INDICATEURS.length; i++) {
            String indicateur = INDICATEURS[i];
            String label = LABELS_INDICATEURS[i];
            Map<String, Double> parCTN = statsParIndicateur.get(indicateur);

            // on normalise par rapport au max de cet indicateur
            double max = parCTN.values().stream()
                    .mapToDouble(Double::doubleValue)
                    .max().orElse(1.0);
            if (max == 0) max = 1.0;

            for (String ctn : ctns) {
                double valeurReelle = parCTN.getOrDefault(ctn, 0.0);
                double score = (valeurReelle / max) * 100.0;

                dataset.addValue(score, ctn, label);
                valeursReelles.put(ctn + "|" + label, valeurReelle);
            }
        }

        // couleurs par CTN
        CategoryPlot plot = jfreeChart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        for (int i = 0; i < dataset.getRowCount(); i++) {
            renderer.setSeriesPaint(i, CouleursCauses.getCouleurCTN(i));
        }
    }

    private void styliserChart() {
        jfreeChart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = jfreeChart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(245, 245, 245));
        plot.setRangeGridlinePaint(new Color(200, 200, 200));

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setItemMargin(0.08);

        // tooltip custom : affiche la valeur reelle et pas le score normalise
        renderer.setDefaultToolTipGenerator(new CategoryToolTipGenerator() {
            @Override
            public String generateToolTip(CategoryDataset ds, int row, int col) {
                String ctn = (String) ds.getRowKey(row);
                String indicateur = (String) ds.getColumnKey(col);
                Double reel = valeursReelles.get(ctn + "|" + indicateur);
                if (reel == null) return ctn;
                return String.format("%s — %s : %.1f", ctn, indicateur, reel);
            }
        });

        Font fontAxe = new Font("SansSerif", Font.PLAIN, 11);
        plot.getDomainAxis().setTickLabelFont(fontAxe);
        plot.getRangeAxis().setTickLabelFont(fontAxe);
    }

    public ChartViewer getNode() {
        return chartViewer;
    }
}