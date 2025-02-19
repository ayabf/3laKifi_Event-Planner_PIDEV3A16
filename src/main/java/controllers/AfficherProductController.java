package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.Product;
import java.io.IOException;

public class AfficherProductController {

    @FXML
    private ImageView productImage;
    @FXML
    private Label lblReference;
    @FXML
    private Label lblName;
    @FXML
    private Label lblDescription;
    @FXML
    private Label lblPrice;
    @FXML
    private Label lblCategory;
    @FXML
    private Label lblStockId;

    private static Product selectedProduct; // Static product to be set from AfficherProductsController

    /**
     * ✅ Displays product details when initialized
     */
    @FXML
    public void initialize() {
        if (selectedProduct != null) {
            lblReference.setText(selectedProduct.getReference());
            lblName.setText(selectedProduct.getName());
            lblDescription.setText(selectedProduct.getDescription());
            lblPrice.setText("$" + selectedProduct.getPrice());
            lblCategory.setText(selectedProduct.getCategory());
            lblStockId.setText(String.valueOf(selectedProduct.getStockId()));

            // Load image
            try {
                productImage.setImage(new Image(selectedProduct.getImageUrl(), 200, 200, false, false));
            } catch (Exception e) {
                productImage.setImage(new Image("/icons/default.png")); // Default image if not found
            }
        }
    }

    /**
     * ✅ Allows AfficherProductsController to set the selected product
     */
    public static void setSelectedProduct(Product product) {
        selectedProduct = product;
    }

    /**
     * ✅ Goes back to the product list
     */
    @FXML
    void retourAfficherProducts() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherProducts.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) lblReference.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            System.out.println("Error loading AfficherProducts.fxml: " + e.getMessage());
        }
    }
}