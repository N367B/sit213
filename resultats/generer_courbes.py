import pandas as pd
import matplotlib.pyplot as plt
import numpy as np

# Charger les fichiers TEB en fonction du SNR pour chaque modulation
file_nrz = 'resultats_NRZ.csv'  # Fichier contenant SNR et TEB pour NRZ
file_nrzt = 'resultats_NRZT.csv'  # Fichier pour NRZT
file_rz = 'resultats_RZ.csv'  # Fichier pour RZ

last_points=5

# Charger les données à partir des fichiers CSV
data_nrz = pd.read_csv(file_nrz)
data_nrzt = pd.read_csv(file_nrzt)
data_rz = pd.read_csv(file_rz)

# Fonction pour lisser les données avec une moyenne mobile
def moving_average(y, window_size=3):
    return np.convolve(y, np.ones(window_size) / window_size, mode='same')

# Créer la figure et les axes pour afficher les courbes sur le même graphique
plt.figure(figsize=(10, 6))

# Tracer la courbe TEB en fonction du SNR pour NRZ avec lissage
x_nrz = data_nrz['SNR(dB)'][:-last_points]
y_smooth_nrz = moving_average(data_nrz['TEB'], window_size=5)[:-last_points]
plt.plot(x_nrz, y_smooth_nrz, label='NRZ', color='blue')

# Tracer la courbe TEB en fonction du SNR pour NRZT avec lissage
x_nrzt = data_nrzt['SNR(dB)'][:-last_points]
y_smooth_nrzt = moving_average(data_nrzt['TEB'], window_size=5)[:-last_points]
plt.plot(x_nrzt, y_smooth_nrzt, label='NRZT', color='green')

# Tracer la courbe TEB en fonction du SNR pour RZ avec lissage
x_rz = data_rz['SNR(dB)'][:-last_points]
y_smooth_rz = moving_average(data_rz['TEB'], window_size=5)[:-last_points]
plt.plot(x_rz, y_smooth_rz, label='RZ', color='red')

# Ajouter le titre et les labels des axes
plt.title('TEB en fonction du SNR pour différentes modulations (lissage appliqué)')
plt.xlabel('SNR (dB)')
plt.ylabel('Taux d\'Erreur Binaire (TEB)')

# Inverser les axes si nécessaire
plt.gca().invert_xaxis()
plt.gca().invert_yaxis()

# Ajouter une légende pour différencier les courbes
plt.legend()

# Enregistrer l'image sous forme de fichier PNG
plt.savefig('courbes_teb_smooth_moving_average.png', dpi=300)

# Afficher le graphique
plt.show()

