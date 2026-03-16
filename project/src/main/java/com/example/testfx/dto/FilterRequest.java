package com.example.testfx.dto;

import java.util.List;

public class FilterRequest {
    private final int year;
    private final List<String> selectedCTNs;
    private final String nafLevel;
    private final String indicator;

    public FilterRequest(int year, List<String> selectedCTNs, String nafLevel, String indicator) {
        this.year = year;
        this.selectedCTNs = selectedCTNs;
        this.nafLevel = nafLevel;
        this.indicator = indicator;
    }

    public int getYear() {
        return year;
    }

    public List<String> getSelectedCTNs() {
        return selectedCTNs;
    }

    public String getNafLevel() {
        return nafLevel;
    }

    public String getIndicator() {
        return indicator;
    }


    // Builder 
    public static class Builder {
        private int year = 2023; // défaut
        private List<String> selectedCTNs = null; // défaut: tous
        private String nafLevel = "CTN"; // défaut
        private String indicator = "atPremierReglement"; // défaut

        public Builder year(int year) { this.year = year; return this; }
        public Builder selectedCTNs(List<String> ctns) { this.selectedCTNs = ctns; return this; }
        public Builder nafLevel(String level) { this.nafLevel = level; return this; }
        public Builder indicator(String ind) { this.indicator = ind; return this; }

        public FilterRequest build() {
            return new FilterRequest(year, selectedCTNs, nafLevel, indicator);
        }
    }
}
