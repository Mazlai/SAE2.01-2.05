package application.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import application.DailyBankState;
import application.control.OperationsManagement;
import application.tools.NoSelectionModel;
import application.tools.PairsOfValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Operation;

/**
 * La classe "OperationsManagementController" permet de traiter l'ensemble des actions de l'utilisateur concernant les opérations déjà effectuées d'un nouveau client sur le compte désigné.
 * Cette classe traite ainsi les données utilisées par l'utilisateur, à la fois sur la "vue", correspondant à la partie graphique de l'interface mais également dans le "modèle", signifiant l'univers dans lequel s'inscrit l'application.
 */

public class OperationsManagementController implements Initializable {

	// Etat application
	private DailyBankState dbs;
	private OperationsManagement om;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private Client clientDuCompte;
	private CompteCourant compteConcerne;
	private ObservableList<Operation> olOperation;
	

	// Manipulation de la fenêtre
	public void initContext(Stage _primaryStage, OperationsManagement _om, DailyBankState _dbstate, Client client, CompteCourant compte) {
		this.primaryStage = _primaryStage;
		this.dbs = _dbstate;
		this.om = _om;
		this.clientDuCompte = client;
		this.compteConcerne = compte;
		this.configure();
	}

	/**
	 * Permet de mettre en place la configuration de la fenêtre ainsi que les données chargées. 
	 */
	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

		this.olOperation = FXCollections.observableArrayList();
		this.lvOperations.setItems(this.olOperation);
		this.lvOperations.setSelectionModel(new NoSelectionModel<Operation>());
		this.updateInfoCompteClient();
		this.validateComponentState();
	}

	/**
	 * Affiche simplement la fenêtre en question, tout en attendant une quelconque réponse de l'utilisateur.
	 */
	public void displayDialog() {
		this.primaryStage.showAndWait();
	}

	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}

	// Attributs de la scene + actions
	@FXML
	private Label lblInfosClient;
	@FXML
	private Label lblInfosCompte;
	@FXML
	private ListView<Operation> lvOperations;
	@FXML
	private Button btnDebit;
	@FXML
	private Button btnCredit;
	@FXML
    private Button btnVirement;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	/**
	 * Permet de retourner sur la page précédente, en fermant la fenêtre actuelle.
	 */
	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}

	/**
	 * Permet de définir une opération de débit en mettant à jour, la liste de l'ensemble des opérations effectuées sur le compte et dépendant de l'état du compte.
	 */
	@FXML
	private void doDebit() {

		Operation op = this.om.enregistrerDebit();
		if (op != null) {
			this.updateInfoCompteClient();
			this.validateComponentState();
		}
	}

	/**
	 * Permet de définir une opération de crédit en mettant à jour, la liste de l'ensemble des opérations effectuées sur le compte et dépendant de l'état du compte.
	 */
	@FXML
	private void doCredit() {
		
		Operation op = this.om.enregistrerCredit();
		if (op != null) {
			this.updateInfoCompteClient();
			this.validateComponentState();
		}
	}

	/**
	 * Permet de définir une opération de crédit en mettant à jour, la liste de l'ensemble des opérations effectuées sur le compte et dépendant de l'état du compte.
	 */
	@FXML
	private void doVirement() {
		Operation op = this.om.enregistrerVirement();
		if (op != null) {
			this.updateInfoCompteClient();
			this.validateComponentState();
		}
		
	}

	/**
	 * Permet de définir l'état des différents boutons dédiés à une interaction potentielle de l'utilisateur, en fonction de certaines caractériques du compte.
	 */
	private void validateComponentState() {
			//Condition compte fermé
			if (this.compteConcerne.estCloture.equals("O")) {
				this.btnCredit.setDisable(true);
				this.btnDebit.setDisable(true);
				this.btnVirement.setDisable(true);
				//Condition Virement	
			} else if ((this.compteConcerne.debitAutorise > 0 && this.compteConcerne.solde > (-this.compteConcerne.debitAutorise)) ||
			 (this.compteConcerne.debitAutorise <= 0 && this.compteConcerne.solde > this.compteConcerne.debitAutorise)) {
				this.btnVirement.setDisable(false);
				this.btnCredit.setDisable(false);
				
			} else {
				this.btnCredit.setDisable(false);
				this.btnDebit.setDisable(false);
				this.btnVirement.setDisable(true);
			}
			/*if (this.compteConcerne.solde > 0 && this.compteConcerne.estCloture.equals("N")) {
			this.btnVirement.setDisable(false);*/
			//Sinon par défaut
	}

	/**
	 * Permet de mettre à jour la liste de l'ensemble des opérations effectuées caractérisées par le type de l'opération, le montant renseigné ou encore le numéro de compte sur lequel est effectué l'opération en question.
	 * Cette procédure dépend ainsi de la procédure de vérification de l'interaction d'un utilisateur pour effectuer l'opération concernée.
	 */
	private void updateInfoCompteClient() {

		PairsOfValue<CompteCourant, ArrayList<Operation>> opesEtCompte;

		opesEtCompte = this.om.operationsEtSoldeDunCompte();

		ArrayList<Operation> listeOP;
		this.compteConcerne = opesEtCompte.getLeft();
		listeOP = opesEtCompte.getRight();

		String info;
		info = this.clientDuCompte.nom + "  " + this.clientDuCompte.prenom + "  (id : " + this.clientDuCompte.idNumCli
				+ ")";
		this.lblInfosClient.setText(info);

		info = "Cpt. : " + this.compteConcerne.idNumCompte + "  "
				+ String.format(Locale.ENGLISH, "%12.02f", this.compteConcerne.solde) + "  /  "
				+ String.format(Locale.ENGLISH, "%8d", this.compteConcerne.debitAutorise);
		this.lblInfosCompte.setText(info);

		this.olOperation.clear();
		for (Operation op : listeOP) {
			this.olOperation.add(op);
		}

		this.validateComponentState();
	}
}
