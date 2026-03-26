package com.example.testfx.onglets;

import com.example.testfx.chart.BarChartSecteur;
import com.example.testfx.chart.ChartManager;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
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
        VBox contenu = new VBox();
        contenu.setPadding(new Insets(20));
        contenu.setSpacing(15);
        contenu.setStyle("-fx-background-color: #F0F4F8;");

        Label titre = new Label("📊 Onglet Analyse");
        titre.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");

        Label description = new Label("Contenu des graphiques et statistiques sera affiché ici");
        description.setStyle("-fx-font-size: 12; -fx-text-fill: #7F8C8D;");


        BarChartSecteur test =  chartManager.getBarChartSecteur();
        var chartNode = test.getNode();
        
        // Définir les tailles préférées et permettre la croissance
        chartNode.setPrefHeight(400);
        VBox.setVgrow(chartNode, Priority.ALWAYS);

        contenu.getChildren().addAll(titre, description, chartNode);
        return contenu;
    }

    @Override
    public String getNom() {
        return "Analyse";
    }

    @Override
    public String getId() {
        return "analyse";
    }
}

