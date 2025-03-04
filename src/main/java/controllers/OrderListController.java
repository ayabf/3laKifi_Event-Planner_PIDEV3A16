package controllers;

import Models.Order;
import Models.Role;
import Models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.OrderService;
import utils.DataSource;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class OrderListController {

    @FXML
    private VBox orderListContainer; // Un VBox où on affiche les commandes

    private final OrderService orderService = new OrderService();
    @FXML
    private TextField searchField; // Ajoute cette variable si elle n'existe pas déjà
    private ObservableList<Order> allOrders = FXCollections.observableArrayList();

    @FXML
    private void searchOrders() {
        String keyword = searchField.getText().toLowerCase().trim();

        if (allOrders == null || allOrders.isEmpty()) {
            return;
        }

        ObservableList<Order> filteredOrders = allOrders.filtered(order ->
                order.getStatus().toLowerCase().contains(keyword) ||
                        order.getExactAddress().toLowerCase().contains(keyword) ||
                        (order.getEventDate() != null && order.getEventDate().toString().contains(keyword)) ||
                        String.valueOf(order.getTotalPrice()).contains(keyword)
        );

        displayOrders(filteredOrders);
    }
    @FXML
    private void openOrderList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/OrderList.fxml"));
            Parent root = loader.load();

            // Passer l'utilisateur connecté au contrôleur
            OrderListController controller = loader.getController();
            controller.setCurrentUser(currentUser); // currentUser est l'utilisateur connecté

            Stage stage = new Stage();
            stage.setTitle("Liste des Commandes");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Erreur lors de l'ouverture de la liste des commandes !");
        }
    }

    private void displayOrders(ObservableList<Order> orders) {
        orderListContainer.getChildren().clear();

        for (Order order : orders) {
            VBox orderCard = createOrderCard(order);
            orderListContainer.getChildren().add(orderCard);
        }
    }

    private User currentUser;
    @FXML
    public void initialize() {

        loadOrders();

        loadOrders(); // Charger les commandes
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        System.out.println("👤 Utilisateur connecté : " + currentUser.getUsername() + " | Rôle : " + currentUser.getRole());
    }


    public void loadOrders() {  // Modifier "private" en "public"
        try {
            List<Order> orders = orderService.getAll();
            orderListContainer.getChildren().clear();

            for (Order order : orders) {
                System.out.println("🔍 Vérification OrderList - Prix avant affichage: " + order.getTotalPrice());
                VBox orderCard = createOrderCard(order);
                orderListContainer.getChildren().add(orderCard);
            }
            System.out.println("🔄 Rafraîchissement des commandes effectué !");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Erreur lors du chargement des commandes !");
        }
        try {
            allOrders.clear();
            allOrders.addAll(orderService.getAll());
            displayOrders(allOrders);
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
        Label eventDateLabel = new Label("Date de l'événement: " +
                order.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
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
        Button editButton = new Button("Update");
        editButton.setOnAction(event -> openEditOrderPage(order));

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #93707E; -fx-text-fill: white;");
        cancelButton.setOnAction(event -> cancelOrder(order));

        if (currentUser != null && currentUser.getRole()== Role.ADMIN) {
            ChoiceBox<String> statusChoiceBox = new ChoiceBox<>();
            statusChoiceBox.getItems().addAll("PENDING", "CONFIRMED", "CANCELLED", "DELIVERED");
            statusChoiceBox.setValue(order.getStatus()); // Valeur actuelle du statut

            Button saveStatusButton = new Button("Save Status");
            saveStatusButton.setOnAction(event -> updateOrderStatus(order, statusChoiceBox.getValue()));

            card.getChildren().addAll(statusChoiceBox, saveStatusButton);
        }
        HBox buttonBox = new HBox(10, editButton, cancelButton); // Espacement de 10px entre les boutons
        buttonBox.setStyle("-fx-padding: 10px 0; -fx-alignment: center;");

        card.getChildren().addAll(totalLabel, statusLabel, eventDateLabel, addressLabel, buttonBox);

        return card;
    }
    private void openStatusEditDialog(Order order) {
        TextInputDialog dialog = new TextInputDialog(order.getStatus());
        dialog.setTitle("Modification du Statut");
        dialog.setHeaderText("Modifier le statut de la commande #" + order.getOrderId());
        dialog.setContentText("Nouveau statut:");

        // ⚠️ Vérification si l'admin a bien entré un statut valide
        dialog.showAndWait().ifPresent(newStatus -> {
            if (!newStatus.equalsIgnoreCase("PENDING") && !newStatus.equalsIgnoreCase("CONFIRMED") &&
                    !newStatus.equalsIgnoreCase("CANCELLED")) {
                showAlert("Erreur", "Statut invalide ! Utilisez : PENDING, CONFIRMED ou CANCELLED.");
                return;
            }

            try {
                orderService.modifierStatutCommande(order.getOrderId(), newStatus, currentUser);
                showAlert("Succès", "Statut mis à jour !");
                loadOrders(); // Rafraîchir la liste
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Impossible de modifier le statut.");
            }
        });
    }
    private void updateOrderStatus(Order order, String newStatus) {
        try {
            orderService.updateStatus(order.getOrderId(), newStatus);
            showAlert("Succès", "Le statut a été mis à jour avec succès !");
            loadOrders(); // Rafraîchir la liste des commandes
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de modifier le statut.");
        }
    }

    private void openEditOrderPage(Order order) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/OrderEdit.fxml"));
            Parent root = loader.load();

            // Obtenir le contrôleur et lui passer l'ordre + le parent
            OrderEditController controller = loader.getController();
            controller.setOrder(order);
            controller.setParentController(this);  // Passer l'instance actuelle de OrderListController

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
    public void refreshOrders() {
        orderListContainer.getChildren().clear(); // Vider l'affichage
        loadOrders(); // Recharger la liste depuis la base
        System.out.println("🔄 Rafraîchissement de l'affichage des commandes !");
    }


    public void updateOrderDisplay(Order updatedOrder) {
        for (Node node : orderListContainer.getChildren()) {
            if (node instanceof VBox) {
                VBox orderCard = (VBox) node;
                Label eventDateLabel = (Label) orderCard.getChildren().get(2);
                Label addressLabel = (Label) orderCard.getChildren().get(3);

                // 🔍 Vérification de l'ID
                if (eventDateLabel.getText().contains(updatedOrder.getOrderId() + "")) {
                    System.out.println("🔄 Mise à jour visuelle de la commande: " + updatedOrder.getOrderId());

                    // ✅ Mise à jour des labels
                    eventDateLabel.setText("Date de l'événement: " +
                            updatedOrder.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                    addressLabel.setText("Adresse: " + updatedOrder.getExactAddress());

                    return;
                }
            }
        }
        System.out.println("⚠ Commande non trouvée dans l'affichage !");
    }

    @FXML
    private void reloadOrders() {
        loadOrders();  // Recharge la liste des commandes
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) orderListContainer.getScene().getWindow();
        stage.close(); // Ferme la fenêtre
    }

}