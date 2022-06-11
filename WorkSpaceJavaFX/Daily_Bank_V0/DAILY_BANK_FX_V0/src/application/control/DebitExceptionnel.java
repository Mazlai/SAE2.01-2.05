package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.StageManagement;
import application.view.DebitExceptionnelController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.CompteCourant;

public class DebitExceptionnel {
	private Stage primaryStage;
	private DebitExceptionnelController dec;
	private CompteCourant cpt;
	private DailyBankState dbstate;

	
	/**
	 * Constructeur de DebitExceptionnelController
	 * @param _parentStage Fenêtre mère dont celle-ci dépendra
	 * @param _dbstate Informations sur l'utilisateur : chef d'agence, guichetier
	 */
	public DebitExceptionnel(Stage _parentStage, DailyBankState _dbstate, CompteCourant _cpt) {

		this.cpt = _cpt;
		this.dbstate = _dbstate;
		
		try {
			FXMLLoader loader = new FXMLLoader(DebitExceptionnelController.class.getResource("debitexceptionnel.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth()+20, root.getPrefHeight()+10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Débit Exceptionnel");
			primaryStage.getIcons().add(new Image(DailyBankMainFrame.class.getResourceAsStream("../../DailyBankIcon.png")));
			this.primaryStage.setResizable(false);

			this.dec = loader.getController();
			this.dec.initContext(this.primaryStage, dbstate, cpt);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
