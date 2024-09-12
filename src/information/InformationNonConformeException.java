package information;

/**
 * Exception levée lorsqu'une information reçue n'est pas conforme
 * à ce qui est attendu
 */
public class InformationNonConformeException extends Exception {
   
    /**
     * numéro de version pour la sérialisation
     */
    private static final long serialVersionUID = 1917L;
        /**
         * pour construire une exception sans motif
         */
        public InformationNonConformeException() {
        super();
    }
    /**
     * pour construire une exception avec motif
     * @param motif  le motif
     */
    public InformationNonConformeException(String motif) {
        super(motif);
    }
}
