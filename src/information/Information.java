package information;

import java.util.*;

/** 
 * La classe Information permet de construire une information composée d'éléments de type T. *  
 * @author prou
 * @param <T> le type des éléments de l'information
 */	
public  class Information <T>  implements Iterable <T> {
   
    private ArrayList <T> content;
   
    /**
     * pour construire une information vide
     */
    public Information() {
        this.content = new ArrayList <T> (); 
    }
   
    /**
     * pour construire à partir d'un tableau de T une information
     * @param content le tableau d'éléments pour initialiser l'information construite
     */
    public Information(T [] content) {
        this.content = new ArrayList <T> (); 
        for (int i = 0; i < content.length; i++) {
                this.content.addLast(content[i]);
        }
    }
   
    /**
     * pour connaître le nombre d'éléments d'une information
     * @return le nombre d'éléments de l'information
     */
    public int nbElements() {
        return this.content.size();
    }
   
    /**
     * pour renvoyer un élément d'une information
     * @param i le rang de l'information à renvoyer (à partir de 0)
     * @return le ieme élément de l'information
     */
    public T iemeElement(int i) {
        return this.content.get(i);
    }
   
    /**
     * pour modifier le ième élément d'une information
     * @param i le rang de l'information à modifier (à partir de 0)
     * @param v la nouvelle ieme information
     */ 
    public void setIemeElement(int i, T v) {
        this.content.set(i, v);
    }
   
    /**
     * pour ajouter un élément à la fin de l'information 
     * @param valeur  l'élément à rajouter
     */
    public void add(T valeur) {
        this.content.add(valeur);
    }
   
   
    /**
     * pour comparer l'information courante avec une autre information
     * @param o  l'information  avec laquelle se comparer
     * @return "true" si les 2 informations contiennent les mêmes
     * éléments aux mêmes places; "false" dans les autres cas
     */	 
    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        if (!(o instanceof Information))
                return false;
        Information <T> information =  (Information <T>) o;
        if (this.nbElements() != information.nbElements())
                return false;
        for (int i = 0; i < this.nbElements(); i++) {
                if (!this.iemeElement(i).equals(information.iemeElement(i)))
            return false;
        }
        return true;
    }
   
    /**
     * pour afficher une information
     * @return representation de l'information sous forme de String
     */
    public String toString() {
        String s = "";
        for (int i = 0; i < this.nbElements(); i++) {
                s += " " + this.iemeElement(i);
        }
        return s;
        }
    
        /**
         * pour utilisation du "for each" 
         */
        public Iterator <T> iterator() {
        return content.iterator();
    }
}
