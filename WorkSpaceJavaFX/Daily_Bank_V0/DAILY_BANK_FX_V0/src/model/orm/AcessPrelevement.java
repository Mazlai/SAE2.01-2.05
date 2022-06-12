package model.orm;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import application.control.ExceptionDialog;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Operation;
import model.data.Prelevement;
import model.orm.exception.ApplicationException;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.ManagementRuleViolation;
import model.orm.exception.Order;
import model.orm.exception.RowNotFoundOrTooManyRowsException;
import model.orm.exception.Table;

public class AcessPrelevement {

	/**
	 * Recherche de tous les prélèvements d'un compte.
	 *
	 * @param idNumCompte id du compte dont on cherche tous les prélèvements
	 * @return Toutes les opérations du compte, liste vide si pas de prélèvement
	 * @throws DataAccessException
	 * @throws DatabaseConnexionException
	 */
	public ArrayList<Prelevement> getPrelevement(int idNumCompte) throws DataAccessException, DatabaseConnexionException {
		ArrayList<Prelevement> alResult = new ArrayList<>();

		try {
			Connection con = LogToDatabase.getConnexion();
			String query = "SELECT * FROM PRELEVEMENTAUTOMATIQUE where idNumCompte = ?";
			query += " ORDER BY dateRecurrente";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, idNumCompte);

			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				int idPrelev = rs.getInt("idPrelev");
				double montant = rs.getDouble("montant");
				int dateRec = rs.getInt("dateRecurrente");
				String beneficiaire = rs.getString("beneficiaire");
				int idNumCompteTrouve = rs.getInt("idNumCompte");
				String estArrete = rs.getString("estArrete");

				alResult.add(new Prelevement(idPrelev, montant, dateRec, beneficiaire, idNumCompteTrouve, estArrete));
			}
			rs.close();
			pst.close();
			return alResult;
		} catch (SQLException e) {
			throw new DataAccessException(Table.PrelevementAutomatique, Order.SELECT, "Erreur accès", e);
		}
	}
	
	/**
	 * Enregistrement d'un débit.
	 *
	 * Se fait par procédure stockée : - Vérifie que le débitAutorisé n'est pas
	 * dépassé - Enregistre l'opération - Met à jour le solde du compte.
	 *
	 * @param idNumCompte compte débité
	 * @param montant     montant débité
	 * @param typeOp      libellé de l'opération effectuée (cf TypeOperation)
	 * @throws RowNotFoundOrTooManyRowsException
	 * @throws DataAccessException
	 * @throws DatabaseConnexionException
	 * @throws ManagementRuleViolation
	 */
	public void insertPrelevement(Prelevement prelevement)
		throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {
	try {

		Connection con = LogToDatabase.getConnexion();

		String query = "INSERT INTO PRELEVEMENTAUTOMATIQUE VALUES (" + "seq_id_prelevauto.NEXTVAL" + ", " + "?" + ", " + "?" + ", "
				+ "?" + ", " + "?" + ", " + "?"  + ")";
		
		PreparedStatement pst = con.prepareStatement(query);
		
		
		pst.setDouble(1, prelevement.montant); 
		pst.setInt(2, prelevement.dateR);
		pst.setString(3, prelevement.beneficiaire);
		pst.setInt(4, prelevement.idNumCompte);
		pst.setString(5, prelevement.estArrete);

		System.err.println(query);

		int result = pst.executeUpdate();
		pst.close();

		if (result != 1) {
			con.rollback();
			throw new RowNotFoundOrTooManyRowsException(Table.PrelevementAutomatique, Order.INSERT,
					"Insert anormal (insert de moins ou plus d'une ligne)", null, result);
		}

	} catch (SQLException e) {
		System.out.println(prelevement.idNumCompte);
		throw new DataAccessException(Table.PrelevementAutomatique, Order.INSERT, "Erreur accès", e);
	}
}
	
	
	
	
	/**
	 * Mise à jour d'un Client.
	 *
	 * client.idNumCli est la clé primaire et doit exister tous les autres champs
	 * sont des mises à jour. client.idAg non mis à jour (un client ne change
	 * d'agence que par delete/insert)
	 *
	 * @param client IN client.idNumCli (clé primaire) doit exister
	 * @throws RowNotFoundOrTooManyRowsException
	 * @throws DataAccessException
	 * @throws DatabaseConnexionException
	 */
	public void updatePrelevement(Prelevement prelevement)
			throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {
		try {
			Connection con = LogToDatabase.getConnexion();

			String query = "UPDATE PRELEVEMENTAUTOMATIQUE SET " + "MONTANT = " + "? , " + "DATERECURRENTE = " + "? , " + "BENEFICIAIRE = "
					+ "? , " + "ESTARRETE = " + "? " + " "
					+ "WHERE idPrelev = ? ";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setDouble(1, prelevement.montant);
			pst.setInt(2, prelevement.dateR);
			pst.setString(3, prelevement.beneficiaire);
			pst.setString(4, "" + prelevement.estArrete.charAt(0));
			pst.setInt(5, prelevement.idPrelevement);

			System.err.println(query);

			int result = pst.executeUpdate();
			pst.close();
			if (result != 1) {
				con.rollback();
				throw new RowNotFoundOrTooManyRowsException(Table.PrelevementAutomatique, Order.UPDATE,
						"Update anormal (update de moins ou plus d'une ligne)", null, result);
			}
			con.commit();
		} catch (SQLException e) {
			throw new DataAccessException(Table.PrelevementAutomatique, Order.UPDATE, "Erreur accès", e);
		}
	}
	
	
	/**
	 * Permet de cloturer un compte bancaire (met son solde à zéro puis met l'état du compte à estClotue = "O"
	 * 
	 * @param compteAsupprimer IN : le compte en question à cloturer
	 * @throws SQLException
	 * @throws ApplicationException
	 */
	 public void arreterPrelevement(Prelevement prelevementAarreter) throws SQLException, ApplicationException {		

		Connection con = LogToDatabase.getConnexion(); //Connexion à la base de données
		
		String query = "UPDATE PRELEVEMENTAUTOMATIQUE SET ESTARRETE = ? WHERE IDPRELEV = ?";
		
		System.out.println(prelevementAarreter.estArrete);
		
		PreparedStatement pst = con.prepareStatement(query);
		pst.setString(1, prelevementAarreter.estArrete);
		pst.setInt(2, prelevementAarreter.idPrelevement);
		
		int result = pst.executeUpdate();
		pst.close();

		if (result != 1) {
			con.rollback();
			throw new RowNotFoundOrTooManyRowsException(Table.PrelevementAutomatique, Order.UPDATE,
					"Insert anormal (insert de moins ou plus d'une ligne)", null, result);
		}con.commit();
		

		if (Math.random() < -1) {
			throw new ApplicationException(Table.PrelevementAutomatique, Order.UPDATE, "todo : test exceptions", null);
		}

	}
}
