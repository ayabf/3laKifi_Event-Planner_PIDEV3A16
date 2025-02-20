package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import services.ProductService;

import java.io.IOException;
import java.sql.SQLException;

public class SupprimerProductController {

    @FXML
    private TextField txtReference;

    private final ProductService productService = new ProductService();

    /**
     * ✅ Deletes a product using its reference.
     */
    @FXML
    void supprimerProduct(ActionEvent event) {
        try {
            String reference = txtReference.getText().trim();

            if (reference.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation", "Veuillez entrer une référence de produit.");
                return;
            }

            // Vérifier si le produit existe avant de supprimer
            if (productService.getByReference(reference) == null) {
                showAlert(Alert.AlertType.WARNING, "Avertissement", "Aucun produit trouvé avec cette référence.");
                return;
            }

            productService.supprimerByReference(reference);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Produit supprimé avec succès !");
            txtReference.clear();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression : " + e.getMessage());
        }
    }

    /**
     * ✅ Cancels the deletion and closes the window.
     */
    @FXML
    void annulerSuppression(ActionEvent event) {
        txtReference.getScene().getWindow().hide(); // Ferme la fenêtre
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
