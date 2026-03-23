package com.example.testfx.onglets;

import javafx.scene.layout.Pane;

/**
 * Interface pour les onglets de l'application
 */
public interface Onglet {
    /**
     * Retourne le contenu de l'onglet
     */
    Pane getContenu();

    /**
     * Retourne le nom de l'onglet
     */
    String getNom();

    /**
     * Retourne l'ID unique de l'onglet
     */
    String getId();
}

