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
 * Classe TransmetteurAnalogiqueBruité, hérite de la classe Transmetteur et ajoute
 * du bruit gaussien à l'information reçue avant de l'envoyer aux destinations connectées.
 */
public class TransmetteurAnalogiqueBruite extends Transmetteur<Float, Float> {

    private double snr; // Rapport signal/bruit (en dB)
    private Random random; // Générateur de nombres aléatoires
    private int nbEchantillonsParBit; // Nombre d'échantillons par bit
    /** Liste pour stocker les valeurs du bruit */
    public List<Float> bruitsGeneres; // Liste pour stocker les valeurs du bruit
    private static final boolean genererFichierBruit = false; // Variable pour contrôler la génération du fichier bruit
    private static final boolean afficherInformations = false; // Variable pour contrôler l'affichage des informations
    /**
     * Constructeur de la classe TransmetteurAnalogiqueBruité.
     * @param snr Le rapport signal/bruit (en dB).
     * @param nbEchant Le nombre d'échantillons par bit.
     */
    public TransmetteurAnalogiqueBruite(double snr, int nbEchant) {
        super();
        this.snr = snr;
        this.nbEchantillonsParBit = nbEchant;
        this.random = new Random();
        this.bruitsGeneres = new ArrayList<>(); // Initialiser la liste des bruits générés
    }

    /**
     * Reçoit une information logique, ajoute du bruit gaussien en fonction du SNR et prépare l'émission.
     * @param information L'information logique reçue.
     * @throws InformationNonConformeException si l'information est nulle ou invalide.
     */
    @Override
    public void recevoir(Information<Float> information) throws InformationNonConformeException {
        if (information == null || information.nbElements() == 0) {
            throw new InformationNonConformeException("L'information reçue est nulle ou invalide.");
        }
        this.informationRecue = information;

        // Ajouter du bruit à l'information reçue
        Information<Float> informationBruitee = ajouterBruit(information);
        this.informationEmise = informationBruitee;

        // Calculer la puissance du signal bruité et du bruit après ajout
        double puissanceSignal = calculerPuissanceSignal(information); // Original signal power
        double puissanceBruit = calculerPuissanceBruit(information, informationBruitee); // Noise power
        double sigma = Math.sqrt(puissanceBruit); // Écart-type du bruit
        double rapportSNR = 10 * Math.log10(puissanceSignal / puissanceBruit); // Rapport S/N en dB
        double rapportEbN0 = rapportSNR + 10 * Math.log10(nbEchantillonsParBit / 2.0); // Eb/N0 en dB
        
        // Affichage des informations calculées après ajout du bruit
        if (afficherInformations) {
		    System.out.println("- Nombre de bits de la séquence : " + (information.nbElements() / nbEchantillonsParBit));
		    System.out.println("- Nombre d'échantillons par bit : " + nbEchantillonsParBit);
		    System.out.println("1-> Puissance MOYENNE de la séquence de bits : " + puissanceSignal);
		    System.out.println("2-> Valeur de sigma (écart-type du bruit) : " + sigma);
		    System.out.println("3-> Puissance moyenne du bruit : " + puissanceBruit);
		    System.out.println("4-> Rapport signal-sur-bruit (S/N, en dB) : " + rapportSNR);
		    System.out.println("5-> Rapport Eb/N0 (en dB) : " + rapportEbN0);
        }
        // Si la variable genererFichierBruit est vraie, générer le fichier bruit.txt
        if (genererFichierBruit) {
            try {
                ajouterBruitsDansFichier("resultats/bruit.txt");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.emettre();
    }

    /**
     * Émet l'information analogique bruitée vers les destinations connectées.
     * @throws InformationNonConformeException si l'information est invalide.
     */
    @Override
    public void emettre() throws InformationNonConformeException {
        if (this.informationEmise == null) {
            throw new InformationNonConformeException("L'information à émettre est nulle.");
        }
        for (DestinationInterface<Float> destinationConnectee : destinationsConnectees) {
            destinationConnectee.recevoir(informationEmise);
        }
    }

    /**
     * Ajoute du bruit gaussien à chaque échantillon de l'information en fonction du SNR.
     * @param information L'information analogique originale.
     * @return L'information avec bruit ajouté.
     */
    public Information<Float> ajouterBruit(Information<Float> information) {
        Information<Float> informationBruitee = new Information<>();
        double puissanceSignal = calculerPuissanceSignal(information);
        double puissanceBruit = puissanceSignal / Math.pow(10, snr / 10); // Puissance du bruit calculée à partir du SNR
        double sigma = Math.sqrt(puissanceBruit); // Écart-type du bruit

        for (int i = 0; i < information.nbElements(); i++) {
            float valeurOriginale = information.iemeElement(i);
            // Générer un bruit gaussien
            float bruit = (float) (random.nextGaussian() * sigma);
            // Ajouter le bruit à l'échantillon original
            float valeurBruitée = valeurOriginale + bruit;
            informationBruitee.add(valeurBruitée);

            // Enregistrer le bruit généré
            bruitsGeneres.add(bruit);
        }
    
        return informationBruitee;
    }

    /**
     * Calcule la puissance moyenne du signal reçu.
     * @param information L'information analogique.
     * @return La puissance moyenne du signal.
     */
    public double calculerPuissanceSignal(Information<Float> information) {
        double puissanceTotale = 0.0;
        for (int i = 0; i < information.nbElements(); i++) {
            float valeur = information.iemeElement(i);
            puissanceTotale += valeur * valeur;
        }
        return puissanceTotale / information.nbElements();
    }

    /**
     * Calcule la puissance moyenne du bruit ajouté au signal.
     * @param signalOriginal L'information analogique originale.
     * @param signalBruite L'information analogique bruitée.
     * @return La puissance moyenne du bruit.
     */
    public double calculerPuissanceBruit(Information<Float> signalOriginal, Information<Float> signalBruite) {
        double puissanceTotaleBruit = 0.0;
        for (int i = 0; i < signalOriginal.nbElements(); i++) {
            float bruit = signalBruite.iemeElement(i) - signalOriginal.iemeElement(i);
            puissanceTotaleBruit += bruit * bruit;
        }
        return puissanceTotaleBruit / signalOriginal.nbElements();
    }

    /**
     * Ajoute les valeurs de bruit triées dans un fichier texte.
     * @param nomFichier Le nom du fichier texte dans lequel écrire les données.
     * @throws IOException Si une erreur survient lors de l'écriture du fichier.
     */
    private void ajouterBruitsDansFichier(String nomFichier) throws IOException {
        // Trier la liste des bruits générés
        Collections.sort(bruitsGeneres);
        // Afficher les valeurs triées du bruit
        //System.out.println("Valeurs triées du bruit : " + bruitsGeneres);
        // Ouvrir le fichier, le créer s'il n'existe pas
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomFichier, false))) {
            // Écrire chaque valeur de bruit triée dans une nouvelle ligne
            for (Float bruit : bruitsGeneres) {
                writer.write(bruit.toString());
                writer.newLine();
            }
        }
        System.out.println("Les valeurs triées du bruit ont été ajoutées au fichier " + nomFichier);
    }
    
    /**
     * Méthode principale pour tester le transmetteur analogique bruité.
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

            // Créer une source fixe
            SourceAleatoire source = new SourceAleatoire(100, 1);
            //SourceFixe source = new SourceFixe("000000000000000000000");
            Information<Boolean> infoLogique = source.getInformationGeneree();
            //System.out.println("Message logique émis");
            //System.out.println("Message logique émis : " + infoLogique);
            // Émettre un signal analogique sans bruit
            Emetteur emetteur = new Emetteur(Amin, Amax, nbEchantillonsParBit, typeModulation);
            //emetteur.connecter(new SondeAnalogique("Signal Analogique émis sans bruit"));
            emetteur.recevoir(infoLogique);
            Information<Float> infoAnalogiqueSansBruit = emetteur.getInformationEmise();
            //System.out.println("Signal analogique sans bruit : " + infoAnalogiqueSansBruit);
            //System.out.println("Signal analogique émis");
            // Transmettre le signal à travers un canal avec bruit
            TransmetteurAnalogiqueBruite transmetteur = new TransmetteurAnalogiqueBruite(snr, nbEchantillonsParBit);
            //transmetteur.connecter(new SondeAnalogique("Signal Analogique reçu avec bruit"));
            transmetteur.recevoir(infoAnalogiqueSansBruit);
            Information<Float> infoAnalogiqueAvecBruit = transmetteur.getInformationEmise();
            //System.out.println("Signal analogique reçu avec bruit");
            //System.out.println("Signal analogique avec bruit : " + infoAnalogiqueAvecBruit);
            // Créer le récepteur et recevoir le signal bruité
            Recepteur recepteur = new Recepteur(Amax, Amin, nbEchantillonsParBit, typeModulation);
            //recepteur.connecter(new SondeLogique("Message logique reçu après bruit", 200));
            recepteur.recevoir(infoAnalogiqueAvecBruit);
            //System.out.println("Message logique reçu après bruit");
            //Information<Boolean> infoLogiqueRecue = recepteur.getInformationEmise();
            //System.out.println("Message logique reçu : " + infoLogiqueRecue);
        } catch (InformationNonConformeException e) {
            System.err.println("Erreur : " + e.getMessage());
        }
    }
}
