package com.example.testfx.onglets;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Onglet Rapports - Affiche les informations du projet
 */
public class OngletRapports implements Onglet {

    @Override
    public Region getContenu() {
        VBox contenu = new VBox(20);
        contenu.setPadding(new Insets(20));
        contenu.getStyleClass().add("onglet-fond");

        contenu.getChildren().addAll(
                creerEntete(),
                new Separator(),
                section("Contexte",
                        "Chaque année en France, plusieurs centaines de milliers d'accidents du travail sont recensés. " +
                                "Ces données, bien que disponibles en open data, restent difficiles à exploiter. " +
                                "Des outils adaptés sont nécessaires pour en extraire des informations pertinentes pour la prévention des risques professionnels."),
                section("Objectifs de l'application",
                        "L'application fournit un outil d'aide à la décision pour analyser les accidents du travail, " +
                                "identifier les secteurs à risque et faciliter la mise en place d'actions de prévention."),
                sectionListe("Fonctionnalités principales",
                        "Visualisation des accidents par secteur : classement et répartition des secteurs les plus touchés",
                        "Analyse de l'évolution temporelle : suivi des tendances sur plusieurs années (2020-2023)",
                        "Filtres dynamiques : exploration par année, secteur, niveau NAF et indicateurs",
                        "Analyse des causes d'accidents : identification des principaux facteurs de risque",
                        "Comparaison fréquence / gravité : positionnement des secteurs selon leur dangerosité",
                        "Analyse détaillée : exploration avancée par type de contrat, taille d'entreprise et ancienneté"),
                sectionListe("Public cible",
                        "Professionnels des ressources humaines : pour identifier les risques dans leur secteur",
                        "Acteurs de la prévention : pour cibler les actions prioritaires",
                        "Organismes de santé au travail : pour suivre les évolutions et tendances",
                        "Chercheurs et étudiants : pour analyser les données dans un cadre académique"),
                sectionDefinitions(),
                sectionSources(),
                sectionListe("Méthode de développement (modèle en V)",
                        "Définir précisément les besoins et spécifications fonctionnelles",
                        "Concevoir l'architecture technique de l'application",
                        "Développer les composants et fonctionnalités",
                        "Valider et tester chaque module avant intégration",
                        "Vérifier la conformité avec les besoins initiaux"),
                sectionUtilisation(),
                sectionEquipe()
        );

        ScrollPane scrollPane = new ScrollPane(contenu);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("onglet-fond");
        return scrollPane;
    }

    // -------------------------------------------------------------------------
    // Méthodes utilitaires de construction
    // -------------------------------------------------------------------------

    /** Crée le bloc titre + sous-titre de la page. */
    private VBox creerEntete() {
        Label titre = new Label("📋 Informations");
        titre.getStyleClass().add("rapport-titre");

        Label soustitre = new Label("Contexte, objectifs et fonctionnement de l'outil");
        soustitre.getStyleClass().add("rapport-soustitre");

        return new VBox(4, titre, soustitre);
    }

    /** Section simple : titre + paragraphe. */
    private VBox section(String titre, String texte) {
        return new VBox(6, titreSec(titre), paragraphe(texte));
    }

    /** Section avec titre + liste à puces. */
    private VBox sectionListe(String titre, String... points) {
        VBox box = new VBox(6);
        box.getChildren().add(titreSec(titre));
        for (String point : points) {
            Label item = paragraphe("• " + point);
            item.setPadding(new Insets(0, 0, 0, 15));
            box.getChildren().add(item);
        }
        return box;
    }

    /** Section Définitions importantes. */
    private VBox sectionDefinitions() {
        VBox box = new VBox(8);
        box.getChildren().add(titreSec("Définitions importantes"));
        box.getChildren().addAll(
                definition("Accident du travail",
                        "Accident survenu par le fait ou à l'occasion du travail à toute personne salariée."),
                definition("CTN (Comité Technique National)",
                        "Classification des activités professionnelles en 9 grands secteurs (AA à II) selon les risques."),
                definition("NAF (Nomenclature d'Activités Française)",
                        "Système de classification des activités économiques en France, utilisé par l'INSEE."),
                definition("Taux de fréquence",
                        "Nombre d'accidents pour 1 000 salariés."),
                definition("Taux de gravité",
                        "Journées d'arrêt pour 1 000 heures travaillées.")
        );
        return box;
    }

    /** Section Sources de données. */
    private VBox sectionSources() {
        return sectionListe("Sources de données",
                "DARES (Direction de l'Animation de la Recherche, des Études et des Statistiques) : données sur les accidents du travail",
                "INSEE (Institut National de la Statistique et des Études Économiques) : nomenclatures et statistiques économiques");
    }

    /** Section Utilisation avec définitions des trois pages. */
    private VBox sectionUtilisation() {
        VBox box = new VBox(8);
        box.getChildren().add(titreSec("Utilisation de l'application"));
        box.getChildren().addAll(
                definition("Tableau de bord",
                        "Affichage interactif de plusieurs graphiques : classement des secteurs, évolution temporelle, répartition et causes d'accidents."),
                definition("Analyse détaillée",
                        "Exploration approfondie avec filtres avancés : type de contrat, taille d'entreprise, ancienneté, type de lésion et facteurs de risque."),
                definition("Informations",
                        "Présentation du contexte, des objectifs et du fonctionnement de l'outil.")
        );
        return box;
    }

    /** Section Équipe de développement. */
    private VBox sectionEquipe() {
        Label membres = paragraphe(
                "EL AOUDI Rym · FONTANEZ Antoine · FUMERON–LECOMTE Baptiste · " +
                        "KACHLER Théo · MANGIN Raphaël · PIERROT Nathan · SAHRAOUI DOUKKALI Mouad");
        membres.setPadding(new Insets(5, 0, 0, 0));
        return new VBox(6,
                titreSec("Équipe de développement"),
                paragraphe("Ce projet a été réalisé par une équipe de 7 étudiants dans le cadre d'un cours de Génie Logiciel :"),
                membres);
    }

    // -------------------------------------------------------------------------
    // Petits composants réutilisables
    // -------------------------------------------------------------------------

    /** Titre de section. */
    private Label titreSec(String texte) {
        Label label = new Label(texte);
        label.getStyleClass().add("rapport-section-titre");
        return label;
    }

    /** Paragraphe de texte avec retour à la ligne automatique. */
    private Label paragraphe(String texte) {
        Label label = new Label(texte);
        label.getStyleClass().add("rapport-texte");
        label.setWrapText(true);
        return label;
    }

    /** Bloc terme en gras + description en gris. */
    private VBox definition(String terme, String description) {
        Label termLabel = new Label(terme);
        termLabel.getStyleClass().add("rapport-def-terme");

        Label descLabel = new Label(description);
        descLabel.getStyleClass().add("rapport-def-desc");
        descLabel.setWrapText(true);

        VBox box = new VBox(2, termLabel, descLabel);
        box.setPadding(new Insets(0, 0, 0, 15));
        return box;
    }

    @Override
    public String getNom() { return "Rapports"; }

    @Override
    public String getId() { return "rapports"; }
}