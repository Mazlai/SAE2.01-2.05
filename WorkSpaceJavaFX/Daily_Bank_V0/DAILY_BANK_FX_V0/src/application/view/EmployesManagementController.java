package application.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import application.DailyBankState;
import application.control.EmployesManagement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Employe;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;

public class EmployesManagementController implements Initializable {

	// Etat application
	private DailyBankState dbs;
	private EmployesManagement em;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private ObservableList<Employe> ole;

	// Manipulation de la fenêtre
	public void initContext(Stage _primaryStage, EmployesManagement _em, DailyBankState _dbstate) {
		this.em = _em;
		this.primaryStage = _primaryStage;
		this.dbs = _dbstate;
		this.configure();
	}

	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

		this.ole = FXCollections.observableArrayList();
		this.lvEmploye.setItems(this.ole);
		this.lvEmploye.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.lvEmploye.getFocusModel().focus(-1);
		this.lvEmploye.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());
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
    private Button btnModifEmploye;

    @FXML
    private Button btnSuppEmploye;

    @FXML
    private TextField txtNum;

    @FXML
    private TextField txtNom;

    @FXML
    private TextField txtPrenom;
    @FXML
    private ListView<Employe> lvEmploye;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}

	@FXML
	private void doRechercher() {
		int numCompte;
		try {
			String nc = this.txtNum.getText();
			if (nc.equals("")) {
				numCompte = -1;
			} else {
				numCompte = Integer.parseInt(nc);
				if (numCompte < 0) {
					this.txtNum.setText("");
					numCompte = -1;
				}
			}
		} catch (NumberFormatException nfe) {
			this.txtNum.setText("");
			numCompte = -1;
		}

		String debutNom = this.txtNom.getText();
		String debutPrenom = this.txtPrenom.getText();

		if (numCompte != -1) {
			this.txtNom.setText("");
			this.txtPrenom.setText("");
		} else {
			if (debutNom.equals("") && !debutPrenom.equals("")) {
				this.txtPrenom.setText("");
			}
		}

		// Recherche des employés en BD
		ArrayList<Employe> listeEmp;
		listeEmp = this.em.getlisteComptes(numCompte, debutNom, debutPrenom);

		this.ole.clear();
		for (Employe emp : listeEmp) {
			this.ole.add(emp);
		}

		this.validateComponentState();
	}

	@FXML
	private void doModifEmploye() {
		int selectedIndice = this.lvEmploye.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			Employe empMod = this.ole.get(selectedIndice);
			Employe result = this.em.modifierEmploye(empMod);
			if (result != null) {
				this.ole.set(selectedIndice, result);
			}
		}
	}

	@FXML
	private void doSupprimerEmploye() {
	}

	@FXML
	private void doNouvelEmploye() {
		Employe employe;
		employe = this.em.nouvelEmploye();
		if (employe != null) {
			this.ole.add(employe);
		}
	}

	private void validateComponentState() {
		this.btnSuppEmploye.setDisable(true);
		int selectedIndice = this.lvEmploye.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			this.btnModifEmploye.setDisable(false);
		} else {
			this.btnModifEmploye.setDisable(true);
		}
	}
}
