package visualisations;
    
import information.Information;

/** 
 * Classe réalisant l'affichage d'information composée d'éléments
 * réels (float)
 * @author prou
 */
public class SondeAnalogique extends Sonde <Float> {
   
    /**
     * pour construire une sonde analogique
     * @param nom  le nom de la fenêtre d'affichage
     */
    public SondeAnalogique(String nom) {
        super(nom);
    }
    
    /**
     * pour recevoir et afficher l'information transmise par la source
     * qui nous est connectée
     * @param information  l'information  à recevoir
     */
    public void recevoir (Information <Float> information) { 
        informationRecue = information;
        int nbElements = information.nbElements();
        float [] table = new float[nbElements];
        int i = 0;
        for (float f : information) {
                table[i] = f;
                i++;
        }
        new VueCourbe (table, nom); 
    }
}
