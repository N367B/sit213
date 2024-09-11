package simulateur;

import destinations.*;
import information.*;
import java.util.Objects;
import sources.*;
import transmetteurs.*;
import visualisations.*;

/** La classe Simulateur permet de construire et simuler une chaîne de
 * transmission composée d'une Source, d'un nombre variable de
 * Transmetteur(s) et d'une Destination.
 * @author cousin
 * @author prou
 *
 */
public class Simulateur {
          
    /** indique si le Simulateur utilise des sondes d'affichage */
    private boolean affichage = false;
    
    /** indique si le Simulateur utilise un message généré de manière aléatoire (message imposé sinon) */
    private boolean messageAleatoire = true;
    
    /** indique si le Simulateur utilise un germe pour initialiser les générateurs aléatoires */
    private boolean aleatoireAvecGerme = false;
    
    /** la valeur de la semence utilisée pour les générateurs aléatoires */
    private Integer seed = null; // pas de semence par défaut
    
    /** la longueur du message aléatoire à transmettre si un message n'est pas imposé */
    private int nbBitsMess = 100; 
    
    /** la chaîne de caractères correspondant à m dans l'argument -mess m */
    private String messageString = "100";
   
       
    /** le  composant Source de la chaine de transmission */
    private Source <Boolean>  source = null;
    
    /** le  composant Transmetteur parfait logique de la chaine de transmission */
    private Transmetteur <Boolean, Boolean>  transmetteurLogique = null;
    
    /** le  composant Destination de la chaine de transmission */
    private Destination <Boolean>  destination = null;
       
   
    /** Le constructeur de Simulateur construit une chaîne de
     * transmission composée d'une Source <Boolean>, d'une Destination
     * <Boolean> et de Transmetteur(s) [voir la méthode
     * analyseArguments]...  <br> Les différents composants de la
     * chaîne de transmission (Source, Transmetteur(s), Destination,
     * Sonde(s) de visualisation) sont créés et connectés.
     * @param args le tableau des différents arguments.
     *
     * @throws ArgumentsException si un des arguments est incorrect
     *
     */   
    public  Simulateur(String [] args) throws ArgumentsException {
        // analyser et récupérer les arguments   	
        analyseArguments(args);
        if (messageAleatoire) {
            if (aleatoireAvecGerme) {
                source = new SourceAleatoire(nbBitsMess, seed);
            }
            else {
                source = new SourceAleatoire(nbBitsMess);
            }
        }
        else {
            source = new SourceFixe(messageString);
        }
        transmetteurLogique = new TransmetteurParfait();
        source.connecter(transmetteurLogique);
        destination = new DestinationFinale();
        transmetteurLogique.connecter(destination);
        if (affichage) {
            source.connecter(new SondeLogique("Source", 200));
            transmetteurLogique.connecter(new SondeLogique("Transmetteur", 200));
            //destination.connecter(new SondeLogique("Destination", 200));
        }
    }
   
   
   
    /** La méthode analyseArguments extrait d'un tableau de chaînes de
     * caractères les différentes options de la simulation.  <br>Elle met
     * à jour les attributs correspondants du Simulateur.
     *
     * @param args le tableau des différents arguments.
     * <br>
     * <br>Les arguments autorisés sont : 
     * <br> 
     * <dl>
     * <dt> -mess m  </dt><dd> m (String) constitué de 7 ou plus digits à 0 | 1, le message à transmettre</dd>
     * <dt> -mess m  </dt><dd> m (int) constitué de 1 à 6 digits, le nombre de bits du message "aléatoire" à transmettre</dd> 
     * <dt> -s </dt><dd> pour demander l'utilisation des sondes d'affichage</dd>
     * <dt> -seed v </dt><dd> v (int) d'initialisation pour les générateurs aléatoires</dd> 
     * </dl>
     *
     * @throws ArgumentsException si un des arguments est incorrect.
     *
     */   
    private  void analyseArguments(String[] args)  throws  ArgumentsException {

        for (int i=0;i<args.length;i++){ // traiter les arguments 1 par 1

            if (args[i].matches("-s")){
                affichage = true;
            }
            
            else if (args[i].matches("-seed")) {
                aleatoireAvecGerme = true;
                i++; 
                // traiter la valeur associee
                try { 
                    seed = Integer.valueOf(args[i]);
                }
                catch (Exception e) {
                    throw new ArgumentsException("Valeur du parametre -seed  invalide :" + args[i]);
                }           		
            }

            else if (args[i].matches("-mess")){
                i++; 
                // traiter la valeur associee
                messageString = args[i];
                if (args[i].matches("[0,1]{7,}")) { // au moins 7 digits
                    messageAleatoire = false;
                    nbBitsMess = args[i].length();
                } 
                else if (args[i].matches("[0-9]{1,6}")) { // de 1 à 6 chiffres
                    messageAleatoire = true;
                    nbBitsMess = Integer.valueOf(args[i]);
                    if (nbBitsMess < 1) 
                        throw new ArgumentsException ("Valeur du parametre -mess invalide : " + nbBitsMess);
                }
                else 
                    throw new ArgumentsException("Valeur du parametre -mess invalide : " + args[i]);
            }
            
            //TODO : ajouter ci-après le traitement des nouvelles options

            else throw new ArgumentsException("Option invalide :"+ args[i]);
        }
      
    }
     
    
       
    /** La méthode execute effectue un envoi de message par la source
     * de la chaîne de transmission du Simulateur.
     *
     * @throws Exception si un problème survient lors de l'exécution
     *
     */ 
    public void execute() throws Exception {      
         
        source.emettre();
        transmetteurLogique.recevoir(source.getInformationEmise());
        transmetteurLogique.emettre();
        destination.recevoir(transmetteurLogique.getInformationEmise());

        
    }
   
              
       
    /** La méthode qui calcule le taux d'erreur binaire en comparant
     * les bits du message émis avec ceux du message reçu.
     *
     * @return  La valeur du Taux dErreur Binaire.
     */   	   
    public float  calculTauxErreurBinaire() {

        Information<Boolean> informationEmise = source.getInformationEmise();
        Information<Boolean> informationRecue = destination.getInformationRecue();  

        if (informationRecue == null) {
            System.err.println("Erreur : informationRecue est null. La transmission n'a peut-être pas encore eu lieu.");
            return -1.0f; // Ou une autre valeur indiquant une erreur
        }
        int nbErreurs = 0;
        for (int i = 0; i < informationEmise.nbElements(); i++) {
            if (!Objects.equals(informationEmise.iemeElement(i), informationRecue.iemeElement(i))) {
                nbErreurs++;
            }
        }

        return (float) nbErreurs / informationEmise.nbElements();

        //return  0.0f;
    }
   
   
    // Getter for messageAleatoire
    public boolean isMessageAleatoire() {
        return messageAleatoire;
    }

    // Getter for aleatoireAvecGerme
    public boolean isAleatoireAvecGerme() {
        return aleatoireAvecGerme;
    }

    // Getter for seed
    public Integer getSeed() {
        return seed;
    }

    // Getter for source
    public Source<Boolean> getSource() {
        return source;
    }

    // Getter for destination
    public Destination<Boolean> getDestination() {
        return destination;
    }
   
    /** La fonction main instancie un Simulateur à l'aide des
     *  arguments paramètres et affiche le résultat de l'exécution
     *  d'une transmission.
     *  @param args les différents arguments qui serviront à l'instanciation du Simulateur.
     */
    public static void main(String [] args) { 

        Simulateur simulateur = null;

        try {
            simulateur = new Simulateur(args);
        }
        catch (Exception e) {
            System.out.println(e); 
            System.exit(-1);
        } 

        try {
            simulateur.execute();
            String s = "java  Simulateur  ";
            for (int i = 0; i < args.length; i++) {
                s += args[i] + "  ";
            }
            System.out.println(s + "  =>   TEB : " + simulateur.calculTauxErreurBinaire());
        }
        catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
            System.exit(-2);
        }              	
    }
}

