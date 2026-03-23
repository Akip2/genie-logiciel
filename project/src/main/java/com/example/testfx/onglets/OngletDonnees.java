package com.example.testfx.onglets;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * Onglet Données - Affiche les données brutes
 */
public class OngletDonnees implements Onglet {

    @Override
    public Pane getContenu() {
        VBox contenu = new VBox();
        contenu.setPadding(new Insets(20));
        contenu.setSpacing(15);
        contenu.setStyle("-fx-background-color: #F0F4F8;");

        Label titre = new Label("📋 Onglet Données");
        titre.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");

        Label description = new Label("Liste des données brutes et tableau");
        description.setStyle("-fx-font-size: 12; -fx-text-fill: #7F8C8D;");

        Label placeholder = new Label("[TableView avec les accidents du travail...]");
        placeholder.setStyle("-fx-font-size: 14; -fx-text-fill: #95A5A6; -fx-padding: 40;");

        contenu.getChildren().addAll(titre, description, placeholder);
        return contenu;
    }

    @Override
    public String getNom() {
        return "Données";
    }

    @Override
    public String getId() {
        return "donnees";
    }
}

