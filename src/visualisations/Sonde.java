package visualisations;
    
import destinations.Destination;
import information.Information;

/** 
 * Classe Abstraite d'un composant destination réalisant un affichage
 * @author prou
 * @param <T> le type des éléments de l'information
 */
public  abstract class Sonde <T> extends Destination <T> {
   
    /**
     * nom de la fenêtre d'affichage
     */   
    protected String nom;
   
    /**
     * pour construire une sonde
     * @param nom  le nom de la fenêtre d'affichage
     */   
    public Sonde(String nom) {
        this.nom = nom;
    }
        
    /**
     * pour recevoir et afficher l'information transmise par la source
     * qui nous est connectée
     * @param information  l'information  à recevoir
     */   
    public abstract void recevoir(Information <T> information);     
}
