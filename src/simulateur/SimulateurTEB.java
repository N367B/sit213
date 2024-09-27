package simulateur;

import java.util.ArrayList;
import java.util.List;
import java.io.FileWriter;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Classe pour simuler la chaîne de transmission et générer la courbe de TEB en fonction du SNR.
 */
public class SimulateurTEB {

    private String typeModulation;
    private int nbSimulations; // Number of simulations per SNR
    private static final Boolean affichage = false;
    private AtomicInteger completedSimulations = new AtomicInteger(0); // Atomic integer for progress tracking
    
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
        
        // Thread pool with a fixed number of threads based on available processors
        int availableThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(availableThreads);
        
        // Calculate total simulations for progress tracking
        int totalSimulations = nbSimulations * (((int) ((snrMax - snrMin) / pasSNR)) + 1);
        completedSimulations.set(0); // Reset the completed simulations count

        // Ouverture du fichier CSV pour écrire les résultats
        try (FileWriter writer = new FileWriter(fichierCSV)) {
            writer.write("Modulation,SNR(dB),TEB\n"); // En-tête du fichier CSV

            // Boucle sur les valeurs de SNR
            List<Future<SimulationResult>> futures = new ArrayList<>();
            for (double snr = snrMin; snr <= snrMax; snr += pasSNR) {
                
                // Pour chaque SNR, on soumet les simulations au thread pool
                for (int simulation = 0; simulation < nbSimulations; simulation++) {
                    final int simIndex = simulation;
                    final double currentSnr = snr;
                    
                    // Soumettre la simulation à l'executorService
                    Future<SimulationResult> future = executorService.submit(() -> {
                        Simulateur simulateur = new Simulateur(new String[] {
                            "-mess", "500", // Taille du message
                            "-form", typeModulation, // Type de modulation
                            "-seed", String.valueOf(simIndex + 1), // Germe différent pour chaque simulation
                            "-nbEch", "30", // Nombre d'échantillons par bit
                            "-ampl", "-1.0", "1.0", // Amplitude
                            "-snrpb", String.valueOf(currentSnr) // SNR par bit
                        });
                        
                        if (affichage) {
                            System.out.println("Simulation " + (simIndex + 1) + " pour modulation " + typeModulation + " avec SNR = " + currentSnr + " dB");
                        }
                        
                        // Exécuter la simulation
                        simulateur.execute();
                        
                        // Calculer le TEB pour cette simulation
                        float teb = simulateur.calculTauxErreurBinaire();

                        // Update the progress bar
                        updateProgressBar(totalSimulations);

                        return new SimulationResult(currentSnr, teb, simIndex);
                    });
                    
                    futures.add(future);
                }
            }
            
            // Récupérer les résultats des simulations
            for (Future<SimulationResult> future : futures) {
                SimulationResult result = future.get(); // Bloque jusqu'à ce que le résultat soit disponible
                tebValues.add(result.teb);
                snrValues.add(result.snr);
                
                if (affichage) {
                    System.out.println("Modulation: " + typeModulation + ", SNR: " + result.snr + " dB, TEB Moyenne: " + result.teb);
                }
                
                // Écriture dans le fichier CSV
                writer.write(typeModulation + "," + result.snr + "," + result.teb + "\n");
            }
            
            if (affichage) {
                System.out.println("Les résultats ont été enregistrés dans : " + fichierCSV);
            }
        } finally {
            // Shut down the executor service gracefully
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.HOURS);
        }
    }

    /**
     * Updates the global progress bar based on completed simulations.
     * @param totalSimulations The total number of simulations to be run.
     */
    private void updateProgressBar(int totalSimulations) {
        int completed = completedSimulations.incrementAndGet();
        int progress = (int) ((completed / (double) totalSimulations) * 100);
        System.out.print("\rProgress: " + progress + "% (" + completed + "/" + totalSimulations + " simulations)");
        if (completed == totalSimulations) {
            System.out.println("\nSimulation complete!");
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

    // Classe pour encapsuler le résultat d'une simulation
    private static class SimulationResult {
        double snr;
        float teb;
        int simulationIndex;

        public SimulationResult(double snr, float teb, int simulationIndex) {
            this.snr = snr;
            this.teb = teb;
            this.simulationIndex = simulationIndex;
        }
    }
}
