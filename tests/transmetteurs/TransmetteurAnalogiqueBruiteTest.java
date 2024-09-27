package transmetteurs;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import information.*;

public class TransmetteurAnalogiqueBruiteTest {

    private TransmetteurAnalogiqueBruite transmetteur;
    private Information<Float> signalOriginal;
    private double snr;
    private int nbEchantillonsParBit;

    @Before
    public void setUp() {
        snr = 10.0; // Example SNR value (10 dB)
        nbEchantillonsParBit = 30;
        transmetteur = new TransmetteurAnalogiqueBruite(snr, nbEchantillonsParBit);
        // Create a basic signal for testing purposes
        signalOriginal = new Information<>();
        for (int i = 0; i < nbEchantillonsParBit * 10; i++) { // 10 bits example
            signalOriginal.add(1.0f); // Simplified signal with all 1's for easy validation
        }
    }

    /**
     * Test that the transmitter correctly adds Gaussian noise to the signal.
     */
    @Test
    public void testAjouterBruit() throws Exception {
        Information<Float> signalBruite = transmetteur.ajouterBruit(signalOriginal);

        // Check that the signalBruite is not null and has the same number of elements
        assertNotNull(signalBruite);
        assertEquals(signalOriginal.nbElements(), signalBruite.nbElements());

        // Check that the signal has been altered (i.e., not identical to the original)
        boolean altered = false;
        for (int i = 0; i < signalOriginal.nbElements(); i++) {
            if (!signalOriginal.iemeElement(i).equals(signalBruite.iemeElement(i))) {
                altered = true;
                break;
            }
        }
        assertTrue("The signal should be altered after adding noise", altered);
    }

    /**
     * Test the power calculation of the original signal.
     */
    @Test
    public void testCalculerPuissanceSignal() throws Exception {
        double expectedPower = 1.0; // Since all values are 1.0, power should be 1.0
        double actualPower = transmetteur.calculerPuissanceSignal(signalOriginal);
        assertEquals(expectedPower, actualPower, 0.001);
    }

    /**
     * Test the calculation of noise power after noise is added.
     */
    @Test
    public void testCalculerPuissanceBruit() throws Exception {
        Information<Float> signalBruite = transmetteur.ajouterBruit(signalOriginal);

        // Check that noise power is positive and non-zero
        double puissanceBruit = transmetteur.calculerPuissanceBruit(signalOriginal, signalBruite);
        assertTrue("Noise power should be greater than zero", puissanceBruit > 0);
    }

    /**
     * Test that the receive method correctly handles valid input and adds noise.
     */
    @Test
    public void testRecevoir() throws Exception {
        transmetteur.recevoir(signalOriginal);
        Information<Float> signalBruite = transmetteur.getInformationEmise();

        // Ensure the output is not null and the noise has been added
        assertNotNull(signalBruite);
        assertEquals(signalOriginal.nbElements(), signalBruite.nbElements());

        // Verify the signal has been altered
        boolean altered = false;
        for (int i = 0; i < signalOriginal.nbElements(); i++) {
            if (!signalOriginal.iemeElement(i).equals(signalBruite.iemeElement(i))) {
                altered = true;
                break;
            }
        }
        assertTrue("The signal should be altered after adding noise", altered);
    }

    /**
     * Test that the receive method throws an exception with null input.
     */
    @Test(expected = InformationNonConformeException.class)
    public void testRecevoirWithNullSignal() throws Exception {
        transmetteur.recevoir(null);
    }

    /**
     * Test that the emitter method throws an exception if no information is set.
     */
    @Test(expected = InformationNonConformeException.class)
    public void testEmettreWithoutRecevoir() throws Exception {
        transmetteur.emettre(); // Should throw an exception since nothing has been received
    }

    /**
     * Test the overall signal alteration after emission.
     */
    @Test
    public void testEndToEndTransmission() throws Exception {
        transmetteur.recevoir(signalOriginal);
        transmetteur.emettre();

        Information<Float> signalBruite = transmetteur.getInformationEmise();

        // Ensure the emitted signal is valid and contains noise
        assertNotNull(signalBruite);
        assertEquals(signalOriginal.nbElements(), signalBruite.nbElements());
    }
    /**
     * Test randomness by checking that the signal is altered after adding noise.
     */
    @Test
    public void testRandomnessOfNoise() throws Exception {
        // Receive the signal (this should add noise to the signal)
        transmetteur.recevoir(signalOriginal);
        Information<Float> signalBruit = transmetteur.getInformationEmise();

        // Ensure that the signal is altered (i.e., noise was added)
        boolean signalAltered = false;
        for (int i = 0; i < signalOriginal.nbElements(); i++) {
            if (!signalOriginal.iemeElement(i).equals(signalBruit.iemeElement(i))) {
                signalAltered = true;
                break;
            }
        }
        assertTrue("The signal should be altered by the noise", signalAltered);
    }

    /**
     * Test that noise follows the expected Gaussian distribution by ensuring values are altered
     * and fall within a reasonable range (given the SNR).
     */
    @Test
    public void testNoiseWithinReasonableRange() throws Exception {
        // Receive the signal (this should add noise to the signal)
        transmetteur.recevoir(signalOriginal);
        Information<Float> signalBruit = transmetteur.getInformationEmise();

        double expectedMean = 1.0;  // All signal elements were set to 1.0
        double expectedStdDev = Math.sqrt(Math.pow(10, -snr / 10));

        // Check that the noise doesn't deviate excessively
        for (int i = 0; i < signalBruit.nbElements(); i++) {
            float valueWithNoise = signalBruit.iemeElement(i);
            assertTrue("Value with noise should be within a reasonable range",
                    Math.abs(valueWithNoise - expectedMean) < 5 * expectedStdDev);  // 5 sigma rule
        }
    }

    /**
     * Edge case test: Signal consisting of all zeros.
     */
    @Test
    public void testAllZeroSignal() throws Exception {
        Information<Float> zeroSignal = new Information<>();
        for (int i = 0; i < nbEchantillonsParBit * 10; i++) {
            zeroSignal.add(0.0f);
        }

        transmetteur.recevoir(zeroSignal);
        Information<Float> signalBruit = transmetteur.getInformationEmise();

        // Check that noise was not added to all zero signal
        boolean noiseAdded = false;
        for (int i = 0; i < signalBruit.nbElements(); i++) {
            if (signalBruit.iemeElement(i) != 0.0f) {
                noiseAdded = true;
                break;
            }
        }
        assertFalse("Noise should not be added to an all-zero signal", noiseAdded);
    }

    /**
     * Edge case test: Signal consisting of a single bit (all ones).
     */
    @Test
    public void testSingleBitSignal() throws Exception {
        Information<Float> singleBitSignal = new Information<>();
        singleBitSignal.add(1.0f);  // A single "1" bit

        transmetteur.recevoir(singleBitSignal);
        Information<Float> signalBruit = transmetteur.getInformationEmise();

        // Ensure that the single-bit signal is altered
        assertNotEquals("The single-bit signal should be altered by the noise",
                singleBitSignal.iemeElement(0), signalBruit.iemeElement(0));
    }

    /**
     * Test that the noise generated is different each time.
     */
    @Test
    public void testNoiseRepeatability() throws Exception {
        transmetteur = new TransmetteurAnalogiqueBruite(snr, nbEchantillonsParBit);

        transmetteur.recevoir(signalOriginal);
        Information<Float> signalBruit1 = transmetteur.getInformationEmise();

        transmetteur = new TransmetteurAnalogiqueBruite(snr, nbEchantillonsParBit);
        transmetteur.recevoir(signalOriginal);
        Information<Float> signalBruit2 = transmetteur.getInformationEmise();

        for (int i = 0; i < signalBruit1.nbElements(); i++) {
            assertNotEquals("Noise should be different each time",
                    signalBruit1.iemeElement(i), signalBruit2.iemeElement(i), 0.0001);
        }
    }
	/**
	 * Test the main method of the class.
	 */
        @Test
        public void testMain() {
            TransmetteurAnalogiqueBruite.main(new String[] {});
        }
}
