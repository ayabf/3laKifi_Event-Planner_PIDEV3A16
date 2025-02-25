package services;

import Models.Cart;
import Models.User;
import Models.session;
import utils.DataSource;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class CartService implements IService<Cart> {
    private Connection connection;

    public CartService() {
        connection = DataSource.getInstance().getConnection();
    }

    @Override
    public void ajouter(Cart cart) throws SQLException {
        String query = "INSERT INTO cart (user_id, created_at, total_price) VALUES (?, NOW(), ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, cart.getUser().getId());
            stmt.setDouble(2, cart.getTotalPrice());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                cart.setCartId(rs.getInt(1));
            }
            System.out.println("✅ Panier ajouté !");
        }
    }

    @Override
    public void modifier(Cart cart) throws SQLException {
        String query = "UPDATE cart SET total_price = ? WHERE cart_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDouble(1, cart.getTotalPrice());
            stmt.setInt(2, cart.getCartId());
            stmt.executeUpdate();
            System.out.println("✅ Panier mis à jour !");
        }
    }

    @Override
    public void supprimer(int cartId) throws SQLException {
        String query = "DELETE FROM cart WHERE cart_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cartId);
            stmt.executeUpdate();
            System.out.println("✅ Panier supprimé !");
        }
    }


    @Override
    public Cart getOne(Cart cart) throws SQLException {
        String query = "SELECT c.*, u.username, u.email FROM cart c " +
                "JOIN user u ON c.user_id = u.id_user WHERE c.cart_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cart.getCartId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User(
                        rs.getInt("user_id"), "", "", rs.getString("username"), "", null,
                        "", "", rs.getString("email"), rs.getInt("numTel")
                );
                LocalDateTime createdAt = rs.getTimestamp("created_at").toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDateTime();
                double totalPrice = rs.getDouble("total_price");
                return new Cart(cart.getCartId(), user, createdAt, totalPrice);
            }
        }
        return null;
    }


    @Override
    public List<Cart> getAll() throws SQLException {
        List<Cart> carts = new ArrayList<>();
        String query = "SELECT c.*, u.username, u.email FROM cart c " +
                "JOIN user u ON c.user_id = u.id_user";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                User user = new User(
                        rs.getInt("user_id"), "", "", rs.getString("username"), "", null,
                        "", "", rs.getString("email"), rs.getInt("numTel")
                );
                LocalDateTime createdAt = rs.getTimestamp("created_at").toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDateTime();
                double totalPrice = rs.getDouble("total_price");

                carts.add(new Cart(rs.getInt("cart_id"), user, createdAt, totalPrice));
            }
        }
        return carts;
    }



    public List<Cart> getAll1(Cart cart) throws SQLException {
        return List.of();
    }

    public void updateCartItem(int cartId, int productId, int newQuantity, double productPrice) throws SQLException {
        // ✅ Vérifier que la connexion est active
        Connection conn = DataSource.getInstance().getConnection();
        if (conn == null || conn.isClosed()) {
            System.err.println("⚠ Connexion fermée ! Impossible de mettre à jour.");
            return;
        }

        // ✅ Mise à jour de la quantité et du total_price dans cart_product
        String query = "UPDATE cart_product SET quantity = ?, total_price = ? WHERE cart_id = ? AND product_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, newQuantity);
            stmt.setDouble(2, newQuantity * productPrice); // Mise à jour du prix total
            stmt.setInt(3, cartId);
            stmt.setInt(4, productId);

            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("✅ Cart mis à jour : Produit " + productId + " | Nouvelle quantité : " + newQuantity + " | Nouveau total : " + (newQuantity * productPrice));
            } else {
                System.err.println("⚠ Aucun produit mis à jour en base !");
            }
        }
    }
    public boolean addProductToCart( int productId, int quantity, double price) throws SQLException {
        // Vérifier si un panier existe déjà pour cet utilisateur

        int userId = session.id_utilisateur;
        int cartId = getOrCreateCart();

        if (cartId == -1) {
            return false; // Échec de récupération/création du panier
        }

        // Vérifier si le produit est déjà dans le panier
        String checkQuery = "SELECT quantity FROM cart_product WHERE cart_id = ? AND product_id = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
            checkStmt.setInt(1, cartId);
            checkStmt.setInt(2, productId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Mise à jour de la quantité si le produit existe déjà
                int newQuantity = rs.getInt("quantity") + quantity;
                String updateQuery = "UPDATE cart_product SET quantity = ? WHERE cart_id = ? AND product_id = ?";
                try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                    updateStmt.setInt(1, newQuantity);
                    updateStmt.setInt(2, cartId);
                    updateStmt.setInt(3, productId);
                    updateStmt.executeUpdate();
                }
            } else {
                // Ajouter un nouveau produit au panier
                String insertQuery = "INSERT INTO cart_product (cart_id, product_id, quantity, total_price) VALUES (?, ?, ?, ?)";
                try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                    insertStmt.setInt(1, cartId);
                    insertStmt.setInt(2, productId);
                    insertStmt.setInt(3, quantity);
                    insertStmt.setDouble(4, price * quantity);
                    insertStmt.executeUpdate();
                }
            }
        }
        return true;
    }

    public int getOrCreateCart() throws SQLException {
        // 📌 Récupérer l'ID de l'utilisateur depuis la session
        int userId = session.id_utilisateur;



        // 🔍 Vérifier si l'utilisateur existe en base
        String checkUserQuery = "SELECT id_user FROM user WHERE id_user = ?";
        try (PreparedStatement checkUserStmt = connection.prepareStatement(checkUserQuery)) {
            checkUserStmt.setInt(1, userId);
            ResultSet userResult = checkUserStmt.executeQuery();

        }

        // 🔍 Vérifier si un panier existe déjà pour cet utilisateur
        String selectCart = "SELECT cart_id FROM cart WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(selectCart)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("cart_id"); // Retourne l'ID du panier existant
            }
        }

        // 🆕 Si aucun panier n'existe, en créer un
        String insertCart = "INSERT INTO cart (user_id, created_at, total_price) VALUES (?, NOW(), 0.0)";
        try (PreparedStatement stmt = connection.prepareStatement(insertCart, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // Retourne l'ID du nouveau panier
            }
        }

        return -1; // Retourne -1 en cas d'échec
    }

}