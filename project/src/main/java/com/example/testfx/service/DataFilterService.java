package com.example.testfx.service;

import com.example.testfx.data.DataRepository;
import com.example.testfx.dto.FilterRequest;
import com.example.testfx.model.AccidentTravail;

import java.util.List;
import java.util.stream.Collectors;

public class DataFilterService {

    private final DataRepository repository;

    public DataFilterService(DataRepository repository) {
        this.repository = repository;
    }

    public List<AccidentTravail> applyBaseFilters(FilterRequest request) {
        // TKA - 15/03/2026 - on check si repo est pret
        if (repository == null || !repository.estCharge()) {
            throw new IllegalStateException("repo pas init");
        }

        List<AccidentTravail> parAnnee = repository.parAnnee(request.getYear());

        List<String> listCtn = request.getSelectedCTNs();
        if (listCtn == null || listCtn.isEmpty()) {
            return parAnnee;
        }

        // TKA - 15/03 - filtre ctn
        return parAnnee.stream()
                .filter(a -> listCtn.contains(a.getCodeCTN()))
                
                .collect(Collectors.toList());
    }
}
