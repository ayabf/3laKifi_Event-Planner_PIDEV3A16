package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import services.OrderService;

import java.sql.SQLException;

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
            orderService.ajouterCommande(cartId, userId, totalPrice, eventDate, address, paymentMethod);
            showAlert("Succès", "Commande validée !");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de valider la commande.");
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
