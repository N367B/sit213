package simulateur;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Classe pour simuler la chaîne de transmission et générer la courbe de TEB 
 * en fonction du nombre d'échantillons par bit pour NRZ et NRZT.
 */
public class SimulateurNbEch {

    private String typeModulation;
    private int nbSimulations;
    private Double snrpb; // SNR par bit
    private static final Boolean affichage = false;
    private AtomicInteger completedSimulations = new AtomicInteger(0);

    /**
     * Constructeur de la classe SimulateurNbEch.
     *
     * @param typeModulation Type de modulation utilisé (NRZ, NRZT).
     * @param nbSimulations  Nombre de simulations à effectuer pour chaque nombre d'échantillons.
     * @param snrpb          SNR par bit en dB.
     */
    public SimulateurNbEch(String typeModulation, int nbSimulations, Double snrpb) {
        this.typeModulation = typeModulation;
        this.nbSimulations = nbSimulations;
        this.snrpb = snrpb;
    }

    /**
     * Méthode pour générer la courbe de TEB en fonction du nombre d'échantillons par bit.
     *
     * @param nbEchMin   Nombre d'échantillons minimal à tester.
     * @param nbEchMax   Nombre d'échantillons maximal à tester.
     * @param pasNbEch   Pas entre les valeurs de nombre d'échantillons.
     * @param fichierCSV Le fichier dans lequel écrire les résultats.
     * @throws Exception Si une erreur survient pendant la simulation.
     */
    public void genererCourbeTEB(int nbEchMin, int nbEchMax, int pasNbEch, String fichierCSV) throws Exception {
        List<Float> tebValues = new ArrayList<>();
        List<Integer> nbEchValues = new ArrayList<>();

        // Thread pool
        int availableThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(availableThreads);

        // Calcul du nombre total de simulations
        int totalSimulations = nbSimulations * (((nbEchMax - nbEchMin) / pasNbEch) + 1);
        completedSimulations.set(0);

        // Ouverture du fichier CSV pour écrire les résultats
        try (FileWriter writer = new FileWriter(fichierCSV)) {
            writer.write("Modulation,NbEch,TEB\n"); // En-tête du fichier CSV

            // Boucle sur les valeurs de nombre d'échantillons
            List<Future<SimulationResult>> futures = new ArrayList<>();
            for (int nbEch = nbEchMin; nbEch <= nbEchMax; nbEch += pasNbEch) {

                // Pour chaque nombre d'échantillons, on soumet les simulations au thread pool
                for (int simulation = 0; simulation < nbSimulations; simulation++) {
                    final int simIndex = simulation;
                    final int currentNbEch = nbEch;

                    Future<SimulationResult> future = executorService.submit(() -> {
                        Simulateur simulateur = new Simulateur(new String[]{
                                "-mess", "1000",
                                "-form", typeModulation,
                                "-seed", String.valueOf(simIndex + 1),
                                "-nbEch", String.valueOf(currentNbEch),
                                "-snrpb", String.valueOf(snrpb),
                                "-codeur",
                                "-ti", "100", "0.5" // Multi-trajets : décalage de 100 échantillons, amplitude 0.5
                        });

                        simulateur.execute();
                        float teb = simulateur.calculTauxErreurBinaire();

                        updateProgressBar(totalSimulations);

                        return new SimulationResult(currentNbEch, teb, simIndex);
                    });

                    futures.add(future);
                }
            }

            // Récupérer les résultats des simulations
            for (Future<SimulationResult> future : futures) {
                SimulationResult result = future.get();

                tebValues.add(result.teb);
                nbEchValues.add(result.nbEch);

                if (affichage) {
                    System.out.println("Modulation: " + typeModulation + ", NbEch: " + result.nbEch + ", TEB: " + result.teb);
                }

                // Écriture dans le fichier CSV
                writer.write(typeModulation + "," + result.nbEch + "," + result.teb + "\n");
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
     *
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
     * Main method to run the simulation for different modulations and nbEch values.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        try {
            System.out.println("Simulation de la chaîne de transmission avec différents nbEch pour les modulations NRZ et NRZT");

            // Valeurs de nbEch à tester
            int nbEchMin = 5;
            int nbEchMax = 380;
            int pasNbEch = 15;

            int nbSimulations = 1; // Nombre de simulations pour chaque nbEch
            Double snrpb = 15.0; // SNR par bit en dB

            // Créez les objets SimulateurNbEch pour chaque modulation
            SimulateurNbEch simNbEchNRZ = new SimulateurNbEch("NRZ", nbSimulations, snrpb);
            SimulateurNbEch simNbEchNRZT = new SimulateurNbEch("NRZT", nbSimulations, snrpb);

            // Générer les courbes TEB pour chaque modulation et enregistrer dans des fichiers CSV
            simNbEchNRZ.genererCourbeTEB(nbEchMin, nbEchMax, pasNbEch, "resultats/resultats_nbEch_NRZ_codeur.csv");
            simNbEchNRZT.genererCourbeTEB(nbEchMin, nbEchMax, pasNbEch, "resultats/resultats_nbEch_NRZT_codeur.csv");

            System.out.println("Fin de la simulation de la chaîne de transmission");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Classe pour encapsuler le résultat d'une simulation
    private static class SimulationResult {
        int nbEch;
        float teb;
        int simulationIndex;

        public SimulationResult(int nbEch, float teb, int simulationIndex) {
            this.nbEch = nbEch;
            this.teb = teb;
            this.simulationIndex = simulationIndex;
        }
    }
}