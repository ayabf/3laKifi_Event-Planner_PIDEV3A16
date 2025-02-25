package controllers;

import Models.Product;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import services.ProductService;

import java.io.IOException;
import java.sql.SQLException;

public class AjouterProductController {

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtDescription;

    @FXML
    private TextField txtPrice;

    @FXML
    private TextField txtStockId;

    @FXML
    private TextField txtImageUrl;

    @FXML
    private TextField txtCategory;

    @FXML
    private TextField txtUserId;

    @FXML
    private Button btnAddProduct;

    private final ProductService productService = new ProductService();

    /**
     * ✅ Adds a new product with a generated reference.
     */
    @FXML
    void ajouterProduct(ActionEvent event) {
        try {
            if (!validateInputs()) {
                return;
            }

            String name = txtName.getText().trim();
            String description = txtDescription.getText().trim();
            float price = Float.parseFloat(txtPrice.getText().trim());
            int stockId = Integer.parseInt(txtStockId.getText().trim());
            String imageUrl = txtImageUrl.getText().trim();
            String category = txtCategory.getText().trim();
            int userId = Integer.parseInt(txtUserId.getText().trim());

            // ✅ Generate a unique reference based on name, userId, and timestamp
            String reference = generateReference(name, userId);

            // ✅ Create and save the product
            Product product = new Product(reference, name, description, price, stockId, imageUrl, category, userId);
            productService.ajouter(product);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Product added successfully!");

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error adding product: " + e.getMessage());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Invalid numeric input. Please check Price, Stock ID, and User ID.");
        }
    }

    /**
     * ✅ Switches to the "Afficher Products" scene.
     */
    @FXML
    void afficherProducts(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherProducts.fxml"));
            Parent root = loader.load();
            txtName.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println("❌ Error loading AfficherProducts.fxml: " + e.getMessage());
        }
    }

    /**
     * ✅ Generates a unique reference for a product.
     */
    private String generateReference(String name, int userId) {
        return name.replaceAll("\\s+", "").toUpperCase() + "-" + userId + "-" + System.currentTimeMillis();
    }

    /**
     * ✅ Validates input fields before adding a product.
     */
    private boolean validateInputs() {
        if (txtName.getText().trim().isEmpty() ||
                txtDescription.getText().trim().isEmpty() ||
                txtPrice.getText().trim().isEmpty() ||
                txtStockId.getText().trim().isEmpty() ||
                txtImageUrl.getText().trim().isEmpty() ||
                txtCategory.getText().trim().isEmpty() ||
                txtUserId.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "All fields must be filled out.");
            return false;
        }

        try {
            Float.parseFloat(txtPrice.getText().trim());
            Integer.parseInt(txtStockId.getText().trim());
            Integer.parseInt(txtUserId.getText().trim());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Price must be a number, Stock ID and User ID must be integers.");
            return false;
        }

        return true;
    }

    /**
     * ✅ Displays an alert popup.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
