package com.example.testfx;

import com.example.testfx.data.DataRepository;
import com.example.testfx.onglets.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;

public class Main extends Application {

    private DataRepository dataRepository;
    private List<Onglet> onglets;
    private Onglet ongletActuel;
    private BorderPane root;
    private VBox contentContainer;

    @Override
    public void start(Stage stage) throws Exception {
        // Chargement des données
        dataRepository = new DataRepository();
        dataRepository.chargerDossier("data");

        // Initialisation des onglets
        onglets = Arrays.asList(
                new OngletAnalyse(),
                new OngletDonnees(),
                new OngletRapports()
        );
        ongletActuel = onglets.get(0);

        // Construction de l'interface
        root = new BorderPane();
        root.getStyleClass().add("root");

        root.setTop(construireHeader());
        root.setLeft(construireMenu());
        root.setCenter(construireContenu());
        root.setBottom(construireFooter());

        Scene scene = new Scene(root, 1200, 750);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setTitle("Projet Genie Logiciel");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Construit le header avec les boutons d'onglets
     */
    private HBox construireHeader() {
        HBox header = new HBox();
        header.getStyleClass().add("header");
        header.setSpacing(15);
        header.setPadding(new Insets(10, 20, 10, 20));
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label titre = new Label("Projet Genie Logiciel");
        titre.getStyleClass().add("header-title");
        HBox.setHgrow(titre, Priority.ALWAYS);

        // Boutons d'onglets
        HBox navigationBox = new HBox();
        navigationBox.setSpacing(10);

        for (Onglet onglet : onglets) {
            Button btn = new Button(onglet.getNom());
            btn.setStyle("-fx-padding: 8 16 8 16; -fx-font-size: 12; -fx-cursor: hand;");
            
            if (onglet == ongletActuel) {
                btn.setStyle("-fx-padding: 8 16 8 16; -fx-font-size: 12; -fx-cursor: hand; -fx-background-color: #FFFFFF; -fx-text-fill: #3498DB; -fx-font-weight: bold;");
            }

            btn.setOnAction(e -> changerOnglet(onglet));
            navigationBox.getChildren().add(btn);
        }

        header.getChildren().addAll(titre, navigationBox);
        return header;
    }

    /**
     * Construit le menu de filtres à gauche
     */
    private VBox construireMenu() {
        VBox menu = new VBox();
        menu.getStyleClass().add("menu");
        menu.setSpacing(0);
        menu.setPrefWidth(250);

        // Titre du menu
        Label titreMenu = new Label("Paramètres d'analyse");
        titreMenu.getStyleClass().add("menu-title");

        // Description
        Label descriptionMenu = new Label("Ajustez les critères pour explorer les données selon vos besoins");
        descriptionMenu.getStyleClass().add("menu-subtitle");

        // Séparateur
        Separator sep1 = new Separator();

        // ANNÉE DE RÉFÉRENCE
        Label anneeSection = new Label("ANNÉE DE RÉFÉRENCE");
        anneeSection.getStyleClass().add("filter-section-title");

        HBox annee = new HBox();
        annee.getStyleClass().add("filter-row");

        ComboBox<Integer> comboAnnee = new ComboBox<>();
        comboAnnee.getStyleClass().add("filter-combo");
        List<Integer> anneesDispo = dataRepository.getAnneesDisponibles();
        comboAnnee.getItems().addAll(anneesDispo);
        comboAnnee.setValue(anneesDispo.getLast());
        comboAnnee.setPromptText("Choisir une année");

        annee.getChildren().add(comboAnnee);

        // SECTEUR CTN
        Label secteurSection = new Label("SECTEUR CTN");
        secteurSection.getStyleClass().add("filter-section-title");

        VBox secteurCheckboxes = new VBox();
        secteurCheckboxes.setSpacing(2);
        secteurCheckboxes.setPadding(new Insets(0, 0, 5, 0));

        List<String> CTNDispo = dataRepository.getListeCTN();
        int countSelected = 0;
        for (String ctn : CTNDispo) {
            CheckBox checkBox = new CheckBox(ctn);
            checkBox.getStyleClass().add("filter-checkbox");
            if (ctn.equals("AA - Métallurgie") || ctn.equals("EE - Chimie, caoutchouc, plasturgies") || ctn.equals("GG - Commerces non alimentaires")) {
                checkBox.setSelected(true);
                countSelected++;
            }
            secteurCheckboxes.getChildren().add(checkBox);
        }

        Label secteurCount = new Label(countSelected + " sélec.");
        secteurCount.setStyle("-fx-font-size: 10; -fx-text-fill: #3498DB; -fx-padding: 0 0 3 0;");

        VBox secteurContainer = new VBox();
        secteurContainer.setSpacing(2);
        secteurContainer.getChildren().addAll(secteurCount, secteurCheckboxes);

        // NIVEAU DE GRANULARITÉ
        Label niveauSection = new Label("NIVEAU DE GRANULARITÉ");
        niveauSection.getStyleClass().add("filter-section-title");

        HBox niveauNAF = new HBox();
        niveauNAF.getStyleClass().add("filter-row");

        ComboBox<String> comboNAF = new ComboBox<>();
        comboNAF.getStyleClass().add("filter-combo");
        List<String> NAFDispo = Arrays.asList("CTN", "NAF38", "NAF2");
        comboNAF.getItems().addAll(NAFDispo);
        comboNAF.setValue(comboNAF.getItems().getFirst());
        comboNAF.setPromptText("Choisir un niveau NAF");

        niveauNAF.getChildren().add(comboNAF);

        // INDICATEUR MESURÉ
        Label indicateurSection = new Label("INDICATEUR MESURÉ");
        indicateurSection.getStyleClass().add("filter-section-title");

        HBox indicateur = new HBox();
        indicateur.getStyleClass().add("filter-row");

        ComboBox<String> comboIndicateur = new ComboBox<>();
        comboIndicateur.getStyleClass().add("filter-combo");
        List<String> indicateurs = Arrays.asList("atPremierReglement", "nombreSalaries", "heuresTravaillees", "journeesIT", "deces", "nouvellesIP", "indiceFrequence", "tauxGravite");
        comboIndicateur.getItems().addAll(indicateurs);
        comboIndicateur.setValue(comboIndicateur.getItems().getFirst());
        comboIndicateur.setPromptText("Choisir un indicateur");

        indicateur.getChildren().add(comboIndicateur);

        // Séparateur
        Region separatorBas = new Region();
        VBox.setVgrow(separatorBas, Priority.ALWAYS);

        // Boutons
        Button btnAppliquerFiltres = new Button("Mettre à jour l'analyse");
        btnAppliquerFiltres.getStyleClass().add("button-apply");

        Button btnRinitialiserFiltres = new Button("Réinitialiser");
        btnRinitialiserFiltres.getStyleClass().add("button-reset");

        // Assemblage du menu
        menu.getChildren().addAll(
                titreMenu,
                descriptionMenu,
                sep1,
                anneeSection,
                annee,
                secteurSection,
                secteurContainer,
                niveauSection,
                niveauNAF,
                indicateurSection,
                indicateur,
                separatorBas,
                btnAppliquerFiltres,
                btnRinitialiserFiltres
        );

        return menu;
    }

    /**
     * Construit le contenu central (adaptable selon l'onglet)
     */
    private Pane construireContenu() {
        contentContainer = new VBox();
        contentContainer.setStyle("-fx-background-color: #F0F4F8;");
        mettreAJourContenu();
        return contentContainer;
    }

    /**
     * Met à jour le contenu central selon l'onglet actuel
     */
    private void mettreAJourContenu() {
        contentContainer.getChildren().clear();
        contentContainer.getChildren().add(ongletActuel.getContenu());
    }

    /**
     * Change l'onglet actuel et met à jour l'affichage
     */
    private void changerOnglet(Onglet onglet) {
        ongletActuel = onglet;
        mettreAJourContenu();
        mettreAJourHeaderButtons();
    }

    /**
     * Met à jour le style des boutons d'onglets
     */
    private void mettreAJourHeaderButtons() {
        // Récupérer le HBox de navigation du header
        HBox header = (HBox) root.getTop();
        HBox navigationBox = (HBox) header.getChildren().get(1);
        
        // Réinitialiser tous les boutons
        for (int i = 0; i < navigationBox.getChildren().size(); i++) {
            Button btn = (Button) navigationBox.getChildren().get(i);
            if (onglets.get(i) == ongletActuel) {
                btn.setStyle("-fx-padding: 8 16 8 16; -fx-font-size: 12; -fx-cursor: hand; -fx-background-color: #FFFFFF; -fx-text-fill: #3498DB; -fx-font-weight: bold;");
            } else {
                btn.setStyle("-fx-padding: 8 16 8 16; -fx-font-size: 12; -fx-cursor: hand;");
            }
        }
    }

    /**
     * Construit le footer
     */
    private HBox construireFooter() {
        HBox footer = new HBox();
        footer.getStyleClass().add("footer");

        Label footerLabel = new Label("Équipe : EL AOUDI · FONTANEZ · FUMERON–LECOMTE · KACHLER · MANGIN · PIERROT · SAHRAOUI DOUKKALI");
        footerLabel.getStyleClass().add("footer-label");
        footer.getChildren().add(footerLabel);

        return footer;
    }

    public static void main(String[] args) {
        launch(args);
    }
}