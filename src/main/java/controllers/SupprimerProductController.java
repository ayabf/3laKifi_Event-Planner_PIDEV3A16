package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import services.ProductService;
import java.sql.SQLException;

public class SupprimerProductController {

    @FXML
    private TextField txtProductId;

    private final ProductService productService = new ProductService();

    @FXML
    void supprimerProduct(ActionEvent event) {
        try {
            int productId = Integer.parseInt(txtProductId.getText());

            // Vérifier si le produit existe avant de supprimer
            if (productService.getOne(new models.Product(productId)) == null) {
                showAlert(Alert.AlertType.WARNING, "Avertissement", "Aucun produit trouvé avec cet ID.");
                return;
            }

            productService.supprimer(productId);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Produit supprimé avec succès !");
            txtProductId.clear();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "L'ID du produit doit être un nombre entier.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression : " + e.getMessage());
        }
    }

    @FXML
    void annulerSuppression(ActionEvent event) {
        txtProductId.getScene().getWindow().hide(); // Ferme la fenêtre
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
