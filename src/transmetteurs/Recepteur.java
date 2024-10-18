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
    public Recepteur(float Amin, float Amax, int nbEchantillonsParBit, String typeModulation) {
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
            int limit = Math.min(nbEchantillonsParBit, informationRecue.nbElements() - i);
            for (int j = 0; j < limit; j++) {
                moyenne += informationRecue.iemeElement(i + j);
            }
            moyenne /= limit;
            informationLogique.add(moyenne >= (Amax + Amin) / 2);
        }
    }

    /**
     * Convertit un signal analogique en signal logique selon la modulation NRZT.
     */
    private void convertNRZT(Information<Boolean> informationLogique) throws InformationNonConformeException {
        /*
        int nbEchPremierTiers = nbEchantillonsParBit / 3;
        int nbEchDeuxiemeTiers = nbEchantillonsParBit / 3;
        int nbEchTroisiemeTiers = nbEchantillonsParBit - nbEchPremierTiers - nbEchDeuxiemeTiers;

        int totalEchParBit = nbEchPremierTiers + nbEchDeuxiemeTiers + nbEchTroisiemeTiers;

        for (int i = 0; i < informationRecue.nbElements(); i += totalEchParBit) {
            float moyenne = 0;
            int start = i + nbEchPremierTiers;
            int end = Math.min(start + nbEchDeuxiemeTiers, informationRecue.nbElements());

            // Moyenne des échantillons du deuxième segment
            int count = end - start;
            for (int j = start; j < end; j++) {
                moyenne += informationRecue.iemeElement(j);
            }
            moyenne /= count > 0 ? count : 1;
            informationLogique.add(moyenne >= (Amax + Amin) / 2);
        }*/
        convertNRZ(informationLogique);
    }

    /**
     * Convertit un signal analogique en signal logique selon la modulation RZ.
     * @param informationLogique L'information logique à émettre.
     */
    private void convertRZ(Information<Boolean> informationLogique) throws InformationNonConformeException {
        Amin = 0.0f;
        int nbEchPremierTiers = nbEchantillonsParBit / 3;
        int nbEchDeuxiemeTiers = nbEchantillonsParBit / 3;
        int nbEchTroisiemeTiers = nbEchantillonsParBit - nbEchPremierTiers - nbEchDeuxiemeTiers;

        int totalEchParBit = nbEchPremierTiers + nbEchDeuxiemeTiers + nbEchTroisiemeTiers;

        for (int i = 0; i < informationRecue.nbElements(); i += totalEchParBit) {
            float moyenne = 0;
            int start = i + nbEchPremierTiers;
            int end = Math.min(start + nbEchDeuxiemeTiers, informationRecue.nbElements());

            // Moyenne des échantillons du deuxième segment
            int count = end - start;
            for (int j = start; j < end; j++) {
                moyenne += informationRecue.iemeElement(j);
            }
            moyenne /= count > 0 ? count : 1;
            informationLogique.add(moyenne >= (Amax + Amin) / 2);
        }
    }

    /**
     * Fonction principale pour tester le récepteur avec les différentes modulations.
     * @param args Les arguments de la ligne de commande.
     */
    public static void main(String[] args) {
        try {
            // Définir les paramètres
            float Amax = 1.0f;
            float Amin = 0.0f;
            int nbEchantillonsParBit = 31; // Par exemple, un nombre non divisible par 3
            String typeModulation = "NRZT"; // Vous pouvez tester avec "NRZ", "NRZT", "RZ"

            // Créer un message fixe (message logique)
            SourceFixe source = new SourceFixe("01111000101100");
            Information<Boolean> infoLogique = source.getInformationGeneree();

            // Convertir le message logique en signal analogique avec l'émetteur
            Emetteur emetteur = new Emetteur(Amin, Amax, nbEchantillonsParBit, typeModulation);
            emetteur.connecter(new SondeAnalogique("Signal Analogique émis"));
            emetteur.recevoir(infoLogique);
            Information<Float> infoAnalogique = emetteur.getInformationEmise();

            // Créer le récepteur pour la modulation choisie
            Recepteur recepteur = new Recepteur(Amin, Amax, nbEchantillonsParBit, typeModulation);
            recepteur.connecter(new SondeLogique("Signal Logique reçu", 200));

            // Recevoir le signal analogique et le convertir en message logique
            recepteur.recevoir(infoAnalogique);

            // Afficher le message logique reçu
            Information<Boolean> infoLogiqueRecue = recepteur.getInformationEmise();
            //System.out.println("Message logique reçu : " + infoLogiqueRecue);

        } catch (InformationNonConformeException e) {
            System.err.println("Erreur lors de la démodulation : " + e.getMessage());
        }
    }

}
