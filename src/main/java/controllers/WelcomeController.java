package controllers;

import Models.session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class WelcomeController {

    @FXML
    private void goToClientSpace() {
        openWindow("/CartView.fxml", "Espace Client - Panier");
    }

    @FXML
    private void goToAdminDashboard() {
        if ("ADMIN".equalsIgnoreCase(session.role_utilisateur)) {
            openWindow("/AdminDashboard.fxml", "Tableau de Bord Admin");
        } else {
            System.out.println("⚠ Accès refusé : Vous n'êtes pas admin !");
        }
    }

    private void openWindow(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();

            // Fermer la fenêtre actuelle
            Stage currentStage = (Stage) root.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
