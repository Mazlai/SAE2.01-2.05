package application.view;

import java.net.URL;
import java.util.ResourceBundle;

import application.DailyBankState;
import application.control.LoginDialog;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Employe;

/**
 * La classe "LoginDialogController" permet de traiter l'ensemble des actions de l'utilisateur concernant la définition d'un membre d'une agence bancaire lors de son authentification. 
 * Cette classe traite ainsi les données renseignées par l'utilisateur, à la fois sur la "vue", correspondant à la partie graphique de l'interface mais également dans le "modèle", signifiant l'univers dans lequel s'inscrit l'application.
 */

public class LoginDialogController implements Initializable {

	// Etat application
	private DailyBankState dbs;
	private LoginDialog ld;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre

	// Manipulation de la fenêtre
	public void initContext(Stage _primaryStage, LoginDialog _ld, DailyBankState _dbstate) {
		this.primaryStage = _primaryStage;
		this.ld = _ld;
		this.dbs = _dbstate;
		this.configure();
	}

	/**
	 * Permet de mettre en place la configuration de la fenêtre (ainsi que les données chargées). 
	 */
	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
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
	private TextField txtLogin;
	@FXML
	private PasswordField txtPassword;
	@FXML
	private Label lblMessage;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	/**
	 * Permet l'annulation de modification sur la page et donc, de retourner sur la page précédente, en fermant la fenêtre actuelle.
	 */
	@FXML
	private void doCancel() {
		this.dbs.setEmpAct(null);
		this.primaryStage.close();
	}

	/**
	 * Permet de traiter les différentes conditions de renseignement dans les champs de texte pour un utilisateur. 
	 * Renseigne une erreur si l'une des conditions n'est pas respectée, ou rien et ferme la fenêtre dès lors que les informations sont correctes.
	 */
	@FXML
	private void doOK() {
		String login = this.txtLogin.getText().trim();
		String password = new String(this.txtPassword.getText().trim());
		if (login.length() == 0 || password.length() == 0) {
			this.afficheErreur("Identifiants incorrects :");
		} else {
			Employe e;
			e = this.ld.chercherParLogin(login, password);
			if (e == null) {
				this.afficheErreur("Identifiants incorrects :");
			} else {
				this.dbs.setEmpAct(e);
				this.primaryStage.close();
			}
		}
	}

	/**
	 * Permet d'afficher un message d'erreur lorsque l'utilisateur n'a pas respecté l'une des conditions nécessaires à son authentification.
	 * @param texte IN : le texte affichant le message d'erreur
	 */
	private void afficheErreur(String texte) {
		this.lblMessage.setText(texte);
		this.lblMessage.setStyle("-fx-text-fill:red; -fx-font-weight: bold;");
	}
}
