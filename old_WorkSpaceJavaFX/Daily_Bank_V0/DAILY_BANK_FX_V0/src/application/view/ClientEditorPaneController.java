package application.view;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import application.DailyBankState;
import application.control.ExceptionDialog;
import application.tools.AlertUtilities;
import application.tools.ConstantesIHM;
import application.tools.EditionMode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.orm.exception.ApplicationException;
import model.orm.exception.Order;
import model.orm.exception.Table;

/**
 * La classe "ClientEditorPaneController" permet de traiter l'ensemble des actions de l'utilisateur concernant la définition d'un nouveau client ou la modification de celui-ci. 
 * Cette classe traite ainsi les données renseignées ou modifiées par l'utilisateur, à la fois sur la "vue", correspondant à la partie graphique de l'interface mais également dans le "modèle", signifiant l'univers dans lequel s'inscrit l'application.
 */

public class ClientEditorPaneController implements Initializable {

	// Etat application
	private DailyBankState dbs;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private Client clientEdite;
	private EditionMode em;
	private Client clientResult;

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
	}

	/** 
	 * Permet de définir l'ensemble des caractéristiques de la fenêtre d'édition d'un client tout en attendant la réponse de l'éditeur.
	 * 
	 * @param client IN : le client édité/concerné
	 * @param mode IN : le mode d'édition d'un client
	 * @return : le résultat d'un client (après création, modification ou suppression)
	 */
	public Client displayDialog(Client client, EditionMode mode) {

		this.em = mode;
		if (client == null) {
			this.clientEdite = new Client(0, "", "", "", "", "", "N", this.dbs.getEmpAct().idAg);
		} else {
			this.clientEdite = new Client(client);
		}
		this.clientResult = null;
		switch (mode) {
		case CREATION:
			this.txtIdcli.setDisable(true);
			this.txtNom.setDisable(false);
			this.txtPrenom.setDisable(false);
			this.txtTel.setDisable(false);
			this.txtMail.setDisable(false);
			this.rbActif.setSelected(true);
			this.rbInactif.setSelected(false);
			if (ConstantesIHM.isAdmin(this.dbs.getEmpAct())) {
				this.rbActif.setDisable(false);
				this.rbInactif.setDisable(false);
			} else {
				this.rbActif.setDisable(true);
				this.rbInactif.setDisable(true);
			}
			this.lblMessage.setText("Informations sur le nouveau client");
			this.butOk.setText("Ajouter");
			this.butCancel.setText("Annuler");
			break;
		case MODIFICATION:
			this.txtIdcli.setDisable(true);
			this.txtNom.setDisable(false);
			this.txtPrenom.setDisable(false);
			this.txtTel.setDisable(false);
			this.txtMail.setDisable(false);
			this.rbActif.setSelected(true);
			this.rbInactif.setSelected(false);
			if (ConstantesIHM.isAdmin(this.dbs.getEmpAct())) {
				this.rbActif.setDisable(false);
				this.rbInactif.setDisable(false);
			} else {
				this.rbActif.setDisable(true);
				this.rbInactif.setDisable(true);
			}
			this.lblMessage.setText("Informations client");
			this.butOk.setText("Modifier");
			this.butCancel.setText("Annuler");
			break;
		case SUPPRESSION:
			// ce mode n'est pas utilisé pour les Clients :
			// la suppression d'un client n'existe pas il faut que le chef d'agence
			// bascule son état "Actif" à "Inactif"
			ApplicationException ae = new ApplicationException(Table.NONE, Order.OTHER, "SUPPRESSION CLIENT NON PREVUE",
					null);
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
			ed.doExceptionDialog();

			break;
		}
		// Paramétrages spécifiques pour les chefs d'agences
		if (ConstantesIHM.isAdmin(this.dbs.getEmpAct())) {
			// rien pour l'instant
		}
		// initialisation du contenu des champs
		this.txtIdcli.setText("" + this.clientEdite.idNumCli);
		this.txtNom.setText(this.clientEdite.nom);
		this.txtPrenom.setText(this.clientEdite.prenom);
		this.txtAdr.setText(this.clientEdite.adressePostale);
		this.txtMail.setText(this.clientEdite.email);
		this.txtTel.setText(this.clientEdite.telephone);

		if (ConstantesIHM.estInactif(this.clientEdite)) {
			this.rbInactif.setSelected(true);
		} else {
			this.rbInactif.setSelected(false);
		}

		this.clientResult = null;

		this.primaryStage.showAndWait();
		return this.clientResult;
	}

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
	private TextField txtIdcli;
	@FXML
	private TextField txtNom;
	@FXML
	private TextField txtPrenom;
	@FXML
	private TextField txtAdr;
	@FXML
	private TextField txtTel;
	@FXML
	private TextField txtMail;
	@FXML
	private RadioButton rbActif;
	@FXML
	private RadioButton rbInactif;
	@FXML
	private ToggleGroup actifInactif;
	@FXML
	private Button butOk;
	@FXML
	private Button butCancel;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	/**
	 * Permet l'annulation de modification sur la page et donc, de retourner sur la page précédente, en fermant la fenêtre actuelle.
	 */
	@FXML
	private void doCancel() {
		this.clientResult = null;
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
				this.clientResult = this.clientEdite;
				this.primaryStage.close();
			}
			break;
		case MODIFICATION:
			if (this.isSaisieValide()) {
				this.clientResult = this.clientEdite;
				this.primaryStage.close();
			}
			break;
		case SUPPRESSION:
			this.clientResult = this.clientEdite;
			this.primaryStage.close();
			break;
		}

	}

	/**
	 * Permet d'indiquer si l'ensemble des champs de saisie sont valides ou non, au sein de l'interface.
	 * @return false si l'une des saisies de l'utilisateur n'est pas conforme au résultat attendu, true dans le cas contraire
	 */
	private boolean isSaisieValide() {
		this.clientEdite.nom = this.txtNom.getText().trim();
		this.clientEdite.prenom = this.txtPrenom.getText().trim();
		this.clientEdite.adressePostale = this.txtAdr.getText().trim();
		this.clientEdite.telephone = this.txtTel.getText().trim();
		this.clientEdite.email = this.txtMail.getText().trim();
		if (this.rbActif.isSelected()) {
			this.clientEdite.estInactif = ConstantesIHM.CLIENT_ACTIF;
		} else {
			this.clientEdite.estInactif = ConstantesIHM.CLIENT_INACTIF;
		}

		if (this.clientEdite.nom.isEmpty()) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le nom ne doit pas être vide",
					AlertType.WARNING);
			this.txtNom.requestFocus();
			return false;
		}
		if (this.clientEdite.prenom.isEmpty()) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le prénom ne doit pas être vide",
					AlertType.WARNING);
			this.txtPrenom.requestFocus();
			return false;
		}

		String regex = "(0)[1-9][0-9]{8}";
		if (!Pattern.matches(regex, this.clientEdite.telephone) || this.clientEdite.telephone.length() > 10) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le téléphone n'est pas valable",
					AlertType.WARNING);
			this.txtTel.requestFocus();
			return false;
		}
		regex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
				+ "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
		if (!Pattern.matches(regex, this.clientEdite.email) || this.clientEdite.email.length() > 20) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le mail n'est pas valable",
					AlertType.WARNING);
			this.txtMail.requestFocus();
			return false;
		}

		return true;
	}
}
