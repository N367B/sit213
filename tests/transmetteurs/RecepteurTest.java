package transmetteurs;

import destinations.MockDestination;
import information.Information;
import information.InformationNonConformeException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RecepteurTest {

    private Recepteur recepteur;
    private float Amax = 1.0f;
    private float Amin = 0.0f;
    private int nbEchantillonsParBit = 30;
    private String typeModulation = "NRZ";

    @Before
    public void setup() {
        recepteur = new Recepteur(Amax, Amin, nbEchantillonsParBit, typeModulation);
    }

    /**
     * Test receiving valid NRZ analog information and check if it's demodulated correctly.
     */
    @Test
    public void testRecevoirAndEmettreNRZ() throws InformationNonConformeException {
        // Create a sample analog signal for NRZ
        Information<Float> infoAnalogique = new Information<>();
        for (int i = 0; i < nbEchantillonsParBit; i++) {
            infoAnalogique.add(Amax); // bit 1
        }
        for (int i = 0; i < nbEchantillonsParBit; i++) {
            infoAnalogique.add(Amin); // bit 0
        }

        // Create a mock destination to receive the demodulated information
        MockDestination<Boolean> mockDestination = new MockDestination<>();
        recepteur.connecter(mockDestination);

        // Receive and emit the information
        recepteur.recevoir(infoAnalogique);

        // Expected demodulated information
        Information<Boolean> expectedInformation = new Information<>();
        expectedInformation.add(true);  // 1
        expectedInformation.add(false); // 0

        // Verify that the demodulated information matches the expected output
        assertEquals(expectedInformation, mockDestination.getInformationRecue());
    }

    /**
     * Test receiving valid RZ analog information and check if it's demodulated correctly.
     */
    @Test
    public void testRecevoirAndEmettreRZ() throws InformationNonConformeException {
        recepteur = new Recepteur(Amax, Amin, nbEchantillonsParBit, "RZ");

        // Create a sample RZ signal
        Information<Float> infoAnalogique = new Information<>();
        for (int i = 0; i < nbEchantillonsParBit / 3; i++) {
            infoAnalogique.add(0.0f);   // 0 in the first third
        }
        for (int i = 0; i < nbEchantillonsParBit / 3; i++) {
            infoAnalogique.add(Amax);   // bit 1 amplitude
        }
        for (int i = 0; i < nbEchantillonsParBit / 3; i++) {
            infoAnalogique.add(0.0f);   // 0 in the third third

        }
        for (int i = 0; i < nbEchantillonsParBit; i++) {
            infoAnalogique.add(Amin);   // bit 0

        }

        // Create a mock destination
        MockDestination<Boolean> mockDestination = new MockDestination<>();
        recepteur.connecter(mockDestination);

        // Receive and emit the information
        recepteur.recevoir(infoAnalogique);

        // Expected demodulated information
        Information<Boolean> expectedInformation = new Information<>();
        expectedInformation.add(true);  // 1
        expectedInformation.add(false); // 0

        // Verify the output
        assertEquals(expectedInformation, mockDestination.getInformationRecue());
    }

    /**
     * Test receiving invalid null information, which should throw InformationNonConformeException.
     */
    @Test(expected = InformationNonConformeException.class)
    public void testRecevoirNullInformation() throws InformationNonConformeException {
        recepteur.recevoir(null);  // Should throw an exception
    }

    /**
     * Test receiving empty information, which should throw InformationNonConformeException.
     */
    @Test(expected = InformationNonConformeException.class)
    public void testRecevoirEmptyInformation() throws InformationNonConformeException {
        Information<Float> emptyInformation = new Information<>();
        recepteur.recevoir(emptyInformation);  // Should throw an exception
    }

    /**
     * Test emitting information when no destinations are connected.
     */
    @Test
    public void testEmettreWithoutDestination() throws InformationNonConformeException {
        // Create a sample analog signal
        Information<Float> infoAnalogique = new Information<>();
        for (int i = 0; i < nbEchantillonsParBit; i++) {
            infoAnalogique.add(Amax);
        }

        // Receive and emit the information (no destinations are connected)
        recepteur.recevoir(infoAnalogique);

        // No exception should be thrown, and no destinations should receive the signal
        assertTrue(recepteur.getDestinationsConnectees().isEmpty());
    }

    /**
     * Test emitting information with NRZT modulation.
     */
    @Test
    public void testRecevoirAndEmettreNRZT() throws InformationNonConformeException {
        recepteur = new Recepteur(Amax, Amin, nbEchantillonsParBit, "NRZT");

        // Create a sample NRZT signal (bit transitions)
        Information<Float> infoAnalogique = new Information<>();
        for (int i = 0; i < nbEchantillonsParBit; i++) {
            infoAnalogique.add(Amax); // bit 1
        }
        for (int i = 0; i < nbEchantillonsParBit; i++) {
            infoAnalogique.add(Amin); // bit 0
        }

        // Create a mock destination
        MockDestination<Boolean> mockDestination = new MockDestination<>();
        recepteur.connecter(mockDestination);

        // Receive and emit the information
        recepteur.recevoir(infoAnalogique);

        // Expected demodulated information
        Information<Boolean> expectedInformation = new Information<>();
        expectedInformation.add(true);  // 1
        expectedInformation.add(false); // 0

        // Verify the output
        assertEquals(expectedInformation, mockDestination.getInformationRecue());
    }
	/**
	 * Test main method.
	 */
        @Test
            public void testMain() {
        	                Recepteur.main(null);
        }
}
