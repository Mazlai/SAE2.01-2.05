package application;

import application.control.DailyBankMainFrame;

/**
 * Classe principale dont le main se charge de l'exécution de l'application "DailyBankApp".
 * Cette dernière fait appel à la classe "DailyBankMainFrame" correspondant à la fenêtre principale lors de l'exécution de l'application.
 * L'ensemble des actions en relation avec la fenêtre principale seront réalisées dans le controlleur "DailyBankMainFrameController".
 */
public class DailyBankApp  {

	public static void main(String[] args) {

		DailyBankMainFrame.runApp();

	}
}
