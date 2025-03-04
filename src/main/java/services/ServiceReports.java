package services;

import Models.ReportStatus;
import Models.Reports;
import Models.session;
import utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceReports {
    Connection cnx = DataSource.getInstance().getConnection();

    public ServiceReports() {}
    int user_id= session.id_utilisateur;
    public List<Reports> getAll() throws SQLException {
        List<Reports> reports = new ArrayList<>();

        // 🔹 Correction : Ajout du titre de publication et du username
        String req = "SELECT r.report_id = ? , r.publication_id = ? , p.title = ? AS publicationTitle , " +
                "r.id_user = ? , u.username = ? AS username, r.description, r.status, r.report_date " +
                "FROM reports r " +
                "JOIN publications p ON r.publication_id = p.publication_id " +
                "JOIN user u ON r.id_user = u.id_user " +
                "WHERE r.id_user = ?";


        try (Statement stm = cnx.createStatement();
             ResultSet rs = stm.executeQuery(req)) {

            while (rs.next()) {
                reports.add(new Reports(
                        rs.getInt("report_id"),
                        rs.getInt("publication_id"),
                        rs.getString("publicationTitle"),
                        rs.getInt("id_user"),
                        rs.getString("username"),
                        rs.getString("description"),
                        ReportStatus.fromString(rs.getString("status")),
                        rs.getDate("report_date").toLocalDate()
                ));
            }
        } catch (SQLException ex) {
            System.out.println("❌ Erreur lors de la récupération des reports : " + ex.getMessage());
        }

        return reports;
    }

    public void supprimer(String publicationTitle, String username) throws SQLException {
        int publication_id = -1;
        int user_id = -1;

        // Récupérer `publication_id`
        String req1 = "SELECT publication_id FROM publications WHERE title = ?";
        try (PreparedStatement stmt1 = cnx.prepareStatement(req1)) {
            stmt1.setString(1, publicationTitle);
            ResultSet rs1 = stmt1.executeQuery();
            if (rs1.next()) {
                publication_id = rs1.getInt("publication_id");
            } else {
                System.out.println("❌ Erreur : Aucune publication trouvée avec ce titre.");
                return;
            }
        }

        // Récupérer `id_user`
        String req2 = "SELECT id_user FROM user WHERE username = ?";
        try (PreparedStatement stmt2 = cnx.prepareStatement(req2)) {
            stmt2.setString(1, username);
            ResultSet rs2 = stmt2.executeQuery();
            if (rs2.next()) {
                user_id = rs2.getInt("id_user");
            } else {
                System.out.println("❌ Erreur : Aucun utilisateur trouvé avec ce nom.");
                return;
            }
        }

        // 🔥 **Correction : Vérifier si un report existe avant de supprimer**
        String checkReq = "SELECT COUNT(*) FROM reports WHERE publication_id = ? AND id_user = ?";
        try (PreparedStatement checkStmt = cnx.prepareStatement(checkReq)) {
            checkStmt.setInt(1, publication_id);
            checkStmt.setInt(2, user_id);
            ResultSet rsCheck = checkStmt.executeQuery();
            if (rsCheck.next() && rsCheck.getInt(1) == 0) {
                System.out.println("❌ Erreur : Aucun report trouvé pour cette publication et cet utilisateur.");
                return;
            }
        }

        // **Exécuter la suppression**
        String req3 = "DELETE FROM reports WHERE publication_id = ? AND id_user = ?";
        try (PreparedStatement stmt3 = cnx.prepareStatement(req3)) {
            stmt3.setInt(1, publication_id);
            stmt3.setInt(2, user_id);
            int rowsDeleted = stmt3.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("✅ Report supprimé avec succès !");
            } else {
                System.out.println("❌ Aucun report supprimé, vérifiez les IDs.");
            }
        }
    }

    public void ajouter(String title, String username, String reason, String description) {
    }

    public void modifier(String publicationTitle, String username, String newStatus) throws SQLException {
        int publication_id = -1;
        int user_id = -1;

        // Récupérer l'ID de la publication
        String req1 = "SELECT publication_id FROM publications WHERE title = ?";
        try (PreparedStatement stmt1 = cnx.prepareStatement(req1)) {
            stmt1.setString(1, publicationTitle);
            ResultSet rs1 = stmt1.executeQuery();
            if (rs1.next()) {
                publication_id = rs1.getInt("publication_id");
            } else {
                System.out.println("Erreur : Aucune publication trouvée avec ce titre.");
                return;
            }
        }

        // Récupérer l'ID de l'utilisateur
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
        }

        // Vérifier si le report existe
        String checkReq = "SELECT COUNT(*) FROM reports WHERE publication_id = ? AND id_user = ?";
        try (PreparedStatement checkStmt = cnx.prepareStatement(checkReq)) {
            checkStmt.setInt(1, publication_id);
            checkStmt.setInt(2, user_id);
            ResultSet rsCheck = checkStmt.executeQuery();
            if (rsCheck.next() && rsCheck.getInt(1) == 0) {
                System.out.println("Erreur : Aucun report trouvé pour cette publication et cet utilisateur.");
                return;
            }
        }

        // Mise à jour du statut dans la table reports
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
        }
    }
    public void modifier(int reportId, String newStatus) throws SQLException {
        if (reportId <= 0 || newStatus == null || newStatus.isEmpty()) {
            System.out.println("❌ Erreur : Paramètres invalides pour la mise à jour. reportId=" + reportId + ", newStatus=" + newStatus);
            return;
        }

        System.out.println("🔄 Tentative de mise à jour du report ID " + reportId + " en : " + newStatus);

        String req = "UPDATE reports SET status = ? WHERE report_id = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(req)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, reportId);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Succès : Statut du report ID " + reportId + " mis à jour en : " + newStatus);
            } else {
                System.out.println("❌ Échec : Aucun report trouvé avec cet ID : " + reportId);
            }
        } catch (SQLException ex) {
            System.err.println("❌ Erreur SQL lors de la mise à jour du report ID " + reportId + " : " + ex.getMessage());
        }
    }




}




