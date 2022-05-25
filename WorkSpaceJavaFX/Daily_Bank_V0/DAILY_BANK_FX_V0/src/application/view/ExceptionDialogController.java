package application.view;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ResourceBundle;

import application.DailyBankState;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.orm.exception.ApplicationException;

/**
 * La classe "ExceptionDialogController" permet de traiter l'ensemble des actions de l'utilisateur concernant les potentielles erreurs présentes sur la base de données, d'une opération effectuée au sein d'un compte d'un client.
 * Cette classe traite ainsi les données déjà renseignées par l'utilisateur, à la fois sur la "vue", correspondant à la partie graphique de l'interface mais également dans le "modèle", signifiant l'univers dans lequel s'inscrit l'application.
 */

public class ExceptionDialogController implements Initializable {

	// Etat application
	private DailyBankState dbs;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private ApplicationException ae;
	// Manipulation de la fenêtre

	public void initContext(Stage _primaryStage, DailyBankState _dbstate, ApplicationException _ae) {
		this.primaryStage = _primaryStage;
		this.dbs = _dbstate;
		this.ae = _ae;
		this.configure();
	}

	/**
	 * Permet de mettre en place la configuration de la fenêtre ainsi que les données chargées. 
	 */
	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
		this.lblTitre.setText(this.ae.getMessage());
		this.txtTable.setText(this.ae.getTableName().toString());
		this.txtOpe.setText(this.ae.getOrder().toString());
		this.txtException.setText(this.ae.getClass().getName());
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		this.ae.printStackTrace(pw);
		this.txtDetails.setText(sw.toString());
	}

	/**
	 * Affiche simplement la fenêtre en question, tout en attendant une quelconque réponse de l'utilisateur.
	 */
	public void displayDialog() {
		this.primaryStage.showAndWait();
	}

	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		return null;
	}

	// Attributs de la scene + actions
	@FXML
	private Label lblTitre;
	@FXML
	private TextField txtTable;
	@FXML
	private TextField txtOpe;
	@FXML
	private TextField txtException;
	@FXML
	private TextArea txtDetails;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	/**
	 * Permet de valider l'ensemble des informations renseignées par l'utilisateur pour pouvoir fermer la fenêtre.
	 */
	@FXML
	private void doOK() {
		this.primaryStage.close();
	}
}
