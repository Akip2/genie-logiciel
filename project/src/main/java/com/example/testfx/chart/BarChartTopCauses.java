package com.example.testfx.chart;

import com.example.testfx.dto.SectorCauses;
import com.example.testfx.model.CauseAccident;
import com.example.testfx.service.IStatisticsService;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * G8 - Classement global des 12 causes d'accidents.
 * Bar chart horizontal trie du plus frequent au moins frequent.
 * Agrege les causes de tous les CTN selectionnes.
 * Complementaire au G4 (StackedBarChartCauses) :
 * G4 montre les causes PAR secteur, G8 montre le classement GLOBAL.
 */
public class BarChartTopCauses {

    private final JFreeChart jfreeChart;
    private final ChartViewer chartViewer;
    private final DefaultCategoryDataset dataset;

    private final IStatisticsService statsService;

    public BarChartTopCauses(IStatisticsService statsService) {
        this.statsService = statsService;

        this.dataset = new DefaultCategoryDataset();

        this.jfreeChart = ChartFactory.createBarChart(
                "Top facteurs de risque",
                "Cause",
                "Nombre d'AT",
                dataset,
                PlotOrientation.HORIZONTAL,
                false,  // pas de legende (une seule serie)
                true,   // tooltips
                false
        );

        styliserChart();
        this.chartViewer = new ChartViewer(jfreeChart);
    }

    /**
     * Met a jour avec l'annee et les CTN selectionnes.
     * Recupere getCausesByCTN puis somme toutes les causes.
     */
    public void update(int year, List<String> ctns) {
        dataset.clear();

        if (ctns == null || ctns.isEmpty()) return;

        jfreeChart.setTitle("Top facteurs de risque (" + year + ")");

        List<SectorCauses> donnees = statsService.getCausesByCTN(year, ctns);
        if (donnees.isEmpty()) return;

        // sommer les causes de tous les secteurs
        Map<CauseAccident, Integer> totaux = new EnumMap<>(CauseAccident.class);
        for (CauseAccident cause : CauseAccident.values()) {
            int total = 0;
            for (SectorCauses sc : donnees) {
                total += sc.repartitionCauses().getOrDefault(cause, 0);
            }
            totaux.put(cause, total);
        }

        // trier par total decroissant
        List<CauseAccident> causeTriees = new ArrayList<>(totaux.keySet());
        causeTriees.sort((a, b) -> totaux.get(b) - totaux.get(a));

        // remplir du bas vers le haut (le plus grand en haut)
        for (int i = causeTriees.size() - 1; i >= 0; i--) {
            CauseAccident cause = causeTriees.get(i);
            int total = totaux.get(cause);
            String libelle = tronquer(cause.getLibelleExcel(), 35);
            dataset.addValue(total, "Causes", libelle);
        }

        // couleur rouge fonce pour toutes les barres
        CategoryPlot plot = jfreeChart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(192, 57, 43));
    }

    private void styliserChart() {
        jfreeChart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = jfreeChart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(245, 245, 245));
        plot.setRangeGridlinePaint(new Color(200, 200, 200));

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDefaultToolTipGenerator(new StandardCategoryToolTipGenerator());
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setSeriesPaint(0, new Color(192, 57, 43));

        Font fontAxe = new Font("SansSerif", Font.PLAIN, 10);
        plot.getDomainAxis().setTickLabelFont(fontAxe);
        plot.getRangeAxis().setTickLabelFont(fontAxe);
    }

    private String tronquer(String texte, int max) {
        if (texte == null) return "";
        if (texte.length() <= max) return texte;
        return texte.substring(0, max - 3) + "...";
    }

    public ChartViewer getNode() {
        return chartViewer;
    }
}