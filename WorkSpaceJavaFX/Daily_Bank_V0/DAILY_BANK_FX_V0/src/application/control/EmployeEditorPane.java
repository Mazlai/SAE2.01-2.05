package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.ClientEditorPaneController;
import application.view.ClientsManagementController;
import application.view.EmployeEditorPaneController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.Employe;

public class EmployeEditorPane {

	private Stage primaryStage;
	private EmployeEditorPaneController eepc;

	
	/**
	 * Constructeur de EmployeEditorPane
	 * @param _parentStage Fenêtre mère dont celle-ci dépendra
	 * @param _dbstate Informations sur l'utilisateur : chef d'agence, guichetier
	 */
	public EmployeEditorPane(Stage _parentStage, DailyBankState _dbstate) {

		try {
			FXMLLoader loader = new FXMLLoader(ClientsManagementController.class.getResource("employeeditorpane.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth()+20, root.getPrefHeight()+10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion d'un employé");
			primaryStage.getIcons().add(new Image(DailyBankMainFrame.class.getResourceAsStream("../../DailyBankIcon.png")));
			this.primaryStage.setResizable(false);

			this.eepc = loader.getController();
			this.eepc.initContext(this.primaryStage, _dbstate);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Ouvre la fenêtre d'édition d'un employé
	 * @param employe Employé à modifier
	 * @param em Type de modification parmi : CREATION, MODIFICATION, SUPPRESSION, VOIR
	 * @return
	 */
	public Employe doEmployeEditorDialog(Employe employe, EditionMode em) {
		return this.eepc.displayDialog(employe, em);
	}
}
