package visualisations;
    
/** 
 * @author B. Prou
 * Updated by E. Cousin - 2021
 *
 */	

import java.util.*;
import javax.swing.*;

/**
 * Classe d'une vue graphique
 */
public class Vue extends JFrame{
    /**
     * 
     */
    private static LinkedList<Vue> lesVues=new LinkedList<Vue>();

    /**
     * 
     */
    private static final long serialVersionUID = 1917L;
    
    /**
     * position en x de la prochaine fenêtre
     */
    protected  static int xPosition = 0;
    /**
     * position en y de la prochaine fenêtre
     */
    protected  static int yPosition = 0;
    /**
     * décalage en y de la prochaine fenêtre
     */
    private static int yDecalage = 200;
 
    /**
     * pour obtenir la position en x de la prochaine fenêtre
     * @return la position en x
     */
    public static int getXPosition() {
        xPosition += 0;
        return xPosition - 0;
    }  

    /**
     * pour obtenir la position en y de la prochaine fenêtre
     * @return la position en y
     */
    public static int getYPosition() {
        yPosition += yDecalage;
        return yPosition - yDecalage;
    }  
       
    
    /**
     * pour construire une vue
     * @param nom  le nom de la fenêtre d'affichage
     */
    public  Vue (String nom) {          
        super(nom);
        lesVues.add(this);
        }

        /**
         * pour réinitialiser la position des fenêtres
         */
        public static void resetPosition(){
        yPosition = 0;
        }
        /**
         * pour fixer la position en x de la prochaine fenêtre
         * @param x  la position en x
         */
        public static void setXPosition(int x){
        xPosition = x;
        }
        /**
         * pour fixer la position en y de la prochaine fenêtre
         */
        public static void kill(){
        for(Vue v:lesVues)
            v.dispose();
        lesVues.clear();
        resetPosition();
    }
   
}
