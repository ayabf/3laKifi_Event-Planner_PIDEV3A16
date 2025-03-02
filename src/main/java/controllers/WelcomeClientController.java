package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;

public class WelcomeClientController {

    @FXML
    private void goToCart(ActionEvent event) {
        openNewScene("/cart.fxml", "Client Cart", event);
    }

    @FXML
    private void goBack(ActionEvent event) {
        openNewScene("/sidebarClient.fxml", "Client Menu", event);
    }

    private void openNewScene(String fxmlPath, String title, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Remplacer la scène actuelle
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("❌ Error loading FXML: " + fxmlPath);
        }
    }
}
