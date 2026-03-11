package com.example.testfx;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class Main extends Application {

    private static final String COLOR_PRIMARY   = "#2C3E50";
    private static final String COLOR_ACCENT    = "#3498DB";
    private static final String COLOR_FOOTER_BG = "#1A252F";
    private static final String COLOR_TEXT_LIGHT = "#ECF0F1";
    private static final String COLOR_TEXT_DIM   = "#95A5A6";

    @Override
    public void start(Stage stage) throws Exception {

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F0F4F8;");

        // HEADER
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(18, 30, 18, 30));
        header.setStyle(
                "-fx-background-color: linear-gradient(to right, " + COLOR_PRIMARY + ", " + COLOR_ACCENT + ");"
        );

        DropShadow headerShadow = new DropShadow();
        headerShadow.setColor(Color.rgb(0, 0, 0, 0.25));
        headerShadow.setOffsetY(3);
        headerShadow.setRadius(8);
        header.setEffect(headerShadow);

        Label titre = new Label("Projet Genie Logiciel");
        titre.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        titre.setTextFill(Color.web(COLOR_TEXT_LIGHT));
        header.getChildren().add(titre);

        // GAUCHE
        // TODO

        // CENTRE
        // TODO


        // DROITE
        // TODO : menu d'option (choix annee, choix secteur, ...)
        VBox menu = new VBox();
        // trbl : top, right, bottom, left
        menu.setPadding(new Insets(10, 20, 10, 20));

        Label titreMenu = new Label("Menu de selection");

        ComboBox<String> comboAnnee = new ComboBox<>();
        comboAnnee.getItems().addAll("2018", "2019", "2020", "2021", "2022");
        comboAnnee.setPromptText("Choisir une année");

        ComboBox<String> comboSecteur = new ComboBox<>();
        comboSecteur.getItems().addAll("Secteur 1", "Secteur 2", "Secteur 3", "Secteur 4", "Secteur 5");
        comboSecteur.setPromptText("Choisir un secteur");

        menu.getChildren().addAll(titreMenu, comboAnnee, comboSecteur);

        // FOOTER
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(12, 30, 12, 30));
        footer.setStyle("-fx-background-color: " + COLOR_FOOTER_BG + ";");

        Label footerLabel = new Label("👥  Équipe : DUPOND · DUPOND · DUPOND · DUPOND · DUPOND");
        footerLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        footerLabel.setTextFill(Color.web(COLOR_TEXT_DIM));
        footer.getChildren().add(footerLabel);

        // ASSEMBLAGE
        root.setTop(header);
//        root.setLeft(// TODO);
//        root.setCenter(// TODO);
        root.setRight(menu);
        root.setBottom(footer);

        Scene scene = new Scene(root, 900, 750);
        stage.setTitle("Carte de France — Départements");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}