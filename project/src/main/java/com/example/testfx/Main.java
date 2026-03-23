package com.example.testfx;

import com.example.testfx.data.DataRepository;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;

public class Main extends Application {

    private static final String COLOR_PRIMARY    = "#2C3E50";
    private static final String COLOR_ACCENT     = "#3498DB";
    private static final String COLOR_FOOTER_BG  = "#1A252F";
    private static final String COLOR_TEXT_LIGHT = "#ECF0F1";
    private static final String COLOR_TEXT_DIM   = "#95A5A6";

    @Override
    public void start(Stage stage) throws Exception {

        // chargement des données
        DataRepository dataRepository = new DataRepository();
        dataRepository.chargerDossier("data");

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #F0F4F8;");

        // HEADER
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(10, 20, 10, 20));
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
        // TODO ?

        // CENTRE
        // TODO : grid (3 lignes / 2 colonnes) pour afficher les graphiques


        // DROITE
        VBox menu = new VBox();
        menu.setPadding(new Insets(10, 20, 10, 20));

        // titre du menu
        Label titreMenu = new Label("Filtres disponibles");
        titreMenu.setStyle("-fx-font-size: 24px;");

        // F1 : liste de choix de l'année
        HBox annee = new HBox();
        Label anneeLabel = new Label("Année : ");

        Region spacerF1 = new Region();
        HBox.setHgrow(spacerF1, Priority.ALWAYS);

        ComboBox<Integer> comboAnnee = new ComboBox<>();
        List<Integer> anneesDispo = dataRepository.getAnneesDisponibles();
        comboAnnee.getItems().addAll(anneesDispo);
        comboAnnee.setValue(anneesDispo.getLast());
        comboAnnee.setPromptText("Choisir une année");

        annee.getChildren().addAll(anneeLabel, spacerF1, comboAnnee);
        annee.setAlignment(Pos.CENTER);

        // F2 : liste de choix du secteur
        HBox secteur = new HBox();
        Label secteurLabel = new Label("Secteur : ");

        Region spacerF2 = new Region();
        HBox.setHgrow(spacerF2, Priority.ALWAYS);

        ComboBox<String> comboSecteur = new ComboBox<>();
        List<String> CTNDispo = dataRepository.getListeCTN();
        comboSecteur.getItems().addAll(CTNDispo);
        comboSecteur.setValue(comboSecteur.getItems().getFirst());
        comboSecteur.setPromptText("Choisir un secteur");

        secteur.getChildren().addAll(secteurLabel, spacerF2, comboSecteur);
        secteur.setAlignment(Pos.CENTER);

        // F3 : liste de choix du niveau NAF
        HBox niveauNAF = new HBox();
        Label niveauNAFLabel = new Label("Niveau NAF : ");

        Region spacerF3 = new Region();
        HBox.setHgrow(spacerF3, Priority.ALWAYS);

        ComboBox<String> comboNAF = new ComboBox<>();
        List<String> NAFDispo = Arrays.asList("CTN", "NAF38", "NAF2");
        comboNAF.getItems().addAll(NAFDispo);
        comboNAF.setValue(comboNAF.getItems().getFirst());
        comboNAF.setPromptText("Choisir un niveau NAF");

        niveauNAF.getChildren().addAll(niveauNAFLabel, spacerF3, comboNAF);
        niveauNAF.setAlignment(Pos.CENTER);

        // F4 : liste de choix de l'indicateur
        HBox indicateur = new HBox();
        Label indicateurLabel = new Label("Indicateur : ");

        Region spacerF4 = new Region();
        HBox.setHgrow(spacerF4, Priority.ALWAYS);

        ComboBox<String> comboIndicateur = new ComboBox<>();
        List<String> indicateurs = Arrays.asList("atPremierReglement", "nombreSalaries", "heuresTravaillees", "journeesIT", "deces", "nouvellesIP", "indiceFrequence", "tauxGravite");
        comboIndicateur.getItems().addAll(indicateurs);
        comboIndicateur.setValue(comboIndicateur.getItems().getFirst());
        comboIndicateur.setPromptText("Choisir un indicateur");

        indicateur.getChildren().addAll(indicateurLabel, spacerF4, comboIndicateur);
        indicateur.setAlignment(Pos.CENTER);

        // séparateur
        Region separatorBas = new Region();
        VBox.setVgrow(separatorBas, Priority.ALWAYS);

        // Bouton appliquer les filtres
        Button btnAppliquerFiltres = new Button("Appliquer les filtres");

        // Bouton réinitialiser les filtres
         Button btnRinitialiserFiltres = new Button("Rinitialiser les filtres");

        // Assemblage des éléments du menu : titre + filtres disponibles
        menu.getChildren().addAll(titreMenu, annee, secteur, niveauNAF, indicateur, separatorBas, btnAppliquerFiltres, btnRinitialiserFiltres);
        menu.setSpacing(10);

        // FOOTER
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(12, 30, 12, 30));
        footer.setStyle("-fx-background-color: " + COLOR_FOOTER_BG + ";");

        Label footerLabel = new Label("Équipe : EL AOUDI · FONTANEZ · FUMERON–LECOMTE · KACHLER · MANGIN · PIERROT · SAHRAOUI DOUKKALI");
        footerLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        footerLabel.setTextFill(Color.web(COLOR_TEXT_DIM));
        footer.getChildren().add(footerLabel);

        // ASSEMBLAGE
        root.setTop(header);
//        root.setLeft(// TODO ?);
//        root.setCenter(// TODO);
        root.setRight(menu);
        root.setBottom(footer);

        Scene scene = new Scene(root, 900, 750);
        stage.setTitle("Projet Genie Logiciel");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}