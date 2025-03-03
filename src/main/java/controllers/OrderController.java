package controllers;

import Models.Order;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import services.OrderService;
import utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class OrderController {
    private final OrderService orderService = new OrderService();

    @FXML private Label orderIdLabel;
    @FXML private Label totalPriceLabel;
    @FXML private DatePicker eventDatePicker;
    @FXML private TextField addressField;
    @FXML private ComboBox<String> paymentMethodBox;
    @FXML private Button confirmOrderButton;
    @FXML private TextField searchField;
    @FXML private FlowPane orderListContainer;


    private int orderId;
    private double totalPrice;
    private ObservableList<Order> allOrders = FXCollections.observableArrayList();
    private void loadOrders() {
        allOrders.clear();
        allOrders.addAll(getOrdersFromDatabase());
        displayOrders(allOrders);
    }
    @FXML private void initialize() {
        searchField.setOnKeyReleased(event -> searchOrders());
    }

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

        try {
            OrderService orderService = new OrderService();
            orderService.updateOrderStatus(orderId, "CONFIRMED");

            // 🔥 Appel de confirmOrder() pour mettre à jour le stock
            boolean stockUpdated = orderService.confirmOrder(orderId);

            if (stockUpdated) {
                showAlert("Succès", "Commande confirmée et stock mis à jour !");
            } else {
                showAlert("Attention", "Commande confirmée, mais mise à jour du stock échouée.");
            }

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
    private void displayOrders(ObservableList<Order> orders) {
        orderListContainer.getChildren().clear();

        for (Order order : orders) {
            VBox orderCard = createOrderCard(order);
            orderListContainer.getChildren().add(orderCard);
        }
    }

    @FXML
    private void searchOrders() {
        String keyword = searchField.getText().toLowerCase().trim();

        // Vérifier que la liste `allOrders` contient bien des commandes
        if (allOrders == null || allOrders.isEmpty()) {
            return;
        }

        // Filtrer les commandes en fonction du mot-clé
        ObservableList<Order> filteredOrders = allOrders.filtered(order ->
                order.getStatus().toLowerCase().contains(keyword) ||
                        order.getExactAddress().toLowerCase().contains(keyword) ||
                        (order.getEventDate() != null && order.getEventDate().toString().contains(keyword)) ||
                        String.valueOf(order.getTotalPrice()).contains(keyword)
        );

        // ✅ Mettre à jour l'affichage des commandes
        displayOrders(filteredOrders);
    }


    private VBox createOrderCard(Order order) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-padding: 15px; -fx-border-radius: 10px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        Label orderIdLabel = new Label("Commande N°" + order.getOrderId());
        orderIdLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        Label statusLabel = new Label("Statut : " + order.getStatus());
        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + getStatusColor(order.getStatus()) + ";");

        Label priceLabel = new Label("Total : $" + order.getTotalPrice());
        priceLabel.setStyle("-fx-font-size: 14px;");

        Label addressLabel = new Label("Adresse : " + order.getExactAddress());
        addressLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

        card.getChildren().addAll(orderIdLabel, statusLabel, priceLabel, addressLabel);
        return card;
    }

    private String getStatusColor(String status) {
        return switch (status.toLowerCase()) {
            case "confirmed" -> "green";
            case "pending" -> "orange";
            case "cancelled" -> "red";
            default -> "black";
        };
    }
    private ObservableList<Order> getOrdersFromDatabase() {
        ObservableList<Order> orders = FXCollections.observableArrayList();

        String query = "SELECT order_id, cart_id, user_id, status, payment_method, address, event_date, ordered_at, total_price FROM `order`";

        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                int cartId = rs.getInt("cart_id");
                int userId = rs.getInt("user_id");
                String status = rs.getString("status");
                String paymentMethod = rs.getString("payment_method");
                String exactAddress = rs.getString("address");
                LocalDateTime eventDate = (rs.getTimestamp("event_date") != null)
                        ? rs.getTimestamp("event_date").toLocalDateTime()
                        : null;
                LocalDateTime orderedAt = rs.getTimestamp("ordered_at").toLocalDateTime();
                double totalPrice = rs.getDouble("total_price");

                orders.add(new Order(orderId, cartId, userId, status, paymentMethod, exactAddress, eventDate, orderedAt, totalPrice));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Erreur lors de la récupération des commandes depuis la base de données !");
        }

        return orders;
    }




}