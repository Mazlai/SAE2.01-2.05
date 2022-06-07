package application.view;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import application.DailyBankState;
import application.control.OperationsManagement;
import application.tools.NoSelectionModel;
import application.tools.PairsOfValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Operation;
import model.orm.AccessCompteCourant;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;

public class OperationsManagementController implements Initializable {

	// Etat application
	private DailyBankState dbs;
	private OperationsManagement om;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private Client clientDuCompte;
	private CompteCourant compteConcerne;
	private ObservableList<Operation> olOperation;
	

	// Manipulation de la fenêtre
	public void initContext(Stage _primaryStage, OperationsManagement _om, DailyBankState _dbstate, Client client, CompteCourant compte) {
		this.primaryStage = _primaryStage;
		this.dbs = _dbstate;
		this.om = _om;
		this.clientDuCompte = client;
		this.compteConcerne = compte;
		this.configure();
	}

	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

		this.olOperation = FXCollections.observableArrayList();
		this.lvOperations.setItems(this.olOperation);
		this.lvOperations.setSelectionModel(new NoSelectionModel<Operation>());
		this.updateInfoCompteClient();
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
	private Label lblInfosClient;
	@FXML
	private Label lblInfosCompte;
	@FXML
	private ListView<Operation> lvOperations;
	@FXML
	private Button btnDebit;
	@FXML
	private Button btnCredit;
	@FXML
	private Button btnPDF;
	@FXML
    private Button btnVirement;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}

	@FXML
	private void doDebit() {
		Operation op = this.om.enregistrerDebit();
		if (op != null) {
			this.updateInfoCompteClient();
			this.validateComponentState();
		}
	}
	
	/**
	 * Permet de générer le pdf du relevé bancaire mensuel
	 */
	@FXML
	private void doPDF() {
		 ArrayList<Operation> ALop = new ArrayList<>();
		 AccessCompteCourant acc = new AccessCompteCourant();
	      try {
			ALop = acc.getViewListOperationsDunCompte(this.compteConcerne.idNumCompte);
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DatabaseConnexionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      //  Assume the below is the input file format
	   // conn=mysqlconnect.ConnectDB();
	    //String input ="select * from personne";
	    //pst=conn.prepareStatement(input);
	    //rs=pst.executeQuery();

	      // creation of a document-object
	      Document document = new Document();
	        try {
	          // create a writer
	          PdfWriter.getInstance(
	          // that listens to the document
	          document,
	          // and directs a PDF-stream to a file
	          new FileOutputStream("C:\\Users\\Etudiant\\Downloads\\Relevé_Mensuel.pdf"));
	          // open document
	          document.open();
	          // ajouter table dans le document
	          PdfPTable table = new PdfPTable(4);
	          PdfPCell cell =
	              new PdfPCell(
	                  new Paragraph("Relevé de comptes"));
	          cell.setColspan(4);
		         // cell.setBackgroundColor(Color.red);
		          cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		          table.addCell(cell);
	          
	          PdfPCell cellNumCompte =
	              new PdfPCell(
	                  new Paragraph("Numéro de Compte : " + this.compteConcerne.idNumCompte));
	         
	          cellNumCompte.setColspan(2);
		      cellNumCompte.setHorizontalAlignment(Element.ALIGN_CENTER);
		      table.addCell(cellNumCompte);
		      
		      PdfPCell cellBank =
		              new PdfPCell(
		                  new Paragraph("Agence bancaire : " + this.dbs.getAgAct().nomAg));
		         
		      cellBank.setColspan(2);
		      cellBank.setHorizontalAlignment(Element.ALIGN_CENTER);
			  table.addCell(cellBank);

	          //Définir le texte d'en-tête de la Table

	          cell = new PdfPCell(new Paragraph("ID de l'opération"));
	          //cell.setBackgroundColor(Color.blue);
	          table.addCell(cell);
	          
	          cell = new PdfPCell(new Paragraph("Montant"));
	          //cell.setBackgroundColor(Color.blue);
	          table.addCell(cell);


	          cell = new PdfPCell(new Paragraph("Date"));
	         // cell.setBackgroundColor(Color.blue);
	          table.addCell(cell);
	          


	          cell = new PdfPCell(new Paragraph("Libellé de L'opération"));
	          //cell.setBackgroundColor(Color.blue);
	          table.addCell(cell);

	          //Fill data to the table


	          for (int i = 0; i < ALop.size(); i++) {
	        	  
	        	  //ArrayList<String> fieldV = new ArrayList<String>();

	             // String fieldValuesArray[] = ALop.get(i).toString().split(",");
	        	  

	            
	            	/*  public int idOperation;
	            		public double montant;
	            		public Date dateOp;
	            		public Date dateValeur;
	            		public int idNumCompte;
	            		public String idTypeOp;*/
	            		

	                  table.addCell(""+ALop.get(i).idOperation);
	                  table.addCell(""+ALop.get(i).montant);
	                  table.addCell(""+ALop.get(i).dateOp);

	                  
	                  table.addCell(ALop.get(i).idTypeOp);

	             
	          }

	          document.add(table);
	      } catch (DocumentException de) {
	          de.printStackTrace();
	      } catch (IOException ioe) {
	          ioe.printStackTrace();
	      }

	      // close the document
	      document.close();
	}

	/**
	 * Permet de définir une opération de crédit en mettant à jour, la liste de l'ensemble des opérations effectuées sur le compte et dépendant de l'état du compte.
	 */
	@FXML
	private void doCredit() {
		
		Operation op = this.om.enregistrerCredit();
		if (op != null) {
			this.updateInfoCompteClient();
			this.validateComponentState();
		}
	}

	/**
	 * Permet de définir une opération de crédit en mettant à jour, la liste de l'ensemble des opérations effectuées sur le compte et dépendant de l'état du compte.
	 */
	@FXML
	private void doVirement() {
		
		Operation op = this.om.enregistrerVirement();
		if (op != null) {
			this.updateInfoCompteClient();
			this.validateComponentState();
		}
		
	}

	/**
	 * Permet de définir l'état des différents boutons dédiés à une interaction potentielle de l'utilisateur, en fonction de certaines caractériques du compte.
	 */
	private void validateComponentState() {
			//Condition compte fermé
			if (this.compteConcerne.estCloture.equals("O")) {
				this.btnCredit.setDisable(true);
				this.btnDebit.setDisable(true);
				this.btnVirement.setDisable(true);
				//Condition Virement	
			} else if ((this.compteConcerne.debitAutorise > 0 && this.compteConcerne.solde > (-this.compteConcerne.debitAutorise)) ||
			 (this.compteConcerne.debitAutorise <= 0 && this.compteConcerne.solde > this.compteConcerne.debitAutorise)) {
				this.btnVirement.setDisable(false);
				this.btnCredit.setDisable(false);
				
			} else {
				this.btnCredit.setDisable(false);
				this.btnDebit.setDisable(false);
				this.btnVirement.setDisable(true);
			}
			/*if (this.compteConcerne.solde > 0 && this.compteConcerne.estCloture.equals("N")) {
			this.btnVirement.setDisable(false);*/
			//Sinon par défaut
	}

	private void updateInfoCompteClient() {

		PairsOfValue<CompteCourant, ArrayList<Operation>> opesEtCompte;

		opesEtCompte = this.om.operationsEtSoldeDunCompte();

		ArrayList<Operation> listeOP;
		this.compteConcerne = opesEtCompte.getLeft();
		listeOP = opesEtCompte.getRight();

		String info;
		info = this.clientDuCompte.nom + "  " + this.clientDuCompte.prenom + "  (id : " + this.clientDuCompte.idNumCli
				+ ")";
		this.lblInfosClient.setText(info);

		info = "Cpt. : " + this.compteConcerne.idNumCompte + "  "
				+ String.format(Locale.ENGLISH, "%12.02f", this.compteConcerne.solde) + "  /  "
				+ String.format(Locale.ENGLISH, "%8d", this.compteConcerne.debitAutorise);
		this.lblInfosCompte.setText(info);

		this.olOperation.clear();
		for (Operation op : listeOP) {
			this.olOperation.add(op);
		}

		this.validateComponentState();
	}
}
