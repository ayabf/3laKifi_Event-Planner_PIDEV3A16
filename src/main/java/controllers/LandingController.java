package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import Models.Role;
import Models.session;
import services.UserService;
import java.io.IOException;
import java.net.URL;

public class LandingController {
    @FXML
    private Button logoutButton;

    @FXML
    private Button adminDashboardButton;

    @FXML
    private Button backButton;

    private Role userRole;
    private final UserService userService = new UserService();
    private static String previousPage = "/log.fxml"; // Default to login page

    @FXML
    public void initialize() {
        // Check role on initialization
        checkAndSetRole();
        // Set the correct profile page based on role
        updatePreviousPageBasedOnRole();
    }

    private void updatePreviousPageBasedOnRole() {
        if (userRole != null) {
            switch (userRole) {
                case ADMIN:
                    previousPage = "/ProfileAdmin.fxml";
                    break;
                case FOURNISSEUR:
                    previousPage = "/ProfileFournisseur.fxml";
                    break;
                case CLIENT:
                    previousPage = "/ProfileClient.fxml";
                    break;
                default:
                    previousPage = "/log.fxml";
            }
        }
    }

    private void checkAndSetRole() {
        String roleStr = userService.getUtilisateurRole(session.id_utilisateur);
        if (roleStr != null) {
            this.userRole = Role.valueOf(roleStr);
            updateUIBasedOnRole();
        } else {
            this.userRole = null;
            updateUIBasedOnRole();
        }
    }

    private void updateUIBasedOnRole() {
        if (adminDashboardButton != null) {
            boolean isAdmin = userRole != null && userRole == Role.ADMIN;
            adminDashboardButton.setVisible(isAdmin);
            adminDashboardButton.setManaged(isAdmin);
        }
    }

    public void initializeWithRole(Role role) {
        System.out.println("Initializing LandingController with role: " + role);
        this.userRole = role;
        updateUIBasedOnRole();
    }

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
        // Always check role before allowing access
        checkAndSetRole();

        if (userRole != Role.ADMIN) {
            showError("Access Denied", new SecurityException("You do not have permission to access the admin dashboard"));
            return;
        }

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
    void handleBack(ActionEvent event) {
        try {
            URL url = getClass().getResource(previousPage);
            if (url == null) {
                throw new IOException("Cannot find " + previousPage);
            }
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/global.css").toExternalForm());
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            showError("Error navigating back", e);
        }
    }

    @FXML
    void handleLogout() {
        try {
            URL loginUrl = getClass().getResource("/log.fxml");
            if (loginUrl == null) {
                throw new IOException("Cannot find log.fxml");
            }
            FXMLLoader loader = new FXMLLoader(loginUrl);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/global.css").toExternalForm());
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(scene);
            // Clear session
            session.id_utilisateur = 0;
        } catch (IOException e) {
            showError("Error during logout", e);
        }
    }

    public static void setPreviousPage(String page) {
        previousPage = page;
    }

    private void showError(String message, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
}