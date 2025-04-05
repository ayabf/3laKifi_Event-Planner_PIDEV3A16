package controllers;

import com.google.zxing.WriterException;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import services.OrderService;
import Models.Order;
import utils.QRCodeGenerator;

import java.io.File;
import java.io.IOException;

import static spark.Spark.*;

public class PaymentController {
    private static final OrderService orderService = new OrderService();

    public static void main(String[] args) {
        port(4242);
        // Route de test
        get("/test", (req, res) -> "Serveur Spark est bien démarré !");

        System.out.println("✅ Serveur Spark démarré sur http://localhost:4242/test");
        // ✅ Remplacez par votre clé API secrète Stripe
        Stripe.apiKey = "sk_test_51QwR8RJig9dgGMXA88GSrTkZMyTs3eDJnxmagMJK14JZZSHHfXqi46pWZ3lJS6CU5cRyjDS3EsSFgF3yV4yhHONv004ryM6e41";

        post("/create-checkout-session", (request, response) -> {
            String orderIdParam = request.queryParams("orderId");
            if (orderIdParam == null || orderIdParam.isEmpty()) {
                return "Erreur: orderId manquant.";
            }

            int orderId = Integer.parseInt(orderIdParam);
            Order order = orderService.getOne(orderId);

            if (order == null || !"CONFIRMED".equalsIgnoreCase(order.getStatus())) {
                return "Erreur: La commande doit être confirmée avant paiement.";
            }

            String YOUR_DOMAIN = "http://localhost:4242";

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(YOUR_DOMAIN + "/success.html")
                    .setCancelUrl(YOUR_DOMAIN + "/cancel.html")
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("eur")
                                                    .setUnitAmount((long) (order.getTotalPrice() * 100)) // Convertir en centimes
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName("Commande #" + orderId)
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();

            Session session = Session.create(params);
            response.redirect(session.getUrl(), 303);
            return "";
        });  // ✅ Route après paiement réussi (génération du QR Code pour la facture)
        get("/payment-success", (request, response) -> {
            String orderIdParam = request.queryParams("orderId");

            if (orderIdParam == null || orderIdParam.isEmpty()) {
                return "Erreur: orderId manquant.";
            }

            int orderId = Integer.parseInt(orderIdParam);
            Order order = orderService.getOne(orderId);

            if (order == null) {
                return "Erreur: Commande introuvable.";
            }

            // ✅ Vérifier et créer le dossier qrcodes
            File qrDir = new File("qrcodes");
            if (!qrDir.exists()) {
                boolean created = qrDir.mkdir();
                if (!created) {
                    return "Erreur: Impossible de créer le dossier des QR Codes.";
                }
            }

            // ✅ Génération du QR Code pour la facture
            String invoiceUrl = "http://localhost:4242/invoice?orderId=" + orderId;
            String qrFilePath = "qrcodes/invoice_" + orderId + ".png";

            response.redirect(invoiceUrl);
            return "";
        });

        // ✅ Route pour afficher la facture
        get("/invoice", (request, response) -> {
            String orderIdParam = request.queryParams("orderId");
            if (orderIdParam == null || orderIdParam.isEmpty()) {
                return "Erreur: orderId manquant.";
            }

            int orderId = Integer.parseInt(orderIdParam);
            Order order = orderService.getOne(orderId);
            if (order == null) {
                return "Erreur: Commande introuvable.";
            }

            String qrCodePath = "/qrcodes/invoice_" + orderId + ".png";

            return "<html><head><title>Facture #" + orderId + "</title></head><body>" +
                    "<h1>Facture pour la commande #" + orderId + "</h1>" +
                    "<p>Total payé: <strong>$" + order.getTotalPrice() + "</strong></p>" +
                    "<p>Adresse: " + order.getExactAddress() + "</p>" +
                    "<p>Date de l'événement: " + order.getEventDate() + "</p>" +
                    "<img src='" + qrCodePath + "' alt='QR Code de la facture'/>" +
                    "<p><a href='" + qrCodePath + "' download>Télécharger le QR Code</a></p>" +
                    "</body></html>";
        });
    }
}