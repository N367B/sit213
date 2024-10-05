package transmetteurs;

import destinations.MockDestination;
import information.Information;
import information.InformationNonConformeException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CodageEmissionTest {

    private CodageEmission codageEmission;

    @Before
    public void setup() {
        codageEmission = new CodageEmission();
    }

    /**
     * Test receiving valid information and verify it is emitted correctly.
     */
    @Test
    public void testRecevoirAndEmettreValidInformation() throws InformationNonConformeException {
        // Create a sample bit information
        Information<Boolean> information = new Information<>();
        information.add(true);   // 1
        information.add(false);  // 0

        // Create a mock destination to receive the information
        MockDestination<Boolean> mockDestination = new MockDestination<>();
        codageEmission.connecter(mockDestination);

        // Receive and emit the information
        codageEmission.recevoir(information);

        // Verify that the information was correctly transmitted
        Information<Boolean> expectedInformation = new Information<>();
        expectedInformation.add(true);
        expectedInformation.add(false);
        expectedInformation.add(true);
        expectedInformation.add(false);
        expectedInformation.add(true);
        expectedInformation.add(false);

        assertEquals(expectedInformation, mockDestination.getInformationRecue());
    }

    /**
     * Test receiving null information, which should throw InformationNonConformeException.
     */
    @Test(expected = InformationNonConformeException.class)
    public void testRecevoirNullInformation() throws InformationNonConformeException {
        codageEmission.recevoir(null);  // Should throw an exception
    }

    /**
     * Test receiving empty information, which should throw InformationNonConformeException.
     */
    @Test(expected = InformationNonConformeException.class)
    public void testRecevoirEmptyInformation() throws InformationNonConformeException {
        Information<Boolean> emptyInformation = new Information<>();
        codageEmission.recevoir(emptyInformation);  // Should throw an exception
    }

    /**
     * Test emitting null information, which should throw InformationNonConformeException.
     */
    @Test(expected = InformationNonConformeException.class)
    public void testEmettreNullInformation() throws InformationNonConformeException {
        codageEmission.emettre();  // Should throw an exception because informationRecue is null
    }

    /**
     * Test connecting and disconnecting a destination.
     */
    @Test
    public void testConnecterDeconnecterDestination() {
        MockDestination<Boolean> mockDestination = new MockDestination<>();
        codageEmission.connecter(mockDestination);

        // Check if the destination is connected
        assertTrue(codageEmission.getDestinationsConnectees().contains(mockDestination));

        // Disconnect the destination
        codageEmission.deconnecter(mockDestination);
        assertFalse(codageEmission.getDestinationsConnectees().contains(mockDestination));
    }

    /**
     * Test receiving information with multiple destinations.
     */
    @Test
    public void testRecevoirWithMultipleDestinations() throws InformationNonConformeException {
        // Create a sample bit information
        Information<Boolean> information = new Information<>();
        information.add(true);   // 1
        information.add(false);  // 0

        // Create mock destinations
        MockDestination<Boolean> mockDestination1 = new MockDestination<>();
        MockDestination<Boolean> mockDestination2 = new MockDestination<>();

        // Connect multiple destinations
        codageEmission.connecter(mockDestination1);
        codageEmission.connecter(mockDestination2);

        // Receive and emit the information
        codageEmission.recevoir(information);

        // Verify that both destinations received the same information
        Information<Boolean> expectedInformation = new Information<>();
        expectedInformation.add(true);
        expectedInformation.add(false);
        expectedInformation.add(true);
        expectedInformation.add(false);
        expectedInformation.add(true);
        expectedInformation.add(false);

        assertEquals(expectedInformation, mockDestination1.getInformationRecue());
        assertEquals(expectedInformation, mockDestination2.getInformationRecue());
    }
}
