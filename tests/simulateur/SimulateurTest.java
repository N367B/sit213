package simulateur;

import org.junit.Test;
import static org.junit.Assert.*;
import sources.SourceAleatoire;
import destinations.DestinationFinale;

public class SimulateurTest {

    @Test
    public void testAnalyseArgumentsWithSeed() throws ArgumentsException {
        String[] args = {"-mess", "100", "-seed", "123", "-s"};
        Simulateur simulateur = new Simulateur(args);

        // Use getter methods to access private fields
        assertTrue(simulateur.isMessageAleatoire());  // Random message
        assertTrue(simulateur.isAleatoireAvecGerme());  // Using seed
        assertEquals((Integer) 123, simulateur.getSeed());  // Check seed value
    }

    @Test(expected = ArgumentsException.class)
    public void testInvalidMessageArgument() throws ArgumentsException {
        String[] args = {"-mess", "abcd"};  // Invalid message
        new Simulateur(args);  // Should throw ArgumentsException
    }

    @Test
    public void testSimulationExecutionWithRandomMessage() throws Exception {
        String[] args = {"-mess", "100", "-seed", "123"};  // Random message of 100 bits with seed
        Simulateur simulateur = new Simulateur(args);
        
        simulateur.execute();  // Should execute without errors

        // Use getter methods to check if information has been emitted and received
        assertNotNull(simulateur.getSource().getInformationEmise());
        assertNotNull(simulateur.getDestination().getInformationRecue());
    }

    @Test
    public void testCalculTauxErreurBinaire_NoErrors() throws Exception {
        String[] args = {"-mess", "111"};  // Fixed message
        Simulateur simulateur = new Simulateur(args);
        
        simulateur.execute();
        
        // The message should be transmitted without errors, so the error rate should be 0
        assertEquals(0.0f, simulateur.calculTauxErreurBinaire(), 0.001);
    }

    @Test
    public void testCalculTauxErreurBinaire_WithErrors() throws Exception {
        String[] args = {"-mess", "3", "-seed", "1"};
        Simulateur simulateur = new Simulateur(args);

        simulateur.execute();  // Randomly generated messages should have errors

        // The TEB should be greater than 0 if errors are introduced
        assertTrue(simulateur.calculTauxErreurBinaire() > 0);
    }
}
