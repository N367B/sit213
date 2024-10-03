package transmetteurs;

import destinations.MockDestination;
import information.Information;
import information.InformationNonConformeException;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TransmetteurAnalogiqueMultiTrajetTest {

    private TransmetteurAnalogiqueMultiTrajet transmetteur;
    private List<float[]> trajetsValides;

    @Before
    public void setup() {
        // Initialisation des trajets multiples valides
        trajetsValides = new ArrayList<>();
        trajetsValides.add(new float[]{3, 0.5f});
        trajetsValides.add(new float[]{5, 0.2f});

        transmetteur = new TransmetteurAnalogiqueMultiTrajet(trajetsValides);
    }

    /**
     * Test qui verifie des informations valides et verifies si ils sont émis correctement 
     */
    @Test
    public void testRecevoirAndEmettreValidInformation() throws InformationNonConformeException {

        Information<Float> informationAnalogique = new Information<>();
        informationAnalogique.add(1.0f);  // t=0
        informationAnalogique.add(0.5f);  // t=1
        informationAnalogique.add(0.0f);  // t=2
        informationAnalogique.add(1.0f);  // t=3
        informationAnalogique.add(0.5f);  // t=4
        informationAnalogique.add(0.0f);  // t=5

        // Création d'une destination 
        MockDestination<Float> mockDestination = new MockDestination<>();
        transmetteur.connecter(mockDestination);

        // Emission et reception du signal
        transmetteur.recevoir(informationAnalogique);

        // Calcul du signal attendu avec trajets multiples
        // Trajets : {3, 0.5f} et {5, 0.2f}
        Information<Float> expectedSignal = new Information<>();
        expectedSignal.add(1.0f);  // t=0, pas d'effet des trajets à ce moment
        expectedSignal.add(0.5f);  // t=1, pas d'effet des trajets à ce moment
        expectedSignal.add(0.0f);  // t=2, pas d'effet des trajets à ce moment
        expectedSignal.add(1.5f);  // t=3, effet du trajet {3, 0.5f} (ajout de 0.5*1.0)
        expectedSignal.add(0.75f); // t=4, effet du trajet {3, 0.5f} (ajout de 0.5*0.5)
        expectedSignal.add(0.2f);  // t=5, effet du trajet {5, 0.2f} (ajout de 0.2*1.0)

        // Vérification que le signal modifié est transmis correctement aux destinations
        Information<Float> actualSignal = mockDestination.getInformationRecue();

        assertNotNull(actualSignal);  // Vérifier que le signal n'est pas null
        assertEquals(expectedSignal.nbElements(), actualSignal.nbElements());  // Vérifier que les tailles sont égales

        // Comparer les valeurs élément par élément 
        for (int i = 0; i < expectedSignal.nbElements(); i++) {
            assertEquals(expectedSignal.iemeElement(i), actualSignal.iemeElement(i), 0.0001);  
        }
    }

    /**
     * Test pour verifier si l'execution de la méthode main du transmetteur fonctionne correctement
     */
    @Test
    public void testMainMethodExecution() {
        // Simule l'exécution de la méthode main pour s'assurer qu'il n'y a pas d'erreurs
        try {
            TransmetteurAnalogiqueMultiTrajet.main(new String[]{});
        } catch (Exception e) {
            fail("La méthode main ne devrait pas lever d'exception : " + e.getMessage());
        }
    }

    /**
     * Test qui reçoit une information nulle et donc leve l'exception InformationNonConformeException
     */
    @Test(expected = InformationNonConformeException.class)
    public void testRecevoirNullInformation() throws InformationNonConformeException {
        transmetteur.recevoir(null);  
    }

    /**
     * Test qui reçoit une information vide et donc doit lancer InformationNonConformeException
     */
    @Test(expected = InformationNonConformeException.class)
    public void testRecevoirEmptyInformation() throws InformationNonConformeException {
        Information<Float> emptyInformation = new Information<>();
        transmetteur.recevoir(emptyInformation);  
    }

    /**
     * Test qui emet une information ou il n'y a pas de destinataire
     */
    @Test
    public void testEmettreWithoutDestination() throws InformationNonConformeException {
        // Créer un signal analogique
        Information<Float> informationAnalogique = new Information<>();
        informationAnalogique.add(1.0f);
        informationAnalogique.add(0.0f);

        // Emission et reception de l'information (sans destinations connectées)
        transmetteur.recevoir(informationAnalogique);

        // Aucune exception ne doit être lancée, et aucune destination ne doit recevoir le signal
        assertTrue(transmetteur.getDestinationsConnectees().isEmpty());
    }

    /**
     * Test qui emet une information nulle et donc doit lancer InformationNonConformeException
     */
    @Test(expected = InformationNonConformeException.class)
    public void testEmettreNullInformation() throws InformationNonConformeException {
        transmetteur.emettre();  
    }

    /**
     * Test avec un décalage plus grand que la taille du message
     */
    @Test
    public void testDecalagePlusGrandQueMessage() throws InformationNonConformeException {
        List<float[]> trajetsIndirects = new ArrayList<>();
        trajetsIndirects.add(new float[]{10, 0.5f});  // dt = 10 (bigger than message size)

        transmetteur = new TransmetteurAnalogiqueMultiTrajet(trajetsIndirects);

        // Create a short message
        Information<Float> informationAnalogique = new Information<>();
        informationAnalogique.add(1.0f);
        informationAnalogique.add(0.5f);

        // Create a mock destination to capture the transmitted signal
        MockDestination<Float> mockDestination = new MockDestination<>();
        transmetteur.connecter(mockDestination);

        // Emit the message
        transmetteur.recevoir(informationAnalogique);

        // Check that the transmitted signal is the same as the input, since dt > message size
        Information<Float> expectedSignal = new Information<>();
        expectedSignal.add(1.0f);
        expectedSignal.add(0.5f);

        Information<Float> actualSignal = mockDestination.getInformationRecue();
        assertEquals(expectedSignal, actualSignal);
    }

    /**
     * Test qui recoit une information avec plusieurs destinations
     */
    @Test
    public void testRecevoirWithMultipleDestinations() throws InformationNonConformeException {
        // Créer un signal analogique
        Information<Float> informationAnalogique = new Information<>();
        informationAnalogique.add(1.0f);
        informationAnalogique.add(0.5f);

        // Création de plusieurs destinations 
        MockDestination<Float> mockDestination1 = new MockDestination<>();
        MockDestination<Float> mockDestination2 = new MockDestination<>();

        // Connecter plusieurs destinations
        transmetteur.connecter(mockDestination1);
        transmetteur.connecter(mockDestination2);

        // Emission et reception du signal
        transmetteur.recevoir(informationAnalogique);

        // Vérifier que les deux destinations reçoivent la même information modifiée
        assertEquals(informationAnalogique, mockDestination1.getInformationRecue());
        assertEquals(informationAnalogique, mockDestination2.getInformationRecue());
    }
}
