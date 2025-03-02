package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.event.ActionEvent; // ✅ Correct pour JavaFX

import java.io.IOException;

public class WelcomeCController {

    @FXML
    private void goToClientDashboard(ActionEvent event) {
        openNewWindow("/cart.fxml", "Espace Client - Panier", event);
    }


    @FXML
    private void goToAdminDashboard(ActionEvent event) {
        openNewWindow("/AdminDashboardC.fxml", "Tableau de Bord Admin", event);
    }

    @FXML
    private void goBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sidebarAdmin.fxml"));
            Parent root = loader.load();

            // 🔹 Obtenir la scène actuelle et remplacer son contenu (PAS une nouvelle fenêtre)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));  // ✅ Remplacement direct de la scène
            stage.setTitle("Menu Admin");  // ✅ Mise à jour du titre
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("❌ Erreur lors du chargement du fichier FXML : /sidebarAdmin.fxml");
        }
    }




    @FXML
    private void goToWelcomePage(ActionEvent event) {
        openNewWindow("/welcomeC.fxml", "Bienvenue !", event);
    }


    private void openNewWindow(String fxmlPath, String title, javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // 🔹 Obtenir la scène actuelle
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(new Scene(root));  // ✅ Remplace la scène actuelle !
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("❌ Erreur lors du chargement du fichier FXML : " + fxmlPath);
        }
    }



}
