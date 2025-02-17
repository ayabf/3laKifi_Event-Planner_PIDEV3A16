package controllers;

import Models.Order;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.OrderService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OrderFormController {
    @FXML private TextField eventDateField;
    @FXML private TextField addressField;
    @FXML private ComboBox<String> paymentMethodBox;
    @FXML private Button validateOrderButton;

    private int cartId;
    private int userId;
    private double totalPrice;

    private final OrderService orderService = new OrderService(); // Initialisation du service

    @FXML private Label totalPriceLabel; // Assurez-vous que ce label est bien lié dans le FXML

    public void setCartDetails(int cartId, int userId, double totalPrice) {
        this.cartId = cartId;
        this.userId = userId;
        this.totalPrice = totalPrice;

        System.out.println("Prix total reçu dans le formulaire: " + totalPrice); // Debugging

        totalPriceLabel.setText("$" + totalPrice); // Mise à jour de l'affichage
    }


    @FXML
    private void validateOrder() {
        String eventDate = eventDateField.getText();
        String address = addressField.getText();
        String paymentMethod = paymentMethodBox.getValue();

        if (eventDate.isEmpty() || address.isEmpty() || paymentMethod == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs !");
            return;
        }

        try {
            Order newOrder = new Order(cartId, userId, "PENDING");
            newOrder.setTotalPrice(totalPrice);
            newOrder.setEventDate(LocalDateTime.parse(eventDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            newOrder.setExactAddress(address);
            newOrder.setPaymentMethod(paymentMethod);

            orderService.ajouter(newOrder);

            showAlert("Succès", "Commande validée !");

            // ✅ Ouvre la page des commandes après validation
            openOrderListPage();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de valider la commande.");
        }
    }

    private void openOrderListPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/OrderList.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Mes Commandes");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la page des commandes.");
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
