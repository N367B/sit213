package transmetteurs;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import information.*;
import destinations.*;

public class DecodageReceptionTest {

    private DecodageReception decodageReception;
    private MockDestination<Boolean> mockDestination;

    @Before
    public void setUp() {
        decodageReception = new DecodageReception();
        mockDestination = new MockDestination<>();
        decodageReception.connecter(mockDestination);
    }

    @Test
    public void testAutomate() {
        assertEquals(0, DecodageReception.automate("000"));
        assertEquals(1, DecodageReception.automate("001"));
        assertEquals(0, DecodageReception.automate("010"));
        assertEquals(0, DecodageReception.automate("011"));
        assertEquals(1, DecodageReception.automate("100"));
        assertEquals(1, DecodageReception.automate("101"));
        assertEquals(0, DecodageReception.automate("110"));
        assertEquals(1, DecodageReception.automate("111"));
    }

    @Test(expected = InformationNonConformeException.class)
    public void testRecevoirNullInformation() throws InformationNonConformeException {
        decodageReception.recevoir(null);
    }

    @Test(expected = InformationNonConformeException.class)
    public void testRecevoirEmptyInformation() throws InformationNonConformeException {
        Information<Boolean> emptyInformation = new Information<>();
        decodageReception.recevoir(emptyInformation);
    }

    @Test
    public void testRecevoirInformationValide() throws InformationNonConformeException {
        // Création d'une information de test correspondant à "000" -> 0, "001" -> 1, "010" -> 0
        Information<Boolean> information = new Information<>();
        
        // Paquet 1 : "000" -> 0
        information.add(false); // Bit 1: 0
        information.add(false); // Bit 2: 0
        information.add(false); // Bit 3: 0
        
        // Paquet 2 : "001" -> 1
        information.add(false); // Bit 1: 0
        information.add(false); // Bit 2: 0
        information.add(true);  // Bit 3: 1
        
        // Paquet 3 : "010" -> 0
        information.add(false); // Bit 1: 0
        information.add(true);  // Bit 2: 1
        information.add(false); // Bit 3: 0
        
        // Envoi de l'information à la méthode recevoir
        decodageReception.recevoir(information);
        
        // Vérification des résultats
        Information<Boolean> resultatAttendu = new Information<>();
        resultatAttendu.add(false); // 0 -> "000"
        resultatAttendu.add(true);  // 1 -> "001"
        resultatAttendu.add(false); // 0 -> "010"

        // Assurez-vous que la destination reçoit les informations attendues
        assertEquals(resultatAttendu.nbElements(), mockDestination.getInformationRecue().nbElements());

        for (int i = 0; i < resultatAttendu.nbElements(); i++) {
            assertEquals(resultatAttendu.iemeElement(i), mockDestination.getInformationRecue().iemeElement(i));
        }
    }

    @Test(expected = InformationNonConformeException.class)
    public void testRecevoirInformationIndivisibleParTrois() throws InformationNonConformeException {
        Information<Boolean> information = new Information<>();
        information.add(true);   // 1
        information.add(false);  // 0
        information.add(true);   // 1
        information.add(true);   // 1
        decodageReception.recevoir(information); // Devrait lever une exception
    }
}

