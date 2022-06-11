package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.CategorieOperation;
import application.tools.StageManagement;
import application.view.OperationEditorPaneController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.CompteCourant;
import model.data.Operation;

/**
 * La classe OperationEditorPane permet de générer la ressource FXML associé à son controller et d'ouvrir la fenetre pour saisir le type d'operation à effectuer et le montant à saisir parmi les differentes categorie d'opérations(Virement,Credit,Debit).. 
 */
public class OperationEditorPane {

	private Stage primaryStage;
	private OperationEditorPaneController oepc;
	
	public OperationEditorPaneController getOepc() {
		return this.oepc;
	}
	
	/**
	 * Permet de générer la ressource operationeditorpane.fxml depuis son controller. Elle prend en parametre la fenetre(Stage) et l'état de l'agence bancaire(DailyBankState).
	 * @param _parentStage
	 * @param _dbstate
	 */
	public OperationEditorPane(Stage _parentStage, DailyBankState _dbstate) {

		try {
			FXMLLoader loader = new FXMLLoader(
					OperationEditorPaneController.class.getResource("operationeditorpane.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, 500 + 20, 250 + 10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Enregistrement d'une opération");
			primaryStage.getIcons().add(new Image(DailyBankMainFrame.class.getResourceAsStream("../../DailyBankIcon.png")));
			this.primaryStage.setResizable(false);

			this.oepc = loader.getController();
			this.oepc.initContext(this.primaryStage, _dbstate);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Permet d'afficher le contenu de la fenetre pour saisir les opérations à effectuer et attends une interaction potentielle avec celle-ci
	 * Prend en parametre le Compte courant à appliquer l'opération et la categorie de l'operation(Virement,Credit,Debit)
	 * @param cpte CompteCourant
	 * @param cm Categorie 
	 * @return le contenu à afficher dans la boites de dialogue
	 */
	public Operation doOperationEditorDialog(CompteCourant cpte, CategorieOperation cm) {
		return this.oepc.displayDialog(cpte, cm);
	}
}