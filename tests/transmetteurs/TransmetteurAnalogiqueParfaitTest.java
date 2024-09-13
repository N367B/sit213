package transmetteurs;

import destinations.MockDestination;
import information.Information;
import information.InformationNonConformeException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TransmetteurAnalogiqueParfaitTest {

    private TransmetteurAnalogiqueParfait transmetteurAnalogique;

    @Before
    public void setup() {
        transmetteurAnalogique = new TransmetteurAnalogiqueParfait();
    }

    /**
     * Test receiving valid information and verify it is emitted correctly.
     */
    @Test
    public void testRecevoirAndEmettreValidInformation() throws InformationNonConformeException {
        // Create a sample analog signal (float values)
        Information<Float> informationAnalogique = new Information<>();
        informationAnalogique.add(1.0f);
        informationAnalogique.add(0.5f);
        informationAnalogique.add(0.0f);

        // Create a mock destination to receive the information
        MockDestination<Float> mockDestination = new MockDestination<>();
        transmetteurAnalogique.connecter(mockDestination);

        // Receive and emit the information
        transmetteurAnalogique.recevoir(informationAnalogique);

        // Verify that the information was correctly transmitted
        assertEquals(informationAnalogique, mockDestination.getInformationRecue());
    }

    /**
     * Test receiving null information, which should throw InformationNonConformeException.
     */
    @Test(expected = InformationNonConformeException.class)
    public void testRecevoirNullInformation() throws InformationNonConformeException {
        transmetteurAnalogique.recevoir(null);  // Should throw an exception
    }

    /**
     * Test receiving empty information, which should throw InformationNonConformeException.
     */
    @Test(expected = InformationNonConformeException.class)
    public void testRecevoirEmptyInformation() throws InformationNonConformeException {
        Information<Float> emptyInformation = new Information<>();
        transmetteurAnalogique.recevoir(emptyInformation);  // Should throw an exception
    }

    /**
     * Test emitting information when no destinations are connected.
     */
    @Test
    public void testEmettreWithoutDestination() throws InformationNonConformeException {
        // Create a sample analog signal
        Information<Float> informationAnalogique = new Information<>();
        informationAnalogique.add(1.0f);
        informationAnalogique.add(0.0f);

        // Receive and emit the information (no destinations are connected)
        transmetteurAnalogique.recevoir(informationAnalogique);

        // No exception should be thrown, and no destinations should receive the signal
        assertTrue(transmetteurAnalogique.getDestinationsConnectees().isEmpty());
    }

    /**
     * Test emitting null information, which should throw InformationNonConformeException.
     */
    @Test(expected = InformationNonConformeException.class)
    public void testEmettreNullInformation() throws InformationNonConformeException {
        transmetteurAnalogique.emettre();  // Should throw an exception because informationRecue is null
    }

    /**
     * Test connecting and disconnecting a destination.
     */
    @Test
    public void testConnecterDeconnecterDestination() {
        MockDestination<Float> mockDestination = new MockDestination<>();
        transmetteurAnalogique.connecter(mockDestination);

        // Check if the destination is connected
        assertTrue(transmetteurAnalogique.getDestinationsConnectees().contains(mockDestination));

        // Disconnect the destination
        transmetteurAnalogique.deconnecter(mockDestination);
        assertFalse(transmetteurAnalogique.getDestinationsConnectees().contains(mockDestination));
    }

    /**
     * Test receiving information with multiple destinations.
     */
    @Test
    public void testRecevoirWithMultipleDestinations() throws InformationNonConformeException {
        // Create a sample analog signal
        Information<Float> informationAnalogique = new Information<>();
        informationAnalogique.add(1.0f);
        informationAnalogique.add(0.5f);

        // Create mock destinations
        MockDestination<Float> mockDestination1 = new MockDestination<>();
        MockDestination<Float> mockDestination2 = new MockDestination<>();

        // Connect multiple destinations
        transmetteurAnalogique.connecter(mockDestination1);
        transmetteurAnalogique.connecter(mockDestination2);

        // Receive and emit the information
        transmetteurAnalogique.recevoir(informationAnalogique);

        // Verify that both destinations received the same information
        assertEquals(informationAnalogique, mockDestination1.getInformationRecue());
        assertEquals(informationAnalogique, mockDestination2.getInformationRecue());
    }
}
