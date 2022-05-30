package application.view;

import java.net.URL;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import application.DailyBankState;
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
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;

/**
 * La classe "CompteEditorPaneController" permet de traiter l'ensemble des actions de l'utilisateur concernant la définition d'un nouveau compte ou la modification de celui-ci. 
 * Cette classe traite ainsi les données renseignées ou modifiées par l'utilisateur, à la fois sur la "vue", correspondant à la partie graphique de l'interface mais également dans le "modèle", signifiant l'univers dans lequel s'inscrit l'application.
 */

public class CompteEditorPaneController implements Initializable {

	// Etat application
	private DailyBankState dbs;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private EditionMode em;
	private Client clientDuCompte;
	private CompteCourant compteEdite;
	private CompteCourant compteResult;
	ComptesManagementController compteAsupp;

	// Manipulation de la fenêtre
	public void initContext(Stage _primaryStage, DailyBankState _dbstate) {
		this.primaryStage = _primaryStage;
		this.dbs = _dbstate;
		this.configure();
	}

	/**
	 * Permet de mettre en place la configuration de la fenêtre ainsi que les données chargées. 
	 */
	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

		this.txtDecAutorise.focusedProperty().addListener((t, o, n) -> this.focusDecouvert(t, o, n));
		this.txtSolde.focusedProperty().addListener((t, o, n) -> this.focusSolde(t, o, n));
	}

	/**
	 * Permet de définir l'ensemble des caractéristiques de la fenêtre d'édition d'un compte tout en attendant la réponse de l'éditeur.
	 * 
	 * @param client IN : le client concerné
	 * @param cpte IN : le compte courant du client concerné
	 * @param mode : le mode d'édition d'un compte
	 * @return le résultat d'un compte (après création, modification ou suppression)
	 */
	public CompteCourant displayDialog(Client client, CompteCourant cpte, EditionMode mode) {
		this.clientDuCompte = client;
		this.em = mode;
		if (cpte == null) {
			this.compteEdite = new CompteCourant(0, 200, 0, "N", this.clientDuCompte.idNumCli);

		} else {
			this.compteEdite = new CompteCourant(cpte);
		}
		this.compteResult = null;
		this.txtIdclient.setDisable(true);
		this.txtIdAgence.setDisable(true);
		this.txtIdNumCompte.setDisable(true);
		switch (mode) {
		case CREATION:
			this.txtDecAutorise.setDisable(false);
			this.txtSolde.setDisable(false);
			this.lblMessage.setText("Informations sur le nouveau compte");
			this.lblSolde.setText("Solde (premier dépôt)");
			this.btnOk.setText("Ajouter");
			this.btnCancel.setText("Annuler");
			break;
		case MODIFICATION:
			this.txtDecAutorise.setDisable(false);
			this.txtSolde.setDisable(true);
			this.lblMessage.setText("Modifications du compte n° : " + this.compteEdite.idNumCompte + " de " + this.clientDuCompte.nom + " " + this.clientDuCompte.prenom);
			this.lblSolde.setText("Solde");
			this.btnOk.setText("OK");
			this.btnCancel.setText("Annuler");
			//return null;
		    break;
		case SUPPRESSION:
			/*AlertUtilities.showAlert(this.primaryStage, "Non implémenté", "Suppression de compte n'est pas implémenté",
					null, AlertType.ERROR);*/
			/*Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Suppression de compte");
			alert.setHeaderText("Confirmation de la suppression du compte");
			alert.setContentText("Voulez vous vraiment supprimer ce compte ?");
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK){
			   alert.close();
			} else {
			   alert.close();
			}*/
			break;
		}

		// Paramétrages spécifiques pour les chefs d'agences
		if (ConstantesIHM.isAdmin(this.dbs.getEmpAct())) {
			// rien pour l'instant
		}

		// initialisation du contenu des champs
		this.txtIdclient.setText("" + this.compteEdite.idNumCli);
		this.txtIdNumCompte.setText("" + this.compteEdite.idNumCompte);
		this.txtIdAgence.setText("" + this.dbs.getEmpAct().idAg);
		this.txtDecAutorise.setText("" + this.compteEdite.debitAutorise);
		this.txtSolde.setText(String.format(Locale.ENGLISH, "%10.02f", this.compteEdite.solde));

		this.compteResult = null;

		this.primaryStage.showAndWait();
		return this.compteResult;
	}

	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}

	/**
	 * Permet de définir dans un champ de texte, les caractéristiques d'un découvert autorisé.
	 * @param txtField (non utilisé ?)
	 * @param oldPropertyValue IN : focus du champ de texte d'un découvert autorisé
	 * @param newPropertyValue (non utilisée ?)
	 * @return null si le focus n'a jamais été envisagé
	 */
	private Object focusDecouvert(ObservableValue<? extends Boolean> txtField, boolean oldPropertyValue,
			boolean newPropertyValue) {
		if (oldPropertyValue) {
			try {
				int val;
				val = Integer.parseInt(this.txtDecAutorise.getText().trim());
				if (val < 0) {
					throw new NumberFormatException();
				}
				this.compteEdite.debitAutorise = val;
			} catch (NumberFormatException nfe) {
				this.txtDecAutorise.setText("" + this.compteEdite.debitAutorise);
			}
		}
		return null;
	}

	/**
	 * Permet de définir dans un champ de texte, les caractéristiques d'un solde.
	 * @param txtField (non utilisé ?)
	 * @param oldPropertyValue IN : focus du champ de texte d'un solde
	 * @param newPropertyValue (non utilisée ?)
	 * @return null si le focus n'a jamais été envisagé
	 */
	private Object focusSolde(ObservableValue<? extends Boolean> txtField, boolean oldPropertyValue,
			boolean newPropertyValue) {
		if (oldPropertyValue) {
			try {
				double val;
				val = Double.parseDouble(this.txtSolde.getText().trim());
				if (val < 0) {
					throw new NumberFormatException();
				}
				this.compteEdite.solde = val;
			} catch (NumberFormatException nfe) {
				this.txtSolde.setText(String.format(Locale.ENGLISH, "%10.02f", this.compteEdite.solde));
			}
		}
		this.txtSolde.setText(String.format(Locale.ENGLISH, "%10.02f", this.compteEdite.solde));
		return null;
	}

	// Attributs de la scene + actions
	@FXML
	private Label lblMessage;
	@FXML
	private Label lblSolde;
	@FXML
	private TextField txtIdclient;
	@FXML
	private TextField txtIdAgence;
	@FXML
	private TextField txtIdNumCompte;
	@FXML
	private TextField txtDecAutorise;
	@FXML
	private TextField txtSolde;
	@FXML
	private Button btnOk;
	@FXML
	private Button btnCancel;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	/**
	 * Permet l'annulation de modification sur la page et donc, de retourner sur la page précédente, en fermant la fenêtre actuelle.
	 */
	@FXML
	private void doCancel() {
		this.compteResult = null;
		this.primaryStage.close();
	}

	/**
	 * Permet l'édition d'un client au coeur d'une agence bancaire, qu'il s'agisse d'un nouveau client, d'un client étant remplacé par un autre, ou de son retrait au sein de celle-ci.
	 */
	@FXML
	private void doAjouter() {
		switch (this.em) {
		case CREATION:
			if (this.isSaisieValide()) {
				this.compteResult = this.compteEdite;
				this.primaryStage.close();
			}
			break;
		case MODIFICATION:
			if (this.isSaisieValide()) {
				this.compteResult = this.compteEdite;
				this.primaryStage.close();
			}
			break;
		case SUPPRESSION:
			this.compteResult = this.compteEdite;
			this.primaryStage.close();
			break;
		}

	}

	/**
	 * Permet d'indiquer si les saisies de l'utilisateur sont conformes aux attentes.
	 * @return true si tout est valide 
	 */
	private boolean isSaisieValide() {

		return true;
	}
}
