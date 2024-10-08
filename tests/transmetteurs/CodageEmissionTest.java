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
     * Test qui recoit des informations valides et verifie si elles sont bien transmises
     */
    @Test
    public void testRecevoirAndEmettreValidInformation() throws InformationNonConformeException {
        // Create a sample bit information
        Information<Boolean> information = new Information<>();
        information.add(true);   // 1
        information.add(false);  // 0


        MockDestination<Boolean> mockDestination = new MockDestination<>();
        codageEmission.connecter(mockDestination);

        //Reception et emission de l'information
        codageEmission.recevoir(information);

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
     * Test qui recoit une information nulle et doit lever l'exception
     */
    @Test(expected = InformationNonConformeException.class)
    public void testRecevoirNullInformation() throws InformationNonConformeException {
        codageEmission.recevoir(null); 
    }

    /**
     * test qui recoit une information null et doit lever l'exception
     */
    @Test(expected = InformationNonConformeException.class)
    public void testRecevoirEmptyInformation() throws InformationNonConformeException {
        Information<Boolean> emptyInformation = new Information<>();
        codageEmission.recevoir(emptyInformation);  
    }

    /**
     * Test qui emet une information null et doit lever une exception
     */
    @Test(expected = InformationNonConformeException.class)
    public void testEmettreNullInformation() throws InformationNonConformeException {
        codageEmission.emettre();  
    }


    /**
     * Test qui recoit une information et emet a plusieurs destinations
     */
    @Test
    public void testRecevoirWithMultipleDestinations() throws InformationNonConformeException {

        Information<Boolean> information = new Information<>();
        information.add(true);   // 1
        information.add(false);  // 0


        MockDestination<Boolean> mockDestination1 = new MockDestination<>();
        MockDestination<Boolean> mockDestination2 = new MockDestination<>();


        codageEmission.connecter(mockDestination1);
        codageEmission.connecter(mockDestination2);

        codageEmission.recevoir(information);


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

    /**
     * Tes de la methode main
     */
    @Test
    public void testMain() {
        CodageEmission.main(new String[]{});
    }
}
