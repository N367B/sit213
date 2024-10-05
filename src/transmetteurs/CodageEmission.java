package transmetteurs;

import destinations.*;
import information.*;
import sources.*;
import visualisations.*;

/**
 * La classe CodageEmission permet de transformer un bit reçu en une sequence de 3 bits.
 */
public class CodageEmission extends Transmetteur<Boolean, Boolean> {

	/**
	 * Constructeur de CodageEmission
	 */
    public CodageEmission() {
    	super();
    }
    
    private Information<Boolean> informationBitCode = new Information<>();

    /**
     * Prend la liste d'information en parametre et converti le bit en une sequence de 3 bits
     */
	@Override
	public void recevoir(Information<Boolean> information) throws InformationNonConformeException {
		
		if (information == null || information.nbElements() == 0) {
            throw new InformationNonConformeException("L'information reçue est nulle ou invalide.");
        }
		this.informationRecue = information;
		informationBitCode = new Information<>();
		
		for (boolean bit : information) {
			if (bit) { //Si le bit reçu est 1
				informationBitCode.add(true);
				informationBitCode.add(false);
				informationBitCode.add(true);
			}else { //Si le bit reçu est 0
				informationBitCode.add(false);
				informationBitCode.add(true);
				informationBitCode.add(false);
			}
		}
		this.emettre();
	}

	/**
	 * Emet la sequence de 3 bits
	 */
	@Override
	public void emettre() throws InformationNonConformeException {
		 if (this.informationRecue == null) {
	            throw new InformationNonConformeException("L'information reçue est nulle.");
	        }
	        for (DestinationInterface<Boolean> destinationConnectee : destinationsConnectees) {
	            destinationConnectee.recevoir(informationBitCode);
	        }
	        informationEmise = informationBitCode; 
	}
    
    

}