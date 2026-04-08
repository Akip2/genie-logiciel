package com.example.testfx;

import com.example.testfx.chart.ChartManager;
import com.example.testfx.data.DataRepository;
import com.example.testfx.dto.FilterRequest;
import com.example.testfx.onglets.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main extends Application {

    // Valeurs par défaut des filtres
    private static final String       NAF_PAR_DEFAUT        = "CTN";
    private static final String       INDICATEUR_PAR_DEFAUT = "atPremierReglement";
    private static final List<String> NIVEAUX_NAF           = Arrays.asList("CTN", "NAF38", "NAF2");
    private static final List<String> INDICATEURS           = Arrays.asList(
            "atPremierReglement", "nombreSalaries", "heuresTravaillees",
            "journeesIT", "deces", "nouvellesIP", "indiceFrequence", "tauxGravite"
    );

    private DataRepository dataRepository;
    private ChartManager   chartManager;
    private List<Onglet>   onglets;
    private Onglet         ongletActuel;

    // Composants utilisés plusieurs fois
    private VBox              contentContainer;
    private HBox              navigationBox;
    private ComboBox<Integer> comboAnnee;
    private VBox              secteurCheckboxes;
    private ComboBox<String>  comboNAF;
    private ComboBox<String>  comboIndicateur;

    @Override
    public void start(Stage stage) throws Exception {
        dataRepository = new DataRepository();
        dataRepository.chargerDossier("data");

        chartManager = new ChartManager(dataRepository);

        onglets = Arrays.asList(
                new OngletAnalyse(chartManager),
                new OngletDonnees(chartManager),
                new OngletRapports()
        );
        ongletActuel = onglets.getFirst();

        chartManager.updateAll(construireFilterRequest());

        BorderPane root = new BorderPane();
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

    // -------------------------------------------------------------------------
    // Construction de l'interface
    // -------------------------------------------------------------------------

    private HBox construireHeader() {
        Label titre = new Label("Projet Génie Logiciel");
        titre.getStyleClass().add("header-title");

        Label sousTitre = new Label("OUTIL D'AIDE À LA DÉCISION — RISQUES PROFESSIONNELS");
        sousTitre.getStyleClass().add("header-subtitle");

        VBox titreBox = new VBox(2, titre, sousTitre);
        titreBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(titreBox, Priority.ALWAYS);

        navigationBox = new HBox(6);
        navigationBox.setAlignment(Pos.CENTER_RIGHT);
        for (Onglet onglet : onglets) {
            navigationBox.getChildren().add(creerBoutonOnglet(onglet));
        }

        HBox header = new HBox(20, titreBox, navigationBox);
        header.getStyleClass().add("header");
        header.setAlignment(Pos.CENTER_LEFT);
        return header;
    }

    private Button creerBoutonOnglet(Onglet onglet) {
        Button btn = new Button(onglet.getNom());
        btn.getStyleClass().add(onglet == ongletActuel ? "button-onglet-actif" : "button-onglet");
        btn.setOnAction(e -> changerOnglet(onglet));
        return btn;
    }

    private VBox construireMenu() {
        // Année
        comboAnnee = creerComboFiltre();
        comboAnnee.getItems().addAll(dataRepository.getAnneesDisponibles());
        comboAnnee.setValue(dataRepository.getAnneesDisponibles().getLast());
        comboAnnee.setPromptText("Choisir une année");

        // Secteurs CTN (3 premiers cochés par défaut)
        secteurCheckboxes = new VBox(2);
        secteurCheckboxes.setPadding(new Insets(0, 0, 5, 0));
        List<String> ctnDispo = dataRepository.getListeCTN();
        for (int i = 0; i < ctnDispo.size(); i++) {
            CheckBox cb = new CheckBox(ctnDispo.get(i));
            cb.getStyleClass().add("filter-checkbox");
            cb.setSelected(i < 3);
            secteurCheckboxes.getChildren().add(cb);
        }

        // Niveau NAF
        comboNAF = creerComboFiltre();
        comboNAF.getItems().addAll(NIVEAUX_NAF);
        comboNAF.setValue(NAF_PAR_DEFAUT);
        comboNAF.setPromptText("Choisir un niveau NAF");

        // Indicateur
        comboIndicateur = creerComboFiltre();
        comboIndicateur.getItems().addAll(INDICATEURS);
        comboIndicateur.setValue(INDICATEUR_PAR_DEFAUT);
        comboIndicateur.setPromptText("Choisir un indicateur");

        // Boutons d'action
        Button btnAppliquer = new Button("Mettre à jour l'analyse");
        btnAppliquer.getStyleClass().add("button-apply");
        btnAppliquer.setOnAction(e -> appliquerFiltres());

        Button btnReinitialiser = new Button("Réinitialiser");
        btnReinitialiser.getStyleClass().add("button-reset");
        btnReinitialiser.setOnAction(e -> reinitialiserFiltres());

        // Espaceur pour pousser les boutons en bas
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox menu = new VBox(
                creerLabel("Paramètres d'analyse", "menu-title"),
                creerLabel("Ajustez les critères pour explorer les données selon vos besoins", "menu-subtitle"),
                new Separator(),
                creerLabel("ANNÉE DE RÉFÉRENCE", "filter-section-title"),
                comboAnnee,
                creerLabel("SECTEUR CTN", "filter-section-title"),
                secteurCheckboxes,
                creerLabel("NIVEAU DE GRANULARITÉ", "filter-section-title"),
                comboNAF,
                creerLabel("INDICATEUR MESURÉ", "filter-section-title"),
                comboIndicateur,
                spacer,
                btnAppliquer,
                btnReinitialiser
        );
        menu.getStyleClass().add("menu");
        menu.setPrefWidth(250);
        return menu;
    }

    private VBox construireContenu() {
        contentContainer = new VBox();
        contentContainer.getStyleClass().add("onglet-fond");
        contentContainer.getChildren().add(ongletActuel.getContenu());
        return contentContainer;
    }

    private HBox construireFooter() {
        Label labelEquipe = new Label("Équipe : EL AOUDI · FONTANEZ · FUMERON–LECOMTE · KACHLER · MANGIN · PIERROT · SAHRAOUI DOUKKALI");
        labelEquipe.getStyleClass().add("footer-label");
        HBox.setHgrow(labelEquipe, Priority.ALWAYS);

        Label labelProjet = new Label("PROJET UNIVERSITAIRE");
        labelProjet.getStyleClass().add("footer-label-right");

        HBox footer = new HBox(labelEquipe, labelProjet);
        footer.getStyleClass().add("footer");
        footer.setAlignment(Pos.CENTER_LEFT);
        return footer;
    }

    // -------------------------------------------------------------------------
    // Helpers de construction
    // -------------------------------------------------------------------------

    /** Crée un ComboBox stylisé prêt à l'emploi. */
    private <T> ComboBox<T> creerComboFiltre() {
        ComboBox<T> combo = new ComboBox<>();
        combo.getStyleClass().add("filter-combo");
        return combo;
    }

    /** Crée un Label avec la classe CSS donnée. */
    private Label creerLabel(String texte, String styleClass) {
        Label label = new Label(texte);
        label.getStyleClass().add(styleClass);
        return label;
    }

    // -------------------------------------------------------------------------
    // Logique des onglets
    // -------------------------------------------------------------------------

    private void changerOnglet(Onglet onglet) {
        ongletActuel = onglet;

        contentContainer.getChildren().clear();
        contentContainer.getChildren().add(ongletActuel.getContenu());

        for (int i = 0; i < onglets.size(); i++) {
            Button btn = (Button) navigationBox.getChildren().get(i);
            btn.getStyleClass().removeAll("button-onglet", "button-onglet-actif");
            btn.getStyleClass().add(onglets.get(i) == ongletActuel ? "button-onglet-actif" : "button-onglet");
        }
    }

    // -------------------------------------------------------------------------
    // Logique des filtres
    // -------------------------------------------------------------------------

    /** Construit un FilterRequest à partir de l'état actuel des contrôles. */
    private FilterRequest construireFilterRequest() {
        int annee = comboAnnee == null
                ? dataRepository.getAnneesDisponibles().getLast()
                : comboAnnee.getValue();

        String naf        = comboNAF        == null ? NAF_PAR_DEFAUT        : comboNAF.getValue();
        String indicateur = comboIndicateur == null ? INDICATEUR_PAR_DEFAUT : comboIndicateur.getValue();

        List<String> ctnSelectionnees;
        if (secteurCheckboxes == null) {
            List<String> toutLesCTN = dataRepository.getListeCTN();
            ctnSelectionnees = toutLesCTN.subList(0, Math.min(3, toutLesCTN.size()));
        } else {
            ctnSelectionnees = secteurCheckboxes.getChildren().stream()
                    .filter(n -> n instanceof CheckBox)
                    .map(n -> (CheckBox) n)
                    .filter(CheckBox::isSelected)
                    .map(CheckBox::getText)
                    .collect(Collectors.toList());
        }

        return new FilterRequest.Builder()
                .year(annee)
                .selectedCTNs(ctnSelectionnees)
                .nafLevel(naf)
                .indicator(indicateur)
                .build();
    }

    private void appliquerFiltres() {
        chartManager.updateAll(construireFilterRequest());
    }

    private void reinitialiserFiltres() {
        comboAnnee.setValue(dataRepository.getAnneesDisponibles().getLast());
        comboNAF.setValue(NAF_PAR_DEFAUT);
        comboIndicateur.setValue(INDICATEUR_PAR_DEFAUT);

        List<javafx.scene.Node> nodes = secteurCheckboxes.getChildren();
        for (int i = 0; i < nodes.size(); i++) {
            ((CheckBox) nodes.get(i)).setSelected(i < 3);
        }

        appliquerFiltres();
    }

    public static void main(String[] args) {
        launch(args);
    }
}