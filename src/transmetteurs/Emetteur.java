package transmetteurs;

import destinations.*;
import information.*;
import sources.*;
import visualisations.*;

/**
 * La classe Emetteur permet de convertir une information logique (booléenne) en un signal analogique.
 */
public class Emetteur extends Transmetteur<Boolean, Float> {

    /**
     * L'amplitude du signal analogique pour représenter un bit '0'.
     */
    private float Amin = 0;

    /**
     * L'amplitude du signal analogique pour représenter un bit '1'.
     */
    private float Amax = 1;

    /**
     * Le nombre d'échantillons par bit.
     */
    private int nbEchantillonsParBit = 30;

    /**
     * Le type de modulation à utiliser (NRZ, NRZT, RZ).
     */
    private String typeModulation;

    /**
     * Information analogique (la sortie de l'émetteur).
     */
    private Information<Float> informationAnalogique;

    /**
     * Constructeur de la classe Emetteur.
     *
     * @param Amax               L'amplitude du signal pour un bit '1'.
     * @param Amin               L'amplitude du signal pour un bit '0'.
     * @param nbEchantillonsParBit Le nombre d'échantillons par bit.
     * @param typeModulation     Le type de modulation à utiliser ("NRZ", "NRZT", ou "RZ").
     */
    public Emetteur(float Amin, float Amax, int nbEchantillonsParBit, String typeModulation) {
    	super();
        this.Amax = Amax;
        this.Amin = Amin;
        this.nbEchantillonsParBit = nbEchantillonsParBit;
        this.typeModulation = typeModulation;
        this.informationAnalogique = new Information<Float>();
        //System.out.println("Amax: " + Amax + ", Amin: " + Amin + ", nbEchantillonsParBit: " + nbEchantillonsParBit + ", typeModulation: " + typeModulation);
    }

    /**
     * Reçoit une information booléenne (logique) et la convertit en information analogique
     * en fonction du type de modulation.
     *
     * @param information L'information logique reçue (bits 0 ou 1).
     * @throws InformationNonConformeException si l'information reçue est invalide.
     */
    @Override
    public void recevoir(Information<Boolean> information) throws InformationNonConformeException {
        if (information == null) {
            throw new InformationNonConformeException("L'information reçue est nulle.");
        }
        this.informationRecue = information;
        this.emettre(); // Une fois que l'information est reçue, on l'émet directement en la convertissant
    }

    /**
     * Émet l'information analogique (signal converti) en fonction du type de modulation choisi.
     * @throws InformationNonConformeException si l'information reçue est invalide.
     */
    @Override
    public void emettre() throws InformationNonConformeException {
        if (informationRecue == null || informationRecue.nbElements() == 0) {
            throw new InformationNonConformeException("Aucune information n'a été reçue.");
        }

        // Conversion en fonction du type de modulation
        for (int i = 0; i < informationRecue.nbElements(); i++) {
            Boolean bitCourant = informationRecue.iemeElement(i);
            Boolean bitPrecedent = (i > 0) ? informationRecue.iemeElement(i - 1) : null;
            Boolean bitSuivant = (i < informationRecue.nbElements() - 1) ? informationRecue.iemeElement(i + 1) : null;
            switch (typeModulation) {
                case "NRZ":
                    convertNRZ(bitCourant);
                    break;
                case "NRZT":
                    convertNRZT(bitCourant, bitPrecedent, bitSuivant); // Prend en compte les bits précédent et suivant
                    break;
                case "RZ":
                    convertRZ(bitCourant);
                    break;
                default:
                    throw new InformationNonConformeException("Type de modulation inconnu : " + typeModulation);
            }
        }
        //System.out.println("Signal Analogique Emis: " + informationAnalogique);
        // Une fois l'information analogique créée, elle est émise
        this.informationEmise = informationAnalogique;
        for (DestinationInterface<Float> destination : destinationsConnectees) {
            destination.recevoir(informationEmise);
        }
    }


    /**
     * Convertit un bit en signal analogique selon la modulation NRZ (Non Return to Zero).
     * @param bit Le bit à convertir.
     */
    private void convertNRZ(Boolean bit) {
        float amplitude = bit ? Amax : Amin;
        //System.out.println("Bit: " + bit + " -> Amplitude: " + amplitude);
        for (int i = 0; i < nbEchantillonsParBit; i++) {
            informationAnalogique.add(amplitude);
        }
    }

    /**
     * Convertit un bit en signal analogique selon la modulation NRZT (Non Return to Zero Transition).
     * @param bitCourant Le bit à convertir.
     * @param bitPrecedent Le bit précédent pour vérifier la continuité.
     * @param bitSuivant Le bit suivant pour vérifier la continuité.
     */
    private void convertNRZT(Boolean bitCourant, Boolean bitPrecedent, Boolean bitSuivant) {
        float moy = (Amax + Amin) / 2;
        int nbEchTiersBit = nbEchantillonsParBit / 3;
        float quantum = (Amax - moy) / nbEchTiersBit; // Pente pour la montée/descente

        // Affichage de debug pour vérifier les bits précédents et suivants
        //System.out.println("----------------------====================----------------");
        //System.out.println("Bit courant: " + bitCourant + ", Bit précédent: " + bitPrecedent + ", Bit suivant: " + bitSuivant);

        // Premier tiers : montée/descente si le bit précédent est différent, sinon plat
        for (int i = 0; i < nbEchTiersBit; i++) {
            float amplitude;

            if (bitPrecedent != null && !bitPrecedent.equals(bitCourant)) {
                // Transition progressive : montée si bitCourant = 1, descente si bitCourant = 0
                amplitude = bitCourant ? moy + (quantum * i) : moy - (quantum * i);
                //System.out.println("Premier tiers - Transition (i=" + i + "): amplitude = " + amplitude);
            } else {
                // Aucun changement, conserver le niveau stable
                amplitude = bitCourant ? Amax : Amin;
                //System.out.println("Premier tiers - Stable (i=" + i + "): amplitude = " + amplitude);
            }

            informationAnalogique.add(amplitude);
        }

        // Deuxième tiers : niveau stable correspondant au bit courant (plat)
        float amplitudeStable = bitCourant ? Amax : Amin;
        for (int i = 0; i < nbEchTiersBit; i++) {
            informationAnalogique.add(amplitudeStable);
            //System.out.println("Deuxième tiers (i=" + i + "): amplitude = " + amplitudeStable);
        }

        // Troisième tiers : montée/descente si le bit suivant est différent, sinon plat
        for (int i = 0; i < nbEchTiersBit; i++) {
            float amplitude;

            if (bitSuivant != null && !bitSuivant.equals(bitCourant)) {
                // Transition progressive : descente si bitCourant = 1, montée si bitCourant = 0
                amplitude = bitCourant ? Amax - (quantum * i) : Amin + (quantum * i);
                //System.out.println("Troisième tiers - Transition (i=" + i + "): amplitude = " + amplitude);
            } else {
                // Aucun changement, conserver le niveau stable
                amplitude = bitCourant ? Amax : Amin;
                //System.out.println("Troisième tiers - Stable (i=" + i + "): amplitude = " + amplitude);
            }

            informationAnalogique.add(amplitude);
        }
    }



    private void convertRZ(Boolean bit) {
        float amplitude = bit ? Amax : 0.0f;  // '1' -> Amax, '0' -> 0
        int nbEchTiersBit = nbEchantillonsParBit / 3;
        // Premier tiers : 0
        for (int i = 0; i < nbEchTiersBit; i++) {
            informationAnalogique.add(0.0f);
        }
        // Deuxième tiers : amplitude si bit = 1, 0 sinon
        for (int i = 0; i < nbEchTiersBit; i++) {
            informationAnalogique.add(amplitude);
        }
        // Troisième tiers : 0
        
        for (int i = 0; i < nbEchTiersBit; i++) {
            informationAnalogique.add(0.0f); 
        }
    }
    

	/**
	 * Méthode principale pour tester la conversion d'une information logique en
	 * signal analogique.
	 * 
	 * @param args Les arguments de la ligne de commande (non utilisés).
	 */
    public static void main(String[] args) {
        try {

            // Sample logical information
            Information<Boolean> infoLogique = new Information<>();
            
            //SourceFixe source = new SourceFixe("10110");
            SourceFixe source = new SourceFixe("01111000101100");
            //System.out.println(source.getInformationGeneree());
            infoLogique = source.getInformationGeneree();
            
            // Test NRZ conversion
            Emetteur emetteurNRZ = new Emetteur(1.0f, 0.0f, 30, "NRZ");
            //Information<Float> signalNRZ = emetteurNRZ.getInformationEmise();
            //System.out.println("Signal NRZ: " + signalNRZ);
            emetteurNRZ.connecter(new SondeAnalogique("Sonde NRZ"));
            emetteurNRZ.recevoir(infoLogique);
            //emetteurNRZ.emettre();

            // Test NRZT conversion
            Emetteur emetteurNRZT = new Emetteur(1.0f, 0.0f, 30, "NRZT");
            //Information<Float> signalNRZT = emetteurNRZT.getInformationEmise();
            //System.out.println("Signal NRZT: " + signalNRZT);
            emetteurNRZT.connecter(new SondeAnalogique("Sonde NRZT"));
            emetteurNRZT.recevoir(infoLogique);
            //emetteurNRZT.emettre();

            // Test RZ conversion
            Emetteur emetteurRZ = new Emetteur(1.0f, 0.0f, 30, "RZ");
            //Information<Float> signalRZ = emetteurRZ.getInformationEmise();
            //System.out.println("Signal RZ: " + signalRZ);
            emetteurRZ.connecter(new SondeAnalogique("Sonde RZ"));
            emetteurRZ.recevoir(infoLogique);
            //emetteurRZ.emettre();

        } catch (InformationNonConformeException e) {
            System.err.println("Error during conversion: " + e.getMessage());

        }

    }

}