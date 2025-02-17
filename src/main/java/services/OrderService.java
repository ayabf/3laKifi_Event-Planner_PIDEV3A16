package services;

import Models.Order;
import utils.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// ✅ Implémente l'interface IService<Order>
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
            stmt.setString(4, order.getEventDate().toString());
            stmt.setString(5, order.getExactAddress());
            stmt.setString(6, order.getPaymentMethod());

            stmt.executeUpdate();
            System.out.println("✅ Commande ajoutée avec succès !");
        }
    }
    @Override
    public void modifier(Order order) throws SQLException {
        String query = "UPDATE `order` SET total_price = ?, event_date = ?, exact_address = ?, payment_method = ?, status = ? WHERE order_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDouble(1, order.getTotalPrice());
            stmt.setString(2, order.getEventDate().toString());
            stmt.setString(3, order.getExactAddress());
            stmt.setString(4, order.getPaymentMethod());
            stmt.setString(5, order.getStatus());
            stmt.setInt(6, order.getOrderId());

            stmt.executeUpdate();
            System.out.println("✅ Commande mise à jour avec succès !");
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
                double totalPrice = rs.getDouble("total_price");
                System.out.println("💰 Prix récupéré depuis la base: " + totalPrice); // Debugging

                Order order = new Order(
                        rs.getInt("order_id"),
                        rs.getInt("cart_id"),
                        rs.getInt("user_id"),
                        rs.getString("status"),
                        rs.getString("payment_method"),
                        rs.getString("exact_address"),
                        rs.getTimestamp("event_date").toLocalDateTime(),
                        rs.getTimestamp("ordered_at").toLocalDateTime(),
                        totalPrice // Vérifie bien que tu assignes la valeur ici
                );

                orderList.add(order);
            }
        }
        return orderList;
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


    public boolean commandeExisteDeja(int cartId) throws SQLException {
        String query = "SELECT COUNT(*) FROM `order` WHERE cart_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cartId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }
}
