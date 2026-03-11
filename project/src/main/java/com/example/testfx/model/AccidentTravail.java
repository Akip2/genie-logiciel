package com.example.testfx.model;

import java.util.EnumMap;
import java.util.Map;

/**
 * Représente une ligne du fichier Assurance Maladie :
 * les statistiques AT pour un croisement (CTN × Code NAF) sur une année donnée.
 *
 * Chaque instance = 1 ligne = 1 couple (secteur d'activité, activité NAF) pour 1 année.
 */
public class AccidentTravail {

    // Identifiants sectoriels
    private String codeCTN;          // ex: "AA"
    private String libelleCTN;       // ex: "Métallurgie"
    private String codeNAF;          // ex: "2550A" (5 caractères)
    private String libelleNAF;       // ex: "Forge, estampage, matriçage..."
    private String codeNAF2;         // ex: "25" (2 caractères)
    private String libelleNAF2;      // ex: "Fabrication de produits métalliques..."
    private String codeNAF38;        // ex: "CH" (agrégé)
    private String libelleNAF38;     // ex: "Métallurgie et fabrication de produits métalliques..."

    // Année
    private int annee;

    // Indicateurs principaux
    private int nombreSalaries;
    private long heuresTravaillees;
    private int nombreSIRET;
    private int atPremierReglement;     // AT en 1er règlement
    private int atAvec4JoursArret;      // dont AT avec 4+ jours d'arrêt
    private int nouvellesIP;            // Nouvelles incapacités permanentes
    private int ipTauxInferieur10;      // dont IP avec taux < 10%
    private int ipTauxSuperieur10;      // dont IP avec taux >= 10%
    private int deces;
    private int journeesIT;            // Journées d'incapacité temporaire
    private double sommeTauxIPInf10;
    private double sommeTauxIP;

    // Causes d'accidents (répartition par risque)
    private final Map<CauseAccident, Integer> causes;

    public AccidentTravail() {
        this.causes = new EnumMap<>(CauseAccident.class);
    }

    // Accès aux causes

    /**
     * Retourne le nombre d'accidents pour une cause donnée.
     */
    public int getCause(CauseAccident cause) {
        return causes.getOrDefault(cause, 0);
    }

    public void setCause(CauseAccident cause, int valeur) {
        causes.put(cause, valeur);
    }

    // Getters / Setters

    public String getCodeCTN() { return codeCTN; }
    public void setCodeCTN(String codeCTN) { this.codeCTN = codeCTN; }

    public String getLibelleCTN() { return libelleCTN; }
    public void setLibelleCTN(String libelleCTN) { this.libelleCTN = libelleCTN; }

    public String getCodeNAF() { return codeNAF; }
    public void setCodeNAF(String codeNAF) { this.codeNAF = codeNAF; }

    public String getLibelleNAF() { return libelleNAF; }
    public void setLibelleNAF(String libelleNAF) { this.libelleNAF = libelleNAF; }

    public String getCodeNAF2() { return codeNAF2; }
    public void setCodeNAF2(String codeNAF2) { this.codeNAF2 = codeNAF2; }

    public String getLibelleNAF2() { return libelleNAF2; }
    public void setLibelleNAF2(String libelleNAF2) { this.libelleNAF2 = libelleNAF2; }

    public String getCodeNAF38() { return codeNAF38; }
    public void setCodeNAF38(String codeNAF38) { this.codeNAF38 = codeNAF38; }

    public String getLibelleNAF38() { return libelleNAF38; }
    public void setLibelleNAF38(String libelleNAF38) { this.libelleNAF38 = libelleNAF38; }

    public int getAnnee() { return annee; }
    public void setAnnee(int annee) { this.annee = annee; }

    public int getNombreSalaries() { return nombreSalaries; }
    public void setNombreSalaries(int nombreSalaries) { this.nombreSalaries = nombreSalaries; }

    public long getHeuresTravaillees() { return heuresTravaillees; }
    public void setHeuresTravaillees(long heuresTravaillees) { this.heuresTravaillees = heuresTravaillees; }

    public int getNombreSIRET() { return nombreSIRET; }
    public void setNombreSIRET(int nombreSIRET) { this.nombreSIRET = nombreSIRET; }

    public int getAtPremierReglement() { return atPremierReglement; }
    public void setAtPremierReglement(int atPremierReglement) { this.atPremierReglement = atPremierReglement; }

    public int getAtAvec4JoursArret() { return atAvec4JoursArret; }
    public void setAtAvec4JoursArret(int atAvec4JoursArret) { this.atAvec4JoursArret = atAvec4JoursArret; }

    public int getNouvellesIP() { return nouvellesIP; }
    public void setNouvellesIP(int nouvellesIP) { this.nouvellesIP = nouvellesIP; }

    public int getIpTauxInferieur10() { return ipTauxInferieur10; }
    public void setIpTauxInferieur10(int ipTauxInferieur10) { this.ipTauxInferieur10 = ipTauxInferieur10; }

    public int getIpTauxSuperieur10() { return ipTauxSuperieur10; }
    public void setIpTauxSuperieur10(int ipTauxSuperieur10) { this.ipTauxSuperieur10 = ipTauxSuperieur10; }

    public int getDeces() { return deces; }
    public void setDeces(int deces) { this.deces = deces; }

    public int getJourneesIT() { return journeesIT; }
    public void setJourneesIT(int journeesIT) { this.journeesIT = journeesIT; }

    public double getSommeTauxIPInf10() { return sommeTauxIPInf10; }
    public void setSommeTauxIPInf10(double sommeTauxIPInf10) { this.sommeTauxIPInf10 = sommeTauxIPInf10; }

    public double getSommeTauxIP() { return sommeTauxIP; }
    public void setSommeTauxIP(double sommeTauxIP) { this.sommeTauxIP = sommeTauxIP; }

    public Map<CauseAccident, Integer> getCauses() { return causes; }

    @Override
    public String toString() {
        return String.format("[%d] %s (%s) - %s (%s) : %d AT, %d salariés",
                annee, codeCTN, libelleCTN, codeNAF, libelleNAF,
                atPremierReglement, nombreSalaries);
    }
}