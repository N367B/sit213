package transmetteurs;

import sources.*;
import destinations.*;
import information.*;

import java.util.*;

/** 
 * Classe Abstraite d'un composant transmetteur d'informations dont
 * les éléments sont de type R en entrée et de type E en sortie;
 * l'entrée du transmetteur implémente l'interface
 * DestinationInterface, la sortie du transmetteur implémente
 * l'interface SourceInterface
 */

public abstract class TransmetteurParfait extends Transmetteur <Boolean, Boolean> {
	
	/**
	 * Recoit une information 
	 * @param information : information reçue
	 * @throws InformationNonConformeException si l'Information comporte une anomalie
	 */
	@Override
	public void recevoir(Information<Boolean> information) throws InformationNonConformeException{
		
		if(information == null) {
			throw new InformationNonConformeException();
		}

		this.informationRecue = information;
		this.informationEmise = informationRecue;
		
	}
	
	/**
     * émet l'information construite par le transmetteur
     * @throws InformationNonConformeException si l'Information comporte une anomalie
     */
	@Override
	public void emettre() throws InformationNonConformeException{
		
		for (DestinationInterface <Boolean> destinationConnectee : destinationsConnectees) {
            destinationConnectee.recevoir(informationEmise);
         }
		
	}
	    
}
