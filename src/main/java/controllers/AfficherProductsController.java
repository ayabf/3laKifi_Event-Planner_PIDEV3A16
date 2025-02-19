package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import Models.Product;
import services.ProductService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.input.MouseEvent;


public class AfficherProductsController {

    @FXML
    private FlowPane productsContainer;

    @FXML
    private ComboBox<String> categoryFilter;


    private final ProductService productService = new ProductService();

    @FXML
    public void initialize() {
        loadCategories();
        loadProducts(null); // Chargement initial
    }

    private void loadCategories() {
        try {
            List<Product> products = productService.getAll(new Product());
            List<String> categories = products.stream()
                    .map(Product::getCategory)
                    .distinct()
                    .collect(Collectors.toList());
            categoryFilter.getItems().addAll(categories);
        } catch (SQLException e) {
            System.out.println("Error loading categories: " + e.getMessage());
        }
    }

    @FXML
    private void filterByCategory() {
        String selectedCategory = categoryFilter.getValue();
        if (selectedCategory == null || selectedCategory.isEmpty()) {
            loadProducts(null); // Load all products when no category is selected
        } else {
            try {
                List<Product> filteredProducts = productService.getByCategory(selectedCategory);
                loadFilteredProducts(filteredProducts);
            } catch (SQLException e) {
                System.out.println("Error filtering products by category: " + e.getMessage());
            }
        }
    }


    private void loadFilteredProducts(List<Product> products) {
        productsContainer.getChildren().clear();
        for (Product product : products) {
            productsContainer.getChildren().add(createProductCard(product));
        }
    }



    // Inside the method that creates product cards
    private void loadProducts(String category) {
        productsContainer.getChildren().clear();
        try {
            List<Product> products = (category == null || category.isEmpty())
                    ? productService.getAll(new Product())
                    : productService.getByCategory(category);

            for (Product product : products) {
                VBox productBox = createProductCard(product);

                // Add click event
                productBox.setOnMouseClicked((MouseEvent event) -> {
                    AfficherProductController.setSelectedProduct(product);
                    afficherProductDetails();
                });

                productsContainer.getChildren().add(productBox);
            }
        } catch (SQLException e) {
            System.out.println("Error loading products: " + e.getMessage());
        }
    }

    /**
     * ✅ Loads the product details page
     */
    private void afficherProductDetails() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherProduct.fxml"));
            Parent root = loader.load();

            // Get the current scene's window
            Stage stage = (Stage) productsContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            System.out.println("Error loading AfficherProduct.fxml: " + e.getMessage());
        }
    }




    private VBox createProductCard(Product product) {
        VBox productBox = new VBox(5);
        productBox.setStyle("-fx-border-color: #ccc; -fx-padding: 10px; -fx-alignment: center;");

        ImageView imageView;
        try {
            imageView = new ImageView(new Image(product.getImageUrl(), 100, 100, false, false));
        } catch (Exception e) {
            imageView = new ImageView(new Image("/icons/default.png"));
        }

        Label nameLabel = new Label(product.getName());
        nameLabel.setStyle("-fx-font-weight: bold;");

        Label priceLabel = new Label("$" + product.getPrice());
        priceLabel.setStyle("-fx-text-fill: #FF5733;");

        productBox.getChildren().addAll(imageView, nameLabel, priceLabel);
        return productBox;
    }
    @FXML
    void modifierProduct(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierProduct.fxml"));
            Parent root = loader.load();
            categoryFilter.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println("❌ Error loading AfficherProducts.fxml: " + e.getMessage());
        }
    }
    @FXML
    void supprimerProduct(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SupprimerProduct.fxml"));
            Parent root = loader.load();
            categoryFilter.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println("❌ Error loading AfficherProducts.fxml: " + e.getMessage());
        }
    }
    @FXML
    void ajouterProduct(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterProduct.fxml"));
            Parent root = loader.load();
            categoryFilter.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println("❌ Error loading AfficherProducts.fxml: " + e.getMessage());
        }
    }
}