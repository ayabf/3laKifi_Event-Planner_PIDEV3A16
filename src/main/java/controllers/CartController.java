package controllers;

import Models.CartItem;
import Models.Order;
import Models.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import services.CartService;
import services.OrderService;
import utils.DataSource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        cartListView.setCellFactory(param -> new CartItemCell()); // Appliquer la cellule personnalisée
        updateSubtotal();
        cartListView.setOnMouseClicked(event -> {
            CartItem selectedItem = cartListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                openCartItemDetails(selectedItem);
            }
        });
    }
    private void openCartItemDetails(CartItem cartItem) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CartItemDetails.fxml"));
            Parent root = loader.load();

            CartItemDetailsController controller = loader.getController();
            controller.setCartItemDetails(cartItem); // Passer les infos du produit au contrôleur

            Stage stage = new Stage();
            stage.setTitle("Détails du panier");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'afficher les détails du panier.");
        }
    }

    private void loadCartItems() {
        cartItems.clear();

        String query = "SELECT p.product_id, p.name, p.price, p.image_url, cp.quantity " +
                "FROM cart_product cp " +
                "JOIN product p ON cp.product_id = p.product_id " +
                "WHERE cp.cart_id = ?";

        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, 1); // Remplace par le bon `cart_id`
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int productId = rs.getInt("product_id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                int quantity = rs.getInt("quantity");
                String imageUrl = rs.getString("image_url");

                Product product = new Product(productId, name, "", price, 1, imageUrl);
                CartItem item = new CartItem(product, quantity);
                cartItems.add(item);
            }

            cartListView.setItems(cartItems);
            updateSubtotal();

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Erreur lors du chargement des produits du panier !");
        }
    }

    private void updateSubtotal() {
        double total = cartItems.stream()
                .mapToDouble(item -> item.getQuantity() * item.getProduct().getPrice())
                .sum();

        subtotalLabel.setText("$" + String.format("%.2f", total));
    }

    @FXML
    private void validateCart() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/OrderForm.fxml"));
            Parent root = loader.load();

            OrderFormController controller = loader.getController();
            controller.setCartDetails(1, 1, calculateTotalPrice()); // Passer les détails

            Stage stage = new Stage();
            stage.setTitle("Validation de la commande");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir le formulaire de commande.");
        }
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

    private class CartItemCell extends ListCell<CartItem> {
        @Override
        protected void updateItem(CartItem item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                HBox cellContainer = new HBox(15);
                cellContainer.getStyleClass().add("cart-item");

                // ✅ Chargement de l'image du produit
                ImageView productImage = new ImageView();
                productImage.setFitWidth(60);
                productImage.setFitHeight(60);
                productImage.setPreserveRatio(true);
                productImage.setStyle("-fx-border-radius: 10px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);");


                try {
                    if (item.getProduct().getImageUrl() != null && !item.getProduct().getImageUrl().isEmpty()) {
                        productImage.setImage(new Image(item.getProduct().getImageUrl()));
                    } else {
                        productImage.setImage(new Image("images/default.png")); // Image par défaut
                    }
                } catch (Exception e) {
                    productImage.setImage(new Image("images/default.png"));
                }

                Label productName = new Label(item.getProduct().getName());
                productName.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

                Label productPrice = new Label("$" + item.getProduct().getPrice());
                productPrice.setStyle("-fx-font-size: 16px;");

                Label quantityLabel = new Label(String.valueOf(item.getQuantity()));
                quantityLabel.setStyle("-fx-font-size: 14px;");

                Button minusButton = new Button("-");
                minusButton.setOnAction(event -> {
                    if (item.getQuantity() > 1) {
                        item.setQuantity(item.getQuantity() - 1);
                        updateCartItemInDB(item);
                        updateSubtotal();
                        cartListView.refresh();
                    }
                });

                Button plusButton = new Button("+");
                plusButton.setOnAction(event -> {
                    int availableStock = getAvailableStock(item.getProduct().getProductId());

                    if (item.getQuantity() < availableStock) {
                        item.setQuantity(item.getQuantity() + 1);
                        try {
                            new CartService().updateCartItem(1, item.getProduct().getProductId(), item.getQuantity(), item.getProduct().getPrice());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        updateSubtotal();
                        cartListView.refresh();
                    } else {
                        // Afficher une alerte si la quantité dépasse le stock disponible
                        showAlert("Stock insuffisant", "Vous ne pouvez pas ajouter plus d'unités que la quantité disponible en stock.");
                    }
                });


                // ✅ Suppression de l'article avec une icône
                ImageView deleteIcon = new ImageView(new Image("images/delete-icon.png"));
                deleteIcon.setFitWidth(24);
                deleteIcon.setFitHeight(24);
                deleteIcon.setPreserveRatio(true);
                deleteIcon.setStyle("-fx-cursor: hand;");
                deleteIcon.setOnMouseClicked(event -> showDeleteConfirmation(item));

                HBox quantityBox = new HBox(5, minusButton, quantityLabel, plusButton);
                cellContainer.getChildren().addAll(productImage, productName, quantityBox, productPrice,deleteIcon);
                setGraphic(cellContainer);
            }
        }
    }

    private void updateCartItemInDB(CartItem item) {
        try {
            new CartService().updateCartItem(1, item.getProduct().getProductId(), item.getQuantity(), item.getProduct().getPrice());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showDeleteConfirmation(CartItem itemToRemove) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText(null);
        alert.setContentText("Voulez-vous vraiment retirer **" + itemToRemove.getProduct().getName() + "** du panier ?");

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #f4f4f4;");
        dialogPane.lookup(".content.label").setStyle("-fx-text-fill: #333;");
        dialogPane.lookup(".button-bar").setStyle("-fx-background-color: #ffffff;");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            deleteCartItemFromDB(itemToRemove);
        }
    }

    private void deleteCartItemFromDB(CartItem itemToRemove) {
        try {
            Connection conn = DataSource.getInstance().getConnection();

            if (conn == null || conn.isClosed()) {
                System.err.println("⚠ Connexion fermée ! Suppression annulée.");
                return;
            }

            String query = "DELETE FROM cart_product WHERE cart_id = ? AND product_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, 1); // Remplace par le bon `cart_id`
                stmt.setInt(2, itemToRemove.getProduct().getProductId());

                int rowsDeleted = stmt.executeUpdate();

                if (rowsDeleted > 0) {
                    cartItems.remove(itemToRemove);
                    updateSubtotal();
                    cartListView.refresh();
                    System.out.println("✅ Produit supprimé de la base : " + itemToRemove.getProduct().getName());
                } else {
                    System.err.println("⚠ Aucun produit supprimé !");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Erreur lors de la suppression du produit en base !");
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
            System.err.println("❌ Erreur lors de la récupération du stock disponible !");
        }
        return 0; // Valeur par défaut si une erreur se produit
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private double getTotalPriceFromCart(int cartId) {
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
            System.err.println("❌ Erreur lors de la récupération du prix total du panier !");
        }
        return 0.0; // Valeur par défaut en cas d'erreur
    }
}