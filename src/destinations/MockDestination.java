package destinations;

import information.Information;

public class MockDestination<T> implements DestinationInterface<T> {
    private Information<T> receivedInformation;

    @Override
    public void recevoir(Information<T> information) {
        this.receivedInformation = information;
    }

    @Override
    public Information<T> getInformationRecue() {
        return receivedInformation;
    }
}
