package com.example.testfx;

import com.example.testfx.chart.BarChartSecteur;
import com.example.testfx.chart.ChartManager;
import com.example.testfx.data.DataRepository;
import com.example.testfx.dto.FilterRequest;
import com.example.testfx.onglets.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main extends Application {

    private DataRepository dataRepository;
    private ChartManager chartManager;
    private List<Onglet> onglets;
    private Onglet ongletActuel;
    private BorderPane root;
    private VBox contentContainer;
    
    // Contrôles de filtre
    private ComboBox<Integer> comboAnnee;
    private VBox secteurCheckboxes;
    private ComboBox<String> comboNAF;
    private ComboBox<String> comboIndicateur;

    @Override
    public void start(Stage stage) throws Exception {
        // Chargement des données
        dataRepository = new DataRepository();
        dataRepository.chargerDossier("data");

        chartManager = new ChartManager(dataRepository);

        // Initialisation des onglets
        onglets = Arrays.asList(
                new OngletAnalyse(chartManager),
                new OngletDonnees(),
                new OngletRapports()
        );
        ongletActuel = onglets.getFirst();

        // Initialiser les graphiques avec les filtres par défaut
        List<Integer> anneesDispo = dataRepository.getAnneesDisponibles();
        int anneeParDefaut = anneesDispo.getLast();
        FilterRequest requestParDefaut = new FilterRequest.Builder()
                .year(anneeParDefaut)
                .selectedCTNs(Arrays.asList("AA - Métallurgie", "EE - Chimie, caoutchouc, plasturgies", "GG - Commerces non alimentaires"))
                .nafLevel("CTN")
                .indicator("atPremierReglement")
                .build();
        chartManager.updateAll(requestParDefaut);

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

        comboAnnee = new ComboBox<>();
        comboAnnee.getStyleClass().add("filter-combo");
        List<Integer> anneesDispo = dataRepository.getAnneesDisponibles();
        comboAnnee.getItems().addAll(anneesDispo);
        comboAnnee.setValue(anneesDispo.getLast());
        comboAnnee.setPromptText("Choisir une année");

        annee.getChildren().add(comboAnnee);

        // SECTEUR CTN
        Label secteurSection = new Label("SECTEUR CTN");
        secteurSection.getStyleClass().add("filter-section-title");

        secteurCheckboxes = new VBox();
        secteurCheckboxes.setSpacing(2);
        secteurCheckboxes.setPadding(new Insets(0, 0, 5, 0));

        List<String> CTNDispo = dataRepository.getListeCTN();
        for (String ctn : CTNDispo) {
            CheckBox checkBox = new CheckBox(ctn);
            checkBox.getStyleClass().add("filter-checkbox");
            if (ctn.equals("AA - Métallurgie") || ctn.equals("EE - Chimie, caoutchouc, plasturgies") || ctn.equals("GG - Commerces non alimentaires")) {
                checkBox.setSelected(true);
            }
            secteurCheckboxes.getChildren().add(checkBox);
        }

        VBox secteurContainer = new VBox();
        secteurContainer.setSpacing(2);
        secteurContainer.getChildren().addAll(secteurCheckboxes);

        // NIVEAU DE GRANULARITÉ
        Label niveauSection = new Label("NIVEAU DE GRANULARITÉ");
        niveauSection.getStyleClass().add("filter-section-title");

        HBox niveauNAF = new HBox();
        niveauNAF.getStyleClass().add("filter-row");

        comboNAF = new ComboBox<>();
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

        comboIndicateur = new ComboBox<>();
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
        btnAppliquerFiltres.setOnAction(e -> appliquerFiltres());

        Button btnRinitialiserFiltres = new Button("Réinitialiser");
        btnRinitialiserFiltres.getStyleClass().add("button-reset");
        btnRinitialiserFiltres.setOnAction(e -> reinitialiserFiltres());

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

    /**
     * Applique les filtres et met à jour les graphiques
     */
    private void appliquerFiltres() {
        // Récupérer l'année sélectionnée
        Integer anneeSelectionnee = comboAnnee.getValue();
        
        // Récupérer les CTN sélectionnées
        List<String> ctnSelectionnees = secteurCheckboxes.getChildren().stream()
                .filter(node -> node instanceof CheckBox)
                .map(node -> (CheckBox) node)
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .collect(Collectors.toList());
        
        // Récupérer le niveau NAF
        String nafLevel = comboNAF.getValue();
        
        // Récupérer l'indicateur
        String indicator = comboIndicateur.getValue();
        
        // Créer la requête de filtre
        FilterRequest request = new FilterRequest.Builder()
                .year(anneeSelectionnee)
                .selectedCTNs(ctnSelectionnees)
                .nafLevel(nafLevel)
                .indicator(indicator)
                .build();
        
        // Mettre à jour tous les graphiques
        chartManager.updateAll(request);
    }

    /**
     * Réinitialise les filtres à leurs valeurs par défaut
     */
    private void reinitialiserFiltres() {
        // Réinitialiser l'année à la dernière année disponible
        List<Integer> anneesDispo = dataRepository.getAnneesDisponibles();
        comboAnnee.setValue(anneesDispo.getLast());
        
        // Réinitialiser les secteurs : sélectionner uniquement les 3 par défaut
        secteurCheckboxes.getChildren().stream()
                .filter(node -> node instanceof CheckBox)
                .map(node -> (CheckBox) node)
                .forEach(checkBox -> {
                    String text = checkBox.getText();
                    checkBox.setSelected(
                            text.equals("AA - Métallurgie") || 
                            text.equals("EE - Chimie, caoutchouc, plasturgies") || 
                            text.equals("GG - Commerces non alimentaires")
                    );
                });
        
        // Réinitialiser le niveau NAF à "CTN"
        comboNAF.setValue("CTN");
        
        // Réinitialiser l'indicateur au premier de la liste
        comboIndicateur.setValue(comboIndicateur.getItems().getFirst());
        
        // Appliquer les filtres
        appliquerFiltres();
    }

    public static void main(String[] args) {
        launch(args);
    }
}