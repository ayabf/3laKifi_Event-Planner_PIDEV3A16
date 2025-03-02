package controllers;

import Models.CartItem;
import Models.Product;
import Models.session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import services.CartService;
import utils.DataSource;

import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class CartController {

    @FXML private ListView<CartItem> cartListView;
    @FXML private Label subtotalLabel;
    @FXML private Button checkoutButton;

    private ObservableList<CartItem> cartItems = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        subtotalLabel.setText("$0.00");
        loadCartItems();
        cartListView.setItems(cartItems);
        cartListView.setCellFactory(param -> new CartItemCell()); // ✅ Correction : Classe bien définie
        updateSubtotal();
    }

    private int getCartIdForCurrentUser() {
        int userId = session.id_utilisateur;
        int cartId = -1;

        String query = "SELECT cart_id FROM cart WHERE user_id = ?";
        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                cartId = rs.getInt("cart_id");
            } else {
                // If the cart doesn't exist, create a new one
                cartId = createNewCart(userId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cartId;
    }

    private double calculateTotalPrice() {
        return cartItems.stream().mapToDouble(item -> item.getQuantity() * item.getProduct().getPrice()).sum();
    }


















    private void openOrderForm(int orderId, double totalPrice) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/OrderForm.fxml"));
            Parent root = loader.load();

            // Passer l'ID de la commande et le totalPrice au contrôleur de la page de commande
            OrderController orderController = loader.getController();
            orderController.setOrderDetails(orderId, totalPrice);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Détails de la Commande");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir le formulaire de commande.");
        }
    }
    @FXML
    private void validateCart() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/OrderForm.fxml"));
            Parent root = loader.load();

            OrderFormController controller = loader.getController();
            controller.setCartDetails(getCartIdForCurrentUser(), session.id_utilisateur, calculateTotalPrice());

            Stage stage = new Stage();
            stage.setTitle("Validation de la commande");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir le formulaire de commande.");
        }
    }

    /**
     * ✅ Creates a new cart if the user doesn't have one
     */
    private int createNewCart(int userId) {
        String insertQuery = "INSERT INTO cart (user_id, total_price) VALUES (?, 0.0)";
        String getIdQuery = "SELECT LAST_INSERT_ID() AS cart_id";

        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
             Statement getIdStmt = conn.createStatement()) {

            insertStmt.setInt(1, userId);
            insertStmt.executeUpdate();

            ResultSet rs = getIdStmt.executeQuery(getIdQuery);
            if (rs.next()) {
                return rs.getInt("cart_id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void loadCartItems() {
        cartItems.clear();
        int cartId = getCartIdForCurrentUser();

        // Log pour vérifier si le cartId est valide
        System.out.println("🔍 User ID de la session: " + session.id_utilisateur);
        System.out.println("📦 Cart ID récupéré: " + cartId);

        if (cartId <= 0) {
            System.err.println("❌ Aucun panier trouvé pour cet utilisateur !");
            return;
        }

        String query = "SELECT p.product_id, p.name, p.price, p.image_url, cp.quantity " +
                "FROM cart_product cp " +
                "JOIN product p ON cp.product_id = p.product_id " +
                "WHERE cp.cart_id = ?";

        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, cartId);
            ResultSet rs = stmt.executeQuery();

            boolean hasItems = false;
            while (rs.next()) {
                hasItems = true;
                Product product = new Product(rs.getInt("product_id"), rs.getString("name"), "",
                        rs.getDouble("price"), 1, rs.getString("image_url"));
                cartItems.add(new CartItem(product, rs.getInt("quantity")));

                // Log des produits ajoutés
                System.out.println("🛒 Produit ajouté au panier : " + product.getName() +
                        " | Quantité: " + rs.getInt("quantity") +
                        " | Prix: " + product.getPrice());
            }

            if (!hasItems) {
                System.out.println("⚠ Aucun produit trouvé dans le panier !");
            }

            cartListView.setItems(cartItems);
            updateSubtotal();

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Erreur lors du chargement du panier !");
        }
    }


    private void updateSubtotal() {
        double total = cartItems.stream()
                .mapToDouble(item -> item.getQuantity() * item.getProduct().getPrice())
                .sum();
        subtotalLabel.setText("$" + String.format("%.2f", total));
    }

    private void updateCartItemInDB(CartItem item) {
        try {
            int cartId = getCartIdForCurrentUser();
            new CartService().updateCartItem(cartId, item.getProduct().getProductId(), item.getQuantity(), item.getProduct().getPrice());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteCartItemFromDB(CartItem itemToRemove) {
        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM cart_product WHERE cart_id = ? AND product_id = ?")) {

            int cartId = getCartIdForCurrentUser();
            stmt.setInt(1, cartId);
            stmt.setInt(2, itemToRemove.getProduct().getProductId());

            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                cartItems.remove(itemToRemove);
                updateSubtotal();
                cartListView.refresh();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getAvailableStock(int productId) {
        String query = "SELECT available_quantity FROM stock WHERE stock_id = (SELECT stock_id FROM product WHERE product_id = ?)";
        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("available_quantity");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void showDeleteConfirmation(CartItem itemToRemove) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText(null);
        alert.setContentText("Voulez-vous retirer *" + itemToRemove.getProduct().getName() + "* du panier ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deleteCartItemFromDB(itemToRemove);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void goBackToWelcome(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/welcomeC.fxml"));
            Parent root = loader.load();

            // Récupérer la scène actuelle
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root)); // Remplacer la scène actuelle
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("❌ Erreur lors du retour à la page d'accueil !");
        }
    }



    /**
     * ✅ Classe interne pour gérer l'affichage des articles dans le ListView
     */
    private class CartItemCell extends ListCell<CartItem> {
        @Override
        protected void updateItem(CartItem item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                HBox cellContainer = new HBox(15);
                cellContainer.setAlignment(Pos.CENTER_LEFT); // ✅ Alignement des éléments

                // 🖼️ Image du produit
                ImageView productImage = new ImageView();
                productImage.setFitWidth(60);
                productImage.setFitHeight(60);
                productImage.setPreserveRatio(true);

                try {
                    if (item.getProduct().getImageUrl() != null && !item.getProduct().getImageUrl().isEmpty()) {
                        productImage.setImage(new Image(item.getProduct().getImageUrl()));
                    } else {
                        productImage.setImage(new Image("images/default.png"));
                    }
                } catch (Exception e) {
                    productImage.setImage(new Image("images/default.png"));
                }

                // 🏷️ Infos du produit
                Label productName = new Label(item.getProduct().getName());
                productName.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

                Label productPrice = new Label("$" + item.getProduct().getPrice());
                productPrice.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

                Label quantityLabel = new Label(String.valueOf(item.getQuantity()));

                // ➖ Bouton pour diminuer la quantité
                Button minusButton = new Button("-");
                minusButton.setOnAction(event -> {
                    if (item.getQuantity() > 1) {
                        item.setQuantity(item.getQuantity() - 1);
                        updateCartItemInDB(item);
                        updateSubtotal();
                        cartListView.refresh();
                    }
                });

                // ➕ Bouton pour augmenter la quantité
                Button plusButton = new Button("+");
                plusButton.setOnAction(event -> {
                    int availableStock = getAvailableStock(item.getProduct().getProductId());
                    if (item.getQuantity() < availableStock) {
                        item.setQuantity(item.getQuantity() + 1);
                        updateCartItemInDB(item);
                        updateSubtotal();
                        cartListView.refresh();
                    }
                });

                // 🗑️ Icône de suppression bien ajustée
                ImageView deleteIcon = new ImageView(new Image("images/delete-icon.png"));
                deleteIcon.setFitWidth(24);  // ✅ Ajustement de la taille
                deleteIcon.setFitHeight(24);
                deleteIcon.setPreserveRatio(true);

                deleteIcon.setOnMouseClicked(event -> showDeleteConfirmation(item));

                // ✅ Ajout d'un effet au survol
                deleteIcon.setOnMouseEntered(event -> deleteIcon.setStyle("-fx-opacity: 0.7;"));
                deleteIcon.setOnMouseExited(event -> deleteIcon.setStyle("-fx-opacity: 1.0;"));

                // 📌 Organisation des éléments
                HBox quantityBox = new HBox(5, minusButton, quantityLabel, plusButton);
                quantityBox.setAlignment(Pos.CENTER);

                // 🌟 On pousse l'icône de suppression vers la droite
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                cellContainer.getChildren().addAll(productImage, productName, quantityBox, productPrice, spacer, deleteIcon);
                setGraphic(cellContainer);
            }
        }

    }
}
