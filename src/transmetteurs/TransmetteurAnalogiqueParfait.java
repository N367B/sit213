package transmetteurs;

import destinations.*;
import information.*;

/**
 * Classe TransmetteurAnalogiqueParfait héritant de la classe TransmetteurAnalogique
 * Transmetteur est un composant qui ne modifie pas l'information reçue
 * par la source qui lui est connectée
 */
public class TransmetteurAnalogiqueParfait extends TransmetteurAnalogique<Float, Float> {

    /**
     * Constructeur du TransmetteurAnalogiqueParfait
     * @param formeModulation La forme d'onde utilisée pour la modulation (NRZ, NRZT, RZ)
     * @param nbEchantillons Le nombre d'échantillons par bit
     */
    public TransmetteurAnalogiqueParfait(String formeModulation, int nbEchantillons) {
        super(formeModulation, nbEchantillons);
    }

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
    }

    /**
     * Émet l'information analogique vers les destinations connectées
     * @throws InformationNonConformeException si l'information est invalide
     */
    @Override
    public void emettre() throws InformationNonConformeException {
        for (DestinationInterface<Float> destinationConnectee : destinationsConnectees) {
            destinationConnectee.recevoir(informationEmise);
        }
    }
}
