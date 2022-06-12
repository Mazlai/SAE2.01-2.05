package application.view;


import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;

import java.util.ResourceBundle;

import application.DailyBankState;

import application.control.PrelevementManagement;
import application.tools.ConstantesIHM;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import javafx.scene.control.SelectionMode;

import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import model.data.CompteCourant;

import model.data.Prelevement;
import model.orm.AccessOperation;


public class PrelevementManagementController implements Initializable {

	// Etat application
	private DailyBankState dbs;
	private PrelevementManagement om;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre

	private CompteCourant compteConcerne;
	private ObservableList<Prelevement> olPrelevement;
	

	// Manipulation de la fenêtre
	public void initContext(Stage _primaryStage, PrelevementManagement _om, DailyBankState _dbstate, CompteCourant compte) {
		this.primaryStage = _primaryStage;
		this.dbs = _dbstate;
		this.om = _om;
		this.compteConcerne = compte;
		this.configure();
	}


	
	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
		this.olPrelevement = FXCollections.observableArrayList();
		this.lvPrelevement.setItems(this.olPrelevement);
		this.lvPrelevement.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.lvPrelevement.getFocusModel().focus(-1);
		this.lvPrelevement.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());
		loadList();
		this.validateComponentState();
	}


	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}

	// Attributs de la scene + actions

    @FXML
    private Button btnFairePrelevement;

    @FXML
    private Button btnModifPrelevement;

    @FXML
    private Button btnVoirPrelevement;

    @FXML
    private ListView<Prelevement> lvPrelevement;

    @FXML
    private Label lblInfosPrelevements;
    
    @FXML
    private Button btnExecuterPrelev;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	public void displayDialog() {
		this.primaryStage.showAndWait();
	}
	
	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}

    @FXML
    void doModifierPrelevement() {
    	int selectedIndice = this.lvPrelevement.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			Prelevement cliMod = this.olPrelevement.get(selectedIndice);
			Prelevement result = this.om.modifierPrelevement(cliMod);
			if (result != null) {
				this.olPrelevement.set(selectedIndice, result);
			}
		}
			
    }
    
    @FXML
    void doVoirPrelevement() {
    	int selectedIndice = this.lvPrelevement.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			Prelevement cliMod = this.olPrelevement.get(selectedIndice);
			Prelevement result = this.om.voirPrelevement(cliMod);
			
		}
    }

    @FXML
    void doPrelevement() {
    	Prelevement pre;
		pre = this.om.enregistrerPrelevement();
		if (pre != null) {
			this.olPrelevement.add(pre);
		}
		loadList();
    }

    
    @FXML
    private void doExecuterPrelevement(){
        int selectedIndice = this.lvPrelevement.getSelectionModel().getSelectedIndex();
        LocalDate local = LocalDate.now();
        int jour = local.getDayOfMonth();
        int idNumCompte = this.olPrelevement.get(selectedIndice).idNumCompte;
        double montant = this.olPrelevement.get(selectedIndice).montant;
        if(this.olPrelevement.get(selectedIndice).dateR == jour){
            try {
                if(this.compteConcerne.solde >= 0) {
                    if (this.compteConcerne.solde - this.olPrelevement.get(selectedIndice).montant >= this.compteConcerne.debitAutorise) {
                        AccessOperation ac = new AccessOperation();
                        ac.insertDebit(idNumCompte, montant, ConstantesIHM.TYPE_OP_8);
                        loadList();
                    }
                    else {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Exécution du prélèvement");
                        alert.setHeaderText("Vous ne pouvez pas exécuter ce prélèvement, le découvert serait dépassé.");
                        alert.showAndWait();
                    }
                }
                else if(this.compteConcerne.solde <= 0) {
                    if (this.compteConcerne.solde - this.olPrelevement.get(selectedIndice).montant >= this.compteConcerne.debitAutorise) {
                        AccessOperation ac = new AccessOperation();
                        ac.insertDebit(idNumCompte, montant, ConstantesIHM.TYPE_OP_8);
                        loadList();
                        
                    } else {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Exécution du prélèvement");
                        alert.setHeaderText("Vous ne pouvez pas exécuter ce prélèvement, le découvert serait dépassé.");
                        alert.showAndWait();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Exécution du prélèvement");
            alert.setHeaderText("Vous ne pouvez pas exécuter ce prélèvement, nous ne sommes pas encore le " + this.olPrelevement.get(selectedIndice).dateR );
            alert.showAndWait();
        }
    }
    
	/**
	 * Permet de définir l'état des différents boutons dédiés à une interaction potentielle de l'utilisateur, en fonction de certaines caractériques du compte.
	 */
	private void validateComponentState() {
		this.btnFairePrelevement.setDisable(false);
		this.btnExecuterPrelev.setDisable(true);
		int selectedIndice = this.lvPrelevement.getSelectionModel().getSelectedIndex();
		
		if(this.compteConcerne.estCloture.equals("O")) {
			if (selectedIndice >= 0 ) {
				Prelevement pre = this.olPrelevement.get(selectedIndice);
				this.btnExecuterPrelev.setDisable(true);
				this.btnFairePrelevement.setDisable(true);
				this.btnModifPrelevement.setDisable(true);
				this.btnVoirPrelevement.setDisable(false);
			}else {
				this.btnFairePrelevement.setDisable(true);
			}
		}
		
		if(!this.compteConcerne.estCloture.equals("O")) {
		  if (selectedIndice >= 0 ) {
			Prelevement pre = this.olPrelevement.get(selectedIndice);
			if (pre.estArrete.equals("N")) {
				this.btnExecuterPrelev.setDisable(false);
				this.btnModifPrelevement.setDisable(false);
				this.btnVoirPrelevement.setDisable(false);
			}
			this.btnVoirPrelevement.setDisable(false);
			if (pre.estArrete.equals("O")) {
				this.btnModifPrelevement.setDisable(true);
				this.btnExecuterPrelev.setDisable(true);				
			}
		  }
		}
	}

	private void loadList() {
		ArrayList<Prelevement> listeCpt;
		listeCpt = this.om.getPrelevementDunCompte();
		this.olPrelevement.clear();
		for (Prelevement co : listeCpt) {
			this.olPrelevement.add(co);
		}
	}
	
}
