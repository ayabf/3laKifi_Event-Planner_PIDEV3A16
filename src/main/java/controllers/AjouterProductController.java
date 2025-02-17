// AjouterProductController.java (Controller for Adding Products)
package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import models.Product;
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

    @FXML
    void ajouterProduct(ActionEvent event) {
        try {
            if (!validateInputs()) {
                return;
            }

            String name = txtName.getText();
            String description = txtDescription.getText();
            float price = Float.parseFloat(txtPrice.getText());
            int stockId = Integer.parseInt(txtStockId.getText());
            String imageUrl = txtImageUrl.getText();
            String category = txtCategory.getText();
            int userId = Integer.parseInt(txtUserId.getText());

            Product product = new Product(name, description, price, stockId, imageUrl, category, userId);
            productService.ajouter(product);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Product added successfully!");
        } catch (SQLException | NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error adding product: " + e.getMessage());
        }
    }
    @FXML
    void afficherProducts(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherProducts.fxml"));
            Parent root = loader.load();
            txtName.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }


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
            Float.parseFloat(txtPrice.getText());
            Integer.parseInt(txtStockId.getText());
            Integer.parseInt(txtUserId.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Price must be a number, Stock ID and User ID must be integers.");
            return false;
        }

        return true;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
