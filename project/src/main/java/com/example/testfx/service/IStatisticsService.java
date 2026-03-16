package com.example.testfx.service;

import com.example.testfx.dto.FilterRequest;
import com.example.testfx.dto.SectorStat;
import com.example.testfx.model.AccidentTravail;

import com.example.testfx.dto.SectorCauses;
import com.example.testfx.dto.SectorRisk;
import com.example.testfx.dto.SectorShare;
import com.example.testfx.dto.SubSectorStat;
import com.example.testfx.dto.YearValue;

import java.util.List;
import java.util.Map;

public interface IStatisticsService {

    List<SectorStat> getStatsByCTN(FilterRequest request, List<AccidentTravail> accidents);

    Map<String, List<YearValue>> getEvolution(List<String> ctns, String indicator);

    List<SectorShare> getShareByCTN(int year);

    List<SectorCauses> getCausesByCTN(int year, List<String> ctns);

    List<SubSectorStat> getTopNAF(int year, String ctn, String nafLevel, String indicator, int limit);

    List<SectorRisk> getRiskProfile(int year);
}
