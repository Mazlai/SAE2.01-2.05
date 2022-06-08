package application;

import application.tools.ConstantesIHM;
import model.data.AgenceBancaire;
import model.data.Employe;

/**
 * Cette classe concerne l'ensemble des états des membres au sein d'une agence bancaire, qu'il soit simple employé ou chef d'agence.
 * Cette dernière concerne également l'état d'une agence bancaire, qu'elle soit active ou au contraire, inactive. 
 */
public class DailyBankState {
	private Employe empAct;
	private AgenceBancaire agAct;
	private boolean isChefDAgence;
	
	/** 
	 * Permet d'obtenir les caractéristiques d'un employé actif.
	 * 
	 * @return l'employé actif et ses informations
	 */
	public Employe getEmpAct() {
		return this.empAct;
	}
	
	/** 
	 * Permet de modifier l'employé déjà présent par un nouveau avec ses nouvelles caractéristiques.
	 * 
	 * @param employeActif IN : le nouveau employé actif
	 */
	public void setEmpAct(Employe employeActif) {
		this.empAct = employeActif;
	}
	
	/** 
	 * Permet d'obtenir les caractéristiques d'une agence bancaire active
	 * 
	 * @return l'agence bancaire active et ses informations
	 */
	public AgenceBancaire getAgAct() {
		return this.agAct;
	}
	
	/** 
	 * Permet de modifier une agence bancaire déjà présente par une nouvealle avec ses caractéristiques.
	 * 
	 * @param agenceActive IN : la nouvelle agence bancaire active
	 */
	public void setAgAct(AgenceBancaire agenceActive) {
		this.agAct = agenceActive;
	}
	
	/** 
	 * Permet de déterminer si l'employé interrogé de l'agence bancaire correspond au chef d'agence ou non.
	 * 
	 * @return l'état de l'employé : s'il est chef d'agence ou non.
	 */
	public boolean isChefDAgence() {
		return this.isChefDAgence;
	}
	
	/** 
	 * Permet de définir un nouvel chef d'agence à la place de l'ancien, au sein de l'agence bancaire.
	 * 
	 * @param isChefDAgence IN : le nouvel chef d'agence
	 */
	public void setChefDAgence(boolean isChefDAgence) {
		this.isChefDAgence = isChefDAgence;
	}
	
	/** 
	 * Permet de définir les droits d'accès d'un employé en fonction de son statut. S'il est chef d'agence, il possède les droits d'accès d'un chef d'agence, à contrario d'un simple employé.
	 * 
	 * @param droitsAccess IN : les droits d'accès d'un employé
	 */
	public void setChefDAgence(String droitsAccess) {
		this.isChefDAgence = false;

		if (droitsAccess.equals(ConstantesIHM.AGENCE_CHEF)) {
			this.isChefDAgence = true;
		}
	}
}
