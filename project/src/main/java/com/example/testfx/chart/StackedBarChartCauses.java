package com.example.testfx.chart;

import com.example.testfx.dto.SectorCauses;
import com.example.testfx.model.CauseAccident;
import com.example.testfx.service.IStatisticsService;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StackedBarRenderer;
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
 * G4 - Stacked bar chart : causes d'accidents par secteur
 * 12 causes empilees par CTN
 * Montre le profil de risque specifique de chaque secteur
 */
public class StackedBarChartCauses {

    private final JFreeChart jfreeChart;
    private final ChartViewer chartViewer;
    private final DefaultCategoryDataset dataset;

    private final IStatisticsService statsService;

    // toggle absolu / pourcentage
    private boolean enPourcentage = false;

    public StackedBarChartCauses(IStatisticsService statsService) {
        this.statsService = statsService;

        this.dataset = new DefaultCategoryDataset();

        this.jfreeChart = ChartFactory.createStackedBarChart(
                "Causes d'accidents par secteur",
                "Secteur",
                "Nombre d'AT",
                dataset,
                PlotOrientation.HORIZONTAL,
                true,   // legende (necessaire vu qu'il y a 12 causes)
                true,   // tooltips
                false   // pas besoin
        );

        styliserChart();
        this.chartViewer = new ChartViewer(jfreeChart);
    }

    /**
     * Remplit le graphique avec les donnees de causes par secteur.
     */
    public void update(int year, List<String> ctns) {
        dataset.clear();

        if (ctns == null || ctns.isEmpty()) return;

        List<SectorCauses> donnees = statsService.getCausesByCTN(year, ctns);
        if (donnees.isEmpty()) return;

        jfreeChart.setTitle("Causes d'accidents par secteur (" + year + ")");

        CategoryPlot plot = jfreeChart.getCategoryPlot();
        plot.getRangeAxis().setLabel(enPourcentage ? "Proportion (%)" : "Nombre d'AT");

        // MSD - 17/03 - trier les causes par total global (plus grosse a gauche)
        Map<CauseAccident, Integer> totauxGlobaux = new EnumMap<>(CauseAccident.class);
        for (CauseAccident cause : CauseAccident.values()) {
            int total = 0;
            for (SectorCauses sc : donnees) {
                total += sc.repartitionCauses().getOrDefault(cause, 0);
            }
            totauxGlobaux.put(cause, total);
        }

        List<CauseAccident> causeTriees = new ArrayList<>(List.of(CauseAccident.values()));
        causeTriees.sort((a, b) -> totauxGlobaux.get(b) - totauxGlobaux.get(a));

        // remplir le dataset dans l'ordre trie
        for (SectorCauses sc : donnees) {
            Map<CauseAccident, Integer> repartition = sc.repartitionCauses();
            String libelleSecteur = tronquer(sc.libelleCTN(), 25);

            int totalSecteur = repartition.values().stream()
                    .mapToInt(Integer::intValue).sum();

            for (CauseAccident cause : causeTriees) {
                int brut = repartition.getOrDefault(cause, 0);
                double valeur;

                if (enPourcentage && totalSecteur > 0) {
                    valeur = (brut * 100.0) / totalSecteur;
                } else {
                    valeur = brut;
                }

                dataset.addValue(valeur, cause.getLibelleExcel(), libelleSecteur);
            }
        }

        appliquerCouleurs(causeTriees);
    }

    private void appliquerCouleurs(List<CauseAccident> causeTriees) {
        CategoryPlot plot = jfreeChart.getCategoryPlot();
        StackedBarRenderer renderer = (StackedBarRenderer) plot.getRenderer();

        for (int i = 0; i < causeTriees.size(); i++) {
            renderer.setSeriesPaint(i, CouleursCauses.getCouleur(causeTriees.get(i)));
        }
    }

    private void styliserChart() {
        jfreeChart.setBackgroundPaint(Color.WHITE);

        CategoryPlot plot = jfreeChart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(245, 245, 245));
        plot.setRangeGridlinePaint(new Color(200, 200, 200));

        StackedBarRenderer renderer = (StackedBarRenderer) plot.getRenderer();
        renderer.setDefaultToolTipGenerator(new StandardCategoryToolTipGenerator());
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setDrawBarOutline(false);

        // police plus petite pour la legende vu qu'il y a 12 entrees
        jfreeChart.getLegend().setItemFont(new Font("SansSerif", Font.PLAIN, 9));

        Font fontAxe = new Font("SansSerif", Font.PLAIN, 10);
        plot.getDomainAxis().setTickLabelFont(fontAxe);
        plot.getRangeAxis().setTickLabelFont(fontAxe);
    }

    /**
     * Active/desactive le mode pourcentage.
     */
    public void setEnPourcentage(boolean enPourcentage) {
        this.enPourcentage = enPourcentage;
    }

    public boolean isEnPourcentage() {
        return enPourcentage;
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
