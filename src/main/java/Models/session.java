package Models;

import utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class session {
    public static int  id_utilisateur;
    public static String nom_utilisateur;
    public static String role_utilisateur;
    public static void setSession(int id, String nom, String role) {
        id_utilisateur = id;
        nom_utilisateur = nom;
        role_utilisateur = role;
    }

    public static void clearSession() {
        id_utilisateur = -1;
        nom_utilisateur = null;
        role_utilisateur = null;
    }

    // ✅ Charge le rôle depuis la BD
    public static void loadUserRole(int userId) {
        String query = "SELECT role FROM user WHERE id_user = ?";

        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                role_utilisateur = rs.getString("role"); // ✅ Stocke le rôle
                System.out.println("✅ Rôle chargé depuis la BD : " + role_utilisateur);
            } else {
                System.out.println("⚠ Aucun rôle trouvé pour l'utilisateur ID: " + userId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
