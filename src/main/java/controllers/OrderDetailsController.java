package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import services.OrderService;
import Models.Order;

import java.io.File;
import java.sql.SQLException;

public class OrderDetailsController {

    @FXML private Label orderIdLabel;
    @FXML private Label totalPriceLabel;
    @FXML private Label orderStatusLabel;
    @FXML private Label eventDateLabel;
    @FXML private ImageView qrCodeImageView;

    private final OrderService orderService = new OrderService();

    public void setOrderDetails(int orderId) {
        try {
            Order order = orderService.getOne(orderId);
            if (order == null) {
                System.err.println("❌ Erreur : Commande introuvable !");
                return;
            }

            // ✅ Affichage des détails
            orderIdLabel.setText("Commande #" + orderId);
            totalPriceLabel.setText("Total : $" + order.getTotalPrice());
            orderStatusLabel.setText("Statut : " + order.getStatus());
            eventDateLabel.setText("Date de l'événement : " + order.getEventDate().toString());

            String qrFilePath = "qrcodes/invoice_" + orderId + ".png";
            File qrFile = new File(qrFilePath);

            if (qrFile.exists()) {
                System.out.println("📸 Chargement du QR Code pour la commande #" + orderId);
                Image qrImage = new Image(qrFile.toURI().toString());
                qrCodeImageView.setImage(qrImage);
            } else {
                System.err.println("⚠ QR Code introuvable pour la commande : " + orderId);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL lors de la récupération de la commande : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
