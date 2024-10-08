package transmetteurs;

import destinations.*;
import information.*;
import sources.*;
import visualisations.*;

/**
 * La classe Emetteur permet de convertir une information logique (booléenne) en un signal analogique.
 */
public class DecodageReception extends Transmetteur<Boolean, Boolean> {

    /**
     * Constructeur de DecodageReception
     */
    public DecodageReception() {
        super();
    }

    private Information<Boolean> informationBitCode = new Information<>();

    /**
     * declaration des etats pour l'automate
     */
    enum Etat{
        Q0, Q1, Q2, Q3, Q4, Q5, Q6, QFINAL1, QFINAL0
    }

    /**
     * automate pour trouver si c'est un true ou un false
     * @param bits
     * @return true ou false
     */
	public static boolean automate(boolean[] bits) {
		if (bits.length != 3) {
			throw new IllegalArgumentException("Le tableau de bits doit avoir exactement 3 éléments.");
		}
	
		Etat etat = Etat.Q0;  // état initial
	
		// Transition based on the first bit
		if (bits[0]) {  // bit 1: 1
			if (bits[1]) {  // bit 2: 1
				etat = (bits[2]) ? Etat.QFINAL1 : Etat.QFINAL0;  // bit 3: 1 -> 1, 0 -> 0
			} else {  // bit 2: 0
				etat = Etat.QFINAL1;  // Always 1
			}
		} else {  // bit 1: 0
			if (bits[1]) {  // bit 2: 1
				etat = Etat.QFINAL0;  // Always 0
			} else {  // bit 2: 0
				etat = (bits[2]) ? Etat.QFINAL1 : Etat.QFINAL0;  // bit 3: 1 -> 1, 0 -> 0
			}
		}
	
		// Return final output based on the final state
		return etat == Etat.QFINAL1;
	}
	
	

    private void decodage() throws InformationNonConformeException{
        if (informationRecue.nbElements() % 3 != 0) {
            throw new InformationNonConformeException("Le message reçu n'est pas divisible par 3");
        }

        informationBitCode = new Information<>();
        for (int i = 0; i < informationRecue.nbElements(); i += 3) {
            boolean[] bits = new boolean[3];
            bits[0] = informationRecue.iemeElement(i);
            bits[1] = informationRecue.iemeElement(i + 1);
            bits[2] = informationRecue.iemeElement(i + 2);

            boolean decodedBit = automate(bits);
            informationBitCode.add(decodedBit);
        }
    }

    @Override
    public void recevoir(Information<Boolean> information) throws InformationNonConformeException {
        if (information == null || information.nbElements() == 0) {
            throw new InformationNonConformeException("L'information reçue est nulle ou invalide.");
        }

        informationRecue = information;
        decodage();
        emettre();
    }

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

	public static void main(String[] args) {
		// Test all possible 3-bit inputs for the automate
		boolean[][] testInputs = {
			{false, false, false},  // "000"
			{false, false, true},   // "001"
			{false, true, false},   // "010"
			{false, true, true},    // "011"
			{true, false, false},   // "100"
			{true, false, true},    // "101"
			{true, true, false},    // "110"
			{true, true, true}      // "111"
		};
	
		//System.out.println("Testing all possible 3-bit sequences:");
		for (boolean[] input : testInputs) {
			boolean result = DecodageReception.automate(input);
			//System.out.println("Input: " + (input[0] ? "1" : "0") + (input[1] ? "1" : "0") + (input[2] ? "1" : "0") + 
			//				   " -> Output: " + (result ? "1" : "0"));
		}
	}
	
}
