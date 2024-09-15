package sources;

import org.junit.Test;
import static org.junit.Assert.*;

import information.Information;

public class SourceAleatoireTest {

    /**
     * Test the constructor that generates a list of random booleans without a seed.
     */
    @Test
    public void testRandomGenerationWithoutSeed() {
        int tailleListe = 10;
        SourceAleatoire sourceAleatoire = new SourceAleatoire(tailleListe);

        // Ensure the list is the correct size
        Information<Boolean> informationGeneree = sourceAleatoire.getInformationGeneree();
        assertNotNull(informationGeneree);
        assertEquals(tailleListe, informationGeneree.nbElements());

        // Verify that the values are booleans (true/false) - since they're random, we can't assert exact values
        for (Boolean value : informationGeneree) {
            assertNotNull(value);
        }
    }

    /**
     * Test the constructor that generates a list of random booleans with a specific seed.
     */
    @Test
    public void testRandomGenerationWithSeed() {
        int tailleListe = 10;
        int seed = 1234;
        SourceAleatoire sourceAleatoire1 = new SourceAleatoire(tailleListe, seed);
        SourceAleatoire sourceAleatoire2 = new SourceAleatoire(tailleListe, seed);

        // Ensure the lists are the same when using the same seed
        Information<Boolean> informationGeneree1 = sourceAleatoire1.getInformationGeneree();
        Information<Boolean> informationGeneree2 = sourceAleatoire2.getInformationGeneree();

        assertNotNull(informationGeneree1);
        assertNotNull(informationGeneree2);
        assertEquals(tailleListe, informationGeneree1.nbElements());
        assertEquals(tailleListe, informationGeneree2.nbElements());

        // Check that both lists have the same values since they use the same seed
        for (int i = 0; i < tailleListe; i++) {
            assertEquals(informationGeneree1.iemeElement(i), informationGeneree2.iemeElement(i));
        }
    }

    /**
     * Test the constructor with null seed.
     */
    @Test
    public void testRandomGenerationWithNullSeed() {
        int tailleListe = 10;
        SourceAleatoire sourceAleatoire1 = new SourceAleatoire(tailleListe, null);
        SourceAleatoire sourceAleatoire2 = new SourceAleatoire(tailleListe, null);

        // Ensure the lists are the correct size
        Information<Boolean> informationGeneree1 = sourceAleatoire1.getInformationGeneree();
        Information<Boolean> informationGeneree2 = sourceAleatoire2.getInformationGeneree();

        assertNotNull(informationGeneree1);
        assertNotNull(informationGeneree2);
        assertEquals(tailleListe, informationGeneree1.nbElements());
        assertEquals(tailleListe, informationGeneree2.nbElements());

        // Lists should not be equal because they use different seeds (null seed generates different random values)
        boolean areListsEqual = true;
        for (int i = 0; i < tailleListe; i++) {
            if (!informationGeneree1.iemeElement(i).equals(informationGeneree2.iemeElement(i))) {
                areListsEqual = false;
                break;
            }
        }

        assertFalse("The lists should not be equal since they are generated with different random seeds.", areListsEqual);
    }

    /**
     * Test invalid case where tailleListe is 0.
     */
    @Test
    public void testZeroLength() {
        SourceAleatoire sourceAleatoire = new SourceAleatoire(0);
        Information<Boolean> informationGeneree = sourceAleatoire.getInformationGeneree();
        assertNotNull(informationGeneree);
        assertEquals(0, informationGeneree.nbElements());
    }
	/**
	 * Test main method.
	 */
        @Test
            public void testMain() {
        		SourceAleatoire.main(null);
        }
}
