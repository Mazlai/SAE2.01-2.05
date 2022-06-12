package model.data;

import java.util.Date;

public class Prelevement {

	public int idPrelevement;
	public int idNumCompte;
	public String beneficiaire;
	public double montant;
	public int dateR;
	public String estArrete; // "O" ou "N"



	public Prelevement(int idPrelevement, double montant , int dateRec, String beneficiaire, int idNumcompteTrouve,String estArrete ) {
		super();
		this.idNumCompte = idNumcompteTrouve;
		this.idPrelevement = idPrelevement;
		this.beneficiaire = beneficiaire;
		this.montant = montant;
		this.dateR = dateRec;
		this.estArrete = estArrete;
	}

	public Prelevement(Prelevement cc) {
		this(cc.idPrelevement,cc.montant, cc.dateR,cc.beneficiaire, cc.idNumCompte, cc.estArrete);
	}

	public Prelevement() {
		this(0 , 0 , 0 ,"?" , 0 , "N");
	}

	@Override
	public String toString() {
		String s = "" + String.format("%05d", this.idPrelevement) + " : montant = " + String.format("%8.02f", this.montant)
				+ "  ,  Bénéficiaire = " + this.beneficiaire + " , Date recurrente =" + this.dateR
				+ " , Numéro du compte = " + String.format("%05d", this.idNumCompte);
		if (this.estArrete == null) {
			s = s + " (Arrêté)";
		} else {
			s = s + (this.estArrete.equals("N") ? " (En cours)" : " (Arrêté)");
		}
		return s;
	}

}
