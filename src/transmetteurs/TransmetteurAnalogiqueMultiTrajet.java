package transmetteurs;

import destinations.*;
import information.*;
import sources.*;
import visualisations.*;

import java.util.List;
import java.util.ArrayList;

/**
 * Classe TransmetteurAnalogiqueMultiTrajet, modélise un canal analogique à trajets multiples.
 * Prend en compte plusieurs trajets indirects avec délais et atténuations définis.
 */
public class TransmetteurAnalogiqueMultiTrajet extends Transmetteur<Float, Float> {

    private List<float[]> trajetsIndirects; // Liste des couples {dt, ar} pour les trajets indirects

    /**
     * Constructeur prenant en compte plusieurs trajets indirects.
     * Vérifie que les délais et les atténuations respectent les contraintes (dt &gt;= 0, 0 &lt;= ar &lt;= 1).
     * @param trajetsIndirects Liste des couples {dt, ar} où dt est le décalage temporel (en échantillons)
     *                         et ar est le coefficient d'atténuation.
     * @throws IllegalArgumentException si les paramètres sont incorrects.
     */
    public TransmetteurAnalogiqueMultiTrajet(List<float[]> trajetsIndirects) {
        super();
        this.trajetsIndirects = new ArrayList<>();
        //Vérification du nombre de trajets indirects
        /*if (trajetsIndirects.size() == 0) {
            throw new IllegalArgumentException("Le nombre de trajets indirects doit être supérieur à 0.");
        }*/
        if (trajetsIndirects.size() > 5) {
            throw new IllegalArgumentException("Le nombre de trajets indirects ne doit pas dépasser 5.");
        }

        // Vérification des paramètres
        for (float[] trajet : trajetsIndirects) {
            if (trajet[0] < 0) {
                throw new IllegalArgumentException("Le décalage temporel (dt) doit être un entier positif ou nul.");
            }
            if (trajet[1] < 0.0f || trajet[1] > 1.0f) { // Atténuation entre 0 et 1
                throw new IllegalArgumentException("Le coefficient d'atténuation (ar) doit être compris entre 0.0 et 1.0.");
            }
            this.trajetsIndirects.add(trajet);
        }
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
            for (float[] trajet : trajetsIndirects) {
                int dt = (int) trajet[0];  // Retard en nombre d'échantillons
                float ar = trajet[1];  // Atténuation exacte
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

    /**
     * Méthode principale pour tester le transmetteur analogique à trajets multiples.
     * @param args Arguments de la ligne de commande (non utilisés ici).
     */
    public static void main(String[] args) {
        try {
            // Définir les paramètres pour la simulation
            String typeModulation = "NRZT";
            float Amax = 1.0f;
            float Amin = 0.0f;
            int nbEchantillonsParBit = 30;
            int messageSize = 20; // Taille du message

            // Paramètres des trajets indirects : {dt (retard), ar (atténuation)}
            List<float[]> trajetsIndirects = new ArrayList<>();
            trajetsIndirects.add(new float[]{3, 0.5f});  // dt=3, ar=0.5
            trajetsIndirects.add(new float[]{5, 0.05f});  // dt=5, ar=0.05 (allowing finer attenuation)

            // Créer une source
            SourceAleatoire source = new SourceAleatoire(messageSize, 1);
            Information<Boolean> infoLogique = source.getInformationGeneree();

            // Émettre un signal analogique sans bruit
            Emetteur emetteur = new Emetteur(Amin, Amax, nbEchantillonsParBit, typeModulation);
            emetteur.connecter(new SondeAnalogique(typeModulation));
            emetteur.recevoir(infoLogique);
            Information<Float> infoAnalogiqueSansBruit = emetteur.getInformationEmise();

            //System.out.println("Signal analogique sans bruit : " + infoAnalogiqueSansBruit);

            // Transmettre le signal à travers un canal avec trajets multiples
            TransmetteurAnalogiqueMultiTrajet transmetteur = new TransmetteurAnalogiqueMultiTrajet(trajetsIndirects);
            transmetteur.connecter(new SondeAnalogique(typeModulation));
            transmetteur.recevoir(infoAnalogiqueSansBruit);
            Information<Float> infoAnalogiqueAvecTrajets = transmetteur.getInformationEmise();

            //System.out.println("Signal analogique avec trajets multiples : " + infoAnalogiqueAvecTrajets);

            // Créer un récepteur et recevoir le signal modifié avec trajets multiples
            Recepteur recepteur = new Recepteur(Amax, Amin, nbEchantillonsParBit, typeModulation);
            recepteur.connecter(new SondeLogique("Recepteur", 200));
            recepteur.recevoir(infoAnalogiqueAvecTrajets);
            Information<Boolean> infoLogiqueRecue = recepteur.getInformationEmise();

            //System.out.println("Message logique reçu : " + infoLogiqueRecue);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
