package simulateur;

import org.junit.Before;
import org.junit.Test;
import java.io.File;
import static org.junit.Assert.*;

public class SimulateurTEBTest {

    private SimulateurTEB simTEBNRZ;
    private SimulateurTEB simTEBNRZT;
    private SimulateurTEB simTEBRZ;

    @Before
    public void setUp() {
        // Set up the simulators with 5 simulations per SNR
        simTEBNRZ = new SimulateurTEB("NRZ", 5);
        simTEBNRZT = new SimulateurTEB("NRZT", 5);
        simTEBRZ = new SimulateurTEB("RZ", 5);
    }

    @Test
    public void testGenererCourbeTEB_NRZ() throws Exception {
        String csvFilePath = "test_results_NRZ.csv";
        Double snrMin = -10.0;
        Double snrMax = 10.0;
        Double pasSNR = 5.0;

        // Generate TEB curve for NRZ and write to a test CSV file
        simTEBNRZ.genererCourbeTEB(snrMin, snrMax, pasSNR, csvFilePath, 100);

        // Assert the file was created
        File csvFile = new File(csvFilePath);
        assertTrue("The CSV file for NRZ modulation was not created", csvFile.exists());

        // You can also verify the file contents if needed, e.g., by reading the file
        // and ensuring expected results are present
        csvFile.delete();
    }

    @Test
    public void testGenererCourbeTEB_NRZT() throws Exception {
        String csvFilePath = "test_results_NRZT.csv";
        Double snrMin = -10.0;
        Double snrMax = 10.0;
        Double pasSNR = 5.0;

        // Generate TEB curve for NRZT and write to a test CSV file
        simTEBNRZT.genererCourbeTEB(snrMin, snrMax, pasSNR, csvFilePath, 100);

        // Assert the file was created
        File csvFile = new File(csvFilePath);
        assertTrue("The CSV file for NRZT modulation was not created", csvFile.exists());

        // Clean up test file
        csvFile.delete();
    }

    @Test
    public void testGenererCourbeTEB_RZ() throws Exception {
        String csvFilePath = "test_results_RZ.csv";
        Double snrMin = -10.0;
        Double snrMax = 10.0;
        Double pasSNR = 5.0;

        // Generate TEB curve for RZ and write to a test CSV file
        simTEBRZ.genererCourbeTEB(snrMin, snrMax, pasSNR, csvFilePath, 100);

        // Assert the file was created
        File csvFile = new File(csvFilePath);
        assertTrue("The CSV file for RZ modulation was not created", csvFile.exists());

        // Clean up test file
        csvFile.delete();
    }

    @Test
    public void testSimulationsWithDifferentSNR() throws Exception {
        String csvFilePath = "test_results_NRZ_varying_snr.csv";
        Double snrMin = -20.0;
        Double snrMax = 20.0;
        Double pasSNR = 10.0;

        // Generate TEB curve with varying SNR for NRZ and write to a test CSV file
        simTEBNRZ.genererCourbeTEB(snrMin, snrMax, pasSNR, csvFilePath, 100);

        // Assert that the results are different for different SNR values
        // This could involve parsing the CSV and comparing TEB values at different SNRs
        File csvFile = new File(csvFilePath);
        assertTrue("The CSV file for varying SNR was not created", csvFile.exists());

        // Clean up test file
        csvFile.delete();
    }

    @Test
    public void testSimulationsForMultipleRuns() throws Exception {
        String csvFilePath = "test_results_multiple_runs.csv";
        Double snrMin = 0.0;
        Double snrMax = 10.0;
        Double pasSNR = 5.0;

        // Generate TEB curve for multiple runs and ensure the average is calculated
        simTEBNRZ.genererCourbeTEB(snrMin, snrMax, pasSNR, csvFilePath, 100);

        // Assert that the results are averaged across multiple runs
        // This can involve verifying that the CSV file contains reasonable averages
        File csvFile = new File(csvFilePath);
        assertTrue("The CSV file for multiple runs was not created", csvFile.exists());

        // Clean up test file
        csvFile.delete();
    }

    @Test
    public void testEdgeCases() throws Exception {
        String csvFilePath = "test_results_edge_cases.csv";
        Double snrMin = -50.0;
        Double snrMax = 50.0;
        Double pasSNR = 20.0;

        simTEBNRZ.genererCourbeTEB(snrMin, snrMax, pasSNR, csvFilePath, 100);

        File csvFile = new File(csvFilePath);
        assertTrue("The CSV file for edge cases was not created", csvFile.exists());

        // Clean up test file
        csvFile.delete();
    }
}
