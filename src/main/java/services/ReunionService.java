package services;

import Models.Reunion;
import utils.MaConnexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReunionService {
    Connection connection= MaConnexion.getInstance().getConn();

    // Annuler une réunion (suppression par ID)
    public void annulerReunion(int id) {
        String query = "DELETE FROM reunion WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, id);
            int affectedRows = pst.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Réunion annulée avec succès !");
            } else {
                System.out.println("Aucune réunion trouvée avec cet ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Ajouter une réunion
    public void ajouterReunion(Reunion reunion) {
        String query = "INSERT INTO reunion (objectif, date_reunion, description, fichier_pv) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, reunion.getObjectif());
            pst.setDate(2, Date.valueOf(reunion.getDateReunion()));
            pst.setString(3, reunion.getDescription());
            pst.setString(4, reunion.getFichierPv());

            pst.executeUpdate();
            System.out.println("Réunion ajoutée avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Récupérer toutes les réunions sans afficher l'ID
    public List<Reunion> getAllReunions() {
        List<Reunion> reunions = new ArrayList<>();
        String query = "SELECT objectif, date_reunion, description, fichier_pv FROM reunion"; // Suppression de l'ID
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Reunion reunion = new Reunion(
                        rs.getString("objectif"),
                        rs.getDate("date_reunion").toLocalDate(),
                        rs.getString("description"),
                        rs.getString("fichier_pv")
                );
                reunions.add(reunion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reunions;
    }

}
