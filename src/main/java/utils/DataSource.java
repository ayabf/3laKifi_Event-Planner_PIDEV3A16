package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSource {
    private Connection cnx;
    private static DataSource instance;

    private String url = "jdbc:mysql://localhost:3306/hackpack"; // Assure-toi que c'est correct
    private String user = "root"; // Remplace par ton utilisateur MySQL
    private String password = ""; // Mets ton mot de passe ici

    private DataSource() {
        try {
            cnx = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Connecté à la base de données !");
        } catch (SQLException ex) {
            System.err.println("❌ Erreur de connexion à la base de données : " + ex.getMessage());
        }
    }

    public static DataSource getInstance() {
        if (instance == null) {
            instance = new DataSource();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (cnx == null || cnx.isClosed()) {
                System.out.println("⚠ Connexion MySQL fermée ! Tentative de reconnexion...");
                cnx = DriverManager.getConnection(url, user, password);
                System.out.println("✅ Connexion rétablie !");
            }
        } catch (SQLException e) {
            System.err.println("❌ Impossible de reconnecter à la base de données : " + e.getMessage());
        }
        return cnx;
    }
}
