package com.example.testfx.service;

import com.example.testfx.dto.FilterRequest;
import com.example.testfx.dto.SectorStat;
import com.example.testfx.model.AccidentTravail;

import java.util.List;

public interface IStatisticsService {

    /**
     * Calcule le total (ou le taux) d'un indicateur (F4) par secteur (CTN) selon les filtres (F1, F2).
     * Le résultat est trié par ordre décroissant de la valeur.
     * Cette version prend en charge les indicateurs complexes (Indice de fréquence, Taux de gravité).
     *
     * @param request La requête contenant les choix de filtres de l'utilisateur (F1, F2, F3, F4)
     * @param accidents La liste pré-filtrée par DataFilterService
     * @return Une liste de SectorStat prête pour l'affichage dans le graphique G1.
     */
    List<SectorStat> getStatsByCTN(FilterRequest request, List<AccidentTravail> accidents);

}
