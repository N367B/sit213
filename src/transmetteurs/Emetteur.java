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
        if (Amax <= Amin) {
            throw new IllegalArgumentException("Amax doit être supérieur à Amin.");
        }
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
     * @param bitCourant Le bit à convertir.
     * @param bitPrecedent Le bit précédent pour vérifier la continuité.
     * @param bitSuivant Le bit suivant pour vérifier la continuité.
     */
    private void convertNRZT(Boolean bitCourant, Boolean bitPrecedent, Boolean bitSuivant) {
        float moy = (Amax + Amin) / 2;

        int nbEchPremierTiers = nbEchantillonsParBit / 3;
        int nbEchDeuxiemeTiers = nbEchantillonsParBit / 3;
        int nbEchTroisiemeTiers = nbEchantillonsParBit - nbEchPremierTiers - nbEchDeuxiemeTiers;

        float quantumPremier = (Amax - moy) / (nbEchPremierTiers > 0 ? nbEchPremierTiers : 1);
        float quantumTroisieme = (Amax - moy) / (nbEchTroisiemeTiers > 0 ? nbEchTroisiemeTiers : 1);

        // Premier tiers : montée/descente si le bit précédent est différent, sinon plat
        for (int i = 0; i < nbEchPremierTiers; i++) {
            float amplitude;
            if (bitPrecedent != null && !bitPrecedent.equals(bitCourant)) {
                // Transition progressive : montée si bitCourant = 1, descente si bitCourant = 0
                amplitude = bitCourant ? moy + (quantumPremier * i) : moy - (quantumPremier * i);
            } else {
                // Aucun changement, conserver le niveau stable
                amplitude = bitCourant ? Amax : Amin;
            }
            informationAnalogique.add(amplitude);
        }

        // Deuxième tiers : niveau stable correspondant au bit courant (plat)
        float amplitudeStable = bitCourant ? Amax : Amin;
        for (int i = 0; i < nbEchDeuxiemeTiers; i++) {
            informationAnalogique.add(amplitudeStable);
        }

        // Troisième tiers : montée/descente si le bit suivant est différent, sinon plat
        for (int i = 0; i < nbEchTroisiemeTiers; i++) {
            float amplitude;
            if (bitSuivant != null && !bitSuivant.equals(bitCourant)) {
                // Transition progressive : descente si bitCourant = 1, montée si bitCourant = 0
                amplitude = bitCourant ? Amax - (quantumTroisieme * i) : Amin + (quantumTroisieme * i);
            } else {
                // Aucun changement, conserver le niveau stable
                amplitude = bitCourant ? Amax : Amin;
            }
            informationAnalogique.add(amplitude);
        }
    }

    /**
     * Convertit un bit en signal analogique selon la modulation RZ (Return to Zero).
     * @param bit Le bit à convertir.
     */
    private void convertRZ(Boolean bit) {
        int nbEchPremierTiers = nbEchantillonsParBit / 3;
        int nbEchDeuxiemeTiers = nbEchantillonsParBit / 3;
        int nbEchTroisiemeTiers = nbEchantillonsParBit - nbEchPremierTiers - nbEchDeuxiemeTiers;

        // Premier tiers : 0
        for (int i = 0; i < nbEchPremierTiers; i++) {
            informationAnalogique.add(0.0f);
        }

        // Deuxième tiers : amplitude si bit = 1, 0 sinon
        float amplitude = bit ? Amax : 0.0f;
        for (int i = 0; i < nbEchDeuxiemeTiers; i++) {
            informationAnalogique.add(amplitude);
        }

        // Troisième tiers : 0
        for (int i = 0; i < nbEchTroisiemeTiers; i++) {
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
            // Exemples d'informations logiques
            Information<Boolean> infoLogique;

            SourceFixe source = new SourceFixe("01111000101100");
            infoLogique = source.getInformationGeneree();

            // Test de la modulation NRZ
            Emetteur emetteurNRZ = new Emetteur(0.0f, 1.0f, 30, "NRZ");
            emetteurNRZ.connecter(new SondeAnalogique("Sonde NRZ"));
            emetteurNRZ.recevoir(infoLogique);

            // Test de la modulation NRZT avec un nombre d'échantillons non divisible par 3
            Emetteur emetteurNRZT = new Emetteur(0.0f, 1.0f, 31, "NRZT");
            emetteurNRZT.connecter(new SondeAnalogique("Sonde NRZT"));
            emetteurNRZT.recevoir(infoLogique);

            // Test de la modulation RZ avec un nombre d'échantillons non divisible par 3
            Emetteur emetteurRZ = new Emetteur(0.0f, 1.0f, 32, "RZ");
            emetteurRZ.connecter(new SondeAnalogique("Sonde RZ"));
            emetteurRZ.recevoir(infoLogique);

        } catch (InformationNonConformeException e) {
            System.err.println("Erreur lors de la conversion : " + e.getMessage());
        }
    }
}
