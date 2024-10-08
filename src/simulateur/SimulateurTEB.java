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
     * Méthode pour générer la courbe de TEB en fonction du SNR, avec et sans codeur.
     * @param snrMin Le SNR minimum à tester.
     * @param snrMax Le SNR maximum à tester.
     * @param pasSNR Le pas entre les valeurs de SNR.
     * @param fichierCSV Le fichier dans lequel écrire les résultats.
     * @throws Exception Si une erreur survient pendant la simulation.
     */
    public void genererCourbeTEB(double snrMin, double snrMax, double pasSNR, String fichierCSV) throws Exception {
        List<Float> tebValuesWithoutCodeur = new ArrayList<>();
        List<Float> tebValuesWithCodeur = new ArrayList<>();
        List<Double> snrValues = new ArrayList<>();
        
        // Thread pool with a fixed number of threads based on available processors
        int availableThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(availableThreads);
        
        // Calculate total simulations for progress tracking
        int totalSimulations = nbSimulations * (((int) ((snrMax - snrMin) / pasSNR)) + 1) * 2; // x2 for with and without codeur
        completedSimulations.set(0); // Reset the completed simulations count

        // Ouverture du fichier CSV pour écrire les résultats
        try (FileWriter writer = new FileWriter(fichierCSV)) {
            writer.write("Modulation,SNR(dB),TEB Without Codeur,TEB With Codeur\n"); // En-tête du fichier CSV

            // Boucle sur les valeurs de SNR
            List<Future<SimulationResult>> futures = new ArrayList<>();
            for (double snr = snrMin; snr <= snrMax; snr += pasSNR) {
                
                // Pour chaque SNR, on soumet les simulations au thread pool
                for (int simulation = 0; simulation < nbSimulations; simulation++) {
                    final int simIndex = simulation;
                    final double currentSnr = snr;
                    
                    // Simulations without the codeur
                    Future<SimulationResult> futureWithoutCodeur = executorService.submit(() -> {
                        Simulateur simulateur = new Simulateur(new String[] {
                            "-mess", "100", // Taille du message
                            "-form", typeModulation, // Type de modulation
                            "-seed", String.valueOf(simIndex + 1), // Germe différent pour chaque simulation
                            "-nbEch", "30", // Nombre d'échantillons par bit
                            "-ampl", "-1.0", "1.0", // Amplitude
                            "-snrpb", String.valueOf(currentSnr) // SNR par bit
                        });
                        
                        // Exécuter la simulation
                        simulateur.execute();
                        
                        // Calculer le TEB pour cette simulation
                        float teb = simulateur.calculTauxErreurBinaire();

                        // Update the progress bar
                        updateProgressBar(totalSimulations);

                        return new SimulationResult(currentSnr, teb, simIndex);
                    });
                    
                    futures.add(futureWithoutCodeur);

                    // Simulations with the codeur
                    Future<SimulationResult> futureWithCodeur = executorService.submit(() -> {
                        Simulateur simulateur = new Simulateur(new String[] {
                            "-mess", "1000", // Taille du message
                            "-form", typeModulation, // Type de modulation
                            "-seed", String.valueOf(simIndex + 1), // Germe différent pour chaque simulation
                            "-nbEch", "30", // Nombre d'échantillons par bit
                            "-ampl", "-1.0", "1.0", // Amplitude
                            "-snrpb", String.valueOf(currentSnr), // SNR par bit
                            "-codeur" // Activer le codeur
                        });
                        
                        // Exécuter la simulation
                        simulateur.execute();
                        
                        // Calculer le TEB pour cette simulation
                        float teb = simulateur.calculTauxErreurBinaire();

                        // Update the progress bar
                        updateProgressBar(totalSimulations);

                        return new SimulationResult(currentSnr, teb, simIndex);
                    });

                    futures.add(futureWithCodeur);
                }
            }
            
            // Récupérer les résultats des simulations
            for (int i = 0; i < futures.size(); i += 2) {
                SimulationResult resultWithoutCodeur = futures.get(i).get();   // Sans codeur
                SimulationResult resultWithCodeur = futures.get(i + 1).get(); // Avec codeur
                
                tebValuesWithoutCodeur.add(resultWithoutCodeur.teb);
                tebValuesWithCodeur.add(resultWithCodeur.teb);
                snrValues.add(resultWithoutCodeur.snr);
                
                if (affichage) {
                    System.out.println("Modulation: " + typeModulation + ", SNR: " + resultWithoutCodeur.snr + " dB, TEB Sans Codeur: " + resultWithoutCodeur.teb + ", TEB Avec Codeur: " + resultWithCodeur.teb);
                }
                
                // Écriture dans le fichier CSV
                writer.write(typeModulation + "," + resultWithoutCodeur.snr + "," + resultWithoutCodeur.teb + "," + resultWithCodeur.teb + "\n");
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
            System.out.println("Simulation de la chaîne de transmission avec différents SNR pour les modulations NRZ, NRZT et RZ, avec et sans codeur");

            // Valeurs de SNR à tester
            double snrMin = -15.0;
            double snrMax = 10.0;
            double pasSNR = 1;

            int nbSimulations = 1; // Nombre de simulations pour chaque SNR

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
