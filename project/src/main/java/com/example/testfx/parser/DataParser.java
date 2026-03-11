package com.example.testfx.parser;

import com.example.testfx.model.AccidentTravail;

import java.io.IOException;
import java.util.List;

/**
 * Interface commune à tous les parsers de données.
 * Permet d'ajouter de nouvelles sources de données (CSV, Excel, API...)
 * sans modifier le code existant.
 * Chaque implémentation sait lire un format spécifique et produit
 * une liste homogène d'objets AccidentTravail.
 * Cette logique est pertinente dans le cadre de l'évolution des
 * formats présents sur le site de l'assurance maladie.
 */
public interface DataParser {

    /**
     * Parse un fichier et retourne la liste des accidents du travail.
     *
     * @param cheminFichier chemin vers le fichier à parser
     * @return liste des enregistrements parsés
     * @throws IOException en cas d'erreur de lecture
     */
    List<AccidentTravail> parse(String cheminFichier) throws IOException;

    /**
     * Indique si ce parser peut traiter le fichier donné.
     * Utilisé par la Factory pour choisir le bon parser.
     *
     * @param cheminFichier chemin vers le fichier
     * @return true si ce parser sait traiter ce fichier
     */
    boolean supporte(String cheminFichier);
}