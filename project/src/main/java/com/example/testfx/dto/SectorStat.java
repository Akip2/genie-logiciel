package com.example.testfx.dto;

/**
 * Data Transfer Object (DTO) représentant les statistiques consolidées
 * pour un secteur d'activité (CTN), en vue d'être affichées dans le graphique.
 */
public class SectorStat {
    private final String codeCTN;
    private final String label;
    private final Number value;

    public SectorStat(String codeCTN, String label, Number value) {
        this.codeCTN = codeCTN;
        this.label = label;
        this.value = value;
    }

    public String getCodeCTN() {
        return codeCTN;
    }

    public String getLabel() {
        return label;
    }

    public Number getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "SectorStat{" +
                "ctn='" + codeCTN + '\'' +
                ", label='" + label + '\'' +
                ", value=" + value +
                '}';
    }
}
