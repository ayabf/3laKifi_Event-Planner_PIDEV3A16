package controllers;

import Models.Product;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import services.ProductService;

import java.io.IOException;
import java.sql.SQLException;

public class ModifierProductController {

    @FXML
    private TextField txtReference;

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
    private Button btnSearchProduct;

    @FXML
    private Button btnUpdateProduct;

    private final ProductService productService = new ProductService();

    /**
     * ✅ Searches for a product using its reference
     */
    @FXML
    void rechercherProduct(ActionEvent event) {
        try {
            String reference = txtReference.getText().trim();
            if (reference.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation", "Please enter a product reference.");
                return;
            }

            Product product = productService.getByReference(reference);
            if (product != null) {
                // Populate the fields
                txtName.setText(product.getName());
                txtDescription.setText(product.getDescription());
                txtPrice.setText(String.valueOf(product.getPrice()));
                txtStockId.setText(String.valueOf(product.getStockId()));
                txtImageUrl.setText(product.getImageUrl());
                txtCategory.setText(product.getCategory());
                txtUserId.setText(String.valueOf(product.getIdUser()));
            } else {
                showAlert(Alert.AlertType.WARNING, "Error", "No product found with this reference!");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error retrieving product: " + e.getMessage());
        }
    }

    /**
     * ✅ Updates the product while keeping the reference unchanged
     */
    @FXML
    void modifierProduct(ActionEvent event) {
        try {
            if (!validateInputs()) {
                return;
            }

            String reference = txtReference.getText().trim();
            Product existingProduct = productService.getByReference(reference);

            if (existingProduct == null) {
                showAlert(Alert.AlertType.WARNING, "Error", "No product found with this reference!");
                return;
            }

            // Update values
            existingProduct.setName(txtName.getText());
            existingProduct.setDescription(txtDescription.getText());
            existingProduct.setPrice(Float.parseFloat(txtPrice.getText()));
            existingProduct.setStockId(Integer.parseInt(txtStockId.getText()));
            existingProduct.setImageUrl(txtImageUrl.getText());
            existingProduct.setCategory(txtCategory.getText());
            existingProduct.setIdUser(Integer.parseInt(txtUserId.getText()));

            // Save modifications
            productService.modifier(existingProduct);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Product updated successfully!");

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error updating product: " + e.getMessage());
        }
    }

    /**
     * ✅ Cancels modification and closes the window
     */
    @FXML
    void annulerModification(ActionEvent event) {
        txtReference.getScene().getWindow().hide(); // Close the window
    }

    /**
     * ✅ Validates form inputs before updating
     */
    private boolean validateInputs() {
        if (txtReference.getText().trim().isEmpty() ||
                txtName.getText().trim().isEmpty() ||
                txtDescription.getText().trim().isEmpty() ||
                txtPrice.getText().trim().isEmpty() ||
                txtStockId.getText().trim().isEmpty() ||
                txtImageUrl.getText().trim().isEmpty() ||
                txtCategory.getText().trim().isEmpty() ||
                txtUserId.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation", "All fields must be filled out.");
            return false;
        }

        try {
            Float.parseFloat(txtPrice.getText());
            Integer.parseInt(txtStockId.getText());
            Integer.parseInt(txtUserId.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Price must be a number, Stock ID and User ID must be integers.");
            return false;
        }

        return true;
    }
    @FXML
    void retour(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherProducts.fxml"));
            Parent root = loader.load();
            txtReference.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println("❌ Error loading AfficherProducts.fxml: " + e.getMessage());
        }
    }

    /**
     * ✅ Displays alerts
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}