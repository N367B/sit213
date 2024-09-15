package simulateur;

import org.junit.Test;
import static org.junit.Assert.*;
import sources.SourceFixe;

public class SimulateurTest {

    /**
     * Test the arguments analysis with seed and logical transmission chain.
     */
    @Test
    public void testAnalyseArgumentsWithSeedLogical() throws ArgumentsException {
        String[] args = {"-mess", "100", "-seed", "123", "-s"};
        Simulateur simulateur = new Simulateur(args);

        assertTrue(simulateur.isMessageAleatoire());  // Random message
        assertTrue(simulateur.isAleatoireAvecGerme());  // Using seed
        assertEquals((Integer) 123, simulateur.getSeed());  // Check seed value
    }

    /**
     * Test invalid message argument throws ArgumentsException.
     */
    @Test(expected = ArgumentsException.class)
    public void testInvalidMessageArgument() throws ArgumentsException {
        String[] args = {"-mess", "abcd"};  // Invalid message
        new Simulateur(args);  // Should throw ArgumentsException
    }

    /**
     * Test execution of the logical transmission chain with random message.
     */
    @Test
    public void testSimulationExecutionWithRandomMessageLogical() throws Exception {
        String[] args = {"-mess", "100", "-seed", "123", "-s"};  // Random message of 100 bits with seed
        Simulateur simulateur = new Simulateur(args);

        simulateur.execute();  // Should execute without errors

        assertNotNull(simulateur.getSource().getInformationEmise());
        assertNotNull(simulateur.getDestination().getInformationRecue());
    }

    /**
     * Test binary error rate calculation with no errors.
     */
    @Test
    public void testCalculTauxErreurBinaire_NoErrorsLogical() throws Exception {
        String[] args = {"-mess", "111"};  // Fixed message
        Simulateur simulateur = new Simulateur(args);

        simulateur.execute();

        // The message should be transmitted without errors, so the error rate should be 0
        assertEquals(0.0f, simulateur.calculTauxErreurBinaire(), 0.001);
    }

    /**
     * Test binary error rate calculation with errors introduced.
     */
    @Test
    public void testCalculTauxErreurBinaire_WithErrorsLogical() throws Exception {
        String[] args = {"-mess", "0011001100110011"};
        Simulateur simulateur = new Simulateur(args);

        simulateur.execute();
        // Generate errors in the transmission
        simulateur.getDestination().setInformationRecue(new SourceFixe("0011001100111100").getInformationGeneree());

        // The TEB should be greater than 0 if errors are introduced
        assertTrue(simulateur.calculTauxErreurBinaire() > 0);
        // The TEB should be 0.25 if 25% of the bits are incorrect
        assertEquals(0.25f, simulateur.calculTauxErreurBinaire(), 0.001);
    }

    /**
     * Test execution of the analog transmission chain (NRZ modulation).
     */
    @Test
    public void testSimulationExecutionWithAnalogNRZ() throws Exception {
        String[] args = {"-mess", "101", "-form", "NRZ", "-nbEch", "30", "-ampl", "0.0", "1.0"};
        Simulateur simulateur = new Simulateur(args);

        simulateur.execute();  // Should execute without errors

        // Ensure the source emits and the destination receives the information
        assertNotNull(simulateur.getSource().getInformationEmise());
        assertNotNull(simulateur.getDestination().getInformationRecue());
    }

    /**
     * Test execution of the analog transmission chain (NRZT modulation).
     */
    @Test
    public void testSimulationExecutionWithAnalogNRZT() throws Exception {
        String[] args = {"-mess", "110", "-form", "NRZT", "-nbEch", "30", "-ampl", "0.0", "1.0"};
        Simulateur simulateur = new Simulateur(args);

        simulateur.execute();  // Should execute without errors

        assertNotNull(simulateur.getSource().getInformationEmise());
        assertNotNull(simulateur.getDestination().getInformationRecue());
    }

    /**
     * Test execution of the analog transmission chain (RZ modulation).
     */
    @Test
    public void testSimulationExecutionWithAnalogRZ() throws Exception {
        String[] args = {"-mess", "011", "-form", "RZ", "-nbEch", "30", "-ampl", "0.0", "1.0"};
        Simulateur simulateur = new Simulateur(args);

        simulateur.execute();  // Should execute without errors

        assertNotNull(simulateur.getSource().getInformationEmise());
        assertNotNull(simulateur.getDestination().getInformationRecue());
    }

    /**
     * Test that ArgumentsException is thrown for invalid modulation type.
     */
    @Test(expected = ArgumentsException.class)
    public void testInvalidModulationArgument() throws ArgumentsException {
        String[] args = {"-mess", "101", "-form", "XYZ"};  // Invalid modulation type
        new Simulateur(args);  // Should throw ArgumentsException
    }
    @Test
    public void testSimulationExecutionWithSondes() throws Exception {
        String[] args = {"-mess", "101", "-form", "NRZ", "-nbEch", "30", "-ampl", "0.0", "1.0", "-s"};  // Active les sondes
        Simulateur simulateur = new Simulateur(args);
        
        simulateur.execute();  // Devrait exécuter le code avec les sondes connectées
        assertNotNull(simulateur.getSource().getInformationEmise());
        assertNotNull(simulateur.getDestination().getInformationRecue());
    }
    @Test(expected = ArgumentsException.class)
    public void testInvalidSeedArgument() throws ArgumentsException {
        String[] args = {"-mess", "100", "-seed", "abc"};  // Valeur de seed invalide
        new Simulateur(args);  // Devrait lancer ArgumentsException
    }

    @Test(expected = ArgumentsException.class)
    public void testInvalidNbEchArgument() throws ArgumentsException {
        String[] args = {"-mess", "100", "-nbEch", "abc"};  // Valeur de nbEch invalide
        new Simulateur(args);  // Devrait lancer ArgumentsException
    }

    @Test(expected = ArgumentsException.class)
    public void testInvalidAmplArgument() throws ArgumentsException {
        String[] args = {"-mess", "100", "-ampl", "1.0", "abc"};  // Valeur d'ampl invalide
        new Simulateur(args);  // Devrait lancer ArgumentsException
    }

    @Test(expected = ArgumentsException.class)
    public void testAmplAminGreaterThanAmax() throws ArgumentsException {
        String[] args = {"-mess", "100", "-ampl", "1.0", "0.5"};  // Amin >= Amax
        new Simulateur(args);  // Devrait lancer ArgumentsException
    }
    
    @Test(expected = ArgumentsException.class)
	public void testInvalidArguments() throws ArgumentsException {
		String[] args = { "-abc", "100" }; // Arguments invalides
		new Simulateur(args); // Devrait lancer ArgumentsException
	}
    
    @Test(expected = ArgumentsException.class)
	public void testInvalidArgumentsMessage() throws ArgumentsException {
        String[] args = { "-mess", "  " }; // Message invalide
		new Simulateur(args); // Devrait lancer ArgumentsException

    }
    
    @Test(expected = Exception.class)
    public void testCalculTauxErreurBinaire_NoInformationReceived() throws Exception {
        String[] args = {"-mess", "111"};  // Message fixe
        Simulateur simulateur = new Simulateur(args);
        
        simulateur.execute();
        // Simuler un cas où la destination ne reçoit rien
        simulateur.getDestination().setInformationRecue(null);

        // Le taux d'erreur devrait être -1.0 car rien n'a été reçu
        simulateur.calculTauxErreurBinaire();
    }

}
