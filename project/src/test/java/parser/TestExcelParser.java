package parser;

import com.example.testfx.model.AccidentTravail;
import com.example.testfx.parser.ExcelParser;
import org.apache.poi.EmptyFileException;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Permet de tester les méthodes de la classe ExcelParser
 */
public class TestExcelParser {

    private final ExcelParser parser = new ExcelParser();

    @Test
    public void test_parse_OK() throws IOException {

        // Configuration de l'attendu
        AccidentTravail a1 = new AccidentTravail();
        AccidentTravail a2 = new AccidentTravail();
        AccidentTravail a3 = new AccidentTravail();
        a1.setCodeNAF("0111Z");
        a2.setCodeNAF("0112Z");
        a3.setCodeNAF("0121Z");

        // Test de la méthode parse
        String nomFichierValide = "src/test/resources/2023.xlsx";
        List<AccidentTravail> resultat = parser.parse(nomFichierValide);

        // Validations
        assertEquals(resultat.get(0).getCodeNAF(), a1.getCodeNAF(), "Le test du premier accident ne passe pas");
        assertEquals(resultat.get(1).getCodeNAF(), a2.getCodeNAF(), "Le test du second accident ne passe pas");
        assertEquals(resultat.get(2).getCodeNAF(), a3.getCodeNAF(), "Le test du troisième accident ne passe pas");

    }

    @Test
    public void test_parse_invalide(){
        String nomFichierInvalide = "src/test/resources/Nom_Invalide_2023.xlsx";

        // Configuration de l'attendu
        String messageErreur = "Impossible d'extraire l'année du fichier : " + nomFichierInvalide + ". Le fichier doit être nommé ANNEE.xlsx (ex: 2023.xlsx)";

        // Vérifications
        Throwable exception = assertThrowsExactly(IllegalArgumentException.class, () -> {
            parser.parse(nomFichierInvalide);
        });
        assertEquals(exception.getMessage(), messageErreur, "Le code ne lève pas la bonne exception lorsque le nom du fichier est invalide.");
    }

    @Test
    public void test_parse_inexistant() {
        String nomFichierInexistant = "src/test/resources/2056.xlsx";

        // Vérification
        assertThrowsExactly(FileNotFoundException.class, () -> {
            parser.parse(nomFichierInexistant);
        });
    }

    @Test
    public void test_parse_format_invalide() {
        String nomFichierFormatInvalide = "src/test/resources/2024.csv";

        // Vérification
        assertThrows(NotOfficeXmlFileException.class, () -> {
            parser.parse(nomFichierFormatInvalide);
        });
    }

    @Test
    public void test_parse_fichier_mal_construit() {
        String nomFichierMalConstruit = "src/test/resources/2021.xslx";
        String nomFichierMalConstruit2 = "src/test/resources/2020.xslx";
        // Vérifications
        assertThrows(IOException.class, () -> {
           parser.parse(nomFichierMalConstruit);
        });


        assertThrows(IOException.class, () -> {
            parser.parse(nomFichierMalConstruit2);
        });
    }

    @Test
    public void test_parse_fichier_vide(){
        String nomFichierVide = "src/test/resources/2025.xlsx";

        // Vérification
        assertThrows(EmptyFileException.class, () -> {
            parser.parse(nomFichierVide);
        });
    }

}
