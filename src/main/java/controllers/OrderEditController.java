package controllers;

import Models.Order;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.OrderService;

import java.sql.SQLException;
import java.time.LocalDate;

public class OrderEditController {

    @FXML private TextField addressField;
    @FXML private DatePicker eventDatePicker;
    @FXML private Button saveButton;

    private Order currentOrder;  // Commande en cours de modification
    private final OrderService orderService = new OrderService();

    // 🛠 Initialiser la commande sélectionnée pour modification
    public void setOrder(Order order) {
        this.currentOrder = order;

        // Remplir les champs avec les valeurs actuelles
        addressField.setText(order.getExactAddress());
        eventDatePicker.setValue(order.getEventDate().toLocalDate());
    }

    @FXML
    private void saveChanges() {
        if (currentOrder == null) {
            showAlert("Erreur", "Aucune commande sélectionnée !");
            return;
        }

        // Récupérer les nouvelles valeurs
        String newAddress = addressField.getText();
        LocalDate newEventDate = eventDatePicker.getValue();

        if (newAddress.isEmpty() || newEventDate == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs !");
            return;
        }

        // Mettre à jour la commande
        currentOrder.setExactAddress(newAddress);
        currentOrder.setEventDate(newEventDate.atStartOfDay());

        try {
            orderService.modifier(currentOrder);
            showAlert("Succès", "Commande modifiée avec succès !");

            // Fermer la fenêtre après modification
            Stage stage = (Stage) saveButton.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de modifier la commande.");
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
