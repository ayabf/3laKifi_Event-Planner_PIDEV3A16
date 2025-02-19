package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.net.URL;

public class LandingController {
    @FXML
    private Button logoutButton;

    @FXML
    void handleClientDashboard(ActionEvent event) {
        try {
            URL url = getClass().getResource("/ClientDashboard.fxml");
            if (url == null) {
                throw new IOException("Cannot find ClientDashboard.fxml");
            }
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/global.css").toExternalForm());
            Stage stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading client dashboard", e);
        }
    }

    @FXML
    void handleAdminDashboard(ActionEvent event) {
        try {
            URL url = getClass().getResource("/AdminDashboard.fxml");
            if (url == null) {
                throw new IOException("Cannot find AdminDashboard.fxml");
            }
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/global.css").toExternalForm());
            Stage stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading admin dashboard", e);
        }
    }

    @FXML
    void handleLogout(ActionEvent event) {
        try {
            URL url = getClass().getResource("/Login.fxml");
            if (url == null) {
                throw new IOException("Cannot find Login.fxml");
            }
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/global.css").toExternalForm());
            Stage stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error logging out", e);
        }
    }

    private void showError(String message, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
} 