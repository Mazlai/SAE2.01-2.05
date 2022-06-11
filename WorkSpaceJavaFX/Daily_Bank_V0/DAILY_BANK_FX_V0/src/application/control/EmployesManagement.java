package application.control;

import java.util.ArrayList;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.ClientsManagementController;
import application.view.EmployesManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.Employe;
import model.orm.AccessClient;
import model.orm.AccessEmploye;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;

public class EmployesManagement {

	private Stage primaryStage;
	private DailyBankState dbs;
	private EmployesManagementController emc;

	/**
	 * Constructeur de EmployesManagement
	 * @param _parentStage Fenêtre mère dont celle-ci dépendra
	 * @param _dbstate Informations sur l'utilisateur : chef d'agence, guichetier
	 */
	public EmployesManagement(Stage _parentStage, DailyBankState _dbstate) {
		this.dbs = _dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(EmployesManagementController.class.getResource("employesmanagement.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth()+50, root.getPrefHeight()+10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion des employés");
			primaryStage.getIcons().add(new Image(DailyBankMainFrame.class.getResourceAsStream("../../DailyBankIcon.png")));
			this.primaryStage.setResizable(false);

			this.emc = loader.getController();
			this.emc.initContext(this.primaryStage, this, _dbstate);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Ouvre la fenêtre de gestion d'un employé
	 */
	public void doEmployeManagementDialog() {
		this.emc.displayDialog();
	}

	/**
	 * Ouvre la fenêtre de modification d'un employé
	 * @param emp Employé à modifier
	 * @return Employé modifié
	 */
	public Employe modifierEmploye(Employe emp) {
		EmployeEditorPane eep = new EmployeEditorPane(this.primaryStage, this.dbs);
		Employe result = eep.doEmployeEditorDialog(emp, EditionMode.MODIFICATION);
		if (result != null) {
			try {
				AccessEmploye ace = new AccessEmploye();
				ace.updateEmploye(result);
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
				ed.doExceptionDialog();
				result = null;
				this.primaryStage.close();
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
				ed.doExceptionDialog();
				result = null;
			}
		}
		return result;
	}

	public Employe nouvelEmploye() {
		Employe employe;
		EmployeEditorPane eep = new EmployeEditorPane(this.primaryStage, this.dbs);
		employe = eep.doEmployeEditorDialog(null, EditionMode.CREATION);
		if (employe != null) {
			try {
				AccessEmploye ace = new AccessEmploye();

				ace.insertEmploye(employe);
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
				employe = null;
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
				ed.doExceptionDialog();
				employe = null;
			}
		}
		return employe;
	}

	/**
	 * Affiche la liste des comptes en fonction de l'identifiant, du nom et du prénom
	 * @param _numCompte Numéro de compte dont on souhaite afficher le compte
	 * @param _debutNom	Nom dans lequel apparaît _debutNom dont on souhaite afficher les compte
	 * @param _debutPrenom Prenom dans lequel apparaît _debutPrenom dont on souhaite afficher les compte
	 * @return Liste des employés correspondant aux filtres appliqués
	 */
	public ArrayList<Employe> getlisteComptes(int _numCompte, String _debutNom, String _debutPrenom) {
		ArrayList<Employe> listeEmp = new ArrayList<>();
		try {
			// Recherche des employés en BD

			AccessEmploye ace = new AccessEmploye();
			listeEmp = ace.getEmployes(this.dbs.getEmpAct().idAg, _numCompte, _debutNom, _debutPrenom);

		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
			ed.doExceptionDialog();
			this.primaryStage.close();
			listeEmp = new ArrayList<>();
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
			ed.doExceptionDialog();
			listeEmp = new ArrayList<>();
		}
		return listeEmp;
	}
}
