package com.example.testfx.chart;

import com.example.testfx.dto.SubSectorStat;
import com.example.testfx.service.IStatisticsService;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.data.category.CategoryDataset;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.Color;
import java.awt.Font;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * G5 - Bar chart : top 10 sous-secteurs NAF dans un CTN.
 * Permet de zoomer pour voir les activites les plus dangereuses d'un secteur.
 */
public class BarChartTopNaf {

    private final JFreeChart jfreeChart;
    private final ChartViewer chartViewer;
    private final DefaultCategoryDataset dataset;

    private final IStatisticsService statsService;

    /** Mapping libellé tronqué -> libellé complet, pour afficher le nom
     *  entier du secteur dans le tooltip au survol. */
    private final Map<String, String> libellesComplets = new HashMap<>();
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

        // on ajoute du bas vers le haut (le plus grand en haut)
        // on mémorise aussi le libellé complet pour le tooltip
        libellesComplets.clear();
        for (int i = top.size() - 1; i >= 0; i--) {
            SubSectorStat s = top.get(i);
            String libelleComplet = s.libelleNAF();
            String libelleTronque = tronquer(libelleComplet, 35);
            libellesComplets.put(libelleTronque, libelleComplet);
            dataset.addValue(s.valeur(), "AT", libelleTronque);
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

        // tooltip custom : affiche le libellé COMPLET (et pas la version tronquée)
        renderer.setDefaultToolTipGenerator(new CategoryToolTipGenerator() {
            @Override
            public String generateToolTip(CategoryDataset ds, int row, int col) {
                String keyTronquee = (String) ds.getColumnKey(col);
                String libelleComplet = libellesComplets.getOrDefault(keyTronquee, keyTronquee);
                Number valeur = ds.getValue(row, col);
                return libelleComplet + " : " + ChartUtils.formater(valeur.doubleValue());
            }
        });
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setSeriesPaint(0, new Color(46, 204, 113));

        // format français + min à 0
        ChartUtils.formaterAxeNumerique((NumberAxis) plot.getRangeAxis());
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
