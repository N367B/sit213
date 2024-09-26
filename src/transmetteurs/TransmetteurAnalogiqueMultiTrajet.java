package transmetteurs;

import destinations.*;
import information.*;
import sources.*;
import visualisations.*;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collections;

/**
 * Classe TransmetteurAnalogiqueMultiTrajet, hérite de la classe Transmetteur
 * Modélise un canal analogique à trajets multiples avec un second trajet retardé et atténué, plus du bruit gaussien.
 */
public class TransmetteurAnalogiqueMultiTrajet extends Transmetteur<Float, Float> {

    private double snr; // Signal-to-noise ratio (Eb/N0)
    private Random random;
    private int tau; // Retard du second trajet (en nombre d'échantillons)
    private float alpha; // Coefficient d'atténuation du second trajet

    /**
     * Constructeur de la classe avec tau et alpha.
     * @param snr Le rapport signal/bruit (en dB)
     * @param tau Retard du second trajet en échantillons
     * @param alpha Atténuation du second trajet (entre 0 et 1)
     */
    public TransmetteurAnalogiqueMultiTrajet(double snr, int tau, float alpha) {
        super();
        this.snr = snr;
        this.tau = tau;
        this.alpha = alpha;
        this.random = new Random();
    }

    /**
     * Ajoute le second trajet et le bruit gaussien au signal reçu.
     * @param signalAnalogique Le signal d'origine.
     * @return Un signal avec trajets multiples et bruit.
     */
    private Information<Float> ajouterTrajetsMultiplesEtBruit(Information<Float> signalAnalogique) {
        Information<Float> signalBruite = new Information<>();
        double variance = 1 / (2 * Math.pow(10, snr / 10)); // Calcul de la variance du bruit gaussien

        for (int i = 0; i < signalAnalogique.nbElements(); i++) {
            float echantillonOrigine = signalAnalogique.iemeElement(i);
            // Trajet direct
            float echantillonFinal = echantillonOrigine;

            // Trajet retardé
            if (i >= tau) {
                echantillonFinal += alpha * signalAnalogique.iemeElement(i - tau);
            }

            // Ajout du bruit gaussien
            echantillonFinal += (float) (random.nextGaussian() * Math.sqrt(variance));
            signalBruite.add(echantillonFinal);
        }

        return signalBruite;
    }

    @Override
    public void recevoir(Information<Float> information) throws InformationNonConformeException {
        if (information == null || information.nbElements() == 0) {
            throw new InformationNonConformeException("L'information reçue est nulle ou invalide.");
        }

        // Ajout du second trajet et du bruit gaussien au signal reçu
        this.informationRecue = information;
        this.informationEmise = ajouterTrajetsMultiplesEtBruit(informationRecue);
        this.emettre();
    }

    @Override
    public void emettre() throws InformationNonConformeException {
        if (this.informationEmise == null) {
            throw new InformationNonConformeException("L'information à émettre est nulle.");
        }

        // Envoi du signal bruité aux destinations connectées
        for (DestinationInterface<Float> destinationConnectee : destinationsConnectees) {
            destinationConnectee.recevoir(informationEmise);
        }
    }
    
   /**
    * Méthode principale pour tester le transmetteur analogique à trajets multiples.
    * @param args Les arguments de la ligne de commande.
    */
    public static void main(String[] args) {
        try {
            // Définir les paramètres pour la simulation
            String typeModulation = "NRZ";
            float Amax = 1.0f;
            float Amin = 0.0f;
            int nbEchantillonsParBit = 30;
            double snr = 10.0; // SNR en dB
            int tau = 5; // Retard du second trajet en nombre d'échantillons
            float alpha = 0.5f; // Coefficient d'atténuation du second trajet

            // Créer une source fixe
            SourceFixe source = new SourceFixe("0111000111001");
            Information<Boolean> infoLogique = source.getInformationGeneree();
            //System.out.println("Message logique émis : " + infoLogique);

            // Émettre un signal analogique sans bruit
            Emetteur emetteur = new Emetteur(Amin, Amax, nbEchantillonsParBit, typeModulation);
            emetteur.connecter(new SondeAnalogique("Signal Analogique émis sans bruit"));
            emetteur.recevoir(infoLogique);
            Information<Float> infoAnalogiqueSansBruit = emetteur.getInformationEmise();
            //System.out.println("Signal analogique sans bruit : " + infoAnalogiqueSansBruit);

            // Transmettre le signal à travers un canal avec trajets multiples et bruit
            TransmetteurAnalogiqueMultiTrajet transmetteur = new TransmetteurAnalogiqueMultiTrajet(snr, tau, alpha);
            transmetteur.connecter(new SondeAnalogique("Signal Analogique reçu avec trajets multiples et bruit"));
            transmetteur.recevoir(infoAnalogiqueSansBruit);
            Information<Float> infoAnalogiqueAvecBruit = transmetteur.getInformationEmise();
            //System.out.println("Signal analogique avec trajets multiples et bruit : " + infoAnalogiqueAvecBruit);

            // Créer le récepteur et recevoir le signal bruité
            Recepteur recepteur = new Recepteur(Amax, Amin, nbEchantillonsParBit, typeModulation);
            recepteur.connecter(new SondeLogique("Message logique reçu après bruit", 200));
            recepteur.recevoir(infoAnalogiqueAvecBruit);
            //Information<Boolean> infoLogiqueRecue = recepteur.getInformationEmise();
            //System.out.println("Message logique reçu : " + infoLogiqueRecue);
            
        } catch (InformationNonConformeException e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }
}
