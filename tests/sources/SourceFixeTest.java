package sources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import information.Information;
import information.InformationNonConformeException;

class SourceFixeTest {

    @Test
    void testConstructionEtInformationGeneree() {
        String message = "101011";
        SourceFixe source = new SourceFixe(message);

        Information<Boolean> informationAttendue = new Information<>();
        informationAttendue.add(true);
        informationAttendue.add(false);
        informationAttendue.add(true);
        informationAttendue.add(false);
        informationAttendue.add(true);
        informationAttendue.add(true);

        assertEquals(informationAttendue, source.getInformationGeneree());
    }

    @Test
    void testMessageInvalide() {
        String messageInvalide = "12345"; // Contient des caractères autres que 0 et 1

        assertThrows(IllegalArgumentException.class, () -> {
            new SourceFixe(messageInvalide);
        });
    }

    @Test
    void testEmission() throws InformationNonConformeException {
        String message = "0011";
        SourceFixe source = new SourceFixe(message);
        DestinationInterface<Boolean> destinationMock = new DestinationInterface<>() {
            private Information<Boolean> informationRecue;

            @Override
            public void recevoir(Information<Boolean> information) throws InformationNonConformeException {
                this.informationRecue = information;
            }

            @Override
            public Information<Boolean> getInformationRecue() {
                return informationRecue;
            }
        };

        source.connecter(destinationMock);
        source.emettre();

        assertEquals(source.getInformationGeneree(), destinationMock.getInformationRecue());
    }
}