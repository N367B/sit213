package simulateur;

import java.util.ArrayList;
import java.util.List;
import java.io.FileWriter;

/**
 * Classe pour simuler la chaîne de transmission et générer la courbe de TEB en
 * fonction du SNR.
 */
public class SimulateurTEB {

    private String typeModulation;
    private int nbSimulations; // Number of simulations per SNR
    private static final Boolean affichage = true;
    /**
     * Constructeur de la classe SimulateurTEB.
     * Il prend en paramètre le type de modulation et le nombre de simulations.
     * @param typeModulation Type de modulation utilisé (NRZ, NRZT, RZ).
     * @param nbSimulations Nombre de simulations à effectuer pour chaque SNR.
     */
    public SimulateurTEB(String typeModulation, int nbSimulations) {
        this.typeModulation = typeModulation;
        this.nbSimulations = nbSimulations;
    }

    /**
     * Méthode pour générer la courbe de TEB en fonction du SNR.
     * À chaque valeur de SNR, la simulation est exécutée plusieurs fois et l'on calcule la moyenne des TEB.
     * @param snrMin Le SNR minimum à tester.
     * @param snrMax Le SNR maximum à tester.
     * @param pasSNR Le pas entre les valeurs de SNR.
     * @param fichierCSV Le fichier dans lequel écrire les résultats.
     * @throws Exception Si une erreur survient pendant la simulation.
     */
    public void genererCourbeTEB(double snrMin, double snrMax, double pasSNR, String fichierCSV) throws Exception {
        List<Float> tebValues = new ArrayList<>();
        List<Double> snrValues = new ArrayList<>();

        // Ouverture du fichier CSV pour écrire les résultats
        try (FileWriter writer = new FileWriter(fichierCSV)) {
            writer.write("Modulation,SNR(dB),TEB\n"); // En-tête du fichier CSV
            int i = 0;
            int nbSimulationsTotal = nbSimulations*(((int) ((snrMax - snrMin) / pasSNR)) + 1);
            // Boucle sur les valeurs de SNR
            for (double snr = snrMax; snr >= snrMin; snr -= pasSNR) {
                float sommeTEB = 0;
                
                // Effectuer plusieurs simulations pour chaque SNR
                for (int simulation = 0; simulation < nbSimulations; simulation++) {
                	i++;
                	// Affichage du numéro de la simulation sur le nombre total de simulations
                	System.out.println("[" + (i) + "/" + (nbSimulationsTotal) + "]\r");
                    // Créer un nouveau simulateur pour chaque SNR
                    Simulateur simulateur = new Simulateur(new String[] {
                        "-mess", "1000", // Taille du message
                        "-form", typeModulation, // Type de modulation
                        "-seed", String.valueOf(simulation + 1), // Germe différent pour chaque simulation
                        "-nbEch", "30", // Nombre d'échantillons par bit
                        "-ampl", "-1.0", "1.0", // Amplitude
                        "-snrpb", String.valueOf(snr) // SNR par bit
                    });
                    
                    if (affichage) {
                    	System.out.println("Simulation " + (simulation + 1) + " pour modulation " + typeModulation + " avec SNR = " + snr + " dB");
                    }
                    // Exécuter la simulation
                    simulateur.execute();

                    // Calculer le TEB pour cette simulation
                    float teb = simulateur.calculTauxErreurBinaire();
                    sommeTEB += teb;
                }

                // Calculer le TEB moyen
                float tebMoyenne = sommeTEB / nbSimulations;
                tebValues.add(tebMoyenne);
                snrValues.add(snr);
                
                if (affichage) {
                    System.out.println("Modulation: " + typeModulation + ", SNR: " + snr + " dB, TEB Moyenne: " + tebMoyenne);
                    }

                // Écriture dans le fichier CSV
                writer.write(typeModulation + "," + snr + "," + tebMoyenne + "\n");
            }
			if (affichage) {
	            System.out.println("Les résultats ont été enregistrés dans : " + fichierCSV);
			}
        }
    }

    /**
     * Main method to run the simulation for different modulations and SNR values.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        try {
            System.out.println("Simulation de la chaîne de transmission avec différents SNR pour les modulations NRZ, NRZT et RZ");

            // Valeurs de SNR à tester
            double snrMin = -10.0;
            double snrMax = 0.0;
            double pasSNR = 0.1;

            int nbSimulations = 10; // Nombre de simulations pour chaque SNR

            // Créez les objets SimulateurTEB pour chaque modulation
            SimulateurTEB simTEBNRZ = new SimulateurTEB("NRZ", nbSimulations);
            SimulateurTEB simTEBNRZT = new SimulateurTEB("NRZT", nbSimulations);
            SimulateurTEB simTEBRZ = new SimulateurTEB("RZ", nbSimulations);

            // Générer les courbes TEB pour chaque modulation et enregistrer dans des fichiers CSV
            simTEBNRZ.genererCourbeTEB(snrMin, snrMax, pasSNR, "resultats/resultats_NRZ.csv");
            simTEBNRZT.genererCourbeTEB(snrMin, snrMax, pasSNR, "resultats/resultats_NRZT.csv");
            simTEBRZ.genererCourbeTEB(snrMin, snrMax, pasSNR, "resultats/resultats_RZ.csv");

            System.out.println("Fin de la simulation de la chaîne de transmission");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
