package com.example.testfx.service;

import com.example.testfx.data.DataRepository;
import com.example.testfx.dto.FilterRequest;
import com.example.testfx.model.AccidentTravail;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service dédié au pré-filtrage des données brutes en fonction des choix de l'utilisateur (F1, F2).
 * Ce service agit comme un "entonnoir" avant que l'agrégation métier (ex: pour G1) ne soit calculée.
 */
public class DataFilterService {

    private final DataRepository repository;

    public DataFilterService(DataRepository repository) {
        this.repository = repository;
    }

    /**
     * Applique les filtres de base F1 (Année) et F2 (Secteurs CTN)
     * pour retourner la liste de travail pour tous les graphiques.
     *
     * @param request L'objet de requête encapsulant les choix utilisateur
     * @return La liste filtrée d'AccidentTravail
     */
    public List<AccidentTravail> applyBaseFilters(FilterRequest request) {
        if (repository == null || !repository.estCharge()) {
            throw new IllegalStateException("Le DataRepository n'est pas initialisé ou chargé.");
        }

        // F1: Filtrage par année (utilisation de la méthode optimisée du repository)
        List<AccidentTravail> filteredByYear = repository.parAnnee(request.getYear());

        // F2: Filtrage par secteurs CTN sélectionnés
        List<String> selectedCTNs = request.getSelectedCTNs();
        if (selectedCTNs == null || selectedCTNs.isEmpty()) {
            // Si pas de CTN spécifique coché, on renvoie toutes les données de l'année (Tout sélectionné)
            return filteredByYear;
        }

        // Sinon on filtre pour ne garder que ceux cochés
        return filteredByYear.stream()
                .filter(a -> selectedCTNs.contains(a.getCodeCTN()))
                .collect(Collectors.toList());
    }
}
