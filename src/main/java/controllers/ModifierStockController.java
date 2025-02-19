package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import models.Stock;
import services.StockService;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class ModifierStockController {

    @FXML
    private TextField txtStockId;

    @FXML
    private TextField txtAvailableQuantity;

    @FXML
    private TextField txtMinimumQuantity;

    @FXML
    private TextField txtLastUpdate;

    @FXML
    private TextField txtUserId;

    private final StockService stockService = new StockService();

    @FXML
    void modifyStock(ActionEvent event) {
        try {
            int stockId = Integer.parseInt(txtStockId.getText());
            int availableQuantity = Integer.parseInt(txtAvailableQuantity.getText());
            int minimumQuantity = Integer.parseInt(txtMinimumQuantity.getText());
            int userId = Integer.parseInt(txtUserId.getText());

            // Mettre à jour last_update automatiquement
            Timestamp lastUpdate = Timestamp.valueOf(LocalDateTime.now());
            txtLastUpdate.setText(String.valueOf(lastUpdate));

            Stock stock = new Stock(stockId, availableQuantity, minimumQuantity, lastUpdate, userId);
            stockService.modifier(stock);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Stock modified successfully!");
        } catch (SQLException | NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error modifying stock: " + e.getMessage());
        }
    }


    public void setStockData(Stock stock) {
        txtStockId.setText(String.valueOf(stock.getStockId()));
        txtAvailableQuantity.setText(String.valueOf(stock.getAvailableQuantity()));
        txtMinimumQuantity.setText(String.valueOf(stock.getMinimumQuantity()));
        txtLastUpdate.setText(String.valueOf(stock.getLastUpdate()));
        txtUserId.setText(String.valueOf(stock.getIdUser()));
    }
    @FXML
    void retour(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherStock.fxml"));
            Parent root = loader.load();
            txtUserId.getScene().setRoot(root);
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
