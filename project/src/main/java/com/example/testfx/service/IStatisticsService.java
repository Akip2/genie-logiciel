package com.example.testfx.service;

import java.util.List;
import java.util.Map;

import com.example.testfx.dto.FilterRequest;
import com.example.testfx.dto.SectorCauses;
import com.example.testfx.dto.SectorRisk;
import com.example.testfx.dto.SectorShare;
import com.example.testfx.dto.SectorStat;
import com.example.testfx.dto.SubSectorStat;
import com.example.testfx.dto.YearValue;
import com.example.testfx.model.AccidentTravail;

public interface IStatisticsService {

    // TKA - 16/03/2026 - Calcule le total/taux d'un indicateur (F4) par secteur
    // Résultat trié par ordre décroissant pour alimenter le graphique principal G1
    List<SectorStat> getStatsByCTN(FilterRequest request, List<AccidentTravail> accidents);

    // TKA - 16/03/2026 - Évolution d'un indicateur sur les années disponibles
    // Retourne une Map (Clé : CTN) contenant l'historique chronologique
    Map<String, List<YearValue>> getEvolution(List<String> ctns, String indicator);

    // TKA - 16/03/2026 - Part de chaque secteur (CTN) par rapport au total
    // Utilisé pour générer un graphique en camembert sur une année donnée
    List<SectorShare> getShareByCTN(int year);

    // TKA - 16/03/2026 - Répartition détaillée des accidents par type de cause
    // Agrége les données de la Map EnumMap interne de tous les secteurs cochés
    List<SectorCauses> getCausesByCTN(int year, List<String> ctns);

    // TKA - 16/03/2026 - Top N des sous-secteurs pour un CTN et une année précis
    // Le niveau NAF (2 ou 38) est géré dynamiquement pour zoomer dans un secteur
    List<SubSectorStat> getTopNAF(int year, String ctn, String nafLevel, String indicator, int limit);

    // TKA - 16/03/2026 - Établit un profil de risque croisant Féquence et Gravité
    // Utilisé pour un nuage de points de tous les secteurs sur une année
    List<SectorRisk> getRiskProfile(int year);
}
