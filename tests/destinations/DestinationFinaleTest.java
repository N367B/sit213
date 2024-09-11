package destinations;

import information.Information;
import information.InformationNonConformeException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class DestinationFinaleTest {

    private DestinationFinale destination;

    @Before
    public void setup() {
        destination = new DestinationFinale();
    }

    /**
     * Test receiving valid information.
     */
    @Test
    public void testRecevoirValidInformation() throws InformationNonConformeException {
        Information<Boolean> information = new Information<>();
        information.add(true);
        information.add(false);

        destination.recevoir(information);

        // Verify that the information was stored correctly
        assertEquals(information, destination.getInformationRecue());
    }

    /**
     * Test receiving null information, should throw InformationNonConformeException.
     */
    @Test(expected = InformationNonConformeException.class)
    public void testRecevoirNullInformation() throws InformationNonConformeException {
        destination.recevoir(null);  // Should throw InformationNonConformeException
    }

    /**
     * Test receiving empty information, should throw InformationNonConformeException.
     */
    @Test(expected = InformationNonConformeException.class)
    public void testRecevoirEmptyInformation() throws InformationNonConformeException {
        Information<Boolean> emptyInformation = new Information<>();

        destination.recevoir(emptyInformation);  // Should throw InformationNonConformeException
    }
}
