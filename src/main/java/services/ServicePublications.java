package services;

import Models.Publications;
import Models.session;
import utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicePublications {
    private final Connection cnx;

    public ServicePublications() {
        this.cnx = DataSource.getInstance().getConnection();
    }

    /**
     * ✅ Ajoute une nouvelle publication dans la base de données.
     */
    public void ajouter(Publications publications) {
        if (cnx == null) {
            System.err.println("❌ Connexion à la base de données non disponible.");
            return;
        }

        // Vérification des mots inappropriés avant l'ajout
        boolean titreInapproprie = ProfanityCheckerService.containsProfanity(publications.getTitle());
        boolean descriptionInappropriee = ProfanityCheckerService.containsProfanity(publications.getDescription());

        String statut = (titreInapproprie || descriptionInappropriee) ? "Inappropriate" : null;
        publications.setStatut(statut);

        // ✅ Ajout avec le statut
        String req = "INSERT INTO publications (title, description, image_url, id_user, publication_date, statut) VALUES (?, ?, ?, ?, CURRENT_DATE, ?)";

        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, publications.getTitle());
            ps.setString(2, publications.getDescription());
            ps.setString(3, publications.getImage_url());
            ps.setInt(4, session.id_utilisateur);
            ps.setString(5, statut);  // Ajout du statut

            ps.executeUpdate();
            System.out.println("✅ Publication ajoutée avec succès ! (Statut : " + statut + ")");
        } catch (SQLException ex) {
            System.err.println("❌ Erreur lors de l'ajout de la publication : " + ex.getMessage());
        }
    }


    /**
     * ✅ Modifie une publication existante.
     */
    public void modifier(Publications publications) {
        String sql = "UPDATE publications SET title = ?, description = ?, image_url = ?, publication_date = CURRENT_DATE WHERE publication_id = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, publications.getTitle());
            ps.setString(2, publications.getDescription());
            ps.setString(3, publications.getImage_url());
            ps.setInt(4, publications.getPublication_id());

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Publication modifiée avec succès !");
            } else {
                System.out.println("⚠ Aucune publication trouvée avec cet ID.");
            }
        } catch (SQLException ex) {
            System.err.println("❌ Erreur lors de la modification : " + ex.getMessage());
        }
    }

    /**
     * ✅ Supprime une publication par ID.
     */
    public void supprimer(int publication_id) {
        String req = "DELETE FROM publications WHERE publication_id = ?";

        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, publication_id);
            int rowsDeleted = ps.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("✅ Publication supprimée avec succès !");
            } else {
                System.out.println("⚠ Aucune publication trouvée avec cet ID.");
            }
        } catch (SQLException ex) {
            System.err.println("❌ Erreur lors de la suppression : " + ex.getMessage());
        }
    }

    /**
     * ✅ Récupère une publication par ID.
     */
    public Publications getOne(int publication_id) {
        if (publication_id <= 0) {
            System.out.println("⚠ L'ID de la publication est invalide.");
            return null;
        }

        String req = "SELECT * FROM publications WHERE publication_id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, publication_id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Publications(
                        rs.getInt("publication_id"),
                        rs.getInt("id_user"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("image_url"),
                        rs.getDate("publication_date").toLocalDate()
                );
            }
        } catch (SQLException ex) {
            System.err.println("❌ Erreur lors de la récupération de la publication : " + ex.getMessage());
        }
        return null;
    }

    /**
     * ✅ Récupère toutes les publications avec les noms des utilisateurs.
     */
    public List<Publications> getAll() {
        List<Publications> publications = new ArrayList<>();
        String req = "SELECT p.*, u.username FROM publications p JOIN user u ON p.id_user = u.id_user ORDER BY p.publication_date DESC";

        try (Statement stm = cnx.createStatement();
             ResultSet rs = stm.executeQuery(req)) {
            while (rs.next()) {
                Publications pub = new Publications(
                        rs.getInt("publication_id"),
                        rs.getInt("id_user"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("image_url"),
                        rs.getDate("publication_date").toLocalDate()
                );
                publications.add(pub);
            }
        } catch (SQLException ex) {
            System.err.println("❌ Erreur lors de la récupération des publications : " + ex.getMessage());
        }
        return publications;
    }

    /**
     * ✅ Vérifie si un utilisateur existe via son `username`.
     */
    public boolean utilisateurExiste(String username) {
        String req = "SELECT COUNT(*) FROM user WHERE username = ?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException ex) {
            System.err.println("❌ Erreur lors de la vérification du username : " + ex.getMessage());
            return false;
        }
    }

    /**
     * ✅ Récupère le `username` d'un utilisateur par `id_user`.
     */
    public String getUsernameById(int userId) {
        String req = "SELECT username FROM user WHERE id_user = ?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("username");
            }
        } catch (SQLException ex) {
            System.err.println("❌ Erreur lors de la récupération du username : " + ex.getMessage());
        }
        return "Utilisateur inconnu";
    }
    public void update(Publications pub) {
        String sql = "UPDATE publications SET statut = ? WHERE publication_id = ?";

        try (PreparedStatement pstmt = cnx.prepareStatement(sql)) {
            pstmt.setString(1, pub.getStatut());
            pstmt.setInt(2, pub.getPublication_id());

            int rowsUpdated = pstmt.executeUpdate();
            System.out.println("Mise à jour statut : " + rowsUpdated + " ligne(s) modifiée(s).");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
