package services;

import Models.Order;
import utils.DataSource;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderService {

    private Connection connection;

    public OrderService() {
        this.connection = DataSource.getInstance().getConnection();
    }


    public List<Order> getAllByUser(int userId) throws SQLException {
        List<Order> orderList = new ArrayList<>();
        String query = "SELECT * FROM `order` WHERE user_id = ? ORDER BY ordered_at DESC";

        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Order order = new Order(
                        rs.getInt("order_id"),
                        rs.getInt("cart_id"),
                        rs.getInt("user_id"),
                        rs.getString("status"),
                        rs.getString("payment_method"),
                        rs.getString("exact_address"),
                        rs.getTimestamp("event_date") != null ? rs.getTimestamp("event_date").toLocalDateTime() : null,
                        rs.getTimestamp("ordered_at") != null ? rs.getTimestamp("ordered_at").toLocalDateTime() : null,
                        rs.getDouble("total_price")
                );
                System.out.println("🔍 Commande récupérée : " + order);
                orderList.add(order);
            }
        }
        return orderList;
    }


    public void ajouter(Order order) {
        if (!userExists(order.getUserId())) {
            System.err.println("❌ ERREUR: L'utilisateur avec l'ID " + order.getUserId() + " n'existe pas dans la base !");
            return;
        }

        if (!cartExists(order.getCartId())) {
            System.err.println("❌ ERREUR: Le panier avec l'ID " + order.getCartId() + " n'existe pas !");
            return;
        }

        if (commandeExisteDeja(order.getCartId())) {
            System.err.println("⚠ ERREUR: Une commande existe déjà pour ce panier !");
            return;
        }

        System.out.println("🔹 Tentative d'insertion de la commande...");

        String query = "INSERT INTO `order` (cart_id, user_id, status, total_price, payment_method, exact_address, event_date, ordered_at) VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";

        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) { // 🔥 Récupérer l'ID généré

            stmt.setInt(1, order.getCartId());
            stmt.setInt(2, order.getUserId());
            stmt.setString(3, order.getStatus());
            stmt.setDouble(4, order.getTotalPrice());
            stmt.setString(5, order.getPaymentMethod());
            stmt.setString(6, order.getExactAddress());
            stmt.setTimestamp(7, Timestamp.valueOf(order.getEventDate()));

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("✅ Commande ajoutée avec succès !");

                // 🔥 Récupérer l'order_id généré
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int orderId = generatedKeys.getInt(1);
                    order.setOrderId(orderId);  // 🔥 Mise à jour de l'objet Order
                    System.out.println("🆕 Order ID généré: " + orderId);
                } else {
                    System.err.println("⚠ Problème : Order ID non récupéré !");
                }
            } else {
                System.err.println("⚠ Problème lors de l'insertion de la commande !");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Erreur lors de l'ajout de la commande !");
        }
    }







    public List<Order> searchOrdersByUser(int userId, String keyword) throws SQLException {
        List<Order> allOrders = getAllByUser(userId);

        return allOrders.stream()
                .filter(order -> order.getStatus().toLowerCase().contains(keyword.toLowerCase()) ||
                        order.getExactAddress().toLowerCase().contains(keyword.toLowerCase()) ||
                        (order.getEventDate() != null && order.getEventDate().toString().contains(keyword)) ||
                        String.valueOf(order.getTotalPrice()).contains(keyword))
                .collect(Collectors.toList());
    }




    private boolean cartExists(int cartId) {
        String query = "SELECT COUNT(*) FROM cart WHERE cart_id = ?";
        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, cartId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean commandeExisteDeja(int cartId) {
        String query = "SELECT COUNT(*) FROM `order` WHERE cart_id = ?";
        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, cartId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }



    public boolean panierContientProduits(int cartId) {
        String query = "SELECT COUNT(*) FROM cart_product WHERE cart_id = ?";
        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, cartId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int creerNouveauPanier(int userId) {
        String checkUserQuery = "SELECT COUNT(*) FROM user WHERE id_user = ?";
        String insertCartQuery = "INSERT INTO cart (user_id) VALUES (?)";
        int newCartId = -1;

        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement checkUserStmt = conn.prepareStatement(checkUserQuery)) {

            checkUserStmt.setInt(1, userId);
            ResultSet rs = checkUserStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {  // Vérifie que l'utilisateur existe
                try (PreparedStatement insertCartStmt = conn.prepareStatement(insertCartQuery, Statement.RETURN_GENERATED_KEYS)) {
                    insertCartStmt.setInt(1, userId);
                    insertCartStmt.executeUpdate();

                    ResultSet generatedKeys = insertCartStmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        newCartId = generatedKeys.getInt(1);
                    }

                    System.out.println("✅ Nouveau panier créé avec l'ID : " + newCartId);
                }
            } else {
                System.err.println("❌ ERREUR: L'utilisateur avec l'ID " + userId + " n'existe pas. Impossible de créer un panier !");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Erreur lors de la création du panier !");
        }

        return newCartId;
    }

    public void modifier(Order order) throws SQLException {
        String query = "UPDATE `order` SET event_date = ?, exact_address = ? WHERE order_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setTimestamp(1, Timestamp.valueOf(order.getEventDate()));
            stmt.setString(2, order.getExactAddress());
            stmt.setInt(3, order.getOrderId());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Commande mise à jour avec succès !");
            } else {
                System.out.println("❌ Aucune mise à jour effectuée !");
            }
        }
    }

    public void supprimer(int id) throws SQLException {
        String query = "DELETE FROM `order` WHERE order_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("✅ Commande supprimée !");
        }
    }

    public Order getOne(int orderId) throws SQLException {
        String query = "SELECT * FROM `order` WHERE order_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, orderId);
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

    public List<Order> getAll() throws SQLException {
        List<Order> orderList = new ArrayList<>();
        String query = "SELECT * FROM `order` ORDER BY ordered_at DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Order order = new Order(
                        rs.getInt("order_id"),
                        rs.getInt("cart_id"),
                        rs.getInt("user_id"),
                        rs.getString("status"),
                        rs.getString("payment_method"),
                        rs.getString("exact_address"),
                        rs.getTimestamp("event_date") != null ? rs.getTimestamp("event_date").toLocalDateTime() : null,
                        rs.getTimestamp("ordered_at") != null ? rs.getTimestamp("ordered_at").toLocalDateTime() : null,
                        rs.getDouble("total_price")
                );

                orderList.add(order);
            }
        }
        return orderList;
    }

    public void annulerCommande(int orderId) throws SQLException {
        String query = "UPDATE `order` SET status = ? WHERE order_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, "CANCELLED");
            stmt.setInt(2, orderId);
            stmt.executeUpdate();
            System.out.println("✅ Commande annulée !");
        }
    }

    public void updateStatus(int orderId, String newStatus, int userId) {
        if (!isAdmin(userId)) {
            System.err.println("❌ ERREUR: L'utilisateur avec ID " + userId + " n'a pas les droits pour modifier le statut !");
            return;
        }

        String query = "UPDATE `order` SET status = ? WHERE order_id = ?";

        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, orderId);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Statut mis à jour avec succès pour la commande " + orderId + " !");
            } else {
                System.err.println("⚠ Aucun changement détecté !");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Erreur lors de la mise à jour du statut !");
        }
    }
    private boolean isAdmin(int userId) {
        String query = "SELECT role FROM user WHERE id_user = ?";

        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                return "ADMIN".equalsIgnoreCase(role);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean userExists(int userId) {
        String query = "SELECT COUNT(*) FROM user WHERE id_user = ?";
        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Erreur lors de la vérification de l'utilisateur !");
        }
        return false;
    }

    public void updateOrderStatus(int orderId, String newStatus) throws SQLException {
        Connection connection = DataSource.getInstance().getConnection();
        String query = "UPDATE `order` SET status = ? WHERE order_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, newStatus);
        preparedStatement.setInt(2, orderId);
        preparedStatement.executeUpdate();
    }

    private void checkConnection() throws SQLException {
        if (this.connection == null || this.connection.isClosed()) {
            System.out.println("🔄 Réouverture de la connexion...");
            this.connection = DataSource.getInstance().getConnection();
        }
    }
    public void closeConnection() {
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                this.connection.close();
                System.out.println("🔌 Connexion fermée proprement.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
