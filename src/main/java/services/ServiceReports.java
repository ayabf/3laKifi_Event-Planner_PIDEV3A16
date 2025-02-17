package services;

import Models.Publications;
import Models.ReportStatus;
import Models.Reports;
import utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceReports {
    Connection cnx = DataSource.getInstance().getConnection();

    public ServiceReports() {
    }
    public void ajouter(String publicationTitle, String username, String reason,String description) throws SQLException {
        int publication_id = -1;
        int user_id = -1;
        String status = "Pending";
        String req1 = "SELECT publication_id FROM publications WHERE title = ?";
        try (PreparedStatement stmt1 = cnx.prepareStatement(req1)) {
            stmt1.setString(1, publicationTitle);
            ResultSet rs = stmt1.executeQuery();
            if (rs.next()) {
                publication_id = rs.getInt("publication_id");
            } else {
                System.out.println(" Erreur : Aucune publication trouvée avec ce titre.");
                return;
            }
        } catch (SQLException ex) {
            System.out.println(" Erreur lors de la récupération de `publication_id` et `description` : " + ex.getMessage());
            return;
        }
        String req2 = "SELECT id_user FROM user WHERE username = ?";
        try (PreparedStatement stmt2 = cnx.prepareStatement(req2)) {
            stmt2.setString(1, username);
            ResultSet rs = stmt2.executeQuery();
            if (rs.next()) {
                user_id = rs.getInt("id_user");
            } else {
                System.out.println("Erreur : Aucun utilisateur trouvé avec ce nom.");
                return;
            }
        } catch (SQLException ex) {
            System.out.println("Erreur lors de la récupération de `user_id` : " + ex.getMessage());
            return;
        }
        String req3 = "INSERT INTO reports (publication_id, id_user, reason, description, report_date, status) VALUES (?, ?, ?, ?, NOW(), ?)";
        try (PreparedStatement stmt3 = cnx.prepareStatement(req3)) {
            stmt3.setInt(1, publication_id);
            stmt3.setInt(2, user_id);
            stmt3.setString(3, reason);
            stmt3.setString(4, description);
            stmt3.setString(5, status);
            stmt3.executeUpdate();
            System.out.println(" Report ajouté avec succès avec la description de la publication !");
        } catch (SQLException ex) {
            System.out.println("Erreur lors de l'ajout du report : " + ex.getMessage());
        }}
    public void supprimer(String publicationTitle, String username) throws SQLException {
        int publication_id = -1;
        int user_id = -1;
        String req1 = "SELECT publication_id FROM publications WHERE title = ?";
        try (PreparedStatement stmt1 = cnx.prepareStatement(req1)) {
            stmt1.setString(1, publicationTitle);
            ResultSet rs1 = stmt1.executeQuery();
            if (rs1.next()) {
                publication_id = rs1.getInt("publication_id");
            } else {
                System.out.println(" Erreur : Aucune publication trouvée avec ce titre.");
                return;
            }
        } catch (SQLException ex) {
            System.out.println("Erreur SQL lors de la récupération de `publication_id` : " + ex.getMessage());
            return;
        }
        String req2 = "SELECT id_user FROM user WHERE username = ?"; // ✅ Correction : `user` au lieu de `users`
        try (PreparedStatement stmt2 = cnx.prepareStatement(req2)) {
            stmt2.setString(1, username);
            ResultSet rs2 = stmt2.executeQuery();
            if (rs2.next()) {
                user_id = rs2.getInt("id_user");
            } else {
                System.out.println("Erreur : Aucun utilisateur trouvé avec ce nom.");
                return;
            }
        } catch (SQLException ex) {
            System.out.println("Erreur SQL lors de la récupération de `id_user` : " + ex.getMessage());
            return;
        }
        String req3 = "DELETE FROM reports WHERE publication_id = ? AND id_user = ?";
        try (PreparedStatement stmt3 = cnx.prepareStatement(req3)) {
            stmt3.setInt(1, publication_id);
            stmt3.setInt(2, user_id);
            int rowsDeleted = stmt3.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println(" Report supprimé avec succès pour la publication `" + publicationTitle + "` et l'utilisateur `" + username + "` !");
            } else {
                System.out.println(" Aucun report trouvé pour cette publication et cet utilisateur.");
            }
        } catch (SQLException ex) {
            System.out.println(" Erreur SQL lors de la suppression du report : " + ex.getMessage());
        }
    }
    public void modifier(String publicationTitle, String username, String newStatus) throws SQLException {
        int publication_id = -1;
        int user_id = -1;
        String req1 = "SELECT publication_id FROM publications WHERE title = ?";
        try (PreparedStatement stmt1 = cnx.prepareStatement(req1)) {
            stmt1.setString(1, publicationTitle);
            ResultSet rs1 = stmt1.executeQuery();
            if (rs1.next()) {
                publication_id = rs1.getInt("publication_id");
            } else {
                System.out.println(" Erreur : Aucune publication trouvée avec ce titre.");
                return;
            }
        } catch (SQLException ex) {
            System.out.println("Erreur SQL lors de la récupération de `publication_id` : " + ex.getMessage());
            return;
        }
        String req2 = "SELECT id_user FROM user WHERE username = ?";
        try (PreparedStatement stmt2 = cnx.prepareStatement(req2)) {
            stmt2.setString(1, username);
            ResultSet rs2 = stmt2.executeQuery();
            if (rs2.next()) {
                user_id = rs2.getInt("id_user");
            } else {
                System.out.println("Erreur : Aucun utilisateur trouvé avec ce nom.");
                return;
            }
        } catch (SQLException ex) {
            System.out.println(" Erreur SQL lors de la récupération de `id_user` : " + ex.getMessage());
            return;
        }
        String checkReq = "SELECT COUNT(*) FROM reports WHERE publication_id = ? AND id_user = ?";
        try (PreparedStatement checkStmt = cnx.prepareStatement(checkReq)) {
            checkStmt.setInt(1, publication_id);
            checkStmt.setInt(2, user_id);
            ResultSet rsCheck = checkStmt.executeQuery();
            if (rsCheck.next() && rsCheck.getInt(1) == 0) {
                System.out.println("Erreur : Aucun report trouvé pour cette publication et cet utilisateur.");
                return;
            }
        } catch (SQLException ex) {
            System.out.println("Erreur lors de la vérification du report : " + ex.getMessage());
            return;
        }
        String req3 = "UPDATE reports SET status = ? WHERE publication_id = ? AND id_user = ?";
        try (PreparedStatement stmt3 = cnx.prepareStatement(req3)) {
            stmt3.setString(1, newStatus);
            stmt3.setInt(2, publication_id);
            stmt3.setInt(3, user_id);
            int rowsUpdated = stmt3.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Le statut du report pour `" + publicationTitle + "` de `" + username + "` a été mis à jour en `" + newStatus + "` !");
            } else {
                System.out.println("Erreur : Impossible de mettre à jour le statut.");
            }
        } catch (SQLException ex) {
            System.out.println("Erreur SQL lors de la mise à jour du statut : " + ex.getMessage());
        }
    }

    public List<Reports> getAll() throws SQLException {
        List<Reports> reports = new ArrayList<>();

        // 🔹 Requête SQL pour récupérer tous les reports
        String req = "SELECT report_id, publication_id, id_user, description, status, report_date FROM reports";

        try (Statement stm = cnx.createStatement();
             ResultSet rs = stm.executeQuery(req)) {

            while (rs.next()) {
                reports.add(new Reports(
                        rs.getInt("report_id"),
                        rs.getInt("publication_id"),
                        rs.getInt("id_user"),
                        rs.getString("description"),
                        ReportStatus.fromString(rs.getString("status")),
                        rs.getDate("report_date").toLocalDate()
                ));
            }
        } catch (SQLException ex) {
            System.out.println("Erreur lors de la récupération des reports : " + ex.getMessage());
        }

        return reports;
    }
}















