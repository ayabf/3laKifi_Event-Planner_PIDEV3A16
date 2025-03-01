package tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import services.OrderService;

import java.net.URI;

public class mainFx extends Application {


    double x,y = 0;

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/log.fxml"));

        stage.setScene(new Scene(root, 800, 500));
        stage.show();
    }

    private final OrderService orderService = new OrderService();


    private void handleUrl(String url) {
        try {
            URI uri = new URI(url);
            String query = uri.getQuery(); // orderId=123

            if (query != null && query.contains("orderId=")) {
                int orderId = Integer.parseInt(query.split("=")[1]);
                orderService.updateOrderStatus(orderId, "PAID");
                showAlert("Succès", "Paiement validé !");
            }
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de traiter le retour de Stripe.");
        }
    }

    private void showAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}