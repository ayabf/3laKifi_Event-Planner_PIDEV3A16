package controllers;

import Models.Order;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import services.OrderService;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminDashboardCController {

    @FXML
    private Label totalOrdersLabel;

    @FXML
    private Label pendingOrdersLabel;

    @FXML
    private Label confirmedOrdersLabel;

    @FXML
    private BarChart<String, Number> ordersByStatusChart;

    private final OrderService orderService = new OrderService();
    @FXML
    private Label sitePerformanceLabel;

    @FXML
    private Label orderAcceptanceTimeLabel;

    @FXML
    private Label siteRevenuePercentageLabel;

    @FXML
    public void initialize() {
        loadStatistics();
    }

    private void loadStatistics() {
        try {
            // Récupérer toutes les commandes
            List<Order> orders = orderService.getAll();

            // Calculer les statistiques
            int totalOrders = orders.size();
            double totalRevenue = orders.stream().mapToDouble(Order::getTotalPrice).sum();
            long pendingOrders = orders.stream().filter(o -> "PENDING".equalsIgnoreCase(o.getStatus())).count();
            long confirmedOrders = orders.stream().filter(o -> "CONFIRMED".equalsIgnoreCase(o.getStatus())).count();
            double sitePerformance = calculateSitePerformance();
            double averageAcceptanceTime = calculateAverageAcceptanceTime();
            double revenuePercentage = calculateRevenuePercentage(totalRevenue);

            // Mettre à jour les labels
            totalOrdersLabel.setText(String.valueOf(totalOrders));
            pendingOrdersLabel.setText(String.valueOf(pendingOrders));
            confirmedOrdersLabel.setText(String.valueOf(confirmedOrders));
            sitePerformanceLabel.setText(String.format("%.1f%%", sitePerformance));
            orderAcceptanceTimeLabel.setText(String.format("%.1f min", averageAcceptanceTime));
            siteRevenuePercentageLabel.setText(String.format("%.1f%%", revenuePercentage));

            // Mettre à jour le graphique
            updateOrdersByStatusChart(orders);

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Erreur lors du chargement des statistiques !");
        }
    }

    private double calculateSitePerformance() {

        return Math.random() * 10 + 90; // Simule une performance entre 90% et 100%
    }

    private double calculateAverageAcceptanceTime() {

        return Math.random() * 5 + 2; // Simule un temps entre 2 et 7 minutes
    }

    private double calculateRevenuePercentage(double totalRevenue) {

        double goalRevenue = 10000.0;
        return (totalRevenue / goalRevenue) * 100;
    }

    private void updateOrdersByStatusChart(List<Order> orders) {
        // Compter le nombre de commandes par statut
        Map<String, Integer> statusCounts = new HashMap<>();
        for (Order order : orders) {
            String status = order.getStatus().toUpperCase();
            statusCounts.put(status, statusCounts.getOrDefault(status, 0) + 1);
        }

        // Créer une série de données pour le graphique
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Commandes par Statut");

        for (Map.Entry<String, Integer> entry : statusCounts.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        // Mettre à jour le graphique
        ordersByStatusChart.getData().clear();
        ordersByStatusChart.getData().add(series);
    }

    public void handleLogout(javafx.event.ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Login.fxml"));
            Scene loginScene = new Scene(loader.load());

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(loginScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("❌ Erreur lors du chargement de la page de connexion vers Order Management!");
        }
    }

    public void goBackToWelcome(javafx.event.ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/welcomeC.fxml"));
            Parent root = loader.load();

            // Récupérer la scène actuelle et remplacer son contenu
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("❌ Erreur lors du retour à la page d'accueil !");
        }
    }
    @FXML
    private void openPromoAdmin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PromoAdmin.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Gestion des Codes Promo");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}