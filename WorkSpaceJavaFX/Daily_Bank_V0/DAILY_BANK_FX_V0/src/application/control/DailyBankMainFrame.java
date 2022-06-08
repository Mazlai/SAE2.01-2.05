package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.view.DailyBankMainFrameController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.data.AgenceBancaire;
import model.orm.AccessAgenceBancaire;
import model.orm.LogToDatabase;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;

/**
 * La classe DailyBankMainFrame hérite de la classe Application. Elle permet le demarrage de la homepage de l'application, de s'y connecter et de s'y deconnecter.
 */
public class DailyBankMainFrame extends Application {

	private DailyBankState dbs;
	private Stage primaryStage;
	
	/**
	 * Permet d'ouvrir la fenetre de démarrage de l'application. Elle prend en parametre la fenetre principale.
	 */
	@Override
	public void start(Stage primaryStage) {

		this.primaryStage = primaryStage;

		try {
			this.dbs = new DailyBankState();
			this.dbs.setAgAct(null);
			this.dbs.setChefDAgence(false);
			this.dbs.setEmpAct(null);

			FXMLLoader loader = new FXMLLoader(
					DailyBankMainFrameController.class.getResource("dailybankmainframe.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth()+20, root.getPrefHeight()+10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			primaryStage.setScene(scene);
			primaryStage.setTitle("Fenêtre Principale");

			/*
			// En mise au point :
			// Forcer une connexion existante pour rentrer dans l'appli en mode connecté

			try {
				Employe e;
				AccessEmploye ae = new AccessEmploye();

				e = ae.getEmploye("Tuff", "Lejeune");

				if (e == null) {
					System.out.println("\n\nPB DE CONNEXION\n\n");
				} else {
					this.dbs.setEmpAct(e);
				}
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(primaryStage, this.dbs, e);
				ed.doExceptionDialog();
				this.dbs.setEmpAct(null);
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(primaryStage, this.dbs, ae);
				ed.doExceptionDialog();
				this.dbs.setEmpAct(null);
			}

			if (this.dbs.getEmpAct() != null) {
				this.dbs.setChefDAgence(this.dbs.getEmpAct().droitsAccess);
				try {
					AccessAgenceBancaire aab = new AccessAgenceBancaire();
					AgenceBancaire agTrouvee;

					agTrouvee = aab.getAgenceBancaire(this.dbs.getEmpAct().idAg);
					this.dbs.setAgAct(agTrouvee);
				} catch (ApplicationException e) {
					System.out.println("\n\nPB DE CONNEXION\n\n");
					ExceptionDialog ed = new ExceptionDialog(primaryStage, this.dbs, e);
					ed.doExceptionDialog();
				}
			}
			*/

			DailyBankMainFrameController dbmc = loader.getController();
			dbmc.initContext(primaryStage, this, this.dbs);

			dbmc.displayDialog();

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	/**
	 * Permet de lancer l'application
	 */
	public static void runApp() {
		Application.launch();
	}
	
	/**
	 * Permet de se déconnecter sur l'application et fermer la connexion à la base de données
	 */
	public void disconnect() {
		this.dbs.setAgAct(null);
		this.dbs.setEmpAct(null);
		this.dbs.setChefDAgence(false);
		try {
			LogToDatabase.closeConnexion();
		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
			ed.doExceptionDialog();
		}
	}
	
	/**
	 * Permet de se connecter sur l'application
	 */
	public void login() {
		LoginDialog ld = new LoginDialog(this.primaryStage, this.dbs);
		ld.doLoginDialog();

		if (this.dbs.getEmpAct() != null) {
			this.dbs.setChefDAgence(this.dbs.getEmpAct().droitsAccess);
			try {
				AccessAgenceBancaire aab = new AccessAgenceBancaire();
				AgenceBancaire agTrouvee;

				agTrouvee = aab.getAgenceBancaire(this.dbs.getEmpAct().idAg);
				this.dbs.setAgAct(agTrouvee);
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
				ed.doExceptionDialog();
				this.dbs.setAgAct(null);
				this.dbs.setEmpAct(null);
				this.dbs.setChefDAgence(false);
			} catch (ApplicationException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
				ed.doExceptionDialog();
				this.dbs.setAgAct(null);
				this.dbs.setEmpAct(null);
				this.dbs.setChefDAgence(false);
			}
		}
	}
	/**
	 * Permet d'ouvrir la fenetre de gestion de clients 
	 */
	public void gestionClients() {
		ClientsManagement cm = new ClientsManagement(this.primaryStage, this.dbs);
		cm.doClientManagementDialog();
	}
	/**
	 * Permet d'ouvrir la fenetre de gestion de employés 
	 */
	public void gestionEmploye() {
		EmployesManagement em = new EmployesManagement(this.primaryStage, this.dbs);
		em.doEmployeManagementDialog();
	}
}
