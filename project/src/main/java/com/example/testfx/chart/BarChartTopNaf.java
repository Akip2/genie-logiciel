package com.example.testfx.chart;

import com.example.testfx.dto.SubSectorStat;
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
import java.util.List;

/**
 * G5 - Bar chart : top 10 sous-secteurs NAF dans un CTN.
 * Permet de zoomer pour voir les activites les plus dangereuses d'un secteur.
 */
public class BarChartTopNaf {

    private final JFreeChart jfreeChart;
    private final ChartViewer chartViewer;
    private final DefaultCategoryDataset dataset;

    private final IStatisticsService statsService;
    private static final int LIMIT = 10;

    public BarChartTopNaf(IStatisticsService statsService) {
        this.statsService = statsService;

        this.dataset = new DefaultCategoryDataset();

        this.jfreeChart = ChartFactory.createBarChart(
                "Top sous-secteurs NAF",
                "Sous-secteur",
                "Nombre d'AT",
                dataset,
                PlotOrientation.HORIZONTAL,
                false,
                true,
                false
        );

        styliserChart();
        this.chartViewer = new ChartViewer(jfreeChart);
    }

    public void update(int year, String ctn, String nafLevel, String indicator) {
        dataset.clear();

        if (ctn == null || ctn.isEmpty()) return;

        List<SubSectorStat> top = statsService.getTopNAF(year, ctn, nafLevel, indicator, LIMIT);
        if (top.isEmpty()) return;

        jfreeChart.setTitle("Top " + LIMIT + " sous-secteurs de " + ctn + " (" + year + ")");

        // du bas vers le haut pour avoir le plus grand en haut
        for (int i = top.size() - 1; i >= 0; i--) {
            SubSectorStat s = top.get(i);
            String libelle = tronquer(s.libelleNAF(), 35);
            dataset.addValue(s.valeur(), "AT", libelle);
        }

        // couleur unique pour ce graphique
        CategoryPlot plot = jfreeChart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(46, 204, 113));
    }

    private void styliserChart() {
        jfreeChart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = jfreeChart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(245, 245, 245));
        plot.setRangeGridlinePaint(new Color(220, 220, 220));

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDefaultToolTipGenerator(new StandardCategoryToolTipGenerator());
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setSeriesPaint(0, new Color(46, 204, 113));

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
