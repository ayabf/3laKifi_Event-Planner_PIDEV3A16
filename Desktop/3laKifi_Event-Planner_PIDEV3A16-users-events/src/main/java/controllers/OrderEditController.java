package controllers;

import Models.Order;
import Models.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.OrderService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class OrderEditController {

    @FXML private TextField addressField;
    @FXML private DatePicker eventDatePicker;
    @FXML private Button saveButton;
    @FXML private Label errorAddress;
    @FXML private Label errorDate;

    private Order currentOrder;
    private final OrderService orderService = new OrderService();
    private OrderListController parentController;

    @FXML
    public void initialize() {
        addressField.textProperty().addListener((observable, oldValue, newValue) -> validateAddressInput());
    }
    public void setOrder(Order order) {
        this.currentOrder = order;
        addressField.setText(order.getExactAddress());
        eventDatePicker.setValue(order.getEventDate().toLocalDate());
    }

    private OrderListController orderListController;  // Référence au contrôleur parent

    public void setParentController(OrderListController orderListController) {
        this.orderListController = orderListController;
    }

    @FXML
    private void saveChanges() {
        if (currentOrder == null) {
            showAlert("Erreur", "Aucune commande sélectionnée !");
            return;
        }
        boolean isValid = true;
        String newAddress = addressField.getText().trim();
        LocalDate newDate = eventDatePicker.getValue();

        if (newAddress.isEmpty() || newAddress.length() < 5) {
            errorAddress.setVisible(true);
            addressField.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            errorAddress.setVisible(false);
            addressField.setStyle("-fx-border-color: green;");
        }
        if (newDate == null || newDate.isBefore(LocalDate.now())) {
            errorDate.setVisible(true);
            eventDatePicker.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            errorDate.setVisible(false);
            eventDatePicker.setStyle("-fx-border-color: green;");
        }
        if (!isValid) {
            return;
        }
        LocalDate newEventDate = eventDatePicker.getValue();

        if (newAddress.isEmpty() || newEventDate == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs !");
            return;
        }

        currentOrder.setExactAddress(newAddress);
        currentOrder.setEventDate(newEventDate.atStartOfDay());

        try {
            orderService.modifier(currentOrder);
            showAlert("Succès", "Commande modifiée avec succès !");

            // Mettre à jour la liste des commandes via OrderListController existant
            if (orderListController != null) {
                orderListController.loadOrders();  // Rafraîchir toute la liste
                orderListController.updateOrderDisplay(currentOrder);  // Mettre à jour juste cette commande
            }

            // Fermer la fenêtre de modification
            Stage stage = (Stage) saveButton.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de modifier la commande.");
        }

    }

    private void closeWindow() {
        Stage stage = (Stage) addressField.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void validateAddressInput() {
        String address = addressField.getText().trim();
        if (address.length() < 5) {
            errorAddress.setVisible(true);
            addressField.setStyle("-fx-border-color: red;");
        } else {
            errorAddress.setVisible(false);
            addressField.setStyle("-fx-border-color: green;");
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