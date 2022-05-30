package application.view;

import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import application.DailyBankState;
import application.control.ExceptionDialog;
import application.tools.AlertUtilities;
import application.tools.ConstantesIHM;
import application.tools.EditionMode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Operation;
import model.orm.AccessClient;
import model.orm.AccessCompteCourant;
import model.orm.AccessOperation;
import model.orm.exception.ApplicationException;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.ManagementRuleViolation;
import model.orm.exception.Order;
import model.orm.exception.RowNotFoundOrTooManyRowsException;
import model.orm.exception.Table;

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
	/**
	 * Permet d'initialiser le contexte de la page
	 * 
	 * @param _primaryStage IN : La fenêtre de l'interface
	 * @param _dbstate IN : La frame de daily bank
	 */
	public void initContext(Stage _primaryStage, DailyBankState _dbstate) {
		this.primaryStage = _primaryStage;
		this.dbs = _dbstate;
		this.configure();
	}

	/**
	 * Permet de créer un event lors de la fermeture de la page
	 */
	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
	}

	/**
	 * Permet de bien agencer les boutons de l'interface qui s'ouvre selon les modes disponibles
	 * 
	 * @param client IN : Le client que l'on va éditer
	 * @param mode IN : Décrit l'état de l'ouverture de la page
	 * @return le client qui est modifié
	 */
	public Client displayDialog(Client client, EditionMode mode) {

		this.em = mode;
		if (client == null) {
			this.clientEdite = new Client(0, "", "", "", "", "", "*", this.dbs.getEmpAct().idAg);
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
			
		case VOIR:
			this.butOk.setVisible(false);
			this.butCancel.setText("Fin consultation");
			this.rbActif.setDisable(true);
			this.rbInactif.setDisable(true);
			this.txtIdcli.setDisable(true);
			this.txtNom.setDisable(true);
			this.txtPrenom.setDisable(true);
			this.txtTel.setDisable(true);
			this.txtMail.setDisable(true);
			this.txtAdr.setDisable(true);
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

	@FXML
	private void doCancel() {
		this.clientResult = null;
		this.primaryStage.close();
	}

	@FXML
	private void doAjouter() {
		switch (this.em) {
		case CREATION:
			if (this.isSaisieValide()) {
				this.clientResult = this.clientEdite;
				if(this.rbInactif.isSelected()==true) {
					AlertUtilities.showAlert(this.primaryStage, "Non conformité des règles", null, "Le client n'a pas de compte.",
	    					AlertType.WARNING);
				}
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
		case VOIR:
			this.clientResult = this.clientEdite;
			this.primaryStage.close();
		}

	}
	
	/**
	 * Permet de rendre inactif un client en cliquant sur le radio button "Inactif" de la page clienteditor.fxml
	 */
	@FXML
	private void doRendreInactif() {
	        AccessCompteCourant acc = new AccessCompteCourant();
	        ArrayList<CompteCourant> alComptes;
	        AccessClient accli = new AccessClient();
	        AccessOperation accop = new AccessOperation();
	        try {
	            alComptes = acc.getCompteCourants(this.clientEdite.idNumCli);
	            
	            int nbDeCompteDuClient = 0;
	            int nbCompteCloture = 0;
	            
	            for(int i = 0 ; i <alComptes.size(); ++i) {
	            	if(alComptes.get(i).estCloture.equals("O")) {
	            		nbCompteCloture +=1;
	            	}
	            	 nbDeCompteDuClient +=1;
	            }
	            if (alComptes.size() != 0) {
		            if(nbCompteCloture == nbDeCompteDuClient) {
		            	 try {
							accli.rendreInactif(this.clientEdite);
						} catch (RowNotFoundOrTooManyRowsException e) {
							e.printStackTrace();
						} catch (SQLException e) {
							e.printStackTrace();
						}
		            }else {
		            	
		         
		            	Alert alert = new Alert(AlertType.CONFIRMATION);
		        		alert.setTitle("Information : code d'erreur !");
		        		alert.setHeaderText("Vous ne pourrez rendre inactif un client, une fois que tous ses comptes seront clôturer.\nVoulez-vous clôturer tous les comptes de "+this.clientEdite.nom + " " + this.clientEdite.prenom + " ?");

		        		ButtonType cloturerAllCompte = new ButtonType("Clôturer tous les comptes");
		        		ButtonType ok = new ButtonType("D'accord");
		        		

		        	
		        		alert.getButtonTypes().clear();

		        		alert.getButtonTypes().addAll(cloturerAllCompte, ok);

		        		Optional<ButtonType> option = alert.showAndWait();

		        		if (option.get() == null) {
		        			alert.close();
		        		} else if (option.get() == cloturerAllCompte) {
		        			 alComptes = acc.getCompteCourants(this.clientEdite.idNumCli);
		     	            
		     	            for(int i = 0 ; i <alComptes.size(); ++i) {
		     	            	
		     	            		try {
										acc.cloturerCompte(alComptes.get(i));
										accli.rendreInactif(this.clientEdite);
									} catch (RowNotFoundOrTooManyRowsException e) {
										e.printStackTrace();
									
								
									} catch (SQLException e) {
										e.printStackTrace();
									} catch (ApplicationException e) {
										e.printStackTrace();
									}
	
		     	            }
		     	            Alert alertinfo = new Alert(AlertType.INFORMATION);
		     	            alertinfo.setHeaderText("Tous les comptes de : " + this.clientEdite.nom + " " + this.clientEdite.prenom+ " ont été clôturé.\nVous pouvez désormais le rendre inactif.");
		     	            alertinfo.setTitle("Comptes clôturés");
		     	            alertinfo.show();
		     	            
		        			
		        		} else if (option.get() == ok) {
		        			alert.close();
		        		} else {
		        			alert.showAndWait();
		        		}
		             }
	            }
	            else {
	            		AlertUtilities.showAlert(this.primaryStage, "Non conformité des règles", null, "Le client n'a pas de compte.",
	    					AlertType.WARNING);
	            		this.rbInactif.setSelected(false);
						this.rbActif.setSelected(true);
	            }
	            
	        } catch (DataAccessException dae) {
	            ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, dae);
	            ed.doExceptionDialog();
	            this.primaryStage.close();
	            return;
	        } catch (DatabaseConnexionException dce) {
	            ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, dce);
	            ed.doExceptionDialog();
	            this.primaryStage.close();
	            return;
	        }
	}

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
