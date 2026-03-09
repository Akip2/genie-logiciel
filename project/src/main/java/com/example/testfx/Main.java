package com.example.testfx;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class Main extends Application {

    // Palette de couleurs
    private static final String COLOR_PRIMARY    = "#2C3E50"; // bleu nuit
    private static final String COLOR_ACCENT     = "#3498DB"; // bleu vif
    private static final String COLOR_FOOTER_BG  = "#1A252F"; // bleu très sombre
    private static final String COLOR_TEXT_LIGHT = "#ECF0F1"; // blanc cassé
    private static final String COLOR_TEXT_DIM   = "#95A5A6"; // gris doux

    @Override
    public void start(Stage stage) {

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F0F4F8;");

        // ── HEADER ────────────────────────────────────────────────
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(18, 30, 18, 30));
        header.setStyle(
                "-fx-background-color: linear-gradient(to right, " + COLOR_PRIMARY + ", " + COLOR_ACCENT + ");"
        );

        // Ombre portée sous le header
        DropShadow headerShadow = new DropShadow();
        headerShadow.setColor(Color.rgb(0, 0, 0, 0.25));
        headerShadow.setOffsetY(3);
        headerShadow.setRadius(8);
        header.setEffect(headerShadow);

        Label titre = new Label("✦  Titre de l'app  ✦");
        titre.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        titre.setTextFill(Color.web(COLOR_TEXT_LIGHT));

        header.getChildren().add(titre);

        // ── FOOTER ────────────────────────────────────────────────
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(12, 30, 12, 30));
        footer.setStyle("-fx-background-color: " + COLOR_FOOTER_BG + ";");

        Label footerLabel = new Label("👥  Équipe : DUPOND · DUPOND · DUPOND · DUPOND · DUPOND");
        footerLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        footerLabel.setTextFill(Color.web(COLOR_TEXT_DIM));

        footer.getChildren().add(footerLabel);

        // ── ASSEMBLAGE ────────────────────────────────────────────
        root.setTop(header);
        root.setBottom(footer);

        Scene scene = new Scene(root, 520, 280);
        stage.setTitle("Test JavaFX");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}