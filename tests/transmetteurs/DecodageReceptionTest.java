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
        // Testing the automate function with boolean inputs instead of 0/1 strings
        assertFalse(DecodageReception.automate(new boolean[]{false, false, false}));  // false -> 0
        assertTrue(DecodageReception.automate(new boolean[]{false, false, true}));   // true -> 1
        assertFalse(DecodageReception.automate(new boolean[]{false, true, false}));   // false -> 0
        assertFalse(DecodageReception.automate(new boolean[]{false, true, true}));    // false -> 0
        assertTrue(DecodageReception.automate(new boolean[]{true, false, false}));    // true -> 1
        assertTrue(DecodageReception.automate(new boolean[]{true, false, true}));     // true -> 1
        assertFalse(DecodageReception.automate(new boolean[]{true, true, false}));    // false -> 0
        assertTrue(DecodageReception.automate(new boolean[]{true, true, true}));      // true -> 1
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
        // Création d'une information de test avec des paquets de 3 bits
        Information<Boolean> information = new Information<>();
        
        // Paquet 1 : false, false, false -> false
        information.add(false); // Bit 1
        information.add(false); // Bit 2
        information.add(false); // Bit 3
        
        // Paquet 2 : false, false, true -> true
        information.add(false); // Bit 1
        information.add(false); // Bit 2
        information.add(true);  // Bit 3
        
        // Paquet 3 : false, true, false -> false
        information.add(false); // Bit 1
        information.add(true);  // Bit 2
        information.add(false); // Bit 3
        
        // Envoi de l'information à la méthode recevoir
        decodageReception.recevoir(information);
        
        // Vérification des résultats attendus
        Information<Boolean> resultatAttendu = new Information<>();
        resultatAttendu.add(false); // false -> "000"
        resultatAttendu.add(true);  // true -> "001"
        resultatAttendu.add(false); // false -> "010"

        // Vérification que la destination reçoit les informations attendues
        assertEquals(resultatAttendu.nbElements(), mockDestination.getInformationRecue().nbElements());

        for (int i = 0; i < resultatAttendu.nbElements(); i++) {
            assertEquals(resultatAttendu.iemeElement(i), mockDestination.getInformationRecue().iemeElement(i));
        }
    }

    @Test(expected = InformationNonConformeException.class)
    public void testRecevoirInformationIndivisibleParTrois() throws InformationNonConformeException {
        // Création d'une information invalide qui n'est pas divisible par 3
        Information<Boolean> information = new Information<>();
        information.add(true);   // 1
        information.add(false);  // 0
        information.add(true);   // 1
        information.add(true);   // 1
        decodageReception.recevoir(information); // Devrait lever une exception
    }

    // Test de la methode main
    @Test
    public void testMain() {
        // Test de la méthode main
        DecodageReception.main(new String[]{});

    }
}
