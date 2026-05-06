# Génie Logiciel — Analyse des accidents du travail

> Application Java/JavaFX d'aide à la décision pour la prévention des risques professionnels en France, basée sur les données ouvertes de l'Assurance Maladie.

Projet universitaire — Master 1 MIAGE — IDMC, Université de Lorraine.
Rendu le **22 mai 2026**.

---

## Présentation

L'application permet d'explorer interactivement les statistiques d'accidents du travail (AT) en France. Les données sont issues des fichiers ouverts de l'Assurance Maladie pour les années **2020, 2021 et 2023** et croisent les codes secteur **CTN** (9 secteurs) avec la nomenclature **NAF** à différents niveaux d'agrégation.

L'outil s'adresse aux professionnels RH, aux acteurs de la prévention et aux étudiants/chercheurs travaillant sur la sinistralité au travail.

### Fonctionnalités principales

- Filtres dynamiques : année, secteur CTN (multi-sélection), niveau de granularité NAF, indicateur mesuré
- 8 graphiques interactifs (classement, comparaison, répartition, causes, top sous-secteurs, fréquence/gravité, etc.)
- Indicateurs calculés : nombre d'AT, indice de fréquence, taux de gravité, décès, journées d'IT, IP
- Vue multi-critères normalisée pour comparer les profils de risque entre secteurs

---

## Stack technique

| Composant         | Version  |
|-------------------|----------|
| Java              | 25       |
| JavaFX            | 25.0.2   |
| Maven             | 3.9+     |
| JFreeChart        | 1.5.5    |
| JFreeChart-FX     | 2.0.2    |
| Apache POI        | 5.5.0    |
| JUnit Jupiter     | 6.0.3    |
| JaCoCo            | 0.8.12   |

---

## Prérequis

- **JDK 25** (Temurin recommandé) — vérifier avec `java -version`
- **Maven 3.9+** — vérifier avec `mvn -v`
- Système : Windows, macOS ou Linux

---

## Installation

```bash
# 1. Cloner le dépôt
git clone https://github.com/Akip2/genie-logiciel.git
cd genie-logiciel/project

# 2. Compiler et résoudre les dépendances
mvn clean install
```

Les fichiers de données (`2020.xlsx`, `2021.xlsx`, `2023.xlsx`) sont déjà inclus dans le dossier `project/data/`.

---

## Lancement

Depuis le dossier `project/` :

```bash
mvn exec:java
```

Le main par défaut est `com.example.testfx.Main`. La fenêtre principale s'ouvre avec l'onglet **Analyse** sélectionné par défaut.

### Lancement depuis IntelliJ

1. Ouvrir le dossier `project/` comme projet Maven
2. Exécuter la classe `com.example.testfx.Main`
3. S'assurer que les VM options JavaFX sont correctement configurées (normalement géré automatiquement par le module-info)

---

## Architecture

```
project/
├── data/                          # Fichiers Excel sources
│   ├── 2020.xlsx
│   ├── 2021.xlsx
│   └── 2023.xlsx
├── src/main/java/com/example/testfx/
│   ├── Main.java                  # Entrée JavaFX, construction du layout
│   ├── chart/                     # Couche graphique (Mouad)
│   │   ├── ChartManager.java      # Façade utilisée par l'UI
│   │   ├── ChartUtils.java        # Helpers de formatage
│   │   ├── CouleursCauses.java    # Palette commune
│   │   ├── BarChartSecteur.java   # G1
│   │   ├── BarChartEvolution.java # G2
│   │   ├── PieChartRepartition.java # G3
│   │   ├── StackedBarChartCauses.java # G4
│   │   ├── BarChartTopNaf.java    # G5
│   │   ├── ScatterChartRisque.java # G6
│   │   ├── BarChartComparaison.java # G7
│   │   └── BarChartTopCauses.java  # G8
│   ├── data/                      # Repository (Baptiste)
│   │   └── DataRepository.java
│   ├── parser/                    # Parsing Excel (Baptiste)
│   │   └── ExcelParser.java
│   ├── service/                   # Logique métier (Théo)
│   │   ├── IStatisticsService.java
│   │   ├── StatisticsServiceImpl.java
│   │   └── DataFilterService.java
│   ├── model/                     # Entités du domaine
│   │   ├── AccidentTravail.java
│   │   └── CauseAccident.java
│   ├── dto/                       # Objets de transfert
│   │   ├── FilterRequest.java
│   │   ├── SectorStat.java
│   │   ├── SectorShare.java
│   │   ├── SectorCauses.java
│   │   ├── SectorRisk.java
│   │   ├── SubSectorStat.java
│   │   └── YearValue.java
│   └── onglets/                   # UI JavaFX (Nathan)
│       ├── Onglet.java
│       ├── OngletAnalyse.java
│       ├── OngletDonnees.java
│       └── OngletRapports.java
└── pom.xml
```

### Couches

1. **Parsing** (`parser`, `data`) : lit les fichiers Excel et les expose via le `DataRepository`. Le mapping est fait par **nom de colonne** et non par position, car l'ordre des colonnes change entre les années.
2. **Métier** (`service`) : calcule les statistiques agrégées (par CTN, par NAF, indicateurs dérivés comme l'indice de fréquence ou le taux de gravité).
3. **Graphiques** (`chart`) : transforme les DTO en `JFreeChart` rendus via `ChartViewer` (le pont JavaFX de JFreeChart). `ChartManager` est la façade : Nathan instancie ce manager, récupère les `ChartViewer` et les place dans son layout.
4. **UI** (`Main`, `onglets`) : interface JavaFX avec navigation par onglets et panneau de filtres.

---

## Données utilisées

Les fichiers Excel proviennent de la base **Assurance Maladie — Risques professionnels**. Chaque ligne correspond à un croisement (secteur CTN × code NAF) pour une année. Les colonnes principales :

- Identifiants : CTN, libellé CTN, code NAF (5 caractères), code NAF38, code NAF2
- Effectifs : nombre de salariés, heures travaillées, nombre de SIRET
- Sinistralité : AT en 1er règlement, AT avec arrêt de 4 jours+, IP, décès, journées d'IT
- 12 colonnes de causes : manutention manuelle, chutes de plain-pied/hauteur, outillage, machines, chimique, électrique, routier, agressions, etc.

**Note** : l'année 2022 est absente des données ouvertes côté Assurance Maladie.

Sources :

- DARES — Accidents du travail : https://dares.travail-emploi.gouv.fr/donnees/les-accidents-du-travail
- INSEE — Nomenclature NAF : https://www.insee.fr/fr/information/2120875
- Assurance Maladie — Risques professionnels : https://www.assurance-maladie.ameli.fr/etudes-et-donnees/par-theme/risques-professionnels-et-sinistralite

---

## Indicateurs

| Indicateur            | Formule                                              |
|-----------------------|------------------------------------------------------|
| Indice de fréquence   | (AT en 1er règlement / Nombre de salariés) × 1000   |
| Taux de gravité       | (Journées d'IT / Heures travaillées) × 1000         |
| Évolution             | ((Valeur N − Valeur N-1) / Valeur N-1) × 100        |

L'indice de fréquence est l'indicateur recommandé pour comparer des secteurs de tailles différentes : le nombre d'AT brut est trompeur car il dépend directement de l'effectif salarié.

---

## Tests

```bash
mvn test
```

Les tests sont exécutés via JUnit 5. La couverture est mesurée par JaCoCo et générée dans `target/site/jacoco/index.html` après un `mvn verify`.

L'intégration continue est configurée dans `.github/workflows/maven.yml` : chaque push ou pull request sur `main` déclenche une build complète.

---

## Équipe

| Membre                          | Rôle                                          |
|---------------------------------|-----------------------------------------------|
| Antoine FONTANEZ                | Architecte / Lead Git, configuration Maven   |
| Baptiste FUMERON-LECOMTE        | Parsing Excel, accès aux données              |
| Théo KACHLER                    | Services métier, calculs statistiques         |
| Raphaël MANGIN                  | Tests JUnit, documentation                    |
| Nathan PIERROT                  | Interface JavaFX, navigation                  |
| Mouad SAHRAOUI DOUKKALI         | Couche graphique (JFreeChart)                 |
| Rim EL AOUDI                    | Rapport, maquettes UI                         |

---

## Méthode

Modèle en V — chaque phase de conception est associée à une phase de validation. Travail en branches Git par fonctionnalité (`feature/...`, `mouad-dev`, etc.) avec pull requests vers `main` et rebase pour garder un historique linéaire.

---

## Licence

Projet universitaire publié à des fins pédagogiques. Le code est mis à disposition sans garantie. Les données sources restent la propriété de leurs producteurs respectifs (Assurance Maladie, DARES, INSEE) et sont diffusées en open data sous leurs licences propres.