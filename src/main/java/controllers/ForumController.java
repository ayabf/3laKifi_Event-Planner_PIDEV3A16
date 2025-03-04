package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

public class ForumController {

    @FXML
    private Button postButton;

    @FXML
    private Button reportsButton;

    @FXML
    private Button statistiquesButton;

    @FXML
    private void handlePostButton() {
        showAlert("Post", "Vous avez cliqué sur le bouton Post.");
    }

    @FXML
    private void handleReportsButton() {
        showAlert("Reports", "Vous avez cliqué sur le bouton Reports.");
    }

    @FXML
    private void handleStatistiquesButton() {
        showAlert("Statistiques", "Vous avez cliqué sur le bouton Statistiques.");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}