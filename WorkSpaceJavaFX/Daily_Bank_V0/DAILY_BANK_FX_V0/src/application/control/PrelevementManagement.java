package application.control;

import java.util.ArrayList;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.CategorieOperation;
import application.tools.ConstantesIHM;
import application.tools.EditionMode;
import application.tools.PairsOfValue;
import application.tools.StageManagement;
import application.view.PrelevementManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Operation;
import model.data.Prelevement;
import model.orm.AccessClient;
import model.orm.AccessCompteCourant;
import model.orm.AccessOperation;
import model.orm.AcessPrelevement;
import model.orm.exception.ApplicationException;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.ManagementRuleViolation;
import model.orm.exception.RowNotFoundOrTooManyRowsException;

public class PrelevementManagement {

	private Stage primaryStage;
	private DailyBankState dbs;
	private PrelevementManagementController omp;
	private CompteCourant compteConcerne;

	public PrelevementManagement(Stage _parentStage, DailyBankState _dbstate, CompteCourant compte) {

		this.compteConcerne = compte;
		this.dbs = _dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(
			PrelevementManagementController.class.getResource("prelevementmanagement.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, 930 + 30, 400 + 20);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion des prélèvements");
			this.primaryStage.setResizable(false);

			this.omp = loader.getController();
			this.omp.initContext(this.primaryStage, this, _dbstate, this.compteConcerne);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doPrelevementManagementDialog() {
		this.omp.displayDialog();
	}
	

	public Prelevement enregistrerPrelevement() {

		PrelevementEditorPane oep = new PrelevementEditorPane(this.primaryStage, this.dbs);
		Prelevement  op = oep.doPrelevementManagementDialog(this.compteConcerne, null, EditionMode.CREATION);
		if (op != null) {
			try {
				AcessPrelevement ao = new AcessPrelevement();
				ao.insertPrelevement(op);
				
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
				op = null;
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
				ed.doExceptionDialog();
				op = null;
			}
		}
		return op;
	}
	
	public Prelevement modifierPrelevement(Prelevement pre) {
		PrelevementEditorPane cep = new PrelevementEditorPane(this.primaryStage, this.dbs);
		Prelevement result = cep.doPrelevementManagementDialog(this.compteConcerne, pre, EditionMode.MODIFICATION);
		if (result != null) {
			try {
				AcessPrelevement ap = new AcessPrelevement();
				ap.updatePrelevement(result);
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
	
	public Prelevement voirPrelevement(Prelevement pre) {
		PrelevementEditorPane cep = new PrelevementEditorPane(this.primaryStage, this.dbs);
		Prelevement result = cep.doPrelevementManagementDialog(this.compteConcerne, pre, EditionMode.VOIR);
		return result;
	}

	
	/**
	 * Permet de récupérer sous forme d'une liste, tous les compte d'un client 
	 * 
	 * @return la liste des comptes du client
	 */
	public ArrayList<Prelevement> getPrelevementDunCompte() {
		ArrayList<Prelevement> listePre = new ArrayList<>();
		try {
			AcessPrelevement acc = new AcessPrelevement();
			listePre = acc.getPrelevement(this.compteConcerne.idNumCompte);
		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
			ed.doExceptionDialog();
			this.primaryStage.close();
			listePre = new ArrayList<>();
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
			ed.doExceptionDialog();
			listePre = new ArrayList<>();
		}
		return listePre;
   }
	
	
}
