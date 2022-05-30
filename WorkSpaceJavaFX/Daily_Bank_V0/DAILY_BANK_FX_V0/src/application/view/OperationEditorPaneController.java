package application.view;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.ArrayList;
import application.DailyBankState;
import application.tools.CategorieOperation;
import application.tools.ConstantesIHM;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.orm.AccessCompteCourant;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.data.CompteCourant;
import model.data.Operation;

/**
 * La classe "OperationsEditorPaneController" permet de traiter l'ensemble des actions de l'utilisateur concernant une opération en train d'être effectuée par un nouveau client sur le compte désigné.
 * Cette classe traite ainsi les données renseignées par l'utilisateur, à la fois sur la "vue", correspondant à la partie graphique de l'interface mais également dans le "modèle", signifiant l'univers dans lequel s'inscrit l'application.
 */

public class OperationEditorPaneController implements Initializable {

	// Etat application
	private DailyBankState dbs;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private CategorieOperation categorieOperation;
	private CompteCourant compteEdite;
	private Operation operationResultat;

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
	 * Permet de définir l'ensemble des caractéristiques de la fenêtre d'édition d'une opération tout en attendant la réponse de l'éditeur.
	 * 
	 * @param cpte IN : le compte concerné
	 * @param mode IN : la catégorie de l'opération effectuée
	 * @return : le résultat d'une opération 
	 */
	public Operation displayDialog(CompteCourant cpte, CategorieOperation mode) {
		this.categorieOperation = mode;
		this.compteEdite = cpte;

		switch (mode) {
		case DEBIT:

			String info = "Cpt. : " + this.compteEdite.idNumCompte + "  "
					+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
					+ String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
			this.lblMessage.setText(info);

			this.btnOk.setText("Effectuer Débit");
			this.btnCancel.setText("Annuler débit");

			ObservableList<String> list = FXCollections.observableArrayList();

			for (String tyOp : ConstantesIHM.OPERATIONS_DEBIT_GUICHET) {
				list.add(tyOp);
			}

			this.cbTypeOpe.setItems(list);
			this.cbTypeOpe.getSelectionModel().select(0);
			break;
			
		case CREDIT:

			String info2 = "Cpt. : " + this.compteEdite.idNumCompte + "  "
					+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
					+ String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
			this.lblMessage.setText(info2);

			this.btnOk.setText("Effectuer Crédit");
			this.btnCancel.setText("Annuler Crédit");

			ObservableList<String> list2 = FXCollections.observableArrayList();

			for (String tyOp : ConstantesIHM.OPERATIONS_CREDIT_GUICHET) {
				list2.add(tyOp);
			}

			this.cbTypeOpe.setItems(list2);
			this.cbTypeOpe.getSelectionModel().select(0);
			break;

		case VIREMENT:

            String info3 = "Cpt. : " + this.compteEdite.idNumCompte + "  "
                    + String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
                    + String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
            this.lblMessage.setText(info3);

            this.lblCompte.setText("Choisissez un compte :");
            this.btnOk.setText("Effectuer Virement");
            this.btnCancel.setText("Annuler Virement");

            String STR = ConstantesIHM.OPERATIONS_VIREMENT_GUICHET;
            this.primaryStage.setTitle(STR);
            
            ObservableList<String> list3 = FXCollections.observableArrayList();

            ArrayList<CompteCourant> listeCpt = new ArrayList<>();
            
            
            try {
                AccessCompteCourant acc = new AccessCompteCourant();
                try {
                    listeCpt = acc.getTousLesComptes();
                } catch (DataAccessException e) {
                    e.printStackTrace();
                }    
                
                for (CompteCourant tyOp : listeCpt) {
                	if(tyOp.idNumCompte == this.compteEdite.idNumCompte) {
                		list3.remove(tyOp.toString());
                	} else {
                		list3.add(tyOp.toString());
                	}
                }
                
            } catch(DatabaseConnexionException e) {
                e.printStackTrace();
            }
            
            this.cbTypeOpe.setItems(list3);
            this.cbTypeOpe.getSelectionModel().select(0);
            break;    
            
        }

		// Paramétrages spécifiques pour les chefs d'agences
		if (ConstantesIHM.isAdmin(this.dbs.getEmpAct())) {
			// rien pour l'instant
		}

		this.operationResultat = null;
		this.cbTypeOpe.requestFocus();

		this.primaryStage.showAndWait();
		return this.operationResultat;
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
	private Label lblMontant;
	@FXML
	private Label lblCompte;	
	@FXML
	private ComboBox<String> cbTypeOpe;	
	@FXML
	private TextField txtMontant;
	@FXML
	private Button btnOk;
	@FXML
	private Button btnCancel;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	/**
	 * Permet l'annulation de modification sur la page et donc, de retourner sur la page précédente, en fermant la fenêtre actuelle.
	 */
	@FXML
	private void doCancel() {
		this.operationResultat = null;
		this.primaryStage.close();
	}

	/**
	 * Permet l'édition d'une opération sur un compte selon sa catégorie avant d'être ajoutée dans la liste des opérations.
	 */
	@FXML
	private void doAjouter() {
		switch (this.categorieOperation) {
		case DEBIT:
			// règles de validation d'un débit :
			// - le montant doit être un nombre valide
			// - et si l'utilisateur n'est pas chef d'agence,
			// - le débit ne doit pas amener le compte en dessous de son découvert autorisé
			double montant;

			this.txtMontant.getStyleClass().remove("borderred");
			this.lblCompte.getStyleClass().remove("borderred");
			this.lblMontant.getStyleClass().remove("borderred");
			this.lblMessage.getStyleClass().remove("borderred");
			String info = "Cpt. : " + this.compteEdite.idNumCompte + "  "
					+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
					+ String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
			this.lblMessage.setText(info);

			try {
				montant = Double.parseDouble(this.txtMontant.getText().trim());
				if (montant <= 0)
					throw new NumberFormatException();
			} catch (NumberFormatException nfe) {
				this.txtMontant.getStyleClass().add("borderred");
				this.lblMontant.getStyleClass().add("borderred");
				this.lblCompte.getStyleClass().add("borderred");
				this.txtMontant.requestFocus();
				return;
			}
			if (this.compteEdite.solde - montant < this.compteEdite.debitAutorise ) {
				info = "Dépassement du découvert ! - Cpt. : " + this.compteEdite.idNumCompte + "  "
						+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
						+ String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
				this.lblMessage.setText(info);
				this.txtMontant.getStyleClass().add("borderred");
				this.lblMontant.getStyleClass().add("borderred");
				this.lblCompte.getStyleClass().add("borderred");
				this.lblMessage.getStyleClass().add("borderred");
				this.txtMontant.requestFocus();
				return;
			}

			String typeOp = this.cbTypeOpe.getValue();
			
			this.operationResultat = new Operation(-1, montant, null, null, this.compteEdite.idNumCli, typeOp);
			this.primaryStage.close();
			break;
		case CREDIT:
			// règles de validation d'un crédit :
			// - le montant doit être un nombre valide
			// - et si l'utilisateur n'est pas chef d'agence,
			double montant2;

			this.txtMontant.getStyleClass().remove("borderred");
			this.lblCompte.getStyleClass().remove("borderred");
			this.lblMontant.getStyleClass().remove("borderred");
			this.lblMessage.getStyleClass().remove("borderred");
			String info2 = "Cpt. : " + this.compteEdite.idNumCompte + "  "
					+ String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
					+ String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
			this.lblMessage.setText(info2);

			try {
				montant2 = Double.parseDouble(this.txtMontant.getText().trim());
				if (montant2 <= 0)
					throw new NumberFormatException();
			} catch (NumberFormatException nfe) {
				this.txtMontant.getStyleClass().add("borderred");
				this.lblMontant.getStyleClass().add("borderred");
				this.lblCompte.getStyleClass().add("borderred");
				this.txtMontant.requestFocus();
				return;
		
			}
			String typeOp2 = this.cbTypeOpe.getValue();
			this.operationResultat = new Operation(-1, montant2, null, null, this.compteEdite.idNumCli, typeOp2);
			this.primaryStage.close();
			break;
			// ce genre d'operation n'est pas encore géré
			/*this.operationResultat = null;
			this.primaryStage.close();
			break;*/
			
		case VIREMENT:
            // règles de validation d'un virement :
            // - le montant doit être un nombre valide
            // - le solde doit être supérieur au débit autorisé
            double montant3;
            
            this.txtMontant.getStyleClass().remove("borderred");
            this.lblMontant.getStyleClass().remove("borderred");
            this.lblCompte.getStyleClass().remove("borderred");
            this.lblMessage.getStyleClass().remove("borderred");
            String info3 = "Cpt. : " + this.compteEdite.idNumCompte + "  "
                    + String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
                    + String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
            this.lblMessage.setText(info3);

            try {
                montant3 = Double.parseDouble(this.txtMontant.getText().trim());
                if (montant3 <= 0)
                    throw new NumberFormatException();
            } catch (NumberFormatException nfe) {
                this.txtMontant.getStyleClass().add("borderred");
                this.lblMontant.getStyleClass().add("borderred");
                this.lblCompte.getStyleClass().add("borderred");
                this.txtMontant.requestFocus();
                return;
            }
            if (this.compteEdite.solde - montant3 < this.compteEdite.debitAutorise) {
                info = "Dépassement du découvert ! - Cpt. : " + this.compteEdite.idNumCompte + "  "
                        + String.format(Locale.ENGLISH, "%12.02f", this.compteEdite.solde) + "  /  "
                        + String.format(Locale.ENGLISH, "%8d", this.compteEdite.debitAutorise);
                this.lblMessage.setText(info);
                this.txtMontant.getStyleClass().add("borderred");
                this.lblMontant.getStyleClass().add("borderred");
                this.lblCompte.getStyleClass().add("borderred");
                this.lblMessage.getStyleClass().add("borderred");
                this.txtMontant.requestFocus();
                return;
            }
            String typeOp3 = this.cbTypeOpe.getValue();
            this.operationResultat = new Operation(-1, montant3, null, null, this.compteEdite.idNumCli, typeOp3);
            this.primaryStage.close();
            break;
		}
	}
	
	/** Renvoie le compte sélectionné
	 * 
	 * @return Le compte sélectionné à l'index actuel dans le modèle
	 */
	public int getCompteSelectionne() {

		return this.cbTypeOpe.getSelectionModel().getSelectedIndex();
	}
}
