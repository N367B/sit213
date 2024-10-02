package simulateur;

import destinations.*;
import information.*;
import java.util.Objects;
import sources.*;
import transmetteurs.*;
import visualisations.*;

import java.util.List;
import java.util.ArrayList;

/** La classe Simulateur permet de construire et simuler une chaîne de
 * transmission composée d'une Source, d'un nombre variable de
 * Transmetteur(s) et d'une Destination.
 * @author cousin
 * @author prou
 *
 */
public class Simulateur {
    
    /** indique si le Simulateur utilise le simulateur logique */
    private boolean defautLogique = true;

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
   
    /** le composant Source de la chaine de transmission */
    private Source<Boolean> source = null;
    
    /** le composant Transmetteur parfait logique de la chaine de transmission */
    private Transmetteur<Boolean, Boolean> transmetteurLogique = null;
    
    /** le composant Transmetteur parfait analogique de la chaine de transmission */
    private TransmetteurAnalogiqueParfait transmetteurAnalogique = null;
    
    /** le composant Transmetteur parfait analogique bruité de la chaine de transmission */
    private TransmetteurAnalogiqueBruite transmetteurAnalogiqueBruite = null;
    
    /** L'emetteur de la chaine de transmission */
    private Emetteur emetteur = null;
    /** Le recepteur de la chaine de transmission */
    private Recepteur recepteur = null;
    
    /** le composant Destination de la chaine de transmission */
    private Destination<Boolean> destination = null;
    
    /** le type de modulation à utiliser (NRZ, NRZT, RZ) */
    private String typeModulation = "RZ";
    
    /** L'amplitude du signal analogique pour représenter un bit '1'. */
    private float Amax = 1.0f;
    
    /** L'amplitude du signal analogique pour représenter un bit '0'. */
    private float Amin = 0.0f;
    
    /** le nombre d'échantillons par bit */
    private int nbEchantillonsParBit = 30;
    
    /** Le SNR par bit en dB */
    private double snrParBit = 0;

    /** Le SNR en dB */
    private double snr = 0;

    /** Les trajets indirects pour le transmetteur analogique à trajets multiples */
    private List<float[]> trajetsIndirects = new ArrayList<>();


    /** Le constructeur de Simulateur construit une chaîne de
     * transmission composée d'une Source &lt;Boolean&gt;, d'une Destination
     * &lt;Boolean&gt; et de Transmetteur(s) [voir la méthode
     * analyseArguments].  <br> Les différents composants de la
     * chaîne de transmission (Source, Transmetteur(s), Destination,
     * Sonde(s) de visualisation) sont créés et connectés.
     * @param args le tableau des différents arguments.
     *
     * @throws ArgumentsException si un des arguments est incorrect
     *
     */   
    public Simulateur(String[] args) throws ArgumentsException {
        // analyser et récupérer les arguments   	
        analyseArguments(args);
        // Générer la source
        if (messageAleatoire) {
            if (aleatoireAvecGerme) {
                source = new SourceAleatoire(nbBitsMess, seed);
            } else {
                source = new SourceAleatoire(nbBitsMess);
            }
        } else {
            source = new SourceFixe(messageString);
        }
        
        if (defautLogique) {
            //System.out.println("Simulateur logique parfait");
            simulateurLogiqueParfait();
            } else if (snrParBit == 0 && snr == 0 && trajetsIndirects.isEmpty()) {
                //System.out.println("Simulateur analogique parfait");
                simulateurAnalogiqueParfait();
            } else if (!trajetsIndirects.isEmpty()) {
                //System.out.println("Simulateur multi-trajet");
                simulateurMultiTrajet();
            } else if (snrParBit != 0 || snr != 0) {
                //System.out.println("Simulateur analogique bruité");
                simulateurAnalogiqueBruite();
            } else {
                //System.out.println("Paramètres : ", snr, snrParBit, trajetsIndirects);
                throw new ArgumentsException("Erreur lors de la configuration des paramètres de la simulation.");
            }

        /*// Type de transmission
		if (typeModulation == null) {
			simulateurLogiqueParfait();
		} else{
			if (snrParBit == 0 && trajetsIndirects.isEmpty()) {
				simulateurAnalogiqueParfait();
			} else if (!trajetsIndirects.isEmpty()) {
                simulateurMultiTrajet();
            }
            else {
				simulateurAnalogiqueBruite();
			}
		}*/

    }
    
    
    private void simulateurLogiqueParfait() {
        transmetteurLogique = new TransmetteurParfait();
        source.connecter(transmetteurLogique);
        destination = new DestinationFinale();
        transmetteurLogique.connecter(destination);
        
        if (affichage) {
            source.connecter(new SondeLogique("Source", 200));
            transmetteurLogique.connecter(new SondeLogique("Transmetteur", 200));
        }
    }
    
	private void simulateurAnalogiqueParfait() {
		emetteur = new Emetteur(Amin, Amax, nbEchantillonsParBit, typeModulation);
		source.connecter(emetteur);
		transmetteurAnalogique = new TransmetteurAnalogiqueParfait();
		emetteur.connecter(transmetteurAnalogique);
		recepteur = new Recepteur(Amin, Amax, nbEchantillonsParBit, typeModulation);
		transmetteurAnalogique.connecter(recepteur);
		destination = new DestinationFinale();
		recepteur.connecter(destination);
		
		if (affichage) {
            source.connecter(new SondeLogique("Source", 200));
            emetteur.connecter(new SondeAnalogique("Emetteur"));
            transmetteurAnalogique.connecter(new SondeAnalogique("Transmetteur"));
            recepteur.connecter(new SondeLogique("Recepteur", 200));
		}
	}
	
	private void simulateurAnalogiqueBruite() {
	    // Calculate the SNR from Eb/N0
        if (snr == 0){
	        snr = snrParBit - 10 * Math.log10(nbEchantillonsParBit / 2.0); // Convert Eb/N0 to SNR
        }
		//double snr = snrParBit;
	    //System.out.println("SNR utilisé dans la simulation : " + snr);
	    emetteur = new Emetteur(Amin, Amax, nbEchantillonsParBit, typeModulation);
	    source.connecter(emetteur);
	    transmetteurAnalogiqueBruite = new TransmetteurAnalogiqueBruite(snr, nbEchantillonsParBit);
	    emetteur.connecter(transmetteurAnalogiqueBruite);
	    recepteur = new Recepteur(Amin, Amax, nbEchantillonsParBit, typeModulation);
	    transmetteurAnalogiqueBruite.connecter(recepteur);
	    destination = new DestinationFinale();
	    recepteur.connecter(destination);
	    
	    if (affichage) {
	        source.connecter(new SondeLogique("Source", 200));
	        emetteur.connecter(new SondeAnalogique("Émetteur"));
	        transmetteurAnalogiqueBruite.connecter(new SondeAnalogique("Transmetteur"));
	        recepteur.connecter(new SondeLogique("Récepteur", 200));
	    }
	}

    private void simulateurMultiTrajet() {
        emetteur = new Emetteur(Amin, Amax, nbEchantillonsParBit, typeModulation);
        source.connecter(emetteur);

        TransmetteurAnalogiqueMultiTrajet transmetteurAnalogiqueMultiTrajet = new TransmetteurAnalogiqueMultiTrajet(trajetsIndirects);
        emetteur.connecter(transmetteurAnalogiqueMultiTrajet);

        TransmetteurAnalogiqueBruite transmetteurAnalogiqueBruite = new TransmetteurAnalogiqueBruite(snrParBit, nbEchantillonsParBit);

        transmetteurAnalogiqueMultiTrajet.connecter(transmetteurAnalogiqueBruite);

        recepteur = new Recepteur(Amin, Amax, nbEchantillonsParBit, typeModulation);
        transmetteurAnalogiqueBruite.connecter(recepteur);
        
        destination = new DestinationFinale();
        recepteur.connecter(destination);

        if (affichage) {
            source.connecter(new SondeLogique("Source", 200));
            emetteur.connecter(new SondeAnalogique("Émetteur"));
            transmetteurAnalogiqueMultiTrajet.connecter(new SondeAnalogique("Transmetteur Multi-Trajet"));
            transmetteurAnalogiqueBruite.connecter(new SondeAnalogique("Transmetteur Bruité"));
            recepteur.connecter(new SondeLogique("Récepteur", 200));
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
     * <dt> -mess m  </dt><dd> m (int) constitué de 1 à 6 digits, le nombre de bits du message "aléatoire" à transmettre</dd> 
     * <dt> -s </dt><dd> pour demander l'utilisation des sondes d'affichage</dd>
     * <dt> -seed v </dt><dd> v (int) d'initialisation pour les générateurs aléatoires</dd> 
     * </dl>
     *
     * @throws ArgumentsException si un des arguments est incorrect.
     *
     */   
    private void analyseArguments(String[] args) throws ArgumentsException {
        for (int i = 0; i < args.length; i++) {
            if (args[i].matches("-s")) {
                affichage = true;
            } else if (args[i].matches("-seed")) {
                aleatoireAvecGerme = true;
                i++;
                try {
                    seed = Integer.valueOf(args[i]);
                } catch (Exception e) {
                    throw new ArgumentsException("Valeur du paramètre -seed invalide : " + args[i]);
                }
            } else if (args[i].matches("-mess")) {
                i++;
                messageString = args[i];
                if (args[i].matches("[0,1]{7,}")) {
                    messageAleatoire = false;
                    nbBitsMess = args[i].length();
                } else if (args[i].matches("[0-9]{1,6}")) {
                    messageAleatoire = true;
                    nbBitsMess = Integer.valueOf(args[i]);
                    if (nbBitsMess < 1) {
                        throw new ArgumentsException("Valeur du paramètre -mess invalide : " + nbBitsMess);
                    }
                } else {
                    throw new ArgumentsException("Valeur du paramètre -mess invalide : " + args[i]);
                }
            } else if (args[i].matches("-form")) {
                i++;
                defautLogique = false;
                typeModulation = args[i]; // NRZ, NRZT, or RZ
                if (!typeModulation.matches("NRZ|NRZT|RZ")) {
                    throw new ArgumentsException("Valeur du paramètre -form invalide : " + typeModulation);
                }
            } else if (args[i].matches("-nbEch")) {
                i++;
                defautLogique = false;
                try {
                    nbEchantillonsParBit = Integer.valueOf(args[i]);
                } catch (Exception e) {
                    throw new ArgumentsException("Valeur du paramètre -nbEch invalide : " + args[i]);
                }
            } else if (args[i].matches("-ampl")) {
                i++;
                defautLogique = false;
                try {
                    Amin = Float.valueOf(args[i]);
                    i++;
                    Amax = Float.valueOf(args[i]);
                } catch (Exception e) {
                    throw new ArgumentsException("Valeur du paramètre -ampl invalide : " + args[i]);
                }
                if (Amin >= Amax) {
                    throw new ArgumentsException("Valeur du paramètre -ampl invalide : Amin doit être inférieur à Amax");
                }
            } else if (args[i].matches("-snrpb")) {
                i++;
                defautLogique = false;
                if (snr != 0) {
                    throw new ArgumentsException("Vous ne pouvez pas spécifier à la fois -snrpb et -snr.");
                }
                try {
                    snrParBit = Double.valueOf(args[i]);
                } catch (Exception e) {
                    throw new ArgumentsException("Valeur du paramètre -snrpb invalide : " + args[i]);
                }
            } else if (args[i].matches("-snr")){
                i++;
                defautLogique = false;
                if (snrParBit != 0) {
                    throw new ArgumentsException("Vous ne pouvez pas spécifier à la fois -snrpb et -snr.");
                }
                try {
                    snr = Double.valueOf(args[i]);
                } catch (Exception e) {
                    throw new ArgumentsException("Valeur du paramètre -snr invalide : " + args[i]);
            }
            } else if (args[i].matches("-ti")) {
                i++;
                defautLogique = false;
                // Parse multi-path parameters (pairs of dt and ar)
                while (i < args.length && args[i].matches("[0-9]+")) {
                    int dt = Integer.valueOf(args[i]); // Delay
                    i++;
                    if (i < args.length && args[i].matches("[0-9]*\\.?[0-9]+")) {
                        float ar = Float.valueOf(args[i]); // Attenuation
                        trajetsIndirects.add(new float[]{dt, ar});
                    } else {
                        throw new ArgumentsException("Valeur du paramètre -ti invalide : " + args[i]);
                    }
                    i++;
                }
                i--; // Step back to not skip next argument
                if (trajetsIndirects.size() > 5) {
                    throw new ArgumentsException("Vous ne pouvez spécifier que jusqu'à 5 trajets indirects.");
                }
            } else {
                throw new ArgumentsException("Option invalide : " + args[i]);
            }
        }
    }    
    /** La méthode execute effectue un envoi de message par la source
     * de la chaîne de transmission du Simulateur.
     * @throws Exception si un problème survient lors de l'exécution
     */
    public void execute() throws Exception {      
        source.emettre();
        //System.out.println(typeModulation);
        //transmetteurLogique.recevoir(source.getInformationEmise());
        //transmetteurLogique.emettre();
        //destination.recevoir(transmetteurLogique.getInformationEmise());
    }
   
    /** La méthode qui calcule le taux d'erreur binaire en comparant
     * les bits du message émis avec ceux du message reçu.
     * @return La valeur du Taux d'Erreur Binaire.
     * @throws Exception si un problème survient lors du calcul.
     */   	   
    public float calculTauxErreurBinaire() throws Exception{
        Information<Boolean> informationEmise = source.getInformationEmise();
        Information<Boolean> informationRecue = destination.getInformationRecue();
        if (informationRecue == null) {
        	throw new Exception("Aucune information reçue");
        }
        int nbErreurs = 0;
        for (int i = 0; i < informationEmise.nbElements(); i++) {
            if (!Objects.equals(informationEmise.iemeElement(i), informationRecue.iemeElement(i))) {
                nbErreurs++;
            }
        }
        return (float) nbErreurs / informationEmise.nbElements();
    }
    
    /**
     * Renvoie true si le message est aléatoire.
     * @return true si le message est aléatoire.
     */
    public boolean isMessageAleatoire() {
        return messageAleatoire;
    }

    /**
     * Renvoie true si la simulation utilise un germe aléatoire.
     * @return true si un germe aléatoire est utilisé.
     */
    public boolean isAleatoireAvecGerme() {
        return aleatoireAvecGerme;
    }

    /**
     * Renvoie la valeur du germe utilisé pour les générateurs aléatoires.
     * @return la valeur du germe.
     */
    public Integer getSeed() {
        return seed;
    }

    /**
     * Renvoie la source de la chaîne de transmission.
     * @return la source de la chaîne de transmission.
     */
    public Source<Boolean> getSource() {
        return source;
    }

    /**
     * Renvoie la destination de la chaîne de transmission.
     * @return la destination de la chaîne de transmission.
     */
    public Destination<Boolean> getDestination() {
        return destination;
    }
   /**
    * Setter pour snrParBit
    * @param snrParBit le SNR par bit en dB
    */
	public void setSnrParBit(double snrParBit) {
		this.snrParBit = snrParBit;
	}
    
    /** La fonction main instancie un Simulateur à l'aide des arguments paramètres 
     * et affiche le résultat de l'exécution d'une transmission.
     * @param args les différents arguments qui serviront à l'instanciation du Simulateur.
     */
    public static void main(String [] args) { 
        Simulateur simulateur = null;
        try {
            simulateur = new Simulateur(args);
        } catch (Exception e) {
            System.out.println(e); 
            System.exit(-1);
        }
        try {
            simulateur.execute();
            String s = "java Simulateur ";
            for (int i = 0; i < args.length; i++) {
                s += args[i] + "  ";
            }
            System.out.println(s + "  =>   TEB : " + simulateur.calculTauxErreurBinaire());
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
            System.exit(-2);
        }
    }
}
