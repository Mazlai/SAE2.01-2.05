package application.control;

import application.DailyBankApp;
import application.tools.StageManagement;
import application.view.SimulationController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Simulation {
	
	private Stage primaryStage;
	private SimulationController smc;
		
	/**
	 * Permet de load l'interface simulation 
	 * 
	 * @param  IN : le stage 
	 * @param  IN : La simulation
	 */
	public Simulation(Stage _parentStage) {

		try {
			FXMLLoader loader = new FXMLLoader(SimulationController.class.getResource("simulation.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth()+50, root.getPrefHeight()+10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion des comptes");
			primaryStage.getIcons().add(new Image(DailyBankMainFrame.class.getResourceAsStream("../../DailyBankIcon.png")));
			this.primaryStage.setResizable(false);

			this.smc = loader.getController();
			this.smc.initContext(this.primaryStage, this);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void doSimulationDialog() {
		this.smc.displayDialog();
	}
	
}
