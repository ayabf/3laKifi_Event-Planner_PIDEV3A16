package controllers;

import Models.Order;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
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

        Label totalLabel = new Label("Total: $" + String.format("%.2f", order.getTotalPrice()));
        Label statusLabel = new Label("Statut: " + order.getStatus());
        Label eventDateLabel = new Label("Date de l'événement: " + order.getEventDate().toString());
        Label addressLabel = new Label("Adresse: " + order.getExactAddress());

        // 🎨 Appliquer la couleur du statut
        switch (order.getStatus().toUpperCase()) {
            case "PENDING":
                statusLabel.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                break;
            case "CONFIRMED":
                statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                break;
            case "CANCELLED":
                statusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                break;
            default:
                statusLabel.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
        }

        // 🔄 Bouton de modification
        Button editButton = new Button("Modifier");
        editButton.setOnAction(event -> openEditOrderPage(order));
        Button cancelButton = new Button("Annuler");
        cancelButton.setStyle("-fx-background-color: red; -fx-text-fill: white;"); // Style rouge
        cancelButton.setOnAction(event -> cancelOrder(order));

        card.getChildren().addAll(totalLabel, statusLabel, eventDateLabel, addressLabel, editButton, cancelButton);

        return card;
    }
    private void openEditOrderPage(Order order) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/OrderEdit.fxml"));
            Parent root = loader.load();

            // Passer la commande sélectionnée au contrôleur
            OrderEditController controller = loader.getController();
            controller.setOrder(order);

            Stage stage = new Stage();
            stage.setTitle("Modifier Commande");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la page de modification.");
        }
    }

    private void cancelOrder(Order order) {
        try {
            orderService.annulerCommande(order.getOrderId());
            showAlert("Commande annulée", "Votre commande a été annulée.");
            loadOrders(); // Rafraîchir la liste
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'annuler la commande.");
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
