package services;

import Models.CodePromo;
import utils.DataSource;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServiceCodePromo {
    private Connection conn = DataSource.getInstance().getConnection();

    /** ✅ Ajoute un code promo avec une connexion sécurisée */
    public CodePromo ajouterCodePromo(CodePromo codePromo) {
        String query = "INSERT INTO code_promo (code_promo, pourcentage, date_expiration) VALUES (?, ?, ?)";

        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, codePromo.getCode());
            ps.setDouble(2, codePromo.getPourcentage());
            ps.setDate(3, new java.sql.Date(codePromo.getDateExpiration().getTime()));

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int newId = rs.getInt(1);
                    codePromo.setId(newId);
                    return codePromo; // ✅ Retourne l'objet mis à jour
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public CodePromo verifierCodePromo(String code) {
        String query = "SELECT * FROM code_promo WHERE code_promo = ? AND date_expiration >= ?";

        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, code);
            ps.setDate(2, Date.valueOf(LocalDate.now()));

            System.out.println("🔍 Requête SQL exécutée : " + query);
            System.out.println("📌 Valeur 1 (code_promo) : " + code);
            System.out.println("📌 Valeur 2 (date_expiration) : " + LocalDate.now());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("✅ Code promo trouvé !");
                return new CodePromo(
                        rs.getInt("id"),
                        rs.getString("code_promo"),  // ✅ Vérifie que c'est bien "code_promo"
                        rs.getDouble("pourcentage"),
                        rs.getDate("date_creation"),
                        rs.getDate("date_expiration")
                );
            } else {
                System.out.println("🚨 Aucun code promo trouvé !");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    /** ✅ Vérifie si un code promo existe */
    public boolean existeDeja(String code) {
        String query = "SELECT COUNT(*) FROM code_promo WHERE code_promo = ?";
        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // ✅ Retourne true si le code existe déjà
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** ✅ Récupère tous les codes promo */
    public List<CodePromo> getAllPromo() {
        List<CodePromo> promos = new ArrayList<>();
        String query = "SELECT * FROM code_promo";

        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                promos.add(new CodePromo(
                        rs.getInt("id"),
                        rs.getString("code_promo"),
                        rs.getDouble("pourcentage"),
                        rs.getDate("date_expiration")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return promos;
    }
    /** ✅ Supprime un code promo */
    public void deletePromo(int id) {
        String query = "DELETE FROM code_promo WHERE id = ?";

        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("✅ Code promo supprimé avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
