package application.view;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import application.DailyBankState;
import application.control.ComptesManagement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.RowNotFoundOrTooManyRowsException;

public class ComptesManagementController implements Initializable {

	// Etat application
	private DailyBankState dbs;
	private ComptesManagement cm;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private Client clientDesComptes;
	private ObservableList<CompteCourant> olCompteCourant;

	// Manipulation de la fenêtre
	public void initContext(Stage _primaryStage, ComptesManagement _cm, DailyBankState _dbstate, Client client) {
		this.cm = _cm;
		this.primaryStage = _primaryStage;
		this.dbs = _dbstate;
		this.clientDesComptes = client;
		this.configure();
	}

	private void configure() {
		String info;

		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

		this.olCompteCourant = FXCollections.observableArrayList();
		this.lvComptes.setItems(this.olCompteCourant);
		this.lvComptes.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.lvComptes.getFocusModel().focus(-1);
		this.lvComptes.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());

		info = this.clientDesComptes.nom + "  " + this.clientDesComptes.prenom + "  (id : "
				+ this.clientDesComptes.idNumCli + ")";
		this.lblInfosClient.setText(info);

		this.loadList();
		this.validateComponentState();
	}

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
	private ListView<CompteCourant> lvComptes;
	@FXML
	private Button btnVoirOpes;
	@FXML
	private Button btnVoirCompte;
	@FXML
	private Button btnModifierCompte;
	@FXML
	private Button btnSupprCompte;
	@FXML
	private TextField txtDecAutorise;
	@FXML
	private TextField txtSolde;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	/**
	 * Permet de fermer l'interface
	 */
	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}

	/**
	 * Permet de voir les opérations financières du compte d'un client
	 */
	@FXML
	private void doVoirOperations() {
		int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			CompteCourant cpt = this.olCompteCourant.get(selectedIndice);
			if (cpt != null) {
				this.cm.gererOperations(cpt);
			}
		}
		this.loadList();
		this.validateComponentState();
	}

	/**
	 * Permet uniquement de voir les information d'un des comptes d'un client 
	 */
	@FXML
	private void doVoirCompte() {
		int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			CompteCourant compteAmodif = this.olCompteCourant.get(selectedIndice);
			CompteCourant result;
			try {
				result = this.cm.voirCompte(compteAmodif);
			} catch (DataAccessException e) {
				e.printStackTrace();
			} catch (RowNotFoundOrTooManyRowsException e) {
				e.printStackTrace();
			} catch (DatabaseConnexionException e) {
				e.printStackTrace();
			}
		}
		
		validateComponentState();
	}
	
	/**
	 * Permet de modifier les informations d'un des comptes du client
	 */
	@FXML
	private void doModifierCompte() {
		int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			CompteCourant compteAmodif = this.olCompteCourant.get(selectedIndice);
			CompteCourant result;
			try {
				result = this.cm.modifierCompte(compteAmodif);
				if (result != null) {
					this.olCompteCourant.set(selectedIndice, result);
					this.loadList();
				}
			} catch (DataAccessException e) {
				e.printStackTrace();
			} catch (RowNotFoundOrTooManyRowsException e) {
				e.printStackTrace();
			} catch (DatabaseConnexionException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Permet de clôturer un compte
	 */
	@FXML
	private void doSupprimerCompte() {
		try {
			int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
			if (selectedIndice >= 0) {
				CompteCourant cliMod = this.olCompteCourant.get(selectedIndice);
				try {
					this.cm.supprimerCompte(cliMod);
					this.loadList();

				} catch (DatabaseConnexionException e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Permet d'ajouter un compte bancaire
	 */
	@FXML
	private void doNouveauCompte() {
		CompteCourant compte;
		try {
			compte = this.cm.creerCompte();
			if (compte != null) {
				this.olCompteCourant.add(compte);
				this.loadList();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Permet d'actualiser les compte courant d'un client en rafraichissant la liste depuis la BD
	 */
	private void loadList() {
		ArrayList<CompteCourant> listeCpt;
		listeCpt = this.cm.getComptesDunClient();
		this.olCompteCourant.clear();
		for (CompteCourant co : listeCpt) {
			this.olCompteCourant.add(co);
		}
	}

	
	/**
	 * Permet de d'activer/desactiver les boutons de manière logique en fonction de l'état de plusieurs facteurs (compte selectionné, compte clôturé)
	 */
	private void validateComponentState() {
		
		this.btnModifierCompte.setDisable(true);
		this.btnSupprCompte.setDisable(true);
		this.btnVoirCompte.setDisable(true);

		int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			if (this.lvComptes.getSelectionModel().getSelectedItem().estCloture.equals("O")) {
				this.btnVoirOpes.setDisable(false);
				this.btnSupprCompte.setDisable(true);
				this.btnModifierCompte.setDisable(true);
				this.btnVoirCompte.setDisable(false);
			} else {
				this.btnVoirOpes.setDisable(false);
				this.btnSupprCompte.setDisable(false);
				this.btnModifierCompte.setDisable(false);
				this.btnVoirCompte.setDisable(false);
			}
		} else {
			this.btnVoirOpes.setDisable(true);
			this.btnSupprCompte.setDisable(true);
			this.btnModifierCompte.setDisable(true);
		}

	}
}
