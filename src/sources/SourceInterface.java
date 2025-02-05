package sources;

import destinations.DestinationInterface;
import information.*;

/** 
 * Interface d'un composant ayant le comportement d'une source
 * d'informations dont les éléments sont de type T
 * @author prou
 * @param <T> le type des éléments de l'information
 */
public interface SourceInterface <T>  {
   
    /**
     * pour obtenir la dernière information émise par une source.
     * @return une information   
     */
    public Information <T>  getInformationEmise();
   
    /**
     * pour connecter une destination à la source
     * @param destination  la destination à connecter
     */
    public void connecter (DestinationInterface <T> destination);
   
    /**
     * pour émettre l'information contenue dans une source
     * @throws InformationNonConformeException si l'Information comporte une anomalie
     */
    public void emettre() throws InformationNonConformeException; 
}
