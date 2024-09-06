package sources;

import java.util.Random;
import information.Information;

/**
 * La classe {@code SourceAleatoire} est une source générant des valeurs booléennes aléatoires.
 * Elle étend la classe {@code Source<Boolean>} et permet de générer une liste de valeurs
 * booléennes aléatoires soit avec une graine spécifique, soit avec une génération totalement aléatoire.
 */
public class SourceAleatoire extends Source<Boolean> {
    
    private int tailleListe;  // La taille de la liste de booléens à générer

    /**
     * Constructeur de la classe {@code SourceAleatoire} qui génère une liste de booléens aléatoires
     * sans utiliser de graine spécifique.
     *
     * @param tailleListe La taille de la liste de booléens à générer.
     */
    public SourceAleatoire(int tailleListe) {
        super();
        informationEmise = new Information<Boolean>();  // Utilisation de la classe Information
        Random random = new Random();

        // Générer la liste de booléens aléatoires
        for (int i = 0; i < tailleListe; i++) {
            informationEmise.add(random.nextBoolean());
        }
    }

    /**
     * Constructeur de la classe {@code SourceAleatoire} qui génère une liste de booléens aléatoires
     * en utilisant une graine spécifique pour la génération aléatoire.
     *
     * @param tailleListe La taille de la liste de booléens à générer.
     * @param seed La graine utilisée pour initialiser la génération aléatoire. Peut être {@code null}.
     */
    public SourceAleatoire(int tailleListe, Integer seed) {
        super();
        informationEmise = new Information<Boolean>();  // Utilisation de la classe Information
        Random random;

        // Initialiser l'objet Random avec la graine si elle est fournie, sinon utiliser une nouvelle instance
        if (seed != null) {
            random = new Random(seed);
        } else {
            random = new Random();
        }

        // Générer la liste de booléens aléatoires
        for (int i = 0; i < tailleListe; i++) {
            informationEmise.add(random.nextBoolean());
        }
    }
}
