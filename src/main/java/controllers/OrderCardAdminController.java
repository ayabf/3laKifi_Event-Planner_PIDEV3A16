package controllers;

import Models.Order;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import services.OrderService;

public class OrderCardAdminController {

    @FXML private Label orderIdLabel, userIdLabel, orderDateLabel, totalLabel;
    @FXML private ComboBox<String> statusComboBox;

    private final OrderService orderService = new OrderService();
    private Order order;

    public void setOrder(Order o) {
        this.order = o;

        orderIdLabel.setText("Order #" + o.getOrderId());
        userIdLabel.setText("User ID: " + o.getUserId());
        orderDateLabel.setText("Ordered at: " + o.getOrderedAt().toString());
        totalLabel.setText("Total: " + o.getTotalPrice() + " TND");

        statusComboBox.getItems().addAll("Pending", "Confirmed", "Delivered", "Cancelled");
        statusComboBox.setValue(o.getStatus());
    }

    @FXML
    public void handleUpdateStatus() {
        String selectedStatus = statusComboBox.getValue();
        if (!selectedStatus.equals(order.getStatus())) {
            try {
                orderService.updateOrderStatus(order.getOrderId(), selectedStatus);
                order.setStatus(selectedStatus);
                System.out.println("✅ Statut mis à jour : " + selectedStatus);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("❌ Erreur de mise à jour du statut !");
            }
        }
    }
}
