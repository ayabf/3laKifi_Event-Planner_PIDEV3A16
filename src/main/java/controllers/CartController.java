package controllers;

import Models.CartItem;
import Models.CodePromo;
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
import services.ServiceCodePromo;
import utils.DataSource;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;

public class CartController {

    @FXML private ListView<CartItem> cartListView;
    @FXML private Label subtotalLabel;
    @FXML private Button checkoutButton;
    @FXML
    private TextField codePromoField;
    @FXML
    private Label discountLabel;
    @FXML
    private Button applyPromoButton;

    private double subtotal = 0.0;
    private double discount = 0.0;
    private ServiceCodePromo serviceCodePromo = new ServiceCodePromo();
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
        int cartId = getCartIdForCurrentUser();
        double updatedTotal = getUpdatedCartTotal(cartId); // Nouvelle récupération en base

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/OrderForm.fxml"));
            Parent root = loader.load();

            OrderFormController controller = loader.getController();
            controller.setCartDetails(cartId, session.id_utilisateur, updatedTotal); // Utilisation du total mis à jour

            Stage stage = new Stage();
            stage.setTitle("Validation de la commande");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir le formulaire de commande.");
        }
    }

    // Fonction pour récupérer le total mis à jour en base de données
    private double getUpdatedCartTotal(int cartId) {
        String query = "SELECT total_price FROM cart WHERE cart_id = ?";
        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, cartId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total_price");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
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
    @FXML
    private void applyPromo() {
        String codePromo = codePromoField.getText().trim();  // ✅ Supprime les espaces invisibles
        System.out.println("🔍 Code promo saisi : [" + codePromo + "]");

        if (codePromo.isEmpty()) {
            discountLabel.setText("❌ Please enter a promo code.");
            return;
        }

        ServiceCodePromo serviceCodePromo = new ServiceCodePromo();
        CodePromo promo = serviceCodePromo.verifierCodePromo(codePromo);
        if (subtotal == 0.0) {
            subtotal = calculateTotalPrice(); // Recalcule le sous-total si jamais il est 0
            if (subtotal == 0.0) {
                System.out.println("❌ Erreur : Subtotal invalide pour calculer la réduction !");
                return;
            }
        }

        if (promo == null) {
            System.out.println("🚨 Code promo invalide ou expiré !");
            discountLabel.setText("❌ Invalid or expired code!");
            return;
        }

        // ✅ Recalcule le total du panier avant la réduction
        double currentSubtotal = calculateTotalPrice();
        discount = currentSubtotal * (promo.getPourcentage() / 100);
        double newTotal = currentSubtotal - discount;

        System.out.println("✅ Promo appliquée : -" + promo.getPourcentage() + "% ($" + String.format("%.2f", discount) + ")");

        // ✅ Mise à jour de l'affichage
        discountLabel.setText("✅ Discount: -" + promo.getPourcentage() + "% ($" + String.format("%.2f", discount) + ")");
        subtotalLabel.setText("$" + String.format("%.2f", newTotal));

        // ✅ Met à jour la base de données
        updateCartTotalInDB(newTotal);
        updateCartProductsTotalInDB(newTotal);

    }
    private void updateCartTotalInDB(double newTotal) {
        int cartId = getCartIdForCurrentUser(); // Récupère l'ID du panier

        if (cartId == -1) {
            System.out.println("❌ Erreur : Aucun panier trouvé pour cet utilisateur.");
            return;
        }

        String updateQuery = "UPDATE cart SET total_price = ? WHERE cart_id = ?";

        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

            stmt.setDouble(1, newTotal);
            stmt.setInt(2, cartId);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Total mis à jour en base de données : $" + String.format("%.2f", newTotal));
            } else {
                System.out.println("❌ Erreur lors de la mise à jour du total.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateCartProductsTotalInDB(double newTotal) {
        int cartId = getCartIdForCurrentUser(); // Récupérer l'ID du panier
        if (subtotal == 0.0) {
            System.out.println("❌ Erreur : Impossible de répartir la réduction car le sous-total est 0 !");
            return;
        }

        if (cartId == -1) {
            System.out.println("❌ Erreur : Aucun panier trouvé pour cet utilisateur.");
            return;
        }

        // Vérifier si subtotal est valide pour éviter la division par zéro
        if (subtotal <= 0.0) {
            System.out.println("🚨 Erreur : Subtotal invalide pour calculer la réduction !");
            return;
        }

        String selectQuery = "SELECT product_id, quantity, total_price FROM cart_product WHERE cart_id = ?";
        String updateQuery = "UPDATE cart_product SET total_price = ? WHERE cart_id = ? AND product_id = ?";

        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
             PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {

            selectStmt.setInt(1, cartId);
            ResultSet rs = selectStmt.executeQuery();

            while (rs.next()) {
                int productId = rs.getInt("product_id");
                int quantity = rs.getInt("quantity");
                double oldTotal = rs.getDouble("total_price");

                // Vérifier si oldTotal est valide
                if (oldTotal <= 0) {
                    System.out.println("⚠ Produit ignoré (total_price invalide) : " + productId);
                    continue;
                }
                if (subtotal <= 0) {
                    System.out.println("❌ Erreur : subtotal invalide, mise à jour des produits annulée.");
                    return;
                }

                // Proportionnalité : nouvelle somme en fonction de la réduction
                double newProductTotal = (oldTotal / subtotal) * newTotal;

                // Vérification finale avant mise à jour
                if (Double.isNaN(newProductTotal) || newProductTotal < 0) {
                    System.out.println("🚨 Erreur : Calcul invalide pour produit " + productId);
                    continue;
                }

                // Mettre à jour la base de données
                updateStmt.setDouble(1, newProductTotal);
                updateStmt.setInt(2, cartId);
                updateStmt.setInt(3, productId);
                updateStmt.executeUpdate();

                System.out.println("✅ Produit " + productId + " mis à jour : $" + String.format("%.2f", newProductTotal));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }





    // ✅ Mettre à jour le sous-total
    public void updateSubtotal(double total) {
        this.subtotal = total;
        subtotalLabel.setText("$" + String.format("%.2f", subtotal));
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
