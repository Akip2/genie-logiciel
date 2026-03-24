package com.example.testfx.chart;

import com.example.testfx.dto.YearValue;
import com.example.testfx.service.IStatisticsService;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.util.List;
import java.util.Map;

/**
 * G2 — Grouped bar chart : comparaison par secteur sur 2020 / 2021 / 2023.
 * Une couleur par année, les secteurs côte à côte.
 * Affiche une ligne horizontale représentant la moyenne.
 */
public class BarChartEvolution {

    private final JFreeChart jfreeChart;
    private final ChartViewer chartViewer;
    private final DefaultCategoryDataset dataset;

    private final IStatisticsService statsService;

    // couleurs par année
    private static final Color COULEUR_2020 = new Color(52, 152, 219);   // bleu
    private static final Color COULEUR_2021 = new Color(243, 156, 18);   // orange
    private static final Color COULEUR_2023 = new Color(46, 204, 113);   // vert

    // couleur de la ligne de moyenne
    private static final Color COULEUR_MOYENNE = new Color(192, 57, 43); // rouge foncé

    public BarChartEvolution(IStatisticsService statsService) {
        this.statsService = statsService;

        this.dataset = new DefaultCategoryDataset();

        this.jfreeChart = ChartFactory.createBarChart(
                "Comparaison des accidents par secteur",
                "Secteur",
                "Nombre d'AT",
                dataset,
                PlotOrientation.VERTICAL,
                true,   // légende
                true,   // tooltips
                false
        );

        styliserChart();
        this.chartViewer = new ChartViewer(jfreeChart);
    }

    /**
     * Met à jour le graphique avec les CTN sélectionnés et l'indicateur.
     */
    public void update(List<String> ctns, String indicator) {
        dataset.clear();

        CategoryPlot plot = jfreeChart.getCategoryPlot();
        // supprimer les anciens markers avant d'en ajouter un nouveau
        plot.clearRangeMarkers();

        if (ctns == null || ctns.isEmpty()) return;

        Map<String, List<YearValue>> evolution = statsService.getEvolution(ctns, indicator);

        double sommeValeurs = 0;
        int nbValeurs = 0;

        // grouped bar : une série par année
        for (Map.Entry<String, List<YearValue>> entry : evolution.entrySet()) {
            String codeCTN = entry.getKey();

            for (YearValue yv : entry.getValue()) {
                dataset.addValue(yv.valeur(), String.valueOf(yv.annee()), codeCTN);
                sommeValeurs += yv.valeur();
                nbValeurs++;
            }
        }

        // mise à jour du titre de l'axe Y
        plot.getRangeAxis().setLabel(getLabelIndicateur(indicator));

        // couleurs par année
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        for (int i = 0; i < dataset.getRowCount(); i++) {
            String annee = (String) dataset.getRowKey(i);
            switch (annee) {
                case "2020" -> renderer.setSeriesPaint(i, COULEUR_2020);
                case "2021" -> renderer.setSeriesPaint(i, COULEUR_2021);
                case "2023" -> renderer.setSeriesPaint(i, COULEUR_2023);
            }
        }

        // ligne horizontale de la moyenne
        if (nbValeurs > 0) {
            double moyenne = sommeValeurs / nbValeurs;
            ValueMarker marker = new ValueMarker(moyenne);
            marker.setPaint(COULEUR_MOYENNE);
            marker.setStroke(new BasicStroke(
                    2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                    1.0f, new float[]{6.0f, 4.0f}, 0.0f  // pointillés
            ));
            marker.setLabel(String.format("Moyenne : %.0f", moyenne));
            marker.setLabelFont(new Font("SansSerif", Font.BOLD, 11));
            marker.setLabelPaint(COULEUR_MOYENNE);
            plot.addRangeMarker(marker);
        }
    }

    private void styliserChart() {
        jfreeChart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = jfreeChart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(245, 245, 245));
        plot.setRangeGridlinePaint(new Color(200, 200, 200));

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDefaultToolTipGenerator(new StandardCategoryToolTipGenerator());
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setItemMargin(0.05);

        Font fontAxe = new Font("SansSerif", Font.PLAIN, 11);
        plot.getDomainAxis().setTickLabelFont(fontAxe);
        plot.getRangeAxis().setTickLabelFont(fontAxe);
    }

    private String getLabelIndicateur(String indicator) {
        return switch (indicator) {
            case "atPremierReglement" -> "Nombre d'AT";
            case "indiceFrequence" -> "Indice de fréquence (\u2030)";
            case "tauxGravite" -> "Taux de gravité (\u2030)";
            case "deces" -> "Nombre de décès";
            case "journeesIT" -> "Journées d'IT";
            default -> "Valeur";
        };
    }

    public ChartViewer getNode() {
        return chartViewer;
    }
}