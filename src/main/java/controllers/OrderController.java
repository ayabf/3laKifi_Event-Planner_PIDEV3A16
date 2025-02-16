package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import services.OrderService;
import utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OrderController {

    @FXML private Label orderIdLabel;
    @FXML private Label totalPriceLabel;
    @FXML private DatePicker eventDatePicker;
    @FXML private TextField addressField;
    @FXML private ComboBox<String> paymentMethodBox;
    @FXML private Button confirmOrderButton;

    private int orderId;
    private double totalPrice;

    public void setOrderDetails(int orderId, double totalPrice) {
        this.orderId = orderId;
        this.totalPrice = totalPrice;
        orderIdLabel.setText("Commande N°" + orderId);
        totalPriceLabel.setText("Total : $" + totalPrice);
    }

    @FXML
    private void confirmOrder() {
        String eventDate = (eventDatePicker.getValue() != null) ? eventDatePicker.getValue().toString() : "";
        String address = addressField.getText();
        String paymentMethod = paymentMethodBox.getValue();

        if (eventDate.isEmpty() || address.isEmpty() || paymentMethod == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        String updateQuery = "UPDATE `order` SET payment_method = ?, address = ?, event_date = ?, status = 'Confirmed' WHERE order_id = ?";
        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

            stmt.setString(1, paymentMethod);
            stmt.setString(2, address);
            stmt.setString(3, eventDate);
            stmt.setInt(4, orderId);

            stmt.executeUpdate();
            showAlert("Succès", "Commande confirmée avec succès !");

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de confirmer la commande.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void viewOrderDetails() {
        System.out.println("Voir les détails de la commande");
        // Ajoutez ici le code pour afficher les détails
    }

    @FXML
    private void cancelOrder() {
        System.out.println("Annuler la commande");
        // Ajoutez ici le code pour annuler une commande
    }
}
