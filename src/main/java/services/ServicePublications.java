package services;

import Models.Publications;
import Models.session;
import utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicePublications implements IService<Publications> {
    Connection cnx = DataSource.getInstance().getConnection();
    public ServicePublications() {
    }

    @Override
    public void ajouter(Publications publications) throws SQLException {
        cnx = DataSource.getInstance().getConnection(); // ✅ Récupérer la connexion active
        if (cnx == null) {
            System.err.println("❌ Impossible d'ajouter la publication : connexion à la base de données non disponible.");
            return;
        }
        int id_user = session.id_utilisateur;
        String req = "INSERT INTO publications (title, description, image_url, id_user) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, publications.getTitle());
            ps.setString(2, publications.getDescription());
            ps.setString(3, publications.getImage_url());
            ps.setInt(4, id_user);
            ps.executeUpdate();
            System.out.println("✅ Publication ajoutée avec succès !");
        } catch (SQLException ex) {
            System.err.println("❌ Erreur lors de l'ajout de la publication : " + ex.getMessage());
        }
    }

    @Override
    public void modifier(Publications publications) throws SQLException {
        try {
            String sql = "UPDATE publications SET title = ?, description = ?, image_url = ?, publication_date = CURRENT_DATE WHERE publication_id = ?";
            PreparedStatement preparedStatement = cnx.prepareStatement(sql);
            preparedStatement.setString(1, publications.getTitle());
            preparedStatement.setString(2, publications.getDescription());
            preparedStatement.setString(3, publications.getImage_url());
            preparedStatement.setInt(4, publications.getPublication_id());
            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Publication modifiée avec succès !");
            } else {
                System.out.println("Aucune publication trouvée avec cet ID.");
            }
        } catch (SQLException ex) {
            System.out.println("Erreur lors de la modification de la publication : " + ex.getMessage());
        }

    }

    @Override
    public void supprimer(int publication_id) throws SQLException {
        try {
            String req = "DELETE FROM publications WHERE publication_id = ?";
            PreparedStatement preparedStatement = cnx.prepareStatement(req);
            preparedStatement.setInt(1, publication_id);

            int rowsDeleted = preparedStatement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Publication supprimée avec succès !");
            } else {
                System.out.println("Aucune publication trouvée avec cet ID.");
            }
        } catch (SQLException ex) {
            System.out.println("Erreur lors de la suppression de la publication : " + ex.getMessage());
        }

    }

    public Publications getOne(Publications publications) throws SQLException {
        return null;
    }
    public Publications getOne(int publication_id) throws SQLException {
        Publications publication = null;

        if (publication_id <= 0) {
            System.out.println("Erreur : L'ID de la publication est invalide.");
            return null;
        }

        String req = "SELECT * FROM publications WHERE publication_id = ?";

        try (PreparedStatement preparedStatement = cnx.prepareStatement(req)) {
            preparedStatement.setInt(1, publication_id);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                publication = new Publications(
                        rs.getInt("publication_id"),
                        rs.getInt("id_user"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("image_url"),
                        rs.getDate("publication_date").toLocalDate()
                );
            }
        } catch (SQLException ex) {
            System.out.println(" Erreur lors de la récupération de la publication : " + ex.getMessage());
        }
        return publication;
    }

    @Override
    public List<Publications> getAll() throws SQLException {
        List<Publications> publications = new ArrayList<>();
        String req = "SELECT * FROM publications";
        try (Statement stm = cnx.createStatement();
             ResultSet rs = stm.executeQuery(req)) {
            while (rs.next()) {
                publications.add(new Publications(
                        rs.getInt("publication_id"),
                        rs.getInt("id_user"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("image_url"),
                        rs.getDate("publication_date").toLocalDate()
                ));
            }
        } catch (SQLException ex) {
            System.out.println(" Erreur lors de la récupération des publications : " + ex.getMessage());
        }
        return publications;
    }

    @Override
    public List<Publications> getAll1(Publications publications) throws SQLException {
        return List.of();
    }

    public boolean utilisateurExiste(String username) {
        cnx = DataSource.getInstance().getConnection(); // Vérifier la connexion
        if (cnx == null) {
            System.err.println("Connexion à la base de données non disponible.");
            return false;
        }

        String req = "SELECT COUNT(*) FROM user WHERE username = ?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            System.err.println(" Erreur lors de la vérification du username : " + ex.getMessage());
        }
        return false;
    }
    public String getUsernameById(int userId) throws SQLException {
        cnx = DataSource.getInstance().getConnection(); // Vérification de la connexion
        if (cnx == null) {
            System.err.println("Connexion à la base de données non disponible.");
            return "Utilisateur inconnu";
        }

        String req = "SELECT username FROM user WHERE id_user = ?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("username");
            }
        } catch (SQLException ex) {
            System.err.println("Erreur lors de la récupération du username : " + ex.getMessage());
        }
        return "Utilisateur inconnu";
    }

}

