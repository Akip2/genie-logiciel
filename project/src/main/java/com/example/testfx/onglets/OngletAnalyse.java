package com.example.testfx.onglets;

import com.example.testfx.chart.ChartManager;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Onglet Analyse - Affiche les graphiques et statistiques
 */
public class OngletAnalyse implements Onglet {

    private final ChartManager chartManager;

    public OngletAnalyse(ChartManager chartManager) {
        this.chartManager = chartManager;
    }

    @Override
    public Pane getContenu() {
        Label titre = new Label("📊 Analyse des accidents du travail");
        titre.getStyleClass().add("onglet-titre");

        Node chartSecteur    = chartManager.getBarChartSecteur().getNode();
        Node chartEvolution  = chartManager.getLineChartEvolution().getNode();
        Node chartRepartition = chartManager.getPieChartRepartition().getNode();
        Node chartCauses     = chartManager.getStackedBarChartCauses().getNode();

        HBox row1 = creerLigne(chartSecteur, chartEvolution);
        HBox row2 = creerLigne(chartRepartition, chartCauses);

        VBox contenu = new VBox(15, titre, new Separator(), row1, row2);
        contenu.setPadding(new Insets(20));
        contenu.getStyleClass().add("onglet-fond");
        VBox.setVgrow(row1, Priority.ALWAYS);
        VBox.setVgrow(row2, Priority.ALWAYS);

        return contenu;
    }

    /**
     * Crée une ligne avec deux graphiques côte à côte.
     */
    private HBox creerLigne(Node gauche, Node droite) {
        gauche.getStyleClass().add("chart-node");
        droite.getStyleClass().add("chart-node");
        HBox.setHgrow(gauche, Priority.ALWAYS);
        HBox.setHgrow(droite, Priority.ALWAYS);

        HBox ligne = new HBox(15, gauche, droite);
        VBox.setVgrow(ligne, Priority.ALWAYS);
        return ligne;
    }

    @Override
    public String getNom() { return "Analyse"; }

    @Override
    public String getId() { return "analyse"; }
}