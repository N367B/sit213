package transmetteurs;

import information.Information;
import information.InformationNonConformeException;
import destinations.DestinationInterface;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;

public class TransmetteurParfaitTest {

    private TransmetteurParfait transmetteur;

    @Before
    public void setup() {
        transmetteur = new TransmetteurParfait();
    }

    /**
     * Test receiving valid information.
     */
    @Test
    public void testRecevoirValidInformation() throws InformationNonConformeException {
        Information<Boolean> information = new Information<>();
        information.add(true);
        information.add(false);
        information.add(true);
        
        transmetteur.recevoir(information);

        // Check if the received information is stored correctly
        assertEquals(information, transmetteur.getInformationRecue());
    }

    /**
     * Test receiving null information, should throw InformationNonConformeException.
     */
    @Test(expected = InformationNonConformeException.class)
    public void testRecevoirNullInformation() throws InformationNonConformeException {
        transmetteur.recevoir(null);  // Should throw an exception
    }

    /**
     * Test emitting information to connected destinations.
     */
    @Test
    public void testEmettreInformation() throws InformationNonConformeException {
        Information<Boolean> information = new Information<>();
        information.add(true);
        information.add(false);

        // Create a mock destination to receive the information
        MockDestination<Boolean> mockDestination = new MockDestination<>();

        // Connect the mock destination and send the information
        transmetteur.connecter(mockDestination);
        
        // Transmit and emit the information
        transmetteur.recevoir(information);
        transmetteur.emettre();

        // Assert that the destination received the correct information
        assertEquals(information, mockDestination.getInformationRecue());
    }

    /**
     * Mock class implementing DestinationInterface to capture the received information.
     */
    private static class MockDestination<T> implements DestinationInterface<T> {
        private Information<T> receivedInformation;

        @Override
        public void recevoir(Information<T> information) {
            this.receivedInformation = information;
        }

        @Override
        public Information<T> getInformationRecue() {
            return receivedInformation;
        }
    }
}
