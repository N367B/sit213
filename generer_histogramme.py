import pandas as pd
import matplotlib.pyplot as plt

# Charger le fichier TXT
file_path = 'bruit.txt'  # Remplacez par le chemin de votre fichier
data = pd.read_csv(file_path, header=None, names=['Values'], dtype=float)  # Charger les données en tant que floats

# Créer l'histogramme
plt.figure(figsize=(8, 6))
plt.hist(data['Values'], bins=25, color='orange', edgecolor='black')
plt.title('Histogramme des valeurs continues')
plt.xlabel('Valeurs')
plt.ylabel('Fréquence')

# Enregistrer l'histogramme en tant qu'image
plt.savefig('histogramme.png', dpi=300)

# Afficher le graphique
plt.show()

