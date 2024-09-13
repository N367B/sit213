package transmetteurs;

import information.Information;
import information.InformationNonConformeException;
import destinations.DestinationInterface;
import visualisations.Sonde;
import visualisations.SondeAnalogique;

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
    public Emetteur(float Amax, float Amin, int nbEchantillonsParBit, String typeModulation) {
    	super();
        this.Amax = Amax;
        this.Amin = Amin;
        this.nbEchantillonsParBit = nbEchantillonsParBit;
        this.typeModulation = typeModulation;
        this.informationAnalogique = new Information<Float>();
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
        if (informationRecue == null) {
            throw new InformationNonConformeException("Aucune information n'a été reçue.");
        }

        // Conversion en fonction du type de modulation
        for (Boolean bit : informationRecue) {
            switch (typeModulation) {
                case "NRZ":
                    convertNRZ(bit);
                    break;
                case "NRZT":
                    convertNRZT(bit);
                    break;
                case "RZ":
                    convertRZ(bit);
                    break;
                default:
                    throw new InformationNonConformeException("Type de modulation inconnu : " + typeModulation);
            }
        }

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
        for (int i = 0; i < nbEchantillonsParBit; i++) {
            informationAnalogique.add(amplitude);
        }
    }

    /**
     * Convertit un bit en signal analogique selon la modulation NRZT (Non Return to Zero Transition).
     * @param bit Le bit à convertir.
     */
    private void convertNRZT(Boolean bit) {
        for (int i = 0; i < nbEchantillonsParBit; i++) {
            float amplitude;
            if (i < nbEchantillonsParBit / 3) {
                amplitude = bit ? Amax * (i / (float) (nbEchantillonsParBit / 3)) : Amin * (i / (float) (nbEchantillonsParBit / 3));
            } else if (i > 2 * nbEchantillonsParBit / 3) {
                amplitude = bit ? Amax * ((nbEchantillonsParBit - i) / (float) (nbEchantillonsParBit / 3)) : Amin * ((nbEchantillonsParBit - i) / (float) (nbEchantillonsParBit / 3));
            } else {
                amplitude = bit ? Amax : Amin;
            }
            informationAnalogique.add(amplitude);
        }
    }

    /**
     * Convertit un bit en signal analogique selon la modulation RZ (Return to Zero).
     * @param bit Le bit à convertir.
     */
    private void convertRZ(Boolean bit) {
        float amplitude = bit ? Amax : Amin;
        for (int i = 0; i < nbEchantillonsParBit / 2; i++) {
            informationAnalogique.add(amplitude);
        }
        for (int i = nbEchantillonsParBit / 2; i < nbEchantillonsParBit; i++) {
            informationAnalogique.add(0f); // Retourne à zéro après la première moitié du bit
        }
    }
    

    public static void main(String[] args) {
        try {

            // Sample logical information
            Information<Boolean> infoLogique = new Information<>();
            infoLogique.add(true);
            infoLogique.add(false);
            infoLogique.add(true);
            infoLogique.add(true);
            infoLogique.add(false);

            // Test NRZ conversion
            Emetteur emetteurNRZ = new Emetteur(1.0f, 0.0f, 10, "NRZ");
            emetteurNRZ.recevoir(infoLogique);
            Information<Float> signalNRZ = emetteurNRZ.getInformationEmise();
            System.out.println("Signal NRZ: " + signalNRZ);
            emetteurNRZ.connecter(new SondeAnalogique("Sonde NRZ"));
            emetteurNRZ.emettre();

            // Test NRZT conversion
            Emetteur emetteurNRZT = new Emetteur(1.0f, -1.0f, 10, "NRZT");
            emetteurNRZT.recevoir(infoLogique);
            Information<Float> signalNRZT = emetteurNRZT.getInformationEmise();
            System.out.println("Signal NRZT: " + signalNRZT);
            emetteurNRZT.connecter(new SondeAnalogique("Sonde NRZT"));
            emetteurNRZT.emettre();

            // Test RZ conversion
            Emetteur emetteurRZ = new Emetteur(1.0f, 0.0f, 10, "RZ");
            emetteurRZ.recevoir(infoLogique);
            Information<Float> signalRZ = emetteurRZ.getInformationEmise();
            System.out.println("Signal RZ: " + signalRZ);
            emetteurRZ.connecter(new SondeAnalogique("Sonde RZ"));
            emetteurRZ.emettre();

        } catch (InformationNonConformeException e) {
            System.err.println("Error during conversion: " + e.getMessage());

        }

    }

}