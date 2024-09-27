package transmetteurs;

import destinations.*;
import information.Information;
import information.InformationNonConformeException;
import java.util.List;

/**
 * Classe TransmetteurAnalogiqueMultiTrajet, modélise un canal analogique à trajets multiples.
 * Prend en compte plusieurs trajets indirects avec délais et atténuations définis.
 */
public class TransmetteurAnalogiqueMultiTrajet extends Transmetteur<Float, Float> {

    private List<int[]> trajetsIndirects; // Liste des couples {dt, ar} pour les trajets indirects

    /**
     * Constructeur prenant en compte plusieurs trajets indirects.
     * @param trajetsIndirects Liste des couples {dt, ar} où dt est le décalage temporel (en échantillons)
     *                         et ar est le coefficient d'atténuation.
     */
    public TransmetteurAnalogiqueMultiTrajet(List<int[]> trajetsIndirects) {
        super();
        this.trajetsIndirects = trajetsIndirects;
    }

    /**
     * Ajoute les effets des trajets multiples au signal analogique.
     * @param signalAnalogique Le signal original.
     * @return Le signal modifié avec les effets des trajets multiples.
     */
    public Information<Float> ajouterTrajetsMultiples(Information<Float> signalAnalogique) {
        Information<Float> signalModifie = new Information<>();

        for (int i = 0; i < signalAnalogique.nbElements(); i++) {
            float echantillonFinal = signalAnalogique.iemeElement(i);

            // Appliquer chaque trajet indirect (retard et atténuation)
            for (int[] trajet : trajetsIndirects) {
                int dt = trajet[0];
                float ar = trajet[1];
                if (i >= dt) {
                    echantillonFinal += ar * signalAnalogique.iemeElement(i - dt);
                }
            }

            signalModifie.add(echantillonFinal);
        }
        return signalModifie;
    }

    /**
     * Méthode recevant le signal et appliquant les effets des trajets multiples.
     * @param information Le signal reçu.
     * @throws InformationNonConformeException Si le signal reçu est nul ou invalide.
     */
    @Override
    public void recevoir(Information<Float> information) throws InformationNonConformeException {
        if (information == null || information.nbElements() == 0) {
            throw new InformationNonConformeException("L'information reçue est nulle ou invalide.");
        }

        // Appliquer les trajets multiples au signal reçu
        this.informationRecue = information;
        this.informationEmise = ajouterTrajetsMultiples(informationRecue);
        this.emettre();
    }

    /**
     * Méthode émettant le signal traité avec trajets multiples aux destinations connectées.
     * @throws InformationNonConformeException Si aucune information n'a été émise.
     */
    @Override
    public void emettre() throws InformationNonConformeException {
        if (this.informationEmise == null) {
            throw new InformationNonConformeException("L'information à émettre est nulle.");
        }

        // Envoi du signal émis aux destinations connectées
        for (DestinationInterface<Float> destinationConnectee : destinationsConnectees) {
            destinationConnectee.recevoir(informationEmise);
        }
    }
}
