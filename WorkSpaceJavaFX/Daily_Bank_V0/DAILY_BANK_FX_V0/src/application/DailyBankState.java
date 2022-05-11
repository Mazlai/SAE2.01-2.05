package application;

import application.tools.ConstantesIHM;
import model.data.AgenceBancaire;
import model.data.Employe;

public class DailyBankState {
	private Employe empAct;
	private AgenceBancaire agAct;
	private boolean isChefDAgence;

	
	/**
	 * @return Retourne l'employé actif
	 */
	public Employe getEmpAct() {
		return this.empAct;
	}

	/**
	 * @param Remplace l'employé actif par le paramètre employeActif
	 */
	public void setEmpAct(Employe employeActif) {
		this.empAct = employeActif;
	}

	/**
	 * @return Retourne l'agence active
	 */
	public AgenceBancaire getAgAct() {
		return this.agAct;
	}

	/**
	 * @param Remplace l'agence active par le paramètre agenceActive
	 */
	public void setAgAct(AgenceBancaire agenceActive) {
		this.agAct = agenceActive;
	}

	/**
	 * @return Indique si l'utilisateur est chef d'agence
	 */
	public boolean isChefDAgence() {
		return this.isChefDAgence;
	}

	/**
	 * @param Change l'état d'un utilisateur en chef d'agence ou non : isChefDAgence
	 */
	public void setChefDAgence(boolean isChefDAgence) {
		this.isChefDAgence = isChefDAgence;
	}

	/**
	 * @param Vérifie si l'utilsateur est chef d'agence grâce auxd droits droitAccess. Si non : il n'est pas mis chef d'agence. Si oui, il est mis chef d'agence.
	 */
	public void setChefDAgence(String droitsAccess) {
		this.isChefDAgence = false;

		if (droitsAccess.equals(ConstantesIHM.AGENCE_CHEF)) {
			this.isChefDAgence = true;
		}
	}
}
