package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import Models.Stock;
import services.StockService;

import java.io.IOException;
import java.sql.SQLException;

public class AjouterStockController {

    @FXML
    private TextField txtAvailableQuantity;

    @FXML
    private TextField txtMinimumQuantity;

    @FXML
    private TextField txtUserId;

    private final StockService stockService = new StockService();

    @FXML
    void ajouterStock(ActionEvent event) {
        try {
            if (!validateInputs()) {
                return;
            }

            int availableQuantity = Integer.parseInt(txtAvailableQuantity.getText());
            int minimumQuantity = Integer.parseInt(txtMinimumQuantity.getText());
            int userId = Integer.parseInt(txtUserId.getText());

            Stock stock = new Stock(availableQuantity, minimumQuantity, userId);
            stockService.ajouter(stock);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Stock ajouté avec succès !");
            clearFields();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout du stock : " + e.getMessage());
        }
    }

    private boolean validateInputs() {
        if (txtAvailableQuantity.getText().trim().isEmpty() ||
                txtMinimumQuantity.getText().trim().isEmpty() ||
                txtUserId.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Erreur de validation", "Tous les champs doivent être remplis.");
            return false;
        }

        try {
            Integer.parseInt(txtAvailableQuantity.getText());
            Integer.parseInt(txtMinimumQuantity.getText());
            Integer.parseInt(txtUserId.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Erreur de validation", "Les valeurs doivent être des nombres entiers.");
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

    private void clearFields() {
        txtAvailableQuantity.clear();
        txtMinimumQuantity.clear();
        txtUserId.clear();
    }
    @FXML
    void afficherstock(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherStock.fxml"));
            Parent root = loader.load();
            txtAvailableQuantity.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println("❌ Error loading AfficherProducts.fxml: " + e.getMessage());
        }
    }
}
