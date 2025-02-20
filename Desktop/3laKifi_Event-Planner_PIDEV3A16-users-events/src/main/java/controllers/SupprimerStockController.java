package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import Models.Stock;
import services.StockService;

import java.io.IOException;
import java.sql.SQLException;

public class SupprimerStockController {

    @FXML
    private TextField txtStockId;

    @FXML
    private Button btnDeleteStock;

    private final StockService stockService = new StockService();

    @FXML
    void deleteStock(ActionEvent event) {
        try {
            int stockId = Integer.parseInt(txtStockId.getText());

            // Créer un objet Stock pour appeler getOne
            Stock stock = new Stock();
            stock.setStockId(stockId);

            // Vérifier si le stock existe avant de supprimer
            if (stockService.getOne(stock) != null) {
                stockService.supprimer(stockId);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Stock deleted successfully!");
            } else {
                showAlert(Alert.AlertType.WARNING, "Not Found", "No stock found with this ID.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Stock ID must be a number.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error deleting stock: " + e.getMessage());
        }
    }
    @FXML
    void retour(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherStock.fxml"));
            Parent root = loader.load();
            txtStockId.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println("❌ Error loading AfficherProducts.fxml: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
