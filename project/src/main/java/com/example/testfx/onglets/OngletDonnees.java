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
 * Onglet Données - Affiche les graphiques avancés
 */
public class OngletDonnees implements Onglet {

    private final ChartManager chartManager;

    public OngletDonnees(ChartManager chartManager) {
        this.chartManager = chartManager;
    }

    @Override
    public Pane getContenu() {
        Label titre = new Label("📈 Graphiques Avancés");
        titre.getStyleClass().add("onglet-titre");

        Node chartTopNaf     = chartManager.getBarChartTopNaf().getNode();
        Node chartRisque     = chartManager.getScatterChartRisque().getNode();
        Node chartComparaison = chartManager.getBarChartComparaison().getNode();
        Node chartTopCauses  = chartManager.getBarChartTopCauses().getNode();

        HBox row1 = creerLigne(chartTopNaf, chartRisque);
        HBox row2 = creerLigne(chartComparaison, chartTopCauses);

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
    public String getNom() { return "Données"; }

    @Override
    public String getId() { return "donnees"; }
}