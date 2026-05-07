package com.example.testfx.chart;

import com.example.testfx.dto.FilterRequest;
import com.example.testfx.dto.SectorStat;
import com.example.testfx.model.AccidentTravail;
import com.example.testfx.service.DataFilterService;
import com.example.testfx.service.IStatisticsService;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.Color;
import java.awt.Font;
import java.util.List;

/**
 * G1 - Bar chart horizontal : classement des secteurs CTN.
 * Le plus accidentogene en haut, le moins en bas.
 */
public class BarChartSecteur {

    /** Limite du nombre de catégories affichées (sinon NAF38 a 38 lignes illisibles). */
    private static final int LIMITE_AFFICHAGE = 10;

    private final JFreeChart jfreeChart;
    private final ChartViewer chartViewer;
    private final DefaultCategoryDataset dataset;

    private final IStatisticsService statsService;
    private final DataFilterService filterService;

    public BarChartSecteur(IStatisticsService statsService, DataFilterService filterService) {
        this.statsService = statsService;
        this.filterService = filterService;

        this.dataset = new DefaultCategoryDataset();

        this.jfreeChart = ChartFactory.createBarChart(
                "Accidents du travail par secteur",
                "Secteur",
                "Nombre d'AT",
                dataset,
                PlotOrientation.HORIZONTAL,
                false,  // pas de legende
                true,   // tooltips
                false   // urls (on s'en sert pas)
        );

        styliserChart();
        this.chartViewer = new ChartViewer(jfreeChart);
    }

    /**
     * Met a jour le graphique avec les filtres.
     */
    public void update(FilterRequest request) {
        dataset.clear();

            List<AccidentTravail> donnees = filterService.applyBaseFilters(request);
            List<SectorStat> stats = statsService.getStatsByCTN(request, donnees);

            // top 10 max — au-delà, l'affichage devient illisible
            int taille = Math.min(stats.size(), LIMITE_AFFICHAGE);
            List<SectorStat> top = stats.subList(0, taille);

            // on ajoute du bas vers le haut (le plus grand en haut)
            for (int i = top.size() - 1; i >= 0; i--) {
                SectorStat s = top.get(i);
                String libelle = tronquer(s.getLabel(), 30);
                dataset.addValue(s.getValue(), "AT", libelle);
            }

            // titre dynamique : précise quand on filtre au top 10
            String suffixeTop = (stats.size() > LIMITE_AFFICHAGE)
                    ? " — Top " + LIMITE_AFFICHAGE
                    : "";
            jfreeChart.setTitle("Classement des secteurs (" + request.getYear() + ")" + suffixeTop);

            CategoryPlot plot = jfreeChart.getCategoryPlot();
            plot.getRangeAxis().setLabel(getLabelIndicateur(request.getIndicator()));

            // une seule couleur (bleu) pour toute la série, fini le code dupliqué
            BarRenderer renderer = (BarRenderer) plot.getRenderer();
            renderer.setSeriesPaint(0, new Color(52, 152, 219));
    }

    private void styliserChart() {
        jfreeChart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = jfreeChart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(245, 245, 245));
        plot.setRangeGridlinePaint(new Color(220, 220, 220));

        BarRenderer renderer = (BarRenderer) plot.getRenderer();

        // tooltip avec format français des nombres
        renderer.setDefaultToolTipGenerator(new StandardCategoryToolTipGenerator(
                StandardCategoryToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT_STRING,
                ChartUtils.getFormatFr()
        ));
        renderer.setSeriesPaint(0, new Color(52, 152, 219));
        renderer.setBarPainter(new org.jfree.chart.renderer.category.StandardBarPainter());

        // format français + axe forcé à 0
        ChartUtils.formaterAxeNumerique((NumberAxis) plot.getRangeAxis());

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
            case "nouvellesIP" -> "Nouvelles IP";
            default -> "Valeur";
        };
    }

    private String tronquer(String texte, int max) {
        if (texte == null) return "";
        if (texte.length() <= max) return texte;
        return texte.substring(0, max - 3) + "...";
    }

    /** Retourne le composant JavaFX a ajouter dans le layout */
    public ChartViewer getNode() {
        return chartViewer;
    }
}
