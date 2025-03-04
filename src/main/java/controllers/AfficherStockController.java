package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import Models.Stock;
import services.StockService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AfficherStockController {

    @FXML
    private TextField txtUserId;

    @FXML
    private ListView<String> stockListView;

    private final StockService stockService = new StockService();

    @FXML
    void rechercherStocks(ActionEvent event) {
        try {
            if (txtUserId.getText().trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Erreur", "Veuillez entrer un ID utilisateur.");
                return;
            }

            int userId = Integer.parseInt(txtUserId.getText());
            List<Stock> stocks = stockService.getStocksByUserId(userId);

            stockListView.getItems().clear();

            if (stocks.isEmpty()) {
                stockListView.getItems().add("Aucun stock trouvé pour cet utilisateur.");
            } else {
                for (Stock stock : stocks) {
                    stockListView.getItems().add(formatStock(stock));
                }
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Erreur", "L'ID utilisateur doit être un nombre entier.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la récupération des stocks : " + e.getMessage());
        }
    }

    private String formatStock(Stock stock) {
        return "Stock ID: " + stock.getStockId() +
                " | Disponible: " + stock.getAvailableQuantity() +
                " | Minimum: " + stock.getMinimumQuantity() +
                " | Dernière mise à jour: " + stock.getLastUpdate();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    void modifierStock(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierStock.fxml"));
            Parent root = loader.load();
            txtUserId.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println("❌ Error loading AfficherProducts.fxml: " + e.getMessage());
        }
    }
    @FXML
    void supprimerStock(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SupprimerStock.fxml"));
            Parent root = loader.load();
            txtUserId.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println("❌ Error loading AfficherProducts.fxml: " + e.getMessage());
        }
    }
    @FXML
    void ajouterStock(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterStock.fxml"));
            Parent root = loader.load();
            txtUserId.getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println("❌ Error loading AfficherProducts.fxml: " + e.getMessage());
        }
    }

}
