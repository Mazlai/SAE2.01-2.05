package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.StageManagement;
import application.view.ExceptionDialogController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.orm.exception.ApplicationException;

/**
 * La classe ExceptionDialog permet de génerer la ressource FXML qui affiche la fenetre de dialogue indiquant les messages d'erreurs, d'exceptions attrapés durant les actions et opérations.
 * Cette fenetre affiche la Table SQL, l'operation et le message d'erreur concerné.
 */
public class ExceptionDialog {

	private Stage primaryStage;
	private ExceptionDialogController edc;

	public ExceptionDialog(Stage _parentStage, DailyBankState _dbstate, ApplicationException ae) {

		try {
			FXMLLoader loader = new FXMLLoader(ExceptionDialogController.class.getResource("exceptiondialog.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, 700 + 20, 400 + 10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Opération impossible");
			this.primaryStage.setResizable(false);

			this.edc = loader.getController();
			this.edc.initContext(this.primaryStage, _dbstate, ae);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**Permet d'afficher le contenu de la fenetre pour les exceptions et les messages d'ereurs et attends une interaction potentielle avec celle-ci
	 * 
	 * @return le contenu à afficher dans la boites de dialogue
	 */
	public void doExceptionDialog() {
		this.edc.displayDialog();
	}

	/*
	 * Test : ApplicationException ae = new ApplicationException(Table.NONE,
	 * Order.OTHER, "M", null ); ExceptionDialogTemp ed = new
	 * ExceptionDialogTemp(primaryStage, dbs, ae); ed.doExceptionDisplay();
	 */
}
