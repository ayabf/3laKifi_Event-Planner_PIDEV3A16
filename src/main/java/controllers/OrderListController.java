package controllers;

import Models.Order;
import Models.session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.OrderService;
import services.StripeService;

import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class OrderListController {

    @FXML private VBox orderListContainer;
    @FXML private TextField searchField;

    private final OrderService orderService = new OrderService();
    private ObservableList<Order> allOrders = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        System.out.println("🔍 Vérification du rôle utilisateur après connexion : " + session.role_utilisateur);

        loadOrders();
        searchField.setOnKeyReleased(event -> searchOrders());
    }








    public void loadOrders() {
        try {
            int userId = session.id_utilisateur;
            String userRole = session.role_utilisateur;

            List<Order> orders;
            if ("ADMIN".equalsIgnoreCase(userRole)) {
                orders = orderService.getAll();
            } else {
                orders = orderService.getAllByUser(userId);
            }

            allOrders.setAll(orders);  // Updates the ObservableList
            displayOrders(allOrders);  // Re-renders the orders

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Erreur lors du chargement des commandes !");
        }
    }

    public void updateOrderDisplay(Order updatedOrder) {
        for (Node node : orderListContainer.getChildren()) {
            if (node instanceof VBox) {
                VBox orderCard = (VBox) node;
                List<Node> children = orderCard.getChildren();

                // Vérifie si l'ID de la commande est affiché dans ce `VBox`
                if (!children.isEmpty() && children.get(0) instanceof Label) {
                    Label orderIdLabel = (Label) children.get(0);

                    // Vérifie si c'est la commande concernée
                    if (orderIdLabel.getText().contains("Commande N°" + updatedOrder.getOrderId())) {
                        System.out.println("🔄 Mise à jour de l'affichage pour la commande: " + updatedOrder.getOrderId());

                        // Mettre à jour les autres labels
                        ((Label) children.get(1)).setText("Statut : " + updatedOrder.getStatus());
                        ((Label) children.get(2)).setText("Total : $" + updatedOrder.getTotalPrice());
                        ((Label) children.get(3)).setText("Adresse : " + updatedOrder.getExactAddress());

                        return; // Sortir de la boucle dès que la commande est mise à jour
                    }
                }
            }
        }
        System.out.println("⚠ Commande non trouvée dans l'affichage !");
    }



    @FXML
    private void searchOrders() {
        String keyword = searchField.getText().toLowerCase().trim();

        if (keyword.isEmpty()) {
            displayOrders(allOrders); // Affiche toutes les commandes si aucun mot-clé n'est saisi
            return;
        }

        try {
            int userId = session.id_utilisateur;
            List<Order> filteredOrders = orderService.searchOrdersByUser(userId, keyword);
            displayOrders(FXCollections.observableArrayList(filteredOrders));
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de récupérer les commandes filtrées.");
        }
    }



    @FXML
    private void reloadOrders() {
        loadOrders();
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) orderListContainer.getScene().getWindow();
        stage.close();
    }


    private void displayOrders(ObservableList<Order> orders) {
        orderListContainer.getChildren().clear();
        for (Order order : orders) {
            VBox orderCard = createOrderCard(order);
            orderListContainer.getChildren().add(orderCard);
        }
    }

    private VBox createOrderCard(Order order) {
        VBox card = new VBox(10);
        card.setStyle("-fx-border-color: black; -fx-padding: 10; -fx-spacing: 5; -fx-background-color: #f9f9f9;");

        Label statusLabel = new Label("Statut : " + order.getStatus());
        Label totalLabel = new Label("Total : $" + order.getTotalPrice());
        Label addressLabel = new Label("Adresse : " + order.getExactAddress());
        Label eventDateLabel = new Label("Date : " + (order.getEventDate() != null ?
                order.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "Non définie"));

        // 🎨 Appliquer une couleur selon le statut
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

        // 📌 Ajouter les boutons dynamiquement
        HBox buttonBox = new HBox(10);
        Button cancelButton = new Button("Annuler");
        cancelButton.setOnAction(event -> cancelOrder(order));

        Button editButton = new Button("Modifier");
        editButton.setOnAction(event -> openEditOrderPage(order));

        buttonBox.getChildren().addAll(cancelButton, editButton);

        // ✅ Ajouter un bouton "Modifier Statut" pour les ADMIN
        if ("ADMIN".equalsIgnoreCase(session.role_utilisateur)) {
            Button updateStatusButton = new Button("Modifier Statut");
            updateStatusButton.setOnAction(event -> openStatusUpdateDialog(order));
            buttonBox.getChildren().add(updateStatusButton);
        }

        // ✅ Ajouter un bouton "Payer" uniquement si la commande est CONFIRMED
        if ("CONFIRMED".equalsIgnoreCase(order.getStatus())) {
            Button payButton = new Button("Payer");
            payButton.setOnAction(event -> openStripeCheckout(order));
            buttonBox.getChildren().add(payButton);
        }

        card.getChildren().addAll(totalLabel, statusLabel, eventDateLabel, addressLabel, buttonBox);

        return card;
    }



    private void openStripeCheckout(Order order) {
        StripeService stripeService = new StripeService();
        String checkoutUrl = stripeService.createCheckoutSession(order);

        if (checkoutUrl != null) {
            try {
                Desktop.getDesktop().browse(new java.net.URI(checkoutUrl));
            } catch (Exception e) {
                showAlert("Erreur", "Impossible d'ouvrir la page de paiement.");
                e.printStackTrace();
            }
        } else {
            showAlert("Erreur", "Échec de la création de la session Stripe.");
        }
    }

    private void openStatusUpdateDialog(Order order) {
        // ✅ Création d'une boîte de dialogue
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Mettre à jour le statut");
        dialog.setHeaderText("Modifier le statut de la commande " + order.getOrderId());

        // 📌 Liste des statuts possibles
        ChoiceBox<String> statusChoiceBox = new ChoiceBox<>();
        statusChoiceBox.getItems().addAll("PENDING", "CONFIRMED", "CANCELLED", "DELIVERED");
        statusChoiceBox.setValue(order.getStatus()); // Statut actuel sélectionné

        VBox content = new VBox(10);
        content.getChildren().addAll(new Label("Sélectionnez un nouveau statut :"), statusChoiceBox);
        dialog.getDialogPane().setContent(content);

        // ✅ Ajouter les boutons OK et Annuler
        ButtonType updateButtonType = new ButtonType("Mettre à jour", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == updateButtonType) {
                return statusChoiceBox.getValue(); // Retourne le statut sélectionné
            }
            return null;
        });

        dialog.showAndWait().ifPresent(newStatus -> {
            updateOrderStatus(order, newStatus);
        });
    }



    private void updateOrderStatus(Order order, String newStatus) {
        try {
            // Update the order status in the database
            orderService.updateOrderStatus(order.getOrderId(), newStatus);

            // Show a success alert
            showAlert("Succès", "Statut mis à jour avec succès !");

            // Reload orders to ensure the updated data is fetched
            loadOrders();

            // Explicitly re-render the orders to reflect the new status in the UI
            displayOrders(allOrders);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de mettre à jour le statut.");
        }
    }






    private void cancelOrder(Order order) {
        try {
            orderService.annulerCommande(order.getOrderId());
            showAlert("Commande annulée", "Votre commande a été annulée.");
            loadOrders();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'annuler la commande.");
        }
    }

    private void openEditOrderPage(Order order) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/OrderEdit.fxml"));
            Parent root = loader.load();

            OrderEditController controller = loader.getController();
            controller.setOrder(order);
            controller.setParentController(this);

            Stage stage = new Stage();
            stage.setTitle("Modifier Commande");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la page de modification.");
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
    private void openAdminDashboardC() {
        try {
            // Charger le fichier FXML du tableau de bord
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminDashboardC.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène
            Stage stage = new Stage();
            stage.setTitle("Tableau de Bord Administrateur");
            stage.setScene(new Scene(root));

            // Afficher la fenêtre
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir le tableau de bord.");
        }
    }

}
