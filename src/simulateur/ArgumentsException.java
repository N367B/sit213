package simulateur;
/**
 * Exception levée lorsqu'une erreur est détectée dans les arguments
 * passés au simulateur
 */
public class ArgumentsException extends Exception {
    
    /**
     * numéro de version pour la sérialisation
     */
    private static final long serialVersionUID = 1789L; 
    
    /**
     * Constructeur pour une exception
     * @param s  le message associé à l'exception
     */
    public ArgumentsException(String s) {
        super(s);
    }
}
