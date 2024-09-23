package simulateur;

import java.util.ArrayList;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;

public class SimulateurTEB {

    private String typeModulation;

    /**
     * Constructeur de la classe SimulateurTEB.
     * Il prend en paramètre le type de modulation.
     * @param typeModulation Type de modulation utilisé (NRZ, NRZT, RZ).
     */
    public SimulateurTEB(String typeModulation) {
        this.typeModulation = typeModulation;
    }

    /**
     * Méthode pour générer la courbe de TEB en fonction du SNR.
     * À chaque valeur de SNR, un nouveau simulateur est créé pour garantir une simulation indépendante.
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

            // Boucle sur les valeurs de SNR
            for (double snr = snrMax; snr >= snrMin; snr -= pasSNR) {
                // Créer un nouveau simulateur pour chaque SNR
                Simulateur simulateur = new Simulateur(new String[] {
                    "-mess", "300", // Taille du message
                    "-form", typeModulation, // Type de modulation
                    "-seed", "1", // Germe pour générateurs aléatoires
                    "-nbEch", "30", // Nombre d'échantillons par bit
                    "-ampl", "-1.0", "1.0", // Amplitude
                    "-snrpb", String.valueOf(snr) // SNR par bit
                });

                System.out.println("Simulation pour modulation " + typeModulation + " avec SNR = " + snr + " dB");

                // Exécuter la simulation
                simulateur.execute();

                // Calculer le TEB
                float teb = simulateur.calculTauxErreurBinaire();
                tebValues.add(teb);
                snrValues.add(snr);

                // Impression dans le terminal
                System.out.println("Modulation: " + typeModulation + ", SNR: " + snr + " dB, TEB: " + teb);

                // Écriture dans le fichier CSV
                writer.write(typeModulation + "," + snr + "," + teb + "\n");
            }

            System.out.println("Les résultats ont été enregistrés dans : " + fichierCSV);
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
            double snrMin = -45.0;
            double snrMax = 10.0;
            double pasSNR = 0.1;

            // Créez les objets SimulateurTEB pour chaque modulation
            SimulateurTEB simTEBNRZ = new SimulateurTEB("NRZ");
            SimulateurTEB simTEBNRZT = new SimulateurTEB("NRZT");
            SimulateurTEB simTEBRZ = new SimulateurTEB("RZ");

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
