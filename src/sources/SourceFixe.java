package sources;

import information.Information;

/**
 * La classe SourceFixe hérite de la classe Source et permet de générer un signal, toujours le même, de type booléen.
 * 
 */
public class SourceFixe extends Source<Boolean> {
    /**
     * Constructeur de la classe SourceFixe
     * @param message le message à envoyer (String), 0 ou 1
     */
    public SourceFixe(String message) {
        super();

        informationGeneree = new Information<Boolean>();

        char[] messageChar = message.toCharArray();

        for (char c : messageChar) {
            switch (c) {
                case '0' -> informationGeneree.add(false);
                case '1' -> informationGeneree.add(true);
                default -> {
                    throw new IllegalArgumentException("Erreur : le message doit être composé de 0 et de 1");
                }
            }
        }

    }
    /**
     * Retourne la liste de booléens générée par la source aléatoire.
     *
     * @return La liste de booléens générée par la source aléatoire.
     */
    public Information<Boolean> getInformationGeneree() {
        return this.informationGeneree;
    }
    /**
     * Main, vérification de la classe SourceFixe
     * @param args les arguments
     */
    public static void main(String[] args) {
        SourceFixe source = new SourceFixe("11001");
        System.out.println(source.informationGeneree);
    }
}