package simulateur;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import sources.SourceFixe;

public class SimulateurTest {

    private Simulateur simulateur;

    @Before
    public void setUp() {
        simulateur = null;
    }

    /**
     * Test the arguments analysis with seed and logical transmission chain.
     */
    @Test
    public void testAnalyseArgumentsWithSeedLogical() throws ArgumentsException {
        String[] args = {"-mess", "100", "-seed", "123"};
        simulateur = new Simulateur(args);

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
        String[] args = {"-mess", "100", "-seed", "123"};  // Random message of 100 bits with seed
        simulateur = new Simulateur(args);

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
        simulateur = new Simulateur(args);

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
        simulateur = new Simulateur(args);

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
        simulateur = new Simulateur(args);

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
        simulateur = new Simulateur(args);

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
        simulateur = new Simulateur(args);

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

    /**
     * Test execution with sondes enabled.
     */
    @Test
    public void testSimulationExecutionWithSondes() throws Exception {
        String[] args = {"-mess", "101", "-form", "NRZ", "-nbEch", "30", "-ampl", "0.0", "1.0"};  // Active les sondes
        simulateur = new Simulateur(args);
        
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

    /**
     * Test that when SNR is given, the analog transmission chain with noise works.
     */
    @Test
    public void testSimulationExecutionWithAnalogNoise() throws Exception {
        String[] args = {"-mess", "101", "-form", "NRZ", "-nbEch", "30", "-ampl", "0.0", "1.0", "-snrpb", "-10.0"};
        simulateur = new Simulateur(args);

        simulateur.execute();  // Should execute without errors and add noise

        // Ensure the source emits and the destination receives the information
        assertNotNull(simulateur.getSource().getInformationEmise());
        assertNotNull(simulateur.getDestination().getInformationRecue());

        // Calculate the TEB (should be > 0 because of noise)
        float teb = simulateur.calculTauxErreurBinaire();
        assertTrue(teb > 0);
    }

    /**
     * Test for execution when no information is received by the destination (edge case).
     */
    @Test(expected = Exception.class)
    public void testCalculTauxErreurBinaire_NoInformationReceived() throws Exception {
        String[] args = {"-mess", "111"};  // Message fixe
        simulateur = new Simulateur(args);
        
        simulateur.execute();
        // Simuler un cas où la destination ne reçoit rien
        simulateur.getDestination().setInformationRecue(null);

        // Le taux d'erreur devrait être -1.0 car rien n'a été reçu
        simulateur.calculTauxErreurBinaire();
    }

    /**
     * Test for multiple runs with varying SNR.
     */
    @Test
    public void testMultipleRunsWithVaryingSNR() throws Exception {
        for (double snr = -5.0; snr <= -20.0; snr -= 5.0) {
            String[] args = {"-mess", "101", "-form", "NRZ", "-nbEch", "30", "-ampl", "0.0", "1.0", "-snrpb", String.valueOf(snr)};
            simulateur = new Simulateur(args);

            simulateur.execute();  // Should execute without errors

            // Ensure the source emits and the destination receives the information
            assertNotNull(simulateur.getSource().getInformationEmise());
            assertNotNull(simulateur.getDestination().getInformationRecue());

            // Calculate the TEB for each SNR
            float teb = simulateur.calculTauxErreurBinaire();
            assertTrue(teb > 0);  // There should be errors due to noise
        }
    }
    
    /**
     * Test execution of multi-path simulation with delay and attenuation.
     */
    @Test
    public void testSimulationExecutionWithMultiPath() throws Exception {
        String[] args = {"-mess", "101", "-form", "RZ", "-nbEch", "30", "-ampl", "0.0", "1.0", "-ti", "3", "0.5", "5", "0.2"};
        simulateur = new Simulateur(args);

        simulateur.execute();  // Should execute without errors

        assertNotNull(simulateur.getSource().getInformationEmise());
        assertNotNull(simulateur.getDestination().getInformationRecue());
    }

    /**
     * Test execution with invalid multi-path arguments.
     */
    @Test(expected = ArgumentsException.class)
    public void testInvalidMultiPathArguments() throws ArgumentsException {
        String[] args = {"-mess", "101", "-ti", "3", "invalid"};  // Invalid attenuation value
        new Simulateur(args);  // Should throw ArgumentsException
    }

    /**
     * Test simulation with both -snrpb and -snr specified, which should throw an exception.
     */
    @Test(expected = ArgumentsException.class)
    public void testSNRandSNRPBConflict() throws ArgumentsException {
        String[] args = {"-mess", "101", "-form", "NRZ", "-snrpb", "10", "-snr", "10"};
        new Simulateur(args);  // Should throw ArgumentsException
    }

    /**
     * Test simulation with both -snrpb and -snr specified inversed, which should throw an exception.
     */
    @Test(expected = ArgumentsException.class)
    public void testSNRandSNRPBConflict2() throws ArgumentsException {
        String[] args = {"-mess", "101", "-form", "NRZ", "-snr", "10", "-snrpb", "10"};
        new Simulateur(args);  // Should throw ArgumentsException
    }
    
    /**
     * Test simulation with -snrpb specified and SNR calculation performed.
     */
    @Test
    public void testSimulationWithSNRPB() throws Exception {
        String[] args = {"-mess", "101", "-form", "NRZ", "-nbEch", "30", "-ampl", "0.0", "1.0", "-snrpb", "10"};
        simulateur = new Simulateur(args);

        simulateur.execute();  // Should execute without errors

        assertNotNull(simulateur.getSource().getInformationEmise());
        assertNotNull(simulateur.getDestination().getInformationRecue());
    }

    /**
     * Test simulation with -snr specified and SNR calculation performed.
     */
    @Test
    public void testSimulationWithSNR() throws Exception {
        String[] args = {"-mess", "101", "-form", "NRZ", "-nbEch", "30", "-ampl", "0.0", "1.0", "-snr", "10"};
        simulateur = new Simulateur(args);

        simulateur.execute();  // Should execute without errors

        assertNotNull(simulateur.getSource().getInformationEmise());
        assertNotNull(simulateur.getDestination().getInformationRecue());
    }

    /**
     * Test simulation with multi-path (multi-trajet) and SNR.
     */
    @Test
    public void testSimulationWithMultiPathAndSNR() throws Exception {
        String[] args = {"-mess", "101", "-form", "RZ", "-nbEch", "30", "-ampl", "0.0", "1.0", "-snr", "10", "-ti", "3", "0.5", "5", "0.2"};
        simulateur = new Simulateur(args);

        simulateur.execute();  // Should execute without errors

        assertNotNull(simulateur.getSource().getInformationEmise());
        assertNotNull(simulateur.getDestination().getInformationRecue());
    }

    /**
     * Test simulation with multi-path (multi-trajet) and SNRPB.
     */
    @Test
    public void testSimulationWithMultiPathAndSNRPB() throws Exception {
        String[] args = {"-mess", "101", "-form", "RZ", "-nbEch", "30", "-ampl", "0.0", "1.0", "-snrpb", "10", "-ti", "3", "0.5", "5", "0.2"};
        simulateur = new Simulateur(args);

        simulateur.execute();  // Should execute without errors

        assertNotNull(simulateur.getSource().getInformationEmise());
        assertNotNull(simulateur.getDestination().getInformationRecue());
    }

    /**
     * Test simulation with multi-path (multi-trajet) exceeding the maximum allowed paths.
     */
    @Test(expected = ArgumentsException.class)
    public void testTooManyMultiPathArguments() throws ArgumentsException {
        String[] args = {"-mess", "101", "-form", "RZ", "-nbEch", "30", "-ampl", "0.0", "1.0", "-ti", "1", "0.5", "2", "0.5", "3", "0.5", "4", "0.5", "5", "0.5", "6", "0.5"};
        new Simulateur(args);  // Should throw ArgumentsException
    }

    /**
     * Test simulation with sondes and multi-path (multi-trajet).
     */
    @Test
    public void testSimulationWithSondesAndMultiPath() throws Exception {
        String[] args = {"-mess", "101", "-form", "RZ", "-nbEch", "30", "-ampl", "0.0", "1.0", "-ti", "3", "0.5", "5", "0.2"};
        simulateur = new Simulateur(args);

        simulateur.execute();  // Should execute with sondes without errors

        assertNotNull(simulateur.getSource().getInformationEmise());
        assertNotNull(simulateur.getDestination().getInformationRecue());
    }

    @Test(expected = ArgumentsException.class)
    public void testInvalidMessageLength() throws ArgumentsException {
        String[] args = {"-mess", "0"};  // Message length should be >= 1
        new Simulateur(args);  // Should throw ArgumentsException
    }

    @Test(expected = ArgumentsException.class)
    public void testInvalidSNRPBValue() throws ArgumentsException {
        String[] args = {"-mess", "101", "-form", "NRZ", "-snrpb", "invalid"};
        new Simulateur(args);  // Should throw ArgumentsException
    }

    @Test(expected = ArgumentsException.class)
    public void testInvalidSNRValue() throws ArgumentsException {
        String[] args = {"-mess", "101", "-form", "NRZ", "-snr", "invalid"};
        new Simulateur(args);  // Should throw ArgumentsException
    }

    @Test
    public void testSimulationConfigurationError() throws ArgumentsException {
        String[] args = {"-mess", "101"};  // No valid form, snr, snrpb, or multi-path provided
        new Simulateur(args);  // Should throw ArgumentsException
    }

    @Test
    public void testSimulationAnalogiqueBruiteWithSondes() throws Exception {
        String[] args = {"-mess", "101", "-form", "NRZ", "-nbEch", "30", "-ampl", "0.0", "1.0", "-snr", "10"};
        simulateur = new Simulateur(args);

        simulateur.execute();  // Should execute with sondes without errors

        assertNotNull(simulateur.getSource().getInformationEmise());
        assertNotNull(simulateur.getDestination().getInformationRecue());
    }

    @Test
    public void testSimulationMultiTrajetWithSondes() throws Exception {
        String[] args = {"-mess", "101", "-form", "RZ", "-nbEch", "30", "-ampl", "0.0", "1.0", "-ti", "3", "0.5", "5", "0.2"};
        simulateur = new Simulateur(args);

        simulateur.execute();  // Should execute with sondes without errors

        assertNotNull(simulateur.getSource().getInformationEmise());
        assertNotNull(simulateur.getDestination().getInformationRecue());
        
        String[] args1 = {"-mess", "101", "-form", "RZ", "-nbEch", "30", "-ampl", "0.0", "1.0", "-ti", "3", "0.5", "5", "0.2", "-snr", "10"};
        simulateur = new Simulateur(args1);

        simulateur.execute();  // Should execute with sondes without errors

        assertNotNull(simulateur.getSource().getInformationEmise());
        assertNotNull(simulateur.getDestination().getInformationRecue());
    }

    @Test
    public void testSetSnrParBit() throws ArgumentsException {
        String[] args = {"-mess", "101", "-form", "NRZ"};
        simulateur = new Simulateur(args);

        simulateur.setSnrParBit(5.0);
        assertEquals(5.0, simulateur.getSnrParBit(), 0.001);
    }

    @Test
    public void testTEBWithDifferentSNRLevels() throws Exception {
        double[] snrLevels = {-20.0, -10.0, -5.0, 0.1, 10.0};  // Different SNR levels to test
        float previousTeb = 1.0f;
        float margin = 0.05f;  // Allowable margin for TEB comparison
    
        for (double snr : snrLevels) {
            String[] args = {"-mess", "300", "-form", "NRZ", "-nbEch", "30", "-ampl", "0.0", "1.0", "-snr", String.valueOf(snr)};
            simulateur = new Simulateur(args);
    
            simulateur.execute();  // Execute the simulation
            float teb = simulateur.calculTauxErreurBinaire();  // Calculate the TEB
    
            // Check that the TEB decreases as the SNR increases, within a margin
            assertTrue("TEB should decrease as SNR increases", teb <= previousTeb + margin);
            previousTeb = teb;
        }
    }
    
    @Test
    public void testTEBWithDifferentSNRPBLevels() throws Exception {
        double[] snrpbLevels = {-10.0, -5.0, 0.1, 5.0, 10.0};  // Different SNRPB levels to test
        float previousTeb = 1.0f;
        float margin = 0.05f;  // Allowable margin for TEB comparison
    
        for (double snrpb : snrpbLevels) {
            String[] args = {"-mess", "300", "-form", "NRZ", "-nbEch", "30", "-ampl", "0.0", "1.0", "-snrpb", String.valueOf(snrpb)};
            simulateur = new Simulateur(args);
    
            simulateur.execute();  // Execute the simulation
            float teb = simulateur.calculTauxErreurBinaire();  // Calculate the TEB
    
            // Check that the TEB decreases as the SNRPB increases, within a margin
            assertTrue("TEB should decrease as SNRPB increases", teb <= previousTeb + margin);
            previousTeb = teb;
        }
    }
    
    @Test
    public void testTEBWithMultiPathAndSNR() throws Exception {
        double[] snrLevels = {-20.0, -10.0, -5.0, 0.1, 10.0};  // Different SNR levels to test
        float previousTeb = 1.0f;
        float margin = 0.05f;  // Allowable margin for TEB comparison
    
        for (double snr : snrLevels) {
            String[] args = {"-mess", "300", "-form", "NRZ", "-nbEch", "30", "-ampl", "0.0", "1.0", "-snr", String.valueOf(snr), "-ti", "3", "0.5", "5", "0.2"};
            simulateur = new Simulateur(args);
    
            simulateur.execute();  // Execute the simulation
            float teb = simulateur.calculTauxErreurBinaire();  // Calculate the TEB
    
            // Check that the TEB decreases as the SNR increases, even with multi-path, within a margin
            assertTrue("TEB should decrease as SNR increases", teb <= previousTeb + margin);
            previousTeb = teb;
        }
    }
    
    /**
     * Check if the calculated TEB for a given SNR and modulation type matches the expected theoretical TEB.
     */
    @Test
    public void testCalculatedTEBMatchesTheoreticalTEBWithSNR() throws Exception {
        double snr = -10.0;
        float expectedTheoreticalTEB = 0.12f; 
        float margin = 0.05f;
        String[] args = {"-mess", "300", "-snr", String.valueOf(snr)};
        simulateur = new Simulateur(args);

        simulateur.execute();  // Execute the simulation
        float calculatedTEB = simulateur.calculTauxErreurBinaire();  // Calculate the TEB

        // Check that the calculated TEB is within a margin of the theoretical TEB
        assertEquals("TEB should match the theoretical value within a margin", expectedTheoreticalTEB, calculatedTEB, margin);
    }

    /**
     * Check if the calculated TEB for a given SNRPB and modulation type matches the expected theoretical TEB.
     */
    @Test
    public void testCalculatedTEBMatchesTheoreticalTEBWithSNRPB() throws Exception {
        double snrpb = 5.0;
        float expectedTheoreticalTEB = 0.03f;
        float margin = 0.04f;

        String[] args = {"-mess", "300", "-snrpb", String.valueOf(snrpb)};
        simulateur = new Simulateur(args);

        simulateur.execute();  // Execute the simulation
        float calculatedTEB = simulateur.calculTauxErreurBinaire();  // Calculate the TEB

        // Check that the calculated TEB is within a margin of the theoretical TEB
        assertEquals("TEB should match the theoretical value within a margin", expectedTheoreticalTEB, calculatedTEB, margin);
    }

      /**
     * Check if the calculated TEB for multi-path simulation with complex multi-trajet (without noise) matches the expected theoretical TEB.
     */
    @Test
    public void testCalculatedTEBMatchesTheoreticalTEBWithComplexMultiPath() throws Exception {
        float expectedTheoreticalTEB = 0.24f;  

        String[] args = {"-mess", "300", "-ti", "35", "0.7", "25", "0.4", "15", "0.3"};
        simulateur = new Simulateur(args);
        float margin = 0.05f;

        simulateur.execute();  // Execute the simulation
        float calculatedTEB = simulateur.calculTauxErreurBinaire();  // Calculate the TEB

        // Check that the calculated TEB is within a margin of the theoretical TEB for complex multi-path
        assertEquals("TEB should match the theoretical value for complex multi-path within a margin", expectedTheoreticalTEB, calculatedTEB, margin);
    }


    /**
     * TODO : ADD SIMILAR TESTS FOR THE CODEUR AND DECODEUR.
     */
}
