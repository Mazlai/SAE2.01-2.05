package application.view;

import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import application.DailyBankState;
import application.control.ExceptionDialog;
import application.control.PrelevementManagement;
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
import model.orm.exception.Order;
import model.orm.exception.RowNotFoundOrTooManyRowsException;
import model.orm.exception.Table;

public class PrelevementEditorPaneController implements Initializable {

	// Etat application
	private DailyBankState dbs;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private Prelevement prelevementEdite;
	private EditionMode em;
	private Prelevement prelevementResult;
	private CompteCourant leCompte;
	// Manipulation de la fenêtre
	/**
	 * Permet d'initialiser le contexte de la page
	 * 
	 * @param _primaryStage IN : La fenêtre de l'interface
	 * @param _dbstate IN : La frame de daily bank
	 */
	public void initContext(Stage _primaryStage, DailyBankState _dbstate) {
		this.primaryStage = _primaryStage;
		this.dbs = _dbstate;
		this.configure();
	}

	/**
	 * Permet de créer un event lors de la fermeture de la page
	 */
	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
		this.txtMontant.focusedProperty().addListener((t, o, n) -> this.focusMontant(t, o, n));
		this.txtDateRecurrente.focusedProperty().addListener((t, o, n) -> this.focusDateR(t, o, n));
	}

	/**
	 * Permet de bien agencer les boutons de l'interface qui s'ouvre selon les modes disponibles
	 * 
	 * @param client IN : Le client que l'on va éditer
	 * @param mode IN : Décrit l'état de l'ouverture de la page
	 * @return le client qui est modifié
	 */
	public Prelevement displayDialog(CompteCourant cpt, Prelevement prelevement, EditionMode mode) {

		this.em = mode;
		this.leCompte = cpt;
		if (prelevement == null) {
			this.prelevementEdite = new Prelevement(0 , 0 , 0 , "", this.leCompte.idNumCompte , "");
		} else {
			this.prelevementEdite = new Prelevement(prelevement);
		}
		this.prelevementResult = null;
		switch (mode) {
		case CREATION:
			txtBeneficiaire.setDisable(false);
			txtDateRecurrente.setDisable(false);
			txtMontant.setDisable(false);
			rbArrete.setDisable(true);
			rbEnCours.setDisable(true);
			rbEnCours.setSelected(true);
			txtIdprelevement.setDisable(true);
			txtIdCompte.setDisable(true);
			
			this.lblMessage.setText("Informations sur le nouveau prelevement");
			this.butOk.setText("Ajouter");
			this.butCancel.setText("Annuler");
			break;
		case MODIFICATION:
			txtIdprelevement.setDisable(true);
			txtIdCompte.setDisable(true);
			rbEnCours.setSelected(true);
			
			if(rbArrete.isSelected() == true ) {
				rbEnCours.setSelected(false);
			}else {
				rbArrete.setSelected(false);
			}
			txtBeneficiaire.setDisable(false);
			txtDateRecurrente.setDisable(false);
			txtMontant.setDisable(false);
			this.lblMessage.setText("Informations prélèvement du compte : "+ this.prelevementEdite.idNumCompte);
			this.butOk.setText("Modifier prélèvement");
			this.butCancel.setText("Annuler");
			break;
		case VOIR:
			this.butOk.setVisible(false);
			this.butCancel.setText("Fin consultation");
			this.txtBeneficiaire.setDisable(true);
			this.txtDateRecurrente.setDisable(true);
			this.txtIdCompte.setDisable(true);
			this.txtIdprelevement.setDisable(true);
			this.txtMontant.setDisable(true);
			this.rbArrete.setDisable(true);
			this.rbEnCours.setDisable(true);
			break;
		}

		// initialisation du contenu des champs
		this.txtIdprelevement.setText("" + this.prelevementEdite.idPrelevement);
		this.txtDateRecurrente.setText("" +this.prelevementEdite.dateR);
		this.txtIdCompte.setText(""+this.prelevementEdite.idNumCompte);
		this.txtBeneficiaire.setText(this.prelevementEdite.beneficiaire);
		this.txtMontant.setText(""+this.prelevementEdite.montant);

	

		this.prelevementResult = null;

		this.primaryStage.showAndWait();
		return this.prelevementResult;
	}

	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}

	/**
	 * Permet de gérer les erreurs lors de la saisie du solde du nouveau compte
	 * 
	 * @param txtField IN : La zone de texte qui contient la somme du solde
	 * @param oldPropertyValue IN : Ancienne valeur 
	 * @param newPropertyValue OUT : Nouvelle valeur
	 * @return
	 */
	private Object focusMontant(ObservableValue<? extends Boolean> txtField, boolean oldPropertyValue,
			boolean newPropertyValue) {
		if (oldPropertyValue) {
			try {
				double val;
				val = Double.parseDouble(this.txtMontant.getText().trim());
				if (val < 0) {
					throw new NumberFormatException();
				}
				this.prelevementEdite.montant = val;
			} catch (NumberFormatException nfe) {
				this.txtMontant.setText(String.format(Locale.ENGLISH, "%10.02f", this.prelevementEdite.montant));
			}
		}
		this.txtMontant.setText(String.format(Locale.ENGLISH, "%10.02f", this.prelevementEdite.montant));
		return null;
	}
	
	/**
	 * Permet de gérer les erreurs lors de la saisie du solde du nouveau compte
	 * 
	 * @param txtField IN : La zone de texte qui contient la somme du solde
	 * @param oldPropertyValue IN : Ancienne valeur 
	 * @param newPropertyValue OUT : Nouvelle valeur
	 * @return
	 */
	private Object focusDateR(ObservableValue<? extends Boolean> txtField, boolean oldPropertyValue,
			boolean newPropertyValue) {
		if (oldPropertyValue) {
			try {
				int val;
				val = Integer.parseInt(this.txtDateRecurrente.getText().trim());
				if (val < 0) {
					throw new NumberFormatException();
				}
				this.prelevementEdite.dateR = val;
			} catch (NumberFormatException nfe) {
				this.txtDateRecurrente.setText(String.valueOf(this.prelevementEdite.dateR));
			}
		}
		this.txtDateRecurrente.setText(String.valueOf(this.prelevementEdite.dateR));
		return null;
	}
	
	// Attributs de la scene + actions
	  @FXML
	    private Label lblMessage;

	    @FXML
	    private TextField txtIdprelevement;

	    @FXML
	    private TextField txtMontant;

	    @FXML
	    private TextField txtBeneficiaire;

	    @FXML
	    private RadioButton rbEnCours;

	    @FXML
	    private ToggleGroup actifInactif;

	    @FXML
	    private RadioButton rbArrete;
	    
	    @FXML
	    private TextField txtIdCompte;

	    @FXML
	    private TextField txtDateRecurrente;

	    @FXML
	    private Button butOk;

	    @FXML
	    private Button butCancel;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	@FXML
	private void doCancel() {
		this.prelevementResult = null;
		this.primaryStage.close();
	}
	


	@FXML
	private void doAjouter() {
		switch (this.em) {
		case CREATION:
			if (this.isSaisieValide()) {
				this.prelevementResult = this.prelevementEdite;
				this.primaryStage.close();
			}
			break;
		case MODIFICATION:
			if (this.isSaisieValide()) {
				this.prelevementResult = this.prelevementEdite;
				if (this.rbArrete.isSelected() == true) {
					AcessPrelevement ap = new AcessPrelevement();
					try {
						ap.arreterPrelevement(prelevementResult);
					} catch (SQLException | ApplicationException e) {
						e.printStackTrace();
					}
				}
				this.primaryStage.close();
			}
			break;
		case SUPPRESSION:
			this.prelevementResult = this.prelevementEdite;
			this.primaryStage.close();
			break;
		case VOIR:
			this.prelevementResult = this.prelevementEdite;
			this.primaryStage.close();
		}

	}
	


	private boolean isSaisieValide() {
		this.prelevementEdite.beneficiaire = this.txtBeneficiaire.getText().trim();
		if (this.rbArrete.isSelected()==true) {
			this.prelevementEdite.estArrete = "O";
		} else {
			this.prelevementEdite.estArrete = "N";
		}


		if (this.prelevementEdite.beneficiaire.isEmpty()) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le bénéficiaire doit être saisi",
					AlertType.WARNING);
			this.txtBeneficiaire.requestFocus();
			return false;
		}

		
		if (this.prelevementEdite.dateR > 30 || this.prelevementEdite.dateR < 1) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "La date (jour) n'est pas valable",
					AlertType.WARNING);
			this.txtDateRecurrente.requestFocus();
			return false;
		}
		return true;
	}
	
	
}
