package controllers;

import Models.Order;
import Models.session;
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
import utils.DataSource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    private Button modifyStatusButton;

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
        System.out.println("🔍 Session utilisateur chargée : ID = " + session.id_utilisateur);
        System.out.println("🔍 Vérification du rôle de l'utilisateur : " + session.id_utilisateur);

        if (!isAdmin(session.id_utilisateur)) {
            modifyStatusButton.setVisible(false); // Cache le bouton si ce n'est pas un admin
        }
        if (session.id_utilisateur <= 0) {
            System.err.println("❌ ERREUR: L'utilisateur n'est pas défini !");
            showAlert("Erreur", "Utilisateur non défini. Veuillez vous reconnecter.");
            return;
        }

        if (eventHourSpinner != null && eventMinuteSpinner != null) {
            eventHourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12));
            eventMinuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 30));
        } else {
            System.err.println("⚠ eventHourSpinner ou eventMinuteSpinner n'est pas initialisé !");
        }
        addressField.textProperty().addListener((observable, oldValue, newValue) -> validateAddressInput());

    }
    private boolean isAdmin(int userId) {
        String query = "SELECT role FROM user WHERE id_user = ?";

        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                return "ADMIN".equalsIgnoreCase(role);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public void setCartDetails(int cartId, int userId, double totalPrice) {
        System.out.println("✅ setCartDetails() appelé avec - Cart ID: " + cartId + ", User ID: " + userId + ", Total: " + totalPrice);

        if (userId <= 0) {
            System.err.println("❌ ERREUR: userId est invalide !");
            return;
        }

        this.cartId = cartId;
        this.userId = userId;  // 🔥 S'assurer que l'ID utilisateur est bien stocké
        this.totalPrice = totalPrice;

        System.out.println("🔹 setCartDetails() appelé avec - Cart ID: " + cartId + ", User ID: " + userId + ", Total: " + totalPrice);

        System.out.println("📦 Commande associée à l'utilisateur ID: " + userId);
        totalPriceLabel.setText("$" + totalPrice);
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

        if (!isValid) {
            return;
        }

        int cartId = getCartIdForUser(session.id_utilisateur);
        System.out.println("🎯 Cart ID trouvé pour utilisateur " + session.id_utilisateur + " : " + cartId);

        if (cartId <= 0) {
            cartId = orderService.creerNouveauPanier(session.id_utilisateur);
        }

        if (orderService.commandeExisteDeja(cartId)) {
            showAlert("Erreur", "Une commande existe déjà pour ce panier !");
            return;
        }

        if (!orderService.panierContientProduits(cartId)) {
            showAlert("Erreur", "Votre panier est vide. Ajoutez des produits avant de valider la commande !");
            return;
        }

        LocalDateTime eventDateTime = LocalDateTime.of(selectedDate, LocalTime.of(selectedHour, selectedMinute));

        Order newOrder = new Order(cartId, userId, "PENDING", totalPrice, eventDateTime, address, paymentMethod);
        orderService.ajouter(newOrder);

        // 🔥 Mise à jour du stock après validation
        orderService.confirmOrder(newOrder.getOrderId());

        showAlert("Succès", "Commande validée et stock mis à jour !");
        openOrderListPage();
    }


    private void showAlertAndRedirect(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait().ifPresent(response -> openOrderListPage());
    }









    private int getCartIdForUser(int userId) {
        System.out.println("🔍 Recherche du Cart ID pour user_id : " + userId);

        String query = "SELECT cart_id FROM cart WHERE user_id = ?";
        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int cartId = rs.getInt("cart_id");
                System.out.println("✅ Cart ID trouvé : " + cartId);
                return cartId;
            } else {
                System.out.println("⚠ Aucun panier trouvé pour l'utilisateur : " + userId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Erreur lors de la récupération du cart_id !");
        }
        return -1; // Retourne -1 si aucun panier n'est trouvé
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

    @FXML
    private void handleClose() {
        Stage stage = (Stage) validateOrderButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleReload() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/OrderForm.fxml"));
            Parent root = loader.load();
            ((OrderFormController) loader.getController()).setCartDetails(cartId, userId, totalPrice);

            Stage stage = (Stage) validateOrderButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}