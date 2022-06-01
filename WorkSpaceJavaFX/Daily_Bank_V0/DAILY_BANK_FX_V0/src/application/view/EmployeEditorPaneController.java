package application.view;

import java.net.URL;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import application.DailyBankState;
import application.control.ExceptionDialog;
import application.tools.AlertUtilities;
import application.tools.ConstantesIHM;
import application.tools.EditionMode;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Employe;
import model.orm.exception.ApplicationException;
import model.orm.exception.Order;
import model.orm.exception.Table;
import model.data.Employe;
import model.data.Client;
import model.data.CompteCourant;

public class EmployeEditorPaneController implements Initializable {

	// Etat application
	private DailyBankState dbs;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
		private Employe employeEdite;
		private EditionMode em;
		private Employe employeResult;
	

    // Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}

	// Attributs de la scene + actions
	
	
	  @FXML
	    private Label lblMessage;

	    @FXML
	    private TextField txtIdEmp;

	    @FXML
	    private TextField txtNom;

	    @FXML
	    private TextField txtPrenom;

	    @FXML
	    private TextField txtLogin;

	    @FXML
	    private TextField txtMotPasse;

	    @FXML
	    private RadioButton rbGuichetier;

	    @FXML
	    private ToggleGroup droitsAccesGroup;

	    @FXML
	    private RadioButton rbChefAgence;

	    @FXML
	    private Button butOk;

	    @FXML
	    private Button butCancel;



	/**
	 * Manipulation de la fenêtre
	 * @param _primaryStage Fenêtre principale
	 * @param _dbstate Type d'utilisateur : guichetier, chef d'agence
	 */
	public void initContext(Stage _primaryStage, DailyBankState _dbstate) {
		this.primaryStage = _primaryStage;
		this.dbs = _dbstate;
		this.configure();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Configure la fermeture de la fenêtre
	 */
	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
	}

	public Employe doEmployeEditorDialog(Employe employe, EditionMode em) {
		return null;
	}
	
	/**
	 * Ouvre la page d'affichage, de modification ou de suppression de l'employé
	 * @param employe Employé comcerné
	 * @param mode Type de modification : affichage, modification ou suppression
	 * @return Employé possiblement modifié
	 */
	public Employe displayDialog(Employe employe, EditionMode mode) {

		this.em = mode;
		if (employe == null) {
			this.employeEdite = new Employe(0, "", "", "", "", "", this.dbs.getEmpAct().idAg);
		} else {
			this.employeEdite = new Employe(employe);
		}
		this.employeResult = null;
		switch (mode) {
		case CREATION:
		this.txtIdEmp.setDisable(true);
			this.txtNom.setDisable(false);
			this.txtPrenom.setDisable(false);
			this.txtLogin.setDisable(false);
			this.txtMotPasse.setDisable(false);
			this.rbGuichetier.setSelected(true);
			this.rbChefAgence.setSelected(false);
			this.lblMessage.setText("Informations sur le nouvel employé");
			this.butOk.setText("Ajouter");
			this.butCancel.setText("Annuler");
			break;
		case MODIFICATION:
		this.txtIdEmp.setDisable(true);
		this.txtNom.setDisable(false);
		this.txtPrenom.setDisable(false);
		this.txtLogin.setDisable(false);
		this.txtMotPasse.setDisable(false);
		this.rbGuichetier.setSelected(true);
		this.rbChefAgence.setSelected(false);
		this.lblMessage.setText("Informations employé");
		this.butOk.setText("Modifier");
		this.butCancel.setText("Annuler");
		break;
		case SUPPRESSION:
			// ce mode n'est pas utilisé pour les employes :
			// la suppression d'un employe n'existe pas il faut que le chef d'agence
			// bascule son état "Actif" à "Inactif"
			ApplicationException ae = new ApplicationException(Table.NONE, Order.OTHER, "SUPPRESSION CLIENT NON PREVUE", null);
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
			ed.doExceptionDialog();
			break;
		}
		// Paramétrages spécifiques pour les chefs d'agences
		if (ConstantesIHM.isAdmin(this.dbs.getEmpAct())) {
			// rien pour l'instant
		}
		// initialisation du contenu des champs
		this.txtIdEmp.setText("" + this.employeEdite.idEmploye);
		this.txtNom.setText(this.employeEdite.nom);
		this.txtPrenom.setText(this.employeEdite.prenom);
		this.txtMotPasse.setText(this.employeEdite.motPasse);
		this.txtLogin.setText(this.employeEdite.login);

		if (this.employeEdite.droitsAccess.equals("guichetier")) {
			this.rbGuichetier.setSelected(true);
			this.rbChefAgence.setSelected(false);
		} else if (this.employeEdite.droitsAccess.equals("chefAgence")) {
			this.rbGuichetier.setSelected(false);
			this.rbChefAgence.setSelected(true);
		}

		this.employeResult = null;

		this.primaryStage.showAndWait();
		return this.employeResult;
	}

	@FXML
	private void doCancel() {
		this.employeResult = null;
		this.primaryStage.close();
	}

	@FXML
	private void doAjouter() {
		switch (this.em) {
		case CREATION:
			if (this.isSaisieValide()) {
				this.employeResult = this.employeEdite;
				this.primaryStage.close();
			}
			break;
		case MODIFICATION:
			if (this.isSaisieValide()) {
				this.employeResult = this.employeEdite;
				this.primaryStage.close();
			}
			break;
		case SUPPRESSION:
			this.employeResult = this.employeEdite;
			this.primaryStage.close();
			break;
		}

	}

	/**
	 * Corrige et vérifie la saisie lors de la modification d'un employé
	 * @return vrai ou faux : la saisie est-elle valide ?
	 */
	private boolean isSaisieValide() {
		this.employeEdite.nom = this.txtNom.getText().trim();
		this.employeEdite.prenom = this.txtPrenom.getText().trim();
		this.employeEdite.login = this.txtLogin.getText().trim();
		this.employeEdite.motPasse = this.txtMotPasse.getText().trim();
		
		if (this.rbGuichetier.isSelected()) {
			this.employeEdite.droitsAccess = "guichetier";
		} else {
			this.employeEdite.droitsAccess = "chefAgence";
		}

		if (this.employeEdite.nom.isEmpty()) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le nom ne doit pas être vide",
					AlertType.WARNING);
			this.txtNom.requestFocus();
			return false;
		}
		else if (this.employeEdite.prenom.isEmpty()) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le prénom ne doit pas être vide",
					AlertType.WARNING);
			this.txtPrenom.requestFocus();
			return false;
		}
		else if (this.employeEdite.login.isEmpty()) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le login ne doit pas être vide",
					AlertType.WARNING);
			this.txtLogin.requestFocus();
			return false;
		}
		else if (this.employeEdite.motPasse.isEmpty()) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le mot de passe ne doit pas être vide",
					AlertType.WARNING);
			this.txtMotPasse.requestFocus();
			return false;
		}

		return true;
	}
}
