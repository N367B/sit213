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
	 * automate pour trouver si c'est un 0 ou un 1
	 * @param bits
	 * @return 0 ou 1
	 */
	public static int automate(String bits) {
		
		Etat etat = Etat.Q0; //etat initial
		char bit1 = bits.charAt(0);
		char bit2 = bits.charAt(1);
		char bit3 = bits.charAt(3);
		
		switch (etat) {
		case Q0 : 
			if (bit1 == '0') {
				etat = Etat.Q1;
			}else {
				etat = Etat.Q4;
			}
			break;
		default:
			break;
		}
		
		switch (etat) {
		case Q1 : 
			if (bit2 == '0') {
				etat = Etat.Q2;
			}else {
				etat = Etat.Q3;
			}
			break;
		case Q4 : 
			if (bit2 == '0') {
				etat = Etat.Q5;
			}else {
				etat = Etat.Q6;
			}
			break;
		default:
			break;
		}
		
		switch (etat) {
		case Q2 : 
			if (bit3 == '0') {
				etat = Etat.QFINAL0;
			}else {
				etat = Etat.QFINAL1;
			}
			break;
		case Q3 : 
			etat = Etat.QFINAL0;
			break;
	    case Q5 : 
			etat = Etat.QFINAL1;
			break;
		case Q6 : 
			if (bit3 == '0') {
				etat = Etat.QFINAL0;
			}else {
				etat = Etat.QFINAL1;
			}
			break;
		default:
			break;
		}
		
		if (etat == Etat.QFINAL0) {
			return 0;
		}else {
			return 1;
		}

	}
	
	private void decodage() throws InformationNonConformeException{
		
		int nbBit = 0;
		
		if (informationRecue.nbElements() %3 !=0) { //Verifie que le message recu est divisble par 3
			throw new InformationNonConformeException("Le message recu n'est pas divisible par 3");
		}else {
			nbBit = informationRecue.nbElements();
			
			for (int i =0; i<nbBit; i = i+3) {
				String chaine = "";
				chaine = chaine + informationRecue.iemeElement(i);
				chaine = chaine + informationRecue.iemeElement(i+1);
				chaine = chaine + informationRecue.iemeElement(i+2);
				automate(chaine);
			}
		}
	}
	 
	@Override
	public void recevoir(Information<Boolean> information) throws InformationNonConformeException {
		
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

}