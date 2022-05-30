package application.view;

import java.net.URL;
import java.util.ResourceBundle;

import application.DailyBankState;
import application.control.DailyBankMainFrame;
import application.tools.AlertUtilities;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.AgenceBancaire;
import model.data.Employe;

/**
 * La classe "DailyBankMainFrameController" correspond à la fenêtre principale de l'application et propose à un employé de l'agence, plusieurs actions qu'il puisse réaliser (gérer les clients, etc.)
 * Cette classe traite ainsi les données utilisées par l'utilisateur, à la fois sur la "vue", correspondant à la partie graphique de l'interface mais également dans le "modèle", signifiant l'univers dans lequel s'inscrit l'application.
 */

public class DailyBankMainFrameController implements Initializable {

	// Etat application
	private DailyBankState dbs;
	private DailyBankMainFrame dbmf;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre

	// Manipulation de la fenêtre
	public void initContext(Stage _containingStage, DailyBankMainFrame _dbmf, DailyBankState _dbstate) {
		this.dbmf = _dbmf;
		this.dbs = _dbstate;
		this.primaryStage = _containingStage;
		this.configure();
		this.validateComponentState();
	}

	/**
	 * Affiche simplement la fenêtre en question, sans nécessairement attendre une quelconque réponse de l'utilisateur.
	 */
	public void displayDialog() {
		this.primaryStage.show();
	}

	/**
	 * Permet de mettre en place la configuration de la fenêtre ainsi que les données chargées. 
	 */
	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
		this.btnConn.managedProperty().bind(this.btnConn.visibleProperty());
		this.btnDeconn.managedProperty().bind(this.btnDeconn.visibleProperty());
	}

	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doQuit();
		e.consume();
		return null;
	}

	// Attributs de la scene + actions
	@FXML
	private Label lblAg;
	@FXML
	private Label lblAdrAg;
	@FXML
	private Label lblEmpNom;
	@FXML
	private Label lblEmpPrenom;
	@FXML
	private MenuItem mitemClient;
	@FXML
	private MenuItem mitemEmploye;
	@FXML
	private MenuItem mitemConnexion;
	@FXML
	private MenuItem mitemDeConnexion;
	@FXML
	private MenuItem mitemQuitter;
	@FXML
	private Button btnConn;
	@FXML
	private Button btnDeconn;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	/**
	 * Indique si l'utilisateur souhaiter fermer l'application "DailyBankApp", dépendant du futur choix de l'utilisateur.
	 */
	@FXML
	private void doQuit() {

		this.actionQuitterBD();

		if (AlertUtilities.confirmYesCancel(this.primaryStage, "Quitter Appli Principale",
				"Etes vous sur de vouloir quitter l'appli ?", null, AlertType.CONFIRMATION)) {
			this.primaryStage.close();
		}
	}

	/**
	 * Permet d'indiquer les mentions ("credits") de l'application et de sa conception.
	 */
	@FXML
	private void doActionAide() {
		String contenu = "DailyBank v1.01\nSAE 2.01 Développement\nIUT-Blagnac";
		AlertUtilities.showAlert(this.primaryStage, "Aide", null, contenu, AlertType.INFORMATION);
	}

	/**
	 * Permet à l'utilisateur de se connecter en renseignant des textes valides, et selon si l'employé existe, et figurant dans l'agence bancaire. 
	 */
	@FXML
	private void doLogin() {

		this.dbmf.login();
		this.validateComponentState();
	}

	/**
	 * Permet à l'utilisateur de se déconnecter de son compte, tout en dépendant de l'existence même de l'employé.
	 */
	@FXML
	private void doDisconnect() {
		this.dbmf.disconnect();
		this.validateComponentState();
	}

	/**
	 * Permet de définir l'état des différents boutons et labels dédiés à une interaction potentielle de l'utilisateur.
	 */
	private void validateComponentState() {
		Employe e = this.dbs.getEmpAct();
		AgenceBancaire a = this.dbs.getAgAct();
		if (e != null && a != null) {
			this.lblAg.setText(a.nomAg);
			this.lblAdrAg.setText(a.adressePostaleAg);
			this.lblEmpNom.setText(e.nom);
			this.lblEmpPrenom.setText(e.prenom);
			if (this.dbs.isChefDAgence()) {
				this.mitemEmploye.setDisable(false);
			} else {
				this.mitemEmploye.setDisable(true);
			}
			this.mitemClient.setDisable(false);
			this.mitemConnexion.setDisable(true);
			this.mitemDeConnexion.setDisable(false);
			this.btnConn.setVisible(false);
			this.btnDeconn.setVisible(true);
		} else {
			this.lblAg.setText("");
			this.lblAdrAg.setText("");
			this.lblEmpNom.setText("");
			this.lblEmpPrenom.setText("");

			this.mitemClient.setDisable(true);
			this.mitemEmploye.setDisable(true);
			this.mitemConnexion.setDisable(false);
			this.mitemDeConnexion.setDisable(true);
			this.btnConn.setVisible(true);
			this.btnDeconn.setVisible(false);
		}
	}

	/**
	 * Permet de pouvoir gérer l'ensemble des clients.
	 */
	@FXML
	private void doClientOption() {
		this.dbmf.gestionClients();
	}

	/**
	 * Permet de pouvoir gérer l'ensemble des employés au sein d'une agence bancaire.
	 */
	@FXML
	private void doEmployeOption() {
		AlertUtilities.showAlert(this.primaryStage, "Gestion des Employé", "En cours de développement",
				"Livraison prévue\nEn juin 2022", AlertType.INFORMATION);
	}

	/**
	 * Permet de se déconnecter de la fenêtre principale.
	 */
	private void actionQuitterBD() {
		this.dbmf.disconnect();
	}
}
