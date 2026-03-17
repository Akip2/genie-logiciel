package com.example.testfx.chart;

import com.example.testfx.dto.SectorRisk;
import com.example.testfx.service.IStatisticsService;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.Color;
import java.awt.Font;
import java.util.List;

/**
 * G6 - Scatter plot : frequence vs gravite.
 * Chaque point = un secteur CTN.
 * En haut a droite = les secteurs dangereux (frequents ET graves).
 */
public class ScatterChartRisque {

    private final JFreeChart jfreeChart;
    private final ChartViewer chartViewer;
    private final XYSeriesCollection dataset;

    private final IStatisticsService statsService;

    public ScatterChartRisque(IStatisticsService statsService) {
        this.statsService = statsService;

        this.dataset = new XYSeriesCollection();

        this.jfreeChart = ChartFactory.createScatterPlot(
                "Fréquence vs Gravité par secteur",
                "Indice de fréquence (AT / 1000 salariés)",
                "Taux de gravité (jours IT / 1000h)",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );

        styliserChart();
        this.chartViewer = new ChartViewer(jfreeChart);
    }

    public void update(int year) {
        dataset.removeAllSeries();

        List<SectorRisk> risques = statsService.getRiskProfile(year);
        if (risques.isEmpty()) return;

        jfreeChart.setTitle("Fréquence vs Gravité (" + year + ")");

        // une serie par CTN pour avoir des couleurs differentes
        XYPlot plot = jfreeChart.getXYPlot();

        // virer les anciennes annotations
        plot.clearAnnotations();

        for (int i = 0; i < risques.size(); i++) {
            SectorRisk r = risques.get(i);

            XYSeries serie = new XYSeries(r.codeCTN());
            serie.add(r.freq(), r.grav());
            dataset.addSeries(serie);

            // couleur du point
            XYItemRenderer renderer = plot.getRenderer();
            renderer.setSeriesPaint(i, CouleursCauses.getCouleurCTN(i));

            // etiquette a cote du point pour identifier le CTN
            XYTextAnnotation label = new XYTextAnnotation(
                    r.codeCTN(), r.freq() + 0.5, r.grav() + 0.02
            );
            label.setFont(new Font("SansSerif", Font.BOLD, 10));
            label.setPaint(CouleursCauses.getCouleurCTN(i));
            plot.addAnnotation(label);
        }
    }

    private void styliserChart() {
        jfreeChart.setBackgroundPaint(Color.WHITE);

        XYPlot plot = jfreeChart.getXYPlot();
        plot.setBackgroundPaint(new Color(245, 245, 245));
        plot.setDomainGridlinePaint(new Color(200, 200, 200));
        plot.setRangeGridlinePaint(new Color(200, 200, 200));

        // taille des points
        XYItemRenderer renderer = plot.getRenderer();
        renderer.setDefaultToolTipGenerator(new StandardXYToolTipGenerator());

        Font fontAxe = new Font("SansSerif", Font.PLAIN, 11);
        plot.getDomainAxis().setTickLabelFont(fontAxe);
        plot.getRangeAxis().setTickLabelFont(fontAxe);
    }

    public ChartViewer getNode() {
        return chartViewer;
    }
}
