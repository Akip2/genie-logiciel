package com.example.testfx;

import com.example.testfx.data.DataRepository;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // chargement des données
        DataRepository dataRepository = new DataRepository();
        dataRepository.chargerDossier("data");

        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");

        // HEADER
        HBox header = new HBox();
        header.getStyleClass().add("header");

        Label titre = new Label("Projet Genie Logiciel");
        titre.getStyleClass().add("header-title");
        header.getChildren().add(titre);
        // TODO ?

        // CENTRE
        // TODO : grid (3 lignes / 2 colonnes) pour afficher les graphiques


        // DROITE
        VBox menu = new VBox();
        menu.getStyleClass().add("menu");

        // titre du menu
        Label titreMenu = new Label("Filtres disponibles");
        titreMenu.getStyleClass().add("menu-title");

        // F1 : liste de choix de l'année
        HBox annee = new HBox();
        annee.getStyleClass().add("filter-row");
        Label anneeLabel = new Label("Année : ");
        anneeLabel.getStyleClass().add("filter-label");

        Region spacerF1 = new Region();
        HBox.setHgrow(spacerF1, Priority.ALWAYS);

        ComboBox<Integer> comboAnnee = new ComboBox<>();
        comboAnnee.getStyleClass().add("filter-combo");
        List<Integer> anneesDispo = dataRepository.getAnneesDisponibles();
        comboAnnee.getItems().addAll(anneesDispo);
        comboAnnee.setValue(anneesDispo.getLast());
        comboAnnee.setPromptText("Choisir une année");

        annee.getChildren().addAll(anneeLabel, spacerF1, comboAnnee);

        // F2 : liste de choix du secteur
        HBox secteur = new HBox();
        secteur.getStyleClass().add("filter-row");
        Label secteurLabel = new Label("Secteur : ");
        secteurLabel.getStyleClass().add("filter-label");

        Region spacerF2 = new Region();
        HBox.setHgrow(spacerF2, Priority.ALWAYS);

        ComboBox<String> comboSecteur = new ComboBox<>();
        comboSecteur.getStyleClass().add("filter-combo");
        List<String> CTNDispo = dataRepository.getListeCTN();
        comboSecteur.getItems().addAll(CTNDispo);
        comboSecteur.setValue(comboSecteur.getItems().getFirst());
        comboSecteur.setPromptText("Choisir un secteur");

        secteur.getChildren().addAll(secteurLabel, spacerF2, comboSecteur);

        // F3 : liste de choix du niveau NAF
        HBox niveauNAF = new HBox();
        niveauNAF.getStyleClass().add("filter-row");
        Label niveauNAFLabel = new Label("Niveau NAF : ");
        niveauNAFLabel.getStyleClass().add("filter-label");

        Region spacerF3 = new Region();
        HBox.setHgrow(spacerF3, Priority.ALWAYS);

        ComboBox<String> comboNAF = new ComboBox<>();
        comboNAF.getStyleClass().add("filter-combo");
        List<String> NAFDispo = Arrays.asList("CTN", "NAF38", "NAF2");
        comboNAF.getItems().addAll(NAFDispo);
        comboNAF.setValue(comboNAF.getItems().getFirst());
        comboNAF.setPromptText("Choisir un niveau NAF");

        niveauNAF.getChildren().addAll(niveauNAFLabel, spacerF3, comboNAF);

        // F4 : liste de choix de l'indicateur
        HBox indicateur = new HBox();
        indicateur.getStyleClass().add("filter-row");
        Label indicateurLabel = new Label("Indicateur : ");
        indicateurLabel.getStyleClass().add("filter-label");

        Region spacerF4 = new Region();
        HBox.setHgrow(spacerF4, Priority.ALWAYS);

        ComboBox<String> comboIndicateur = new ComboBox<>();
        comboIndicateur.getStyleClass().add("filter-combo");
        List<String> indicateurs = Arrays.asList("atPremierReglement", "nombreSalaries", "heuresTravaillees", "journeesIT", "deces", "nouvellesIP", "indiceFrequence", "tauxGravite");
        comboIndicateur.getItems().addAll(indicateurs);
        comboIndicateur.setValue(comboIndicateur.getItems().getFirst());
        comboIndicateur.setPromptText("Choisir un indicateur");

        indicateur.getChildren().addAll(indicateurLabel, spacerF4, comboIndicateur);

        // séparateur
        Region separatorBas = new Region();
        VBox.setVgrow(separatorBas, Priority.ALWAYS);

        // Bouton appliquer les filtres
        Button btnAppliquerFiltres = new Button("Appliquer les filtres");
        btnAppliquerFiltres.getStyleClass().add("button-filter");

        // Bouton réinitialiser les filtres
        Button btnRinitialiserFiltres = new Button("Rinitialiser les filtres");
        btnRinitialiserFiltres.getStyleClass().add("button-filter");

        // Assemblage des éléments du menu : titre + filtres disponibles
        menu.getChildren().addAll(titreMenu, annee, secteur, niveauNAF, indicateur, separatorBas, btnAppliquerFiltres, btnRinitialiserFiltres);

        // FOOTER
        HBox footer = new HBox();
        footer.getStyleClass().add("footer");

        Label footerLabel = new Label("Équipe : EL AOUDI · FONTANEZ · FUMERON–LECOMTE · KACHLER · MANGIN · PIERROT · SAHRAOUI DOUKKALI");
        footerLabel.getStyleClass().add("footer-label");
        footer.getChildren().add(footerLabel);

        // ASSEMBLAGE
        root.setTop(header);
//        root.setLeft(// TODO ?);
//        root.setCenter(// TODO);
        root.setRight(menu);
        root.setBottom(footer);

        Scene scene = new Scene(root, 900, 750);
        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
        stage.setTitle("Projet Genie Logiciel");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}