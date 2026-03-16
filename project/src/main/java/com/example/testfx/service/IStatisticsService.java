package com.example.testfx.service;

import com.example.testfx.dto.FilterRequest;
import com.example.testfx.dto.SectorStat;
import com.example.testfx.model.AccidentTravail;

import java.util.List;

public interface IStatisticsService {

    List<SectorStat> getStatsByCTN(FilterRequest request, List<AccidentTravail> accidents);

}
