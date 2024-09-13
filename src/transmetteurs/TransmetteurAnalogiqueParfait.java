package transmetteurs;

import destinations.*;
import information.*;

/**
 * Classe TransmetteurAnalogiqueParfait héritant de la classe TransmetteurAnalogique
 * Transmetteur est un composant qui ne modifie pas l'information reçue
 * par la source qui lui est connectée
 */
public class TransmetteurAnalogiqueParfait extends  Transmetteur<Float, Float> {



    /**
     * Reçoit une information logique, la convertit en analogique et prépare l'émission
     * @param information L'information logique reçue
     * @throws InformationNonConformeException si l'information est nulle ou invalide
     */
    @Override
    public void recevoir(Information<Float> information) throws InformationNonConformeException {
        if (information == null || information.nbElements() == 0) {
            throw new InformationNonConformeException();
        }
        this.informationRecue = information;
        this.informationEmise = informationRecue;
        this.emettre();
    }

    /**
     * Émet l'information analogique vers les destinations connectées
     * @throws InformationNonConformeException si l'information est invalide
     */
    @Override
    public void emettre() throws InformationNonConformeException {
        if (this.informationRecue == null) {
            throw new InformationNonConformeException("L'information reçue est nulle.");
        }
        for (DestinationInterface<Float> destinationConnectee : destinationsConnectees) {
            destinationConnectee.recevoir(informationEmise);
        }
        System.out.println("Transmetteur Analogique Emis: " + this.informationEmise);
    }
}
