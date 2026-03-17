package com.example.testfx.chart;

import com.example.testfx.dto.SectorShare;
import com.example.testfx.service.IStatisticsService;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieToolTipGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.data.general.DefaultPieDataset;

import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;
import java.util.List;

/**
 * G3 - Pie chart : repartition des AT par secteur.
 * Chaque part = un CTN avec son pourcentage.
 */
public class PieChartRepartition {

    private final JFreeChart jfreeChart;
    private final ChartViewer chartViewer;
    private final DefaultPieDataset<String> dataset;

    private final IStatisticsService statsService;

    public PieChartRepartition(IStatisticsService statsService) {
        this.statsService = statsService;

        this.dataset = new DefaultPieDataset<>();

        this.jfreeChart = ChartFactory.createPieChart(
                "Répartition des accidents par secteur",
                dataset,
                true,   // legende
                true,   // tooltips
                false
        );

        styliserChart();
        this.chartViewer = new ChartViewer(jfreeChart);
    }

    public void update(int year) {
        dataset.clear();

        List<SectorShare> shares = statsService.getShareByCTN(year);

        jfreeChart.setTitle("Répartition des AT par secteur (" + year + ")");

        // remplissage du dataset
        for (SectorShare s : shares) {
            String key = s.codeCTN();
            dataset.setValue(key, s.totalAccidents());
        }

        // couleurs des parts
        @SuppressWarnings("unchecked")
        PiePlot<String> plot = (PiePlot<String>) jfreeChart.getPlot();
        for (int i = 0; i < shares.size(); i++) {
            plot.setSectionPaint(shares.get(i).codeCTN(), CouleursCauses.getCouleurCTN(i));
        }
    }

    private void styliserChart() {
        jfreeChart.setBackgroundPaint(Color.WHITE);

        @SuppressWarnings("unchecked")
        PiePlot<String> plot = (PiePlot<String>) jfreeChart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setShadowPaint(null);

        // afficher le pourcentage sur chaque part
        plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 11));
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
                "{0} ({2})", new DecimalFormat("0"), new DecimalFormat("0.0%")
        ));

        // tooltip au survol : nom + valeur + pourcentage
        plot.setToolTipGenerator(new StandardPieToolTipGenerator(
                "{0} : {1} AT ({2})", new DecimalFormat("#,##0"), new DecimalFormat("0.0%")
        ));
    }

    public ChartViewer getNode() {
        return chartViewer;
    }
}
