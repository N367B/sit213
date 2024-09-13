package transmetteurs;

import sources.*;
import destinations.*;
import information.*;

import java.util.*;

/** 
 * Classe abstraite d'un composant TransmetteurAnalogique
 * Prend des informations de type R (logique) en entrée et émet des informations de type E (analogique) en sortie.
 * @param <R> le type des éléments reçus (logique)
 * @param <E> le type des éléments émis (analogique)
 */
public abstract class TransmetteurAnalogique<R, E> extends Transmetteur<R, E> {

    /**
     * Forme de modulation utilisée (NRZ, NRZT, RZ)
     */
    protected String formeModulation;

    /**
     * Nombre d'échantillons par bit pour la transmission analogique
     */
    protected int nbEchantillons;

    /**
     * Constructeur de base pour un transmetteur analogique
     * @param formeModulation la forme d'onde à utiliser (NRZ, NRZT, RZ)
     * @param nbEchantillons le nombre d'échantillons par bit
     */
    public TransmetteurAnalogique(String formeModulation, int nbEchantillons) {
        super();
        this.formeModulation = formeModulation;
        this.nbEchantillons = nbEchantillons;
    }
}
