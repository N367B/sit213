package destinations;

import information.*;


/** 
 * Interface d'un composant ayant le comportement d'une destination
 * d'informations dont les éléments sont de type T
 * @author prou
 * @param <T> le type des éléments de l'information
 */
public  interface DestinationInterface <T>  {   
   
    /**
     * pour obtenir la dernière information reçue par une destination.
     * @return une information   
     */  
    public Information <T>  getInformationRecue(); 
        
    /**
     * pour recevoir une information de la source qui nous est
     * connectée
     * @param information  l'information  à recevoir
     * @throws InformationNonConformeException si l'Information comporte des anomalies
     */
    public void recevoir(Information <T> information) throws InformationNonConformeException;
   
}
