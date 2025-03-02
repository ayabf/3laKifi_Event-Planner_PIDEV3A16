package services;

import Models.CodePromo;
import utils.DataSource;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServiceCodePromo {
    private Connection conn = DataSource.getInstance().getConnection();

    public void ajouterCodePromo(CodePromo codePromo) {
        String query = "INSERT INTO code_promo (code_promo, pourcentage, date_expiration) VALUES (?, ?, ?)"; // ✅ Correction ici

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, codePromo.getCode());
            ps.setDouble(2, codePromo.getPourcentage());
            ps.setDate(3, new java.sql.Date(codePromo.getDateExpiration().getTime()));
            ps.executeUpdate();
            System.out.println("🎉 Code promo ajouté avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
}
