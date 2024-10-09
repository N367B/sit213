package transmetteurs;

import destinations.*;
import information.*;
import sources.*;
import visualisations.*;

/**
 * La classe CodageEmission permet de transformer un bit reçu en une sequence de 3 bits.
 */
public class CodageEmission extends Transmetteur<Boolean, Boolean> {

	/**
	 * Constructeur de CodageEmission
	 */
    public CodageEmission() {
    	super();
    }
    
    private Information<Boolean> informationBitCode = new Information<>();

    /**
     * Prend la liste d'information en parametre et converti le bit en une sequence de 3 bits
     */
	@Override
	public void recevoir(Information<Boolean> information) throws InformationNonConformeException {
		
		if (information == null || information.nbElements() == 0) {
            throw new InformationNonConformeException("L'information reçue est nulle ou invalide.");
        }
		this.informationRecue = information;
		informationBitCode = new Information<>();
		
		for (boolean bit : information) {
			if (bit) { //Si le bit reçu est 1
				informationBitCode.add(true);
				informationBitCode.add(false);
				informationBitCode.add(true);
			}else { //Si le bit reçu est 0
				informationBitCode.add(false);
				informationBitCode.add(true);
				informationBitCode.add(false);
			}
		}
		this.emettre();
	}

	/**
	 * Emet la sequence de 3 bits
	 */
	@Override
	public void emettre() throws InformationNonConformeException {
		 if (this.informationRecue == null) {
	            throw new InformationNonConformeException("L'information reçue est nulle.");
	        }
	        for (DestinationInterface<Boolean> destinationConnectee : destinationsConnectees) {
	            destinationConnectee.recevoir(informationBitCode);
	        }
	        informationEmise = informationBitCode; 
	}
    
    /**
     * Main de la classe CodageEmission
     * @param args les arguments de la ligne de commande
     */
    public static void main(String[] args) {
        try {
            // Create a fixed source with a simple message (e.g., "101")
            Source<Boolean> source = new SourceFixe("101");

            // Create the CodageEmission (the transmitter that will encode the bits)
            CodageEmission codeur = new CodageEmission();

            // Create a destination to receive the encoded message
            DestinationFinale destination = new DestinationFinale();

            // Connect the source to the codeur
            source.connecter(codeur);

            // Connect the codeur to the destination
            codeur.connecter(destination);

            // Emit the message from the source
            source.emettre();

            // Get and print the received information from the destination
            Information<Boolean> informationRecue = destination.getInformationRecue();
            //System.out.println("Message encodé reçu par la destination :");
            for (Boolean bit : informationRecue) {
                bit = bit == null ? false : bit;
                //System.out.print(bit ? "1" : "0");
            }
            //System.out.println();  // New line after the message
            
        } catch (InformationNonConformeException e) {
            System.err.println("Erreur lors de l'émission ou la réception de l'information : " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}