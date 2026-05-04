package data;

import com.example.testfx.data.DataRepository;
import com.example.testfx.model.AccidentTravail;
import org.apache.poi.EmptyFileException;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Permet de tester les méthodes de la classe DataRepository
 */
public class TestDataRepository {
    private DataRepository dataRepository;
    private DataRepository nonCharge;
    private AccidentTravail a1;
    private AccidentTravail a2;
    private AccidentTravail a3;

    @BeforeEach
    public void init(){
        dataRepository = new DataRepository();
        nonCharge =  new DataRepository();
        try {
            // On peut utiliser cette méthode dans le before each car ses tests on été écrits et exécutés avant l'écriture de la méthode init
            dataRepository.chargerFichier("src/test/resources/2023.xlsx");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        a1 = new AccidentTravail();
        a2 = new AccidentTravail();
        a3 = new AccidentTravail();
        a1.setCodeNAF("0111Z");
        a2.setCodeNAF("0112Z");
        a3.setCodeNAF("0121Z");
    }

    @AfterEach
    public void clean(){
        dataRepository = null;
        nonCharge = null;
        a1 = null;
        a2 = null;
        a3 = null;
    }

    @Test
    public void test_chargerFichier_OK() throws IOException {

        String nomFichierValide = "src/test/resources/2023.xlsx";

        // Validations
        assertDoesNotThrow(() -> dataRepository.chargerFichier(nomFichierValide));
    }

    @Test
    public void test_chargerFichier_invalide(){
        String nomFichierInvalide = "src/test/resources/Nom_Invalide_2023.xlsx";

        // Configuration de l'attendu
        String messageErreur = "Impossible d'extraire l'année du fichier : " + nomFichierInvalide + ". Le fichier doit être nommé ANNEE.xlsx (ex: 2023.xlsx)";

        // Vérifications
        Throwable exception = assertThrowsExactly(IllegalArgumentException.class, () -> {
            dataRepository.chargerFichier(nomFichierInvalide);
        });
        assertEquals(exception.getMessage(), messageErreur, "Le code ne lève pas la bonne exception lorsque le nom du fichier est invalide.");
    }

    @Test
    public void test_chargerFichier_inexistant() {
        String nomFichierInexistant = "src/test/resources/2056.xlsx";

        // Vérification
        assertThrowsExactly(FileNotFoundException.class, () -> {
            dataRepository.chargerFichier(nomFichierInexistant);
        });
    }

    @Test
    public void test_chargerFichier_format_invalide() {
        String nomFichierFormatInvalide = "src/test/resources/2024.csv";

        // Vérification
        assertThrows(NotOfficeXmlFileException.class, () -> {
            dataRepository.chargerFichier(nomFichierFormatInvalide);
        });
    }

    @Test
    public void test_chargerFichier_fichier_mal_construit() {
        String nomFichierMalConstruit = "src/test/resources/2021.xslx";
        String nomFichierMalConstruit2 = "src/test/resources/2020.xslx";
        // Vérifications
        assertThrows(IOException.class, () -> {
            dataRepository.chargerFichier(nomFichierMalConstruit);
        });


        assertThrows(IOException.class, () -> {
            dataRepository.chargerFichier(nomFichierMalConstruit2);
        });
    }

    @Test
    public void test_chargerFichier_fichier_vide(){
        String nomFichierVide = "src/test/resources/2025.xlsx";

        // Vérification
        assertThrows(EmptyFileException.class, () -> {
            dataRepository.chargerFichier(nomFichierVide);
        });
    }

    @Test
    public void test_estCharge(){

        // Test
        boolean faux = nonCharge.estCharge();
        boolean vrai = dataRepository.estCharge();

        // Validations
        assertTrue(vrai, "dataRepo doit être chargé");
        assertFalse(faux, "nonCharge ne doit pas être chargé");
    }

    @Test
    public void test_getNombreEnregistrements(){

        // Test de la méthode getNombreEnregistrements
        int vide = nonCharge.getNombreEnregistrements();
        int trois = dataRepository.getNombreEnregistrements();

        // Validations
        assertEquals(0, vide, "nonCharge doit avoir 0 enregistrements");
        assertEquals(3, trois, "dataRepo doit contenir 3 enregistrements");
    }

    @Test
    public void test_getAnneesDisponibles_OK_plusieurs_annees(){
        // Initialisation
        try {
            dataRepository.chargerFichier("src/test/resources/2026.xlsx");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<Integer> anneesAttendues = new ArrayList<>();
        anneesAttendues.add(2023);
        anneesAttendues.add(2026);

        // Test
        List<Integer> annees = dataRepository.getAnneesDisponibles();

        // Validation
        assertEquals(anneesAttendues.getFirst(), annees.getFirst());
        assertEquals(anneesAttendues.getLast(), annees.getLast());
    }

    @Test
    public void test_getAnneesDisponibles_OK_une_annee(){
        // Initialisation
        List<Integer> anneesAttendues = new ArrayList<>();
        anneesAttendues.add(2023);

        // Test
        List<Integer> annees = dataRepository.getAnneesDisponibles();

        // Validation
        assertEquals(anneesAttendues.getFirst(), annees.getFirst());
    }

    @Test
    public void test_getAnneesDisponibles_non_charge(){
        // Test
        List<Integer> annees = nonCharge.getAnneesDisponibles();

        // Validation
        assertTrue(annees.isEmpty());
    }

    @Test
    public void test_chargerDossier_OK(){
        assertThrowsExactly(EmptyFileException.class, () -> {
                nonCharge.chargerDossier("src/test/resources");
            });
    }

    @Test
    public void test_anneeDisponible(){
        assertTrue(dataRepository.anneeDisponible(2023));
        assertFalse(dataRepository.anneeDisponible(1910));
        assertFalse(nonCharge.anneeDisponible(2023));
    }

    @Test
    public void test_getTout(){
        // Initialisation
        List<AccidentTravail> attendu = new ArrayList<>();
        attendu.add(a1);
        attendu.add(a2);
        attendu.add(a3);

        // Test
        List<AccidentTravail> ok = dataRepository.getTout();
        List<AccidentTravail> non_charge = nonCharge.getTout();

        // Validations
        assertEquals(attendu.get(0).getCodeNAF(), ok.get(0).getCodeNAF());
        assertEquals(attendu.get(1).getCodeNAF(), ok.get(1).getCodeNAF());
        assertEquals(attendu.get(2).getCodeNAF(), ok.get(2).getCodeNAF());

        assertTrue(non_charge.isEmpty());
    }

    @Test
    public void test_parAnnee_OK(){
        try {
            dataRepository.chargerFichier("src/test/resources/2026.xlsx");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Test
        List<AccidentTravail> resultat = dataRepository.parAnnee(2023);

        // Validation
        assertEquals(resultat.getFirst().getCodeNAF(), a1.getCodeNAF());
        // a3 correspond au dernier accident de l'annee 2023 mais pas de l'annee 2026
        assertEquals(resultat.getLast().getCodeNAF(), a3.getCodeNAF());

    }

    @Test
    public void test_parAnnee_non_charge(){

        // Test
        List<AccidentTravail> resultat = nonCharge.parAnnee(2023);

        // Validation
        assertTrue(resultat.isEmpty());
    }

    @Test
    public void test_parAnnee_annee_inexistante(){
        // Test
        List<AccidentTravail> resultat = dataRepository.parAnnee(3012);

        // Validation
        assertTrue(resultat.isEmpty());
    }

    @Test
    public void test_parCTN(){
        // Test
        List<AccidentTravail> ok = dataRepository.parCTN("AA");
        List<AccidentTravail> non_charge = nonCharge.parCTN("AA");
        List<AccidentTravail> inexistant = dataRepository.parCTN("YEAH");

        // Validations
        assertEquals(2, ok.size());
        assertTrue(non_charge.isEmpty());
        assertTrue(inexistant.isEmpty());
    }

    @Test    // Test fait par Rim
    public void test_parCTNetAnnee(){
        // Test
        List<AccidentTravail> ok = dataRepository.parCTNetAnnee("AA", 2023);
        List<AccidentTravail> non_charge = nonCharge.parCTNetAnnee("AA", 2023);
        List<AccidentTravail> inexistant = dataRepository.parCTNetAnnee("YEAH", 2023);

        // Validations
        assertEquals(2, ok.size());
        assertTrue(non_charge.isEmpty());
        assertTrue(inexistant.isEmpty());
    }

    @Test   // Test fait par Rim
    public void test_parCodeNAF(){
        // Test
        List<AccidentTravail> ok = dataRepository.parCodeNAF("0111Z");
        List<AccidentTravail> non_charge = nonCharge.parCodeNAF("0111Z");
        List<AccidentTravail> inexistant = dataRepository.parCodeNAF("9999X");

        // Validations
        assertEquals(1, ok.size());
        assertEquals("0111Z", ok.getFirst().getCodeNAF());
        assertTrue(non_charge.isEmpty());
        assertTrue(inexistant.isEmpty());
    }

    @Test  // Test fait par Rim
    public void test_parCodeNAF2(){
        // Test
        List<AccidentTravail> ok = dataRepository.parCodeNAF2("01");
        List<AccidentTravail> non_charge = nonCharge.parCodeNAF2("01");
        List<AccidentTravail> inexistant = dataRepository.parCodeNAF2("99");

        // Validations
        assertEquals(3, ok.size());
        assertTrue(non_charge.isEmpty());
        assertTrue(inexistant.isEmpty());
    }

    @Test // Test fait par Rim
    public void test_rechercherActivite(){
        // Test
        List<AccidentTravail> ok = dataRepository.rechercherActivite("Culture");
        List<AccidentTravail> non_charge = nonCharge.rechercherActivite("Culture");
        List<AccidentTravail> inexistant = dataRepository.rechercherActivite("YEAH");

        // Validations
        assertEquals(3, ok.size());
        assertTrue(non_charge.isEmpty());
        assertTrue(inexistant.isEmpty());
    }

    @Test
    public void test_getListe_CTN(){
        // Initialisation
        List<String> attendus = new ArrayList<>();
        attendus.add("AA");
        attendus.add("AB");

        // Test
        List<String> ok = dataRepository.getListeCTN();
        List<String> non_charge = nonCharge.getListeCTN();

        // Validations
        assertEquals(attendus, ok);
        assertTrue(non_charge.isEmpty());
    }

    @Test
    public void test_getReferentielsCTN(){
        // Initialisation
        Map<String, String> attendu = new TreeMap<>();
        attendu.put("AA", "Métallurgie");
        attendu.put("AB", "Métallurgie");

        // Test
        Map<String, String> ok = dataRepository.getReferentielCTN();
        Map<String, String> non_charge = nonCharge.getReferentielCTN();

        // Validations
        assertEquals(attendu, ok);
        assertTrue(non_charge.isEmpty());
    }

    @Test
    public void test_getReferentielNAF2(){
        // Initialisation
        Map<String, String> attendu = new TreeMap<>();
        attendu.put("01", "Culture et production animale, chasse et services annexes");

        // Test
        Map<String, String> ok = dataRepository.getReferentielNAF2();
        Map<String, String> non_charge = nonCharge.getReferentielNAF2();

        // Validations
        assertEquals(attendu, ok);
        assertTrue(non_charge.isEmpty());
    }

}
