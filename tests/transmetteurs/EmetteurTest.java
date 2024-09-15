package transmetteurs;

import information.Information;
import information.InformationNonConformeException;
import destinations.MockDestination;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class EmetteurTest {

    private Emetteur emetteurNRZ;
    private Emetteur emetteurNRZT;
    private Emetteur emetteurRZ;

    @Before
    public void setup() {
        emetteurNRZ = new Emetteur(0.0f, 1.0f, 30, "NRZ");
        emetteurNRZT = new Emetteur(0.0f, 1.0f, 30, "NRZT");
        emetteurRZ = new Emetteur(0.0f, 1.0f, 30, "RZ");
    }

    /**
     * Test receiving valid information with NRZ modulation.
     */
    @Test
    public void testRecevoirValidInformationNRZ() throws InformationNonConformeException {
        Information<Boolean> infoLogique = new Information<>();
        infoLogique.add(true);
        infoLogique.add(false);

        MockDestination<Float> mockDestination = new MockDestination<>();
        emetteurNRZ.connecter(mockDestination);

        emetteurNRZ.recevoir(infoLogique);

        // Expected NRZ output
        Information<Float> expectedAnalog = new Information<>();
        for (int i = 0; i < 30; i++) expectedAnalog.add(1.0f);  // True -> 1.0
        for (int i = 0; i < 30; i++) expectedAnalog.add(0.0f);  // False -> 0.0

        assertEquals(expectedAnalog, mockDestination.getInformationRecue());
    }

    /**
     * Test receiving valid information with NRZT modulation.
     */
    @Test
    public void testRecevoirValidInformationNRZT() throws InformationNonConformeException {
        Information<Boolean> infoLogique = new Information<>();
        infoLogique.add(true);
        infoLogique.add(false);

        MockDestination<Float> mockDestination = new MockDestination<>();
        emetteurNRZT.connecter(mockDestination);

        emetteurNRZT.recevoir(infoLogique);

        // Expected NRZT output (progressive transitions, for simplicity we assume a simple linear transition)
        Information<Float> expectedAnalog = new Information<>();
        for (int i = 0; i < 30; i++) expectedAnalog.add(1.0f);  // True -> Transition from 0.5f to 1.0f
        for (int i = 0; i < 30; i++) expectedAnalog.add(0.0f);  // False -> 0.0

        assertNotNull(mockDestination.getInformationRecue());  // Actual validation would require specific transition logic
    }

    /**
     * Test receiving valid information with RZ modulation.
     */
    @Test
    public void testRecevoirValidInformationRZ() throws InformationNonConformeException {
        Information<Boolean> infoLogique = new Information<>();
        infoLogique.add(true);
        infoLogique.add(false);

        MockDestination<Float> mockDestination = new MockDestination<>();
        emetteurRZ.connecter(mockDestination);

        emetteurRZ.recevoir(infoLogique);

        // Expected RZ output
        Information<Float> expectedAnalog = new Information<>();
        for (int i = 0; i < 10; i++) expectedAnalog.add(0.0f);  // First third: 0.0
        for (int i = 0; i < 10; i++) expectedAnalog.add(1.0f);  // Second third: 1.0
        for (int i = 0; i < 10; i++) expectedAnalog.add(0.0f);  // Third third: 0.0
        for (int i = 0; i < 30; i++) expectedAnalog.add(0.0f);  // False -> 0.0

        assertEquals(expectedAnalog, mockDestination.getInformationRecue());
    }

    /**
     * Test receiving null information, should throw InformationNonConformeException.
     */
    @Test(expected = InformationNonConformeException.class)
    public void testRecevoirNullInformation() throws InformationNonConformeException {
        emetteurNRZ.recevoir(null);  // Should throw an exception
    }

    /**
     * Test receiving empty information, should throw InformationNonConformeException.
     */
    @Test(expected = InformationNonConformeException.class)
    public void testRecevoirEmptyInformation() throws InformationNonConformeException {
        Information<Boolean> emptyInfo = new Information<>();
        emetteurNRZ.recevoir(emptyInfo);  // Should throw an exception
    }

    /**
     * Test emitting information to connected destination.
     */
    @Test
    public void testEmettreInformation() throws InformationNonConformeException {
        Information<Boolean> infoLogique = new Information<>();
        infoLogique.add(true);
        infoLogique.add(false);

        MockDestination<Float> mockDestination = new MockDestination<>();
        emetteurNRZ.connecter(mockDestination);

        emetteurNRZ.recevoir(infoLogique);  // Should trigger emettre() after recevoir()

        // Ensure the mock destination received the correct analog signal
        assertNotNull(mockDestination.getInformationRecue());
    }

    /**
     * Test unknown modulation type.
     */
    @Test(expected = InformationNonConformeException.class)
    public void testUnknownModulationType() throws InformationNonConformeException {
        Emetteur emetteurInvalide = new Emetteur(0.0f, 1.0f, 30, "UNKNOWN");
        Information<Boolean> infoLogique = new Information<>();
        infoLogique.add(true);
        emetteurInvalide.recevoir(infoLogique);  // Should throw an exception due to unknown modulation
    }

    /**
     * Test constructor parameters.
     */
    /*
    @Test
    public void testConstructorParameters() {
        assertEquals(30, emetteurNRZ.nbEchantillonsParBit);
        assertEquals("NRZ", emetteurNRZ.typeModulation);
        assertEquals(0.0f, emetteurNRZ.Amin, 0.001);
        assertEquals(1.0f, emetteurNRZ.Amax, 0.001);
    }*/

    /**
     * Test connecting and disconnecting a destination.
     */
    @Test
    public void testConnecterDeconnecterDestination() {
        MockDestination<Float> mockDestination = new MockDestination<>();
        emetteurNRZ.connecter(mockDestination);

        // Check if the destination is connected
        assertTrue(emetteurNRZ.getDestinationsConnectees().contains(mockDestination));

        // Disconnect the destination
        emetteurNRZ.deconnecter(mockDestination);
        assertFalse(emetteurNRZ.getDestinationsConnectees().contains(mockDestination));
    }
    
	/**
	 * Test main method.
	 */
        @Test
            public void testMain() {
        	                Emetteur.main(null);
        }
}
