package transmetteurs;

import destinations.*;
import information.*;

/** 
 * Classe TransmetteurParfait héritant de la classe Transmetteur
 * TransmetteurParfait est un composant qui ne modifie pas l'information reçue
 * par la source qui lui est connectée
 */

public class TransmetteurParfait extends Transmetteur <Boolean, Boolean> {
    
	
    /**
     * Constructeur de la classe TransmetteurParfait
     */
    public TransmetteurParfait() {
        super();
    }
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
