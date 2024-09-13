package transmetteurs;

import information.*;
import destinations.*;
import sources.*;
import visualisations.*;

/**
 * La classe Recepteur permet de convertir un signal analogique en information logique (booléenne).
 */
public class Recepteur extends Transmetteur<Float, Boolean> {

    /**
     * L'amplitude du signal analogique pour représenter un bit '0'.
     */
    private float Amin;

    /**
     * L'amplitude du signal analogique pour représenter un bit '1'.
     */
    private float Amax;

    /**
     * Le nombre d'échantillons par bit.
     */
    private int nbEchantillonsParBit;

    /**
     * Le type de modulation à utiliser (NRZ, NRZT, RZ).
     */
    private String typeModulation;

    /**
     * Constructeur de la classe Recepteur.
     *
     * @param Amax               L'amplitude maximale du signal.
     * @param Amin               L'amplitude minimale du signal.
     * @param nbEchantillonsParBit Le nombre d'échantillons par bit.
     * @param typeModulation     Le type de modulation utilisé ("NRZ", "NRZT", ou "RZ").
     */
    public Recepteur(float Amax, float Amin, int nbEchantillonsParBit, String typeModulation) {
        super();
        this.Amax = Amax;
        this.Amin = Amin;
        this.nbEchantillonsParBit = nbEchantillonsParBit;
        this.typeModulation = typeModulation;
    }

    /**
     * Reçoit un signal analogique (float) et le convertit en information logique (booléenne)
     * en fonction du type de modulation.
     *
     * @param information L'information analogique reçue (float values).
     * @throws InformationNonConformeException si l'information reçue est invalide.
     */
    @Override
    public void recevoir(Information<Float> information) throws InformationNonConformeException {
        if (information == null) {
            throw new InformationNonConformeException("L'information reçue est nulle.");
        }
        this.informationRecue = information;
        this.emettre();
    }

    /**
     * Émet l'information logique (signal démodulé).
     * @throws InformationNonConformeException si l'information reçue est invalide.
     */
    @Override
    public void emettre() throws InformationNonConformeException {
        if (informationRecue == null || informationRecue.nbElements() == 0) {
            throw new InformationNonConformeException("Aucune information n'a été reçue.");
        }

        Information<Boolean> informationLogique = new Information<>();

        // Conversion en fonction du type de modulation
        switch (typeModulation) {
            case "NRZ":
                convertNRZ(informationLogique);
                break;
            case "NRZT":
                convertNRZT(informationLogique);
                break;
            case "RZ":
                convertRZ(informationLogique);
                break;
            default:
                throw new InformationNonConformeException("Type de modulation inconnu : " + typeModulation);
        }

        // Émet l'information démodulée
        this.informationEmise = informationLogique;
        for (DestinationInterface<Boolean> destination : destinationsConnectees) {
            destination.recevoir(informationEmise);
        }
    }

    /**
     * Convertit un signal analogique en signal logique selon la modulation NRZ.
     */
    private void convertNRZ(Information<Boolean> informationLogique) throws InformationNonConformeException {
        for (int i = 0; i < informationRecue.nbElements(); i += nbEchantillonsParBit) {
            float moyenne = 0;
            // Moyenne des échantillons pour déterminer s'il s'agit d'un '1' ou '0'
            for (int j = 0; j < nbEchantillonsParBit; j++) {
                moyenne += informationRecue.iemeElement(i + j);
            }
            moyenne /= nbEchantillonsParBit;
            informationLogique.add(moyenne >= (Amax + Amin) / 2);
        }
    }

    /**
     * Convertit un signal analogique en signal logique selon la modulation NRZT.
     */
    private void convertNRZT(Information<Boolean> informationLogique) throws InformationNonConformeException {
        for (int i = 0; i < informationRecue.nbElements(); i += nbEchantillonsParBit) {
            float moyenne = 0;
            // Moyenne des échantillons pour déterminer s'il s'agit d'un '1' ou '0'
            for (int j = nbEchantillonsParBit / 3; j < 2 * nbEchantillonsParBit / 3; j++) {
                moyenne += informationRecue.iemeElement(i + j);
            }
            moyenne /= (nbEchantillonsParBit / 3);
            informationLogique.add(moyenne >= (Amax + Amin) / 2);
        }
    }

    /**
     * Convertit un signal analogique en signal logique selon la modulation RZ.
     */
    private void convertRZ(Information<Boolean> informationLogique) throws InformationNonConformeException {
        for (int i = 0; i < informationRecue.nbElements(); i += nbEchantillonsParBit) {
            float moyenne = 0;
            // Moyenne des échantillons du deuxième tiers pour déterminer s'il s'agit d'un '1' ou '0'
            for (int j = nbEchantillonsParBit / 3; j < 2 * nbEchantillonsParBit / 3; j++) {
                moyenne += informationRecue.iemeElement(i + j);
            }
            moyenne /= (nbEchantillonsParBit / 3);
            informationLogique.add(moyenne >= (Amax + Amin) / 2);
        }
    }

    public static void main(String[] args) {
        try {
        	String typeModulation = "NRZT";
            // Sample analog signal
            Information<Float> infoAnalogique = new Information<>();
            // Sample logical information
            Information<Boolean> infoLogique = new Information<>();
            
            // Generate a logical information
            SourceFixe source = new SourceFixe("01111000101100");
            System.out.println(source.getInformationGeneree());
            infoLogique = source.getInformationGeneree();
            
            // Convert the logical information to analog
            Emetteur emetteur = new Emetteur(1.0f, 0.0f, 30, typeModulation);
            emetteur.connecter(new SondeAnalogique(typeModulation));
            emetteur.recevoir(infoLogique);
            infoAnalogique = emetteur.getInformationEmise();
            
            // Test the receiver
            Recepteur recepteur = new Recepteur(1.0f, 0.0f, 30, typeModulation);
            recepteur.connecter(new SondeLogique("Sonde logique", 200));
            recepteur.recevoir(infoAnalogique);
            recepteur.emettre();
            System.out.println(recepteur.getInformationEmise());
        } catch (InformationNonConformeException e) {
            System.err.println("Error during demodulation: " + e.getMessage());
        }
    }
}
