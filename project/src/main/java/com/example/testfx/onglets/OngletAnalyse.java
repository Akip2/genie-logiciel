package com.example.testfx.onglets;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * Onglet Analyse - Affiche les graphiques et statistiques
 */
public class OngletAnalyse implements Onglet {

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

        Label placeholder = new Label("[Graphiques - BarChart, PieChart, ScatterChart...]");
        placeholder.setStyle("-fx-font-size: 14; -fx-text-fill: #95A5A6; -fx-padding: 40;");

        contenu.getChildren().addAll(titre, description, placeholder);
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

