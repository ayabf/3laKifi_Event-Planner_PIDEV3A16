package controllers;

import Models.Order;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.OrderService;
import javafx.scene.control.Alert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


import java.sql.SQLException;


public class OrderFormController {
    @FXML private TextField addressField;
    @FXML private ComboBox<String> paymentMethodBox;
    @FXML private Button validateOrderButton;
    @FXML private DatePicker eventDatePicker;
    @FXML private Spinner<Integer> eventHourSpinner;
    @FXML private Spinner<Integer> eventMinuteSpinner;
    @FXML private Label totalPriceLabel;
    @FXML private Button timePickerButton;


    @FXML
    private Label selectedTimeLabel;
    private int selectedHour = 12;
    private int selectedMinute = 0;

    private int cartId;
    private int userId;
    private double totalPrice;

    private final OrderService orderService = new OrderService(); // Initialisation du service

    //controle de saisie :
    @FXML private Label errorAddress;
    @FXML private Label errorPayment;
    @FXML private Label errorDate;
    @FXML private Label errorTime;


    @FXML
    public void initialize() {
        if (eventHourSpinner != null && eventMinuteSpinner != null) {
            eventHourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12));
            eventMinuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 30));
        } else {
            System.err.println("⚠ `eventHourSpinner` ou `eventMinuteSpinner` n'est pas initialisé !");
        }
        addressField.textProperty().addListener((observable, oldValue, newValue) -> validateAddressInput());

    }



    public void setCartDetails(int cartId, int userId, double totalPrice) {
        this.cartId = cartId;
        this.userId = userId;
        this.totalPrice = totalPrice;

        System.out.println("Prix total reçu dans le formulaire: " + totalPrice); // Debugging

        totalPriceLabel.setText("$" + totalPrice); // Mise à jour de l'affichage
    }


    @FXML
    private void validateOrder() {
        LocalDate selectedDate = eventDatePicker.getValue();
        String address = addressField.getText();
        String paymentMethod = paymentMethodBox.getValue();
        boolean isValid = true;

        if (address.isEmpty() || address.length() < 5) {
            errorAddress.setVisible(true);
            addressField.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            errorAddress.setVisible(false);
            addressField.setStyle("-fx-border-color: green;");
        }
        if (paymentMethod == null) {
            errorPayment.setVisible(true);
            paymentMethodBox.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            errorPayment.setVisible(false);
            paymentMethodBox.setStyle("-fx-border-color: green;");
        }
        if (selectedDate == null || selectedDate.isBefore(LocalDate.now())) {
            errorDate.setVisible(true);
            eventDatePicker.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            errorDate.setVisible(false);
            eventDatePicker.setStyle("-fx-border-color: green;");
        }
        if (selectedHour < 0 || selectedMinute < 0) {
            errorTime.setVisible(true);
            isValid = false;
        } else {
            errorTime.setVisible(false);
        }
        if (!isValid) {
            return;
        }

        if (selectedDate == null || address.isEmpty() || paymentMethod == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs !");
            return;
        }
        LocalDateTime eventDateTime = LocalDateTime.of(selectedDate, LocalTime.of(selectedHour, selectedMinute));
        System.out.println("📅 Date sauvegardée : " + eventDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));


        // 📌 Créer et enregistrer la commande
        Order newOrder = new Order(cartId, userId, "PENDING");
        newOrder.setTotalPrice(totalPrice);
        newOrder.setEventDate(eventDateTime);
        newOrder.setExactAddress(address);
        newOrder.setPaymentMethod(paymentMethod);

        try {
            orderService.ajouter(newOrder);
            showAlert("Succès", "Commande validée !");
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

    @FXML
    private void openTimePicker() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Sélectionner l'heure");

        VBox content = new VBox();
        content.setSpacing(10);

        // Liste déroulante pour l'heure (0-23)
        ComboBox<Integer> hourBox = new ComboBox<>();
        for (int i = 0; i < 24; i++) {
            hourBox.getItems().add(i);
        }
        hourBox.setValue(selectedHour);

        // Liste déroulante pour les minutes (0-59, par pas de 5)
        ComboBox<Integer> minuteBox = new ComboBox<>();
        for (int i = 0; i < 60; i += 5) {
            minuteBox.getItems().add(i);
        }
        minuteBox.setValue(selectedMinute);

        HBox timeSelection = new HBox(10, new Label("Heure:"), hourBox, new Label("Minute:"), minuteBox);
        content.getChildren().add(timeSelection);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                selectedHour = hourBox.getValue();
                selectedMinute = minuteBox.getValue();
                selectedTimeLabel.setText(String.format("Heure sélectionnée: %02d:%02d", selectedHour, selectedMinute));
            }
        });
    }

//controle de saisie :
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



}