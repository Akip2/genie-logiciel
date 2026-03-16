package com.example.testfx.dto;

import com.example.testfx.model.CauseAccident;
import java.util.Map;

// TKA - 16/03 - records pour les DTO
public record SectorCauses(
        String codeCTN,
        String libelleCTN,
        Map<CauseAccident, Integer> repartitionCauses
) {}
