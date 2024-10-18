import subprocess
import re
import csv
from concurrent.futures import ThreadPoolExecutor, as_completed
from threading import Lock

class SimulateurTEB:
    def __init__(self, type_modulation, nb_simulations):
        """
        Constructor for SimulateurTEB class.

        :param type_modulation: Modulation type used (NRZ, NRZT, RZ)
        :param nb_simulations: Number of simulations to perform for each SNR value
        """
        self.type_modulation = type_modulation
        self.nb_simulations = nb_simulations
        self.lock = Lock()  # For thread-safe progress update
        self.completed_simulations = 0

    def run_simulation(self, mess, snr, use_codeur, nbEch=30):
        """
        Runs a single simulation using the 'simulateur' executable and returns the TEB value.

        :param mess: Message length (int)
        :param snr: Signal-to-noise ratio per bit (float)
        :param use_codeur: Whether to use the codeur (True/False)
        :param nbEch: Number of samples per bit (int)
        :return: A tuple with the current SNR and the TEB value
        """
        command = ['./simulateur', '-mess', str(mess), '-form', self.type_modulation, '-snrpb', str(snr), '-nbEch', str(nbEch)]
        if use_codeur:
            command.append('-codeur')

        try:
            result = subprocess.run(command, capture_output=True, text=True, check=True)
            output = result.stdout

            # Extract the TEB value using regex (handle scientific notation like 1.0E-4)
            teb_match = re.search(r'TEB\s*:\s*([+-]?\d*\.?\d+[eE]?[+-]?\d*)', output)
            if teb_match:
                teb_value = float(teb_match.group(1))
                return teb_value
            else:
                print(f"TEB value not found in the output for SNR = {snr}")
                return None

        except subprocess.CalledProcessError as e:
            print(f"Error during execution for SNR = {snr}: {e}")
            return None

    def generer_courbe_TEB(self, snr_min, snr_max, pas_snr, fichier_csv, mess):
        """
        Generates a curve of TEB as a function of SNR, with and without the codeur.

        :param snr_min: Minimum SNR value (float)
        :param snr_max: Maximum SNR value (float)
        :param pas_snr: Step size between SNR values (float)
        :param fichier_csv: Path to CSV file for saving the results
        :param mess: Message length for the simulation (int)
        """
        snr_values = [snr_min + i * pas_snr for i in range(int((snr_max - snr_min) / pas_snr) + 1)]
        total_simulations = len(snr_values) * self.nb_simulations * 2  # *2 for with and without codeur
        futures = []

        # Create thread pool to run multiple simulations concurrently
        with ThreadPoolExecutor(max_workers=4) as executor, open(fichier_csv, mode='w', newline='') as file:
            writer = csv.writer(file)
            writer.writerow(['Modulation', 'SNR(dB)', 'TEB Without Codeur', 'TEB With Codeur'])  # CSV Header

            for snr in snr_values:
                for _ in range(self.nb_simulations):
                    # Submit both simulations (with and without codeur)
                    future_without = executor.submit(self.run_simulation, mess, snr, False)  # Without codeur
                    future_with = executor.submit(self.run_simulation, mess, snr, True)   # With codeur

                    futures.append((snr, future_without, future_with))

            # Collect the results as they complete
            for future_snr, future_without_codeur, future_with_codeur in futures:
                teb_without_codeur = future_without_codeur.result()
                teb_with_codeur = future_with_codeur.result()

                if teb_without_codeur is not None and teb_with_codeur is not None:
                    # Write both results for the same SNR to the CSV
                    writer.writerow([self.type_modulation, future_snr, teb_without_codeur, teb_with_codeur])

                # Update progress for each completed pair
                with self.lock:
                    self.completed_simulations += 2  # Two simulations done per SNR value
                    self.update_progress(self.completed_simulations, total_simulations)

        print(f"Results saved to {fichier_csv}")

    def update_progress(self, completed, total):
        """
        Updates the progress bar.

        :param completed: Completed simulations count
        :param total: Total number of simulations
        """
        progress = int((completed / total) * 100)
        print(f"\rProgress: {progress}% ({completed}/{total})", end='')


if __name__ == "__main__":
    # Example usage of the SimulateurTEB class
    snr_min = -10.0
    snr_max = 20.0
    pas_snr = 0.1
    mess = 100000
    nb_simulations = 1

    # Create instances for each modulation type
    simTEBNRZ = SimulateurTEB("NRZ", nb_simulations)
    simTEBNRZT = SimulateurTEB("NRZT", nb_simulations)
    simTEBRZ = SimulateurTEB("RZ", nb_simulations)

    # Generate TEB curves and save to CSV
    simTEBNRZ.generer_courbe_TEB(snr_min, snr_max, pas_snr, 'resultats/resultats_NRZ.csv', mess)
    simTEBNRZT.generer_courbe_TEB(snr_min, snr_max, pas_snr, 'resultats/resultats_NRZT.csv', mess)
    simTEBRZ.generer_courbe_TEB(snr_min, snr_max, pas_snr, 'resultats/resultats_RZ.csv', mess)

    print("Simulation complete")
