package application.view;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import application.control.Simulation;
import application.tools.AlertUtilities;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class SimulationController implements Initializable {

	// Etat application
	private Simulation sm;
	
	// Fenêtre physique
	private Stage primaryStage;
	
	// Manipulation de la fenêtre
	public void initContext(Stage _primaryStage, Simulation _sm) {
		this.sm = _sm;
		this.primaryStage = _primaryStage;
		this.configure();
	}	
		
	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
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
	private Label lblTauxAssurance;
	@FXML 
	private TextField txtCapital;
	@FXML
	private TextField txtDuree;
	@FXML
	private TextField txtTauxInteret;
	@FXML
	private TextField txtTauxAssurance;
	@FXML
	private RadioButton btnAssurance;
	@FXML
	private RadioButton btnSansAssurance;
	@FXML
	private Button btnSimuler;
	@FXML
	private Button btnAnnuler;
	@FXML
	private ToggleGroup group;
			
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
	 * Permet de réaliser la simulation d'emprunt en fonction de certaines caractéristiques, le tout affiché dans une alertbox (taux d'intérêt, taux d'assurance, typage, ...)
	 */
	@FXML
	private void doSimulation() {
			
		if(this.isSaisieValide()) {
			
			try {
				double capital = Double.parseDouble(this.txtCapital.getText().toString());
				double duree = Double.parseDouble(this.txtDuree.getText().toString());
				double tauxinteret = Double.parseDouble(this.txtTauxInteret.getText().toString());
				
				if(tauxinteret < 0 || tauxinteret > 100) {
					AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Merci de mettre un taux valide !",
							AlertType.WARNING);
				} else if (tauxinteret == 0) {
					double mensualite = capital/12/duree;
					
					Alert alertinfo = new Alert(AlertType.INFORMATION);
			        alertinfo.setHeaderText("La mensualité hors assurance serait de : " + mensualite + "€.");
			        alertinfo.setTitle("Résultat de la simulation");
			        alertinfo.show();
				} else {
					double mensualite = capital*((tauxinteret/100/12)/(1-Math.pow(1+tauxinteret/100/12, -duree*12)));
					
					if(this.group.getSelectedToggle() == this.btnAssurance) {
						double tauxassurance = Double.parseDouble(this.txtTauxAssurance.getText().toString());
						
						if(tauxassurance < 0 || tauxassurance > 100) {
							AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Merci de mettre un taux valide !",
									AlertType.WARNING);
						} else {
							double mensualiteassurance = mensualite + (tauxassurance*capital/100/12);
							
							Alert alertinfo = new Alert(AlertType.INFORMATION);
					        alertinfo.setHeaderText("La mensualité hors assurance serait de : " + mensualite + "€."
					        						+ "\nLa mensualité avec assurance serait de : " + mensualiteassurance + "€.");
					        alertinfo.setTitle("Résultat de la simulation");
					        alertinfo.show();
						}
					} else {
						Alert alertinfo = new Alert(AlertType.INFORMATION);
				        alertinfo.setHeaderText("La mensualité hors assurance serait de : " + mensualite + "€.");
				        alertinfo.setTitle("Résultat de la simulation");
				        alertinfo.show();
					}
				}				
			} catch (NumberFormatException e) {
				AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Veuillez à respecter la casse lors de vos saisies.",
						AlertType.WARNING);
			}				
		}
	}
		
	/**
	 * Permet d'indiquer si la saisie rentrée par l'utilisateur est valide
	 * @return false s'il est logique de constater que la saisie soit invalide, true à l'inverse
	 */
	private boolean isSaisieValide() {		
			
		if(this.txtCapital.getText().trim().isEmpty()) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le capital doit être renseigné.",
					AlertType.WARNING);
			this.txtCapital.requestFocus();
			return false;
		} else if (this.txtCapital.getText().trim().length() > 6) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le capital ne doit pas dépasser 6 chiffres !",
					AlertType.WARNING);
			this.txtCapital.requestFocus();
			return false;
		}
		
 		if(this.txtDuree.getText().trim().isEmpty()) {
 			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "La durée doit être renseignée.",
					AlertType.WARNING);
			this.txtDuree.requestFocus();
			return false;
 		} else if (this.txtDuree.getText().trim().length() > 6) {
 			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "La durée ne doit pas dépasser 6 chiffres !",
					AlertType.WARNING);
			this.txtDuree.requestFocus();
			return false;
 		}
 		
		if(this.txtTauxInteret.getText().trim().isEmpty()) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le taux d'int�r�t doit �tre renseign� ou n'est pas valide.",
					AlertType.WARNING);
			this.txtTauxInteret.requestFocus();
			return false;
		}
		
		if(this.group.getSelectedToggle() == null) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Indiquez si vous avez une assurance.",
					AlertType.WARNING);
			this.txtTauxInteret.requestFocus();
			return false;
		}
		
		if(this.group.getSelectedToggle() == this.btnAssurance) {
			if(this.txtTauxAssurance.getText().trim().isEmpty()) {
				AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le taux d'assurance doit �tre renseign� ou n'est pas valide.",
						AlertType.WARNING);
				this.txtTauxAssurance.requestFocus();
				return false;
			}
		}
				
		return true;
	}
	
	/**
	 * Permet d'activer/désactiver les boutons de manière logique en fonction de l'état de certains facteurs (si le client est assur� ou non)
	 */
	private void validateComponentState() {	
		
		this.lblTauxAssurance.setDisable(false);
		this.txtTauxAssurance.setDisable(false);
		
		//set "lblTauxAssurance and txtTauxAssurance" to disabled state each time "btnSansAssurance" radiobutton is selected
		this.lblTauxAssurance.disableProperty().bind(this.btnSansAssurance.selectedProperty());
		this.txtTauxAssurance.disableProperty().bind(this.btnSansAssurance.selectedProperty());
	}
	
}
