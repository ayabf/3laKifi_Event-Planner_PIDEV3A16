package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import models.Product;
import services.ProductService;
import java.sql.SQLException;

public class ModifierProductController {

    @FXML
    private TextField txtProductId;

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

    private final ProductService productService = new ProductService();
    private Product currentProduct;

    @FXML
    void rechercherProduct(ActionEvent event) {
        try {
            int productId = Integer.parseInt(txtProductId.getText());
            Product product = productService.getOne(new Product(productId));

            if (product != null) {
                currentProduct = product; // Stocke le produit actuel pour modification
                txtName.setText(product.getName());
                txtDescription.setText(product.getDescription());
                txtPrice.setText(String.valueOf(product.getPrice()));
                txtStockId.setText(String.valueOf(product.getStockId()));
                txtImageUrl.setText(product.getImageUrl());
                txtCategory.setText(product.getCategory());
                txtUserId.setText(String.valueOf(product.getIdUser()));
            } else {
                showAlert(Alert.AlertType.WARNING, "Avertissement", "Aucun produit trouvé avec cet ID.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "L'ID du produit doit être un nombre entier.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la récupération du produit : " + e.getMessage());
        }
    }

    @FXML
    void modifierProduct(ActionEvent event) {
        if (currentProduct == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez d'abord rechercher un produit par ID.");
            return;
        }

        try {
            if (!validateInputs()) {
                return;
            }

            currentProduct.setName(txtName.getText());
            currentProduct.setDescription(txtDescription.getText());
            currentProduct.setPrice(Float.parseFloat(txtPrice.getText()));
            currentProduct.setStockId(Integer.parseInt(txtStockId.getText()));
            currentProduct.setImageUrl(txtImageUrl.getText());
            currentProduct.setCategory(txtCategory.getText());
            currentProduct.setIdUser(Integer.parseInt(txtUserId.getText()));

            productService.modifier(currentProduct);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Produit modifié avec succès !");
        } catch (SQLException | NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la modification : " + e.getMessage());
        }
    }

    @FXML
    void annulerModification(ActionEvent event) {
        txtProductId.getScene().getWindow().hide(); // Ferme la fenêtre
    }

    private boolean validateInputs() {
        if (txtName.getText().trim().isEmpty() ||
                txtDescription.getText().trim().isEmpty() ||
                txtPrice.getText().trim().isEmpty() ||
                txtStockId.getText().trim().isEmpty() ||
                txtImageUrl.getText().trim().isEmpty() ||
                txtCategory.getText().trim().isEmpty() ||
                txtUserId.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Tous les champs doivent être remplis !");
            return false;
        }

        try {
            Float.parseFloat(txtPrice.getText());
            Integer.parseInt(txtStockId.getText());
            Integer.parseInt(txtUserId.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Prix doit être un nombre, Stock ID et User ID des entiers.");
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
