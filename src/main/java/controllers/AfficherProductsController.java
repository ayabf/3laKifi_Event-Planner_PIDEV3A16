package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import models.Product;
import services.ProductService;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

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

    private void loadProducts(String category) {
        try {
            List<Product> products;

            if (category == null || category.isEmpty()) {
                products = productService.getAll(new Product());  // Pass an empty Product instance
            } else {
                products = productService.getByCategory(category);  // Ensure this method is correctly implemented
            }

            if (products != null) {
                productsContainer.getChildren().clear(); // Clear the UI container only if products exist
                for (Product product : products) {
                    productsContainer.getChildren().add(createProductCard(product));
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Error loading products: " + e.getMessage());
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
}
