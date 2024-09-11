package sources;

import information.Information;
import org.junit.Test;
import static org.junit.Assert.*;

public class SourceFixeTest {

    /**
     * Test the constructor with a valid message.
     */
    @Test
    public void testValidMessage() {
        String message = "11001";
        SourceFixe sourceFixe = new SourceFixe(message);

        // Expected Information<Boolean> based on the input message "11001"
        Information<Boolean> expectedInformation = new Information<>();
        expectedInformation.add(true);
        expectedInformation.add(true);
        expectedInformation.add(false);
        expectedInformation.add(false);
        expectedInformation.add(true);

        // Assert that the generated information matches the expected information
        assertEquals(expectedInformation, sourceFixe.getInformationGeneree());
    }

    /**
     * Test the constructor with an empty message.
     */
    @Test
    public void testEmptyMessage() {
        String message = "";
        SourceFixe sourceFixe = new SourceFixe(message);

        // Expected empty Information<Boolean>
        Information<Boolean> expectedInformation = new Information<>();

        // Assert that the generated information is empty
        assertEquals(expectedInformation, sourceFixe.getInformationGeneree());
    }

    /**
     * Test the constructor with an invalid message.
     * Since the code uses System.exit(1) for invalid input, this test will require handling of System.exit.
     * We will modify the SourceFixe class to throw an IllegalArgumentException for this test.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidMessage() {
        String invalidMessage = "11002";  // Contains an invalid character '2'
        new SourceFixe(invalidMessage);  // Should throw an IllegalArgumentException
    }
}
