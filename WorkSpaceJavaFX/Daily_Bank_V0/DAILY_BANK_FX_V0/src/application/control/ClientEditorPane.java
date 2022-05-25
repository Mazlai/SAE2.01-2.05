package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.ClientEditorPaneController;
import application.view.ClientsManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;

/**
 * La classe ClientEditorPane permet de générer la ressource FXML associé à son controller et d'ouvrir la fenetre pour saisir les informations d'un client. 
 *
 */
public class ClientEditorPane {

	private Stage primaryStage;
	private ClientEditorPaneController cepc;
	
	/**Procédure pour générer la ressource clienteditorpane.fxml depuis son controller. Elle prend en parametre la fenetre(Stage) et l'état de l'agence bancaire(DailyBankState).
	 * @param _parentStage
	 * @param _dbstate
	 */
	public ClientEditorPane(Stage _parentStage, DailyBankState _dbstate) {

		try {
			FXMLLoader loader = new FXMLLoader(ClientsManagementController.class.getResource("clienteditorpane.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth()+20, root.getPrefHeight()+10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion d'un client");
			this.primaryStage.setResizable(false);

			this.cepc = loader.getController();
			this.cepc.initContext(this.primaryStage, _dbstate);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**Permet d'afficher le contenu de la fenetre pour saisir les informations d'un client et attends une interaction potentielle avec celle-ci
	 * @param client
	 * @param em
	 * @return le contenu à afficher dans la boites de dialogue
	 */
	public Client doClientEditorDialog(Client client, EditionMode em) {
		return this.cepc.displayDialog(client, em);
	}
}
