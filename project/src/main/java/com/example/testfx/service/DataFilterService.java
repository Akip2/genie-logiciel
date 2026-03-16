package com.example.testfx.service;

import java.util.List;
import java.util.stream.Collectors;

import com.example.testfx.data.DataRepository;
import com.example.testfx.dto.FilterRequest;
import com.example.testfx.model.AccidentTravail;

public class DataFilterService {

    private final DataRepository repository;

    public DataFilterService(DataRepository repository) {
        this.repository = repository;
    }

    public List<AccidentTravail> applyBaseFilters(FilterRequest request) {
        // TKA - 16/03/2026 - Vérification de la disponibilité du DataRepository
        // Indispensable avant tout filtrage pour éviter un NullPointerException
        if (repository == null || !repository.estCharge()) {
            throw new IllegalStateException("repo pas init");
        }

        // TKA - 16/03/2026 - Récupération des données pour l'année ciblée
        // On récupère le dataset brut correspondant au filtre F1
        List<AccidentTravail> parAnnee = repository.parAnnee(request.getYear());

        List<String> listCtn = request.getSelectedCTNs();
        // TKA - 16/03/2026 - Si aucun CTN n'est sélectionné, on retourne tout
        // Cela correspond à une vue globale sur tous les secteurs
        if (listCtn == null || listCtn.isEmpty()) {
            return parAnnee;
        }

        // TKA - 16/03/2026 - Filtrage final sur les secteurs CTN (filtre F2)
        // On ne garde que les lignes dont le codeCTN est dans la liste sélectionnée
        return parAnnee.stream()
                .filter(a -> listCtn.contains(a.getCodeCTN()))
                
                .collect(Collectors.toList());
    }
}
