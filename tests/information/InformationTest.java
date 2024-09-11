package information;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class InformationTest {

    private Information<Boolean> emptyInformation;
    private Information<Boolean> initializedInformation;

    @Before
    public void setup() {
        // Initialize the empty information
        emptyInformation = new Information<>();

        // Initialize with an array
        Boolean[] initialArray = {true, false, true};
        initializedInformation = new Information<>(initialArray);
    }

    /**
     * Test the empty constructor.
     */
    @Test
    public void testEmptyConstructor() {
        assertEquals(0, emptyInformation.nbElements());  // Should have 0 elements
    }

    /**
     * Test the array-based constructor.
     */
    @Test
    public void testArrayConstructor() {
        assertEquals(3, initializedInformation.nbElements());  // Should have 3 elements
        assertEquals(true, initializedInformation.iemeElement(0));
        assertEquals(false, initializedInformation.iemeElement(1));
        assertEquals(true, initializedInformation.iemeElement(2));
    }

    /**
     * Test adding elements to information.
     */
    @Test
    public void testAddElement() {
        emptyInformation.add(true);
        emptyInformation.add(false);

        assertEquals(2, emptyInformation.nbElements());
        assertEquals(true, emptyInformation.iemeElement(0));
        assertEquals(false, emptyInformation.iemeElement(1));
    }

    /**
     * Test setting an element.
     */
    @Test
    public void testSetElement() {
        initializedInformation.setIemeElement(1, true);
        assertEquals(true, initializedInformation.iemeElement(1));  // Should update to true
    }

    /**
     * Test equality of two Information objects.
     */
    @Test
    public void testEquals() {
        Boolean[] sameArray = {true, false, true};
        Information<Boolean> sameInformation = new Information<>(sameArray);

        assertTrue(initializedInformation.equals(sameInformation));  // Should be equal

        sameInformation.setIemeElement(2, false);
        assertFalse(initializedInformation.equals(sameInformation));  // Should not be equal
    }

    /**
     * Test the toString() method.
     */
    @Test
    public void testToString() {
        assertEquals(" true false true", initializedInformation.toString());
    }

    /**
     * Test iteration over the elements.
     */
    @Test
    public void testIteration() {
        Boolean[] expectedValues = {true, false, true};
        int index = 0;

        for (Boolean value : initializedInformation) {
            assertEquals(expectedValues[index], value);
            index++;
        }
    }
}
