package controllers;

import Models.Order;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import services.OrderService;
import utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderListController {

    @FXML
    private VBox orderListContainer; // Un VBox où on affiche les commandes

    private final OrderService orderService = new OrderService();

    @FXML
    public void initialize() {
        loadOrders();
    }

    private void loadOrders() {
        try {
            List<Order> orders = orderService.getAll();
            orderListContainer.getChildren().clear();

            for (Order order : orders) {
                System.out.println("🔍 Vérification OrderList - Prix avant affichage: " + order.getTotalPrice());
                VBox orderCard = createOrderCard(order);
                orderListContainer.getChildren().add(orderCard);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Erreur lors du chargement des commandes !");
        }
    }


    public List<Order> getAllByUser(int userId) throws SQLException {
        List<Order> orderList = new ArrayList<>();
        String query = "SELECT * FROM `order` WHERE user_id = ? ORDER BY ordered_at DESC";

        try (Connection conn = DataSource.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Order order = new Order(
                        rs.getInt("order_id"),
                        rs.getInt("cart_id"),
                        rs.getInt("user_id"),
                        rs.getString("status"),
                        rs.getString("payment_method"),
                        rs.getString("exact_address"),
                        rs.getTimestamp("event_date").toLocalDateTime(),
                        rs.getTimestamp("ordered_at").toLocalDateTime(),
                        rs.getDouble("total_price")
                );
                orderList.add(order);
            }
        }
        return orderList;
    }


    private VBox createOrderCard(Order order) {
        VBox card = new VBox();
        card.setStyle("-fx-border-color: black; -fx-padding: 10; -fx-spacing: 5; -fx-background-color: #f9f9f9;");

        // Création des labels
        Label totalLabel = new Label("Total: $" + String.format("%.2f", order.getTotalPrice()));
        Label statusLabel = new Label("Statut: " + order.getStatus());
        Label eventDateLabel = new Label("Date de l'événement: " + order.getEventDate().toString());
        Label addressLabel = new Label("Adresse: " + order.getExactAddress());

        // 🎨 Appliquer une couleur selon le statut
        switch (order.getStatus().toUpperCase()) {
            case "PENDING":
                statusLabel.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;"); // 🔶 Jaune
                break;
            case "CONFIRMED":
                statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;"); // ✅ Vert
                break;
            case "CANCELLED":
                statusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;"); // ❌ Rouge
                break;
            default:
                statusLabel.setStyle("-fx-text-fill: black; -fx-font-weight: bold;"); // ⚪ Par défaut
        }

        // Ajouter les éléments à la carte
        card.getChildren().addAll(totalLabel, statusLabel, eventDateLabel, addressLabel);

        return card;
    }


}
