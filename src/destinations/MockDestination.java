package destinations;

import information.Information;

/**
 * MockDestination is a mock class that implements DestinationInterface.
 * It is used for testing purposes.
 * @param <T> The type of the information to be received.
 */
public class MockDestination<T> implements DestinationInterface<T> {
    private Information<T> receivedInformation;

    /**
     * Constructor for MockDestination.
     */
    public MockDestination() {
        this.receivedInformation = null;
    }

    /**
     * Receives information.
     * @param information The information to be received.
     */
    @Override
    public void recevoir(Information<T> information) {
        this.receivedInformation = information;
    }

    /**
     * Gets the received information.
     * @return The received information.
     */
    @Override
    public Information<T> getInformationRecue() {
        return receivedInformation;
    }
}
