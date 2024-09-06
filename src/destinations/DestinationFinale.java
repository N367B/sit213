package destinations;

import information.*;

/**
 * Classe concrète représentant une destination qui reçoit des informations de type Boolean
 */
public class DestinationFinale extends Destination<Boolean> {

    public void recevoir(Information<Boolean> information) throws InformationNonConformeException {
        if (information == null || information.nbElements() == 0) {
            throw new InformationNonConformeException("L'information est vide ou non conforme.");
        }
        this.informationRecue = information;
    }
}
