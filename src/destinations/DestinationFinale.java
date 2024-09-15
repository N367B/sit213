package destinations;

import information.Information;
import information.InformationNonConformeException;

/**
 * Classe concrète représentant une destination qui reçoit des informations de type Boolean
 */
public class DestinationFinale extends Destination<Boolean> {
	
	/**
	 * Constructeur de la classe DestinationFinale
	 */
	public DestinationFinale() {
		super();
	}

	/**
	 * Méthode permettant de recevoir une information de type Boolean
	 * 
	 * @param information L'information reçue
	 * @throws InformationNonConformeException si l'Information comporte une anomalie
	 */
	@Override
    public void recevoir(Information<Boolean> information) throws InformationNonConformeException {
        if (information == null || information.nbElements() == 0) {
            throw new InformationNonConformeException("L'information est vide ou non conforme.");
        }
        this.informationRecue = information;
    }
}
