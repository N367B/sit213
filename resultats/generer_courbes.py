import pandas as pd
import matplotlib.pyplot as plt
import numpy as np

# Charger les fichiers TEB en fonction du SNR pour chaque modulation
file_nrz = 'resultats_NRZ.csv'  # Fichier contenant SNR et TEB pour NRZ
file_nrzt = 'resultats_NRZT.csv'  # Fichier pour NRZT
file_rz = 'resultats_RZ.csv'  # Fichier pour RZ

last_points = 5  # Nombre de points à ignorer à la fin pour éviter le bord des moyennes mobiles

# Charger les données à partir des fichiers CSV
data_nrz = pd.read_csv(file_nrz)
data_nrzt = pd.read_csv(file_nrzt)
data_rz = pd.read_csv(file_rz)

# Fonction pour lisser les données avec une moyenne mobile
def moving_average(y, window_size=3):
    return np.convolve(y, np.ones(window_size) / window_size, mode='same')

# Créer la figure et les axes pour afficher les courbes sur le même graphique
plt.figure(figsize=(10, 6))

# Plot NRZ with and without Codeur
x_nrz = data_nrz['SNR(dB)'][:-last_points]
y_smooth_nrz_without = moving_average(data_nrz['TEB Without Codeur'], window_size=5)[:-last_points]
y_smooth_nrz_with = moving_average(data_nrz['TEB With Codeur'], window_size=5)[:-last_points]
plt.plot(x_nrz, y_smooth_nrz_without, label='NRZ (Sans Codeur)', color='blue', linestyle='--')
plt.plot(x_nrz, y_smooth_nrz_with, label='NRZ (Avec Codeur)', color='blue', linestyle='-')

# Plot NRZT with and without Codeur
x_nrzt = data_nrzt['SNR(dB)'][:-last_points]
y_smooth_nrzt_without = moving_average(data_nrzt['TEB Without Codeur'], window_size=5)[:-last_points]
y_smooth_nrzt_with = moving_average(data_nrzt['TEB With Codeur'], window_size=5)[:-last_points]
plt.plot(x_nrzt, y_smooth_nrzt_without, label='NRZT (Sans Codeur)', color='green', linestyle='--')
plt.plot(x_nrzt, y_smooth_nrzt_with, label='NRZT (Avec Codeur)', color='green', linestyle='-')

# Plot RZ with and without Codeur
x_rz = data_rz['SNR(dB)'][:-last_points]
y_smooth_rz_without = moving_average(data_rz['TEB Without Codeur'], window_size=5)[:-last_points]
y_smooth_rz_with = moving_average(data_rz['TEB With Codeur'], window_size=5)[:-last_points]
plt.plot(x_rz, y_smooth_rz_without, label='RZ (Sans Codeur)', color='red', linestyle='--')
plt.plot(x_rz, y_smooth_rz_with, label='RZ (Avec Codeur)', color='red', linestyle='-')

# Ajouter le titre et les labels des axes
plt.title('TEB en fonction du SNR pour différentes modulations (Avec et Sans Codeur)')
plt.xlabel('SNR (dB)')
plt.ylabel('Taux d\'Erreur Binaire (TEB)')

# Set y-axis to logarithmic scale
plt.yscale('log')

# Optionally, set x-axis limits if needed
plt.xlim(min(x_nrz), max(x_nrz))

# Ajouter une légende pour différencier les courbes
plt.legend()

# Enregistrer l'image sous forme de fichier PNG
plt.savefig('courbes_teb_smooth_with_without_codeur_logscale.png', dpi=300)

# Afficher le graphique
plt.show()
