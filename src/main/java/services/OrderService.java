package services;

import Models.Order;
import Models.Role;
import Models.User;
import utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderService implements IService<Order> {

    private final Connection connection;

    public OrderService() {
        this.connection = DataSource.getInstance().getConnection();
    }

    @Override
    public void ajouter(Order order) throws SQLException {
        if (commandeExisteDeja(order.getCartId())) {
            System.out.println("⚠ Une commande avec ce cart_id existe déjà ! Opération annulée.");
            return;
        }

        String query = "INSERT INTO `order` (cart_id, user_id, total_price, event_date, exact_address, payment_method, status, ordered_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, 'PENDING', NOW())";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, order.getCartId());
            stmt.setInt(2, order.getUserId());
            stmt.setDouble(3, order.getTotalPrice());
            stmt.setTimestamp(4, Timestamp.valueOf(order.getEventDate())); // ✅ Correct
            stmt.setString(5, order.getExactAddress());
            stmt.setString(6, order.getPaymentMethod());

            stmt.executeUpdate();
            System.out.println("✅ Commande ajoutée avec succès !");
        }
    }


    public void modifier(Order order) throws SQLException {
        String query = "UPDATE `order` SET event_date = ?, exact_address = ? WHERE order_id = ?";
        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setTimestamp(1, Timestamp.valueOf(order.getEventDate()));
            stmt.setString(2, order.getExactAddress());
            stmt.setInt(3, order.getOrderId());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Commande mise à jour dans la base !");
                System.out.println("📅 Nouvelle Date: " + order.getEventDate());
                System.out.println("📍 Nouvelle Adresse: " + order.getExactAddress());
            } else {
                System.out.println("❌ Aucune commande mise à jour !");
            }
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String query = "DELETE FROM `order` WHERE order_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("✅ Commande supprimée !");
        }
    }

    @Override
    public Order getOne(Order order) throws SQLException {
        String query = "SELECT * FROM `order` WHERE order_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, order.getOrderId());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Order(
                        rs.getInt("order_id"),
                        rs.getInt("cart_id"),
                        rs.getInt("user_id"),
                        rs.getString("status"),
                        rs.getString("payment_method"),
                        rs.getString("exact_address"),
                        rs.getTimestamp("event_date").toLocalDateTime(),
                        rs.getTimestamp("ordered_at").toLocalDateTime(),
                        rs.getDouble("total_price")
                );
            }
        }
        return null;
    }


    @Override
    public List<Order> getAll() throws SQLException {
        List<Order> orderList = new ArrayList<>();
        String query = "SELECT * FROM `order` ORDER BY ordered_at DESC";

        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Order order = new Order(
                        rs.getInt("order_id"),
                        rs.getInt("cart_id"),
                        rs.getInt("user_id"),
                        rs.getString("status"),
                        rs.getString("payment_method"),
                        rs.getString("exact_address"),
                        rs.getTimestamp("event_date") != null ? rs.getTimestamp("event_date").toLocalDateTime() : null, // ✅ Vérification
                        rs.getTimestamp("ordered_at") != null ? rs.getTimestamp("ordered_at").toLocalDateTime() : null,
                        rs.getDouble("total_price")
                );

                orderList.add(order);
            }
        }
        return orderList;
    }

    @Override
    public List<Order> getAll1(Order order) throws SQLException {
        return List.of();
    }


    public List<Order> getAllByUser(int userId) throws SQLException {
        List<Order> orderList = new ArrayList<>();
        String query = "SELECT * FROM `order` WHERE user_id = ? ORDER BY ordered_at DESC"; // ✅ Filtre par user_id

        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);  // ✅ Associe uniquement aux commandes de l'utilisateur
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Order order = new Order(
                        rs.getInt("order_id"),
                        rs.getInt("cart_id"),
                        rs.getInt("user_id"),
                        rs.getString("status"),
                        rs.getString("payment_method"),
                        rs.getString("exact_address"),
                        rs.getTimestamp("event_date").toLocalDateTime(),
                        rs.getTimestamp("ordered_at").toLocalDateTime(),
                        rs.getDouble("total_price")
                );
                orderList.add(order);
            }
        }
        return orderList;
    }
    public void annulerCommande(int orderId) throws SQLException {
        String query = "UPDATE `order` SET status = ? WHERE order_id = ?";
        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "CANCELLED"); // ✅ Bien mettre une chaîne de caractères
            stmt.setInt(2, orderId);
            stmt.executeUpdate();
            System.out.println("✅ Commande annulée avec succès !");
        }
    }

    public void updateStatus(int orderId, String newStatus) throws SQLException {
        String query = "UPDATE `order` SET status = ? WHERE order_id = ?";
        Connection conn = DataSource.getInstance().getConnection(); // Récupère une nouvelle connexion

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, orderId);
            stmt.executeUpdate();
            System.out.println("✅ Statut mis à jour pour la commande " + orderId + " -> " + newStatus);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Erreur lors de la mise à jour du statut.");
        }
    }



    public void modifierStatutCommande(int orderId, String newStatus, User user) throws SQLException {
        if ((user.getRole() == Role.ADMIN)) { // ✅ Vérification du rôle
            System.out.println("⛔ Accès refusé : Seul un administrateur peut modifier le statut !");
            return;
        }

        String query = "UPDATE `order` SET status = ? WHERE order_id = ?";
        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, orderId);
            stmt.executeUpdate();
            System.out.println("✅ Statut de la commande mis à jour avec succès !");
        }
    }

    public boolean commandeExisteDeja(int cartId) throws SQLException {
        String query = "SELECT COUNT(*) FROM `order` WHERE cart_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cartId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }
}
