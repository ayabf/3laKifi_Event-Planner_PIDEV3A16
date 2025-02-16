package services;

import Models.Order;
import utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderService {

    private Connection connection;

    public OrderService() {
        this.connection = DataSource.getInstance().getConnection();
    }
    public void ajouterCommande(int cartId, int userId, double totalPrice, String eventDate, String address, String paymentMethod) throws SQLException {
        // Vérifier si une commande existe déjà pour ce cart_id
        if (commandeExisteDeja(cartId)) {
            System.out.println("⚠ Une commande avec ce cart_id existe déjà ! Opération annulée.");
            return; // Bloquer l'insertion si une commande existe déjà
        }

        System.out.println("Prix total avant insertion: " + totalPrice); // Debug

        String query = "INSERT INTO `order` (cart_id, user_id, total_price, event_date, exact_address, payment_method, status, ordered_at) VALUES (?, ?, ?, ?, ?, ?, 'PENDING', NOW())";

        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, cartId);
            stmt.setInt(2, userId);
            stmt.setDouble(3, totalPrice);
            stmt.setString(4, eventDate);
            stmt.setString(5, address);
            stmt.setString(6, paymentMethod);

            stmt.executeUpdate();
            System.out.println("✅ Commande ajoutée avec succès !");
        }
    }

    public boolean commandeExisteDeja(int cartId) throws SQLException {
        String query = "SELECT COUNT(*) FROM `order` WHERE cart_id = ?";
        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, cartId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Si le résultat > 0, alors une commande existe déjà
            }
        }
        return false;
    }

}
