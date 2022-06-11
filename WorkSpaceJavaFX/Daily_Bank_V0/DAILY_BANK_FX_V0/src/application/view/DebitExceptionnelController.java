package application.view;

import java.net.URL;
import java.util.ResourceBundle;

import application.DailyBankState;
import application.control.DailyBankMainFrame;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import model.data.CompteCourant;
import model.data.Employe;
import model.orm.AccessOperation;

public class DebitExceptionnelController implements Initializable {

	@FXML
	Button doAnnuler;
	@FXML
	Button doOK;
	@FXML
	TextField montant;
	private DailyBankMainFrame dbmf;
	private DailyBankState dbs;
	private Stage primaryStage;
	private CompteCourant cpt;
	
	@Override
	public void initialize(URL location, ResourceBundle resources ) {
		this.montant.requestFocus();
	}
	
	@FXML
	public void doAnnuler() {
		this.primaryStage.close();
	}
	
	@FXML
	public void doOK() {
		try {
			if(this.dbs.isChefDAgence()) {
				AccessOperation ao = new AccessOperation();
				ao.insertDebitExceptionnel(this.cpt.idNumCompte, Math.abs(Double.parseDouble(montant.getText())), "Debit Exceptionnel");
				this.primaryStage.close();
			}
			else {
				Alert alerte = new Alert(AlertType.ERROR, "Vous devez contacter votre chef d'agence pour effectuer ce genre d'opération", null);
				alerte.showAndWait();
			}
		}catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public void initContext(Stage _containingStage, DailyBankState _dbstate, CompteCourant _cpt) {
		this.dbs = _dbstate;
		this.primaryStage = _containingStage;
		this.cpt = _cpt;
		
		this.primaryStage.showAndWait();
	}

}