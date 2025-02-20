package services;

import Models.Comments;
import utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceComments {
    Connection cnx = DataSource.getInstance().getConnection();

    public ServiceComments() {
    }


    public void ajouter(Comments comments, String publicationTitle, String username) throws SQLException {
        int publication_id = -1;
        int user_id = -1;
        String req1 = "SELECT publication_id FROM publications WHERE title = ?";
        try (PreparedStatement stmt1 = cnx.prepareStatement(req1)) {
            stmt1.setString(1, publicationTitle);
            ResultSet rs = stmt1.executeQuery();
            if (rs.next()) {
                publication_id = rs.getInt("publication_id");
            } else {
                System.out.println("Erreur : Aucune publication trouvée avec ce titre.");
                return;
            }
        } catch (SQLException ex) {
            System.out.println("Erreur lors de la récupération de `publication_id` : " + ex.getMessage());
            return;
        }
        String req2 = "SELECT id_user FROM user WHERE username = ?";
        try (PreparedStatement stmt2 = cnx.prepareStatement(req2)) {
            stmt2.setString(1, username);
            ResultSet rs = stmt2.executeQuery();
            if (rs.next()) {
                user_id = rs.getInt("id_user");
            } else {
                System.out.println("Erreur : Aucun utilisateur trouvé avec ce username.");
                return;
            }
        } catch (SQLException ex) {
            System.out.println("Erreur lors de la récupération de `id_user` : " + ex.getMessage());
            return;
        }
        String req3 = "INSERT INTO comments (`content`, `publication_id`, `id_user`) VALUES (?, ?, ?)";
        try (PreparedStatement stmt3 = cnx.prepareStatement(req3)) {
            stmt3.setString(1, comments.getContent());
            stmt3.setInt(2, publication_id);
            stmt3.setInt(3, user_id);
            stmt3.executeUpdate();
            System.out.println(" Commentaire ajouté avec succès !");
        } catch (SQLException ex) {
            System.out.println("Erreur lors de l'ajout du commentaire : " + ex.getMessage());
        }
    }

    public void modifier(String username, String publicationTitle, String newContent) throws SQLException {
        int comment_id = -1;
        String req1 = "SELECT c.comment_id FROM comments c " +
                "JOIN user u ON c.id_user = u.id_user " +
                "JOIN publications p ON c.publication_id = p.publication_id " +
                "WHERE u.username = ? AND p.title = ?";

        try (PreparedStatement stmt1 = cnx.prepareStatement(req1)) {
            stmt1.setString(1, username);
            stmt1.setString(2, publicationTitle);
            ResultSet rs = stmt1.executeQuery();

            if (rs.next()) {
                comment_id = rs.getInt("comment_id");
            } else {
                System.out.println("Erreur : Aucun commentaire trouvé pour cet utilisateur et cette publication.");
                return;
            }
        } catch (SQLException ex) {
            System.out.println(" Erreur lors de la récupération du `comment_id` : " + ex.getMessage());
            return;
        }
        String req2 = "UPDATE comments SET content = ?, comment_date = NOW() WHERE comment_id = ?";
        try (PreparedStatement stmt2 = cnx.prepareStatement(req2)) {
            stmt2.setString(1, newContent);
            stmt2.setInt(2, comment_id);
            int rowsUpdated = stmt2.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println(" Commentaire modifié avec succès !");
            } else {
                System.out.println(" Aucun commentaire trouvé avec cet ID.");
            }
        } catch (SQLException ex) {
            System.out.println(" Erreur lors de la modification du commentaire : " + ex.getMessage());
        }
    }
    public void supprimer(String username, String publicationTitle) throws SQLException {
        int comment_id = -1;
        String req1 = "SELECT c.comment_id FROM comments c " +
                "JOIN user u ON c.id_user = u.id_user " +
                "JOIN publications p ON c.publication_id = p.publication_id " +
                "WHERE u.username = ? AND p.title = ?";
        try (PreparedStatement stmt1 = cnx.prepareStatement(req1)) {
            stmt1.setString(1, username);
            stmt1.setString(2, publicationTitle);
            ResultSet rs = stmt1.executeQuery();

            if (rs.next()) {
                comment_id = rs.getInt("comment_id"); // ✅ Récupération de `comment_id`
            } else {
                System.out.println("Erreur : Aucun commentaire trouvé pour cet utilisateur et cette publication.");
                return;
            }
        } catch (SQLException ex) {
            System.out.println("Erreur lors de la récupération du `comment_id` : " + ex.getMessage());
            return;
        }
        String req2 = "DELETE FROM comments WHERE comment_id = ?";
        try (PreparedStatement stmt2 = cnx.prepareStatement(req2)) {
            stmt2.setInt(1, comment_id);
            int rowsDeleted = stmt2.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println(" Commentaire supprimé avec succès !");
            } else {
                System.out.println(" Aucun commentaire trouvé avec cet ID.");
            }
        } catch (SQLException ex) {
            System.out.println(" Erreur lors de la suppression du commentaire : " + ex.getMessage());
        }
    }


    public List<Comments> getAllByPublicationTitle(String publicationTitle) throws SQLException {
        List<Comments> comments = new ArrayList<>();

        String req = "SELECT p.title AS publicationTitle, u.username, c.content " +
                "FROM comments c " +
                "JOIN user u ON c.id_user = u.id_user " +
                "JOIN publications p ON c.publication_id = p.publication_id " +
                "WHERE p.title = ? ORDER BY c.comment_date DESC";

        try (PreparedStatement stmt = cnx.prepareStatement(req)) {
            stmt.setString(1, publicationTitle);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Comments comment = new Comments(
                        rs.getString("publicationTitle"),
                        rs.getString("username"),
                        rs.getString("content")
                );
                comments.add(comment);
            }
        } catch (SQLException ex) {
            System.out.println(" Erreur lors de la récupération des commentaires : " + ex.getMessage());
        }

        return comments;
    }






}