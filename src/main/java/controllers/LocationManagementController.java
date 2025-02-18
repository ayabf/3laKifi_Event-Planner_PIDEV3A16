package controllers;

import Models.Location;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import services.ServiceLocation;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LocationManagementController {
    @FXML
    private FlowPane locationsContainer;
    @FXML
    private TextField searchField;

    private final ServiceLocation locationService = new ServiceLocation();

    @FXML
    void initialize() {
        loadLocationData();
        setupSearchFunctionality();
    }

    private void loadLocationData() {
        try {
            List<Location> locations = locationService.getAll();
            displayLocations(locations);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les locations.");
            e.printStackTrace();
        }
    }

    private void displayLocations(List<Location> locations) {
        locationsContainer.getChildren().clear();
        for (Location location : locations) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/LocationCard.fxml"));
                Parent locationCard = loader.load();
                
                LocationCardController controller = loader.getController();
                controller.setLocation(location);
                
                // Add event handlers for edit and delete
                controller.setOnEdit(e -> updateLocation(location));
                controller.setOnDelete(e -> deleteLocation(location));
                
                locationsContainer.getChildren().add(locationCard);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setupSearchFunctionality() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                List<Location> allLocations = locationService.getAll();
                List<Location> filteredLocations = allLocations.stream()
                    .filter(location -> 
                        location.getName().toLowerCase().contains(newValue.toLowerCase()) ||
                        location.getVille().name().toLowerCase().contains(newValue.toLowerCase()) ||
                        location.getDimension().toLowerCase().contains(newValue.toLowerCase()))
                    .collect(Collectors.toList());
                displayLocations(filteredLocations);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    void addLocation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddLocation.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter une location");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            // Refresh the view after adding
            loadLocationData();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre d'ajout.");
            e.printStackTrace();
        }
    }

    private void updateLocation(Location location) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateLocation.fxml"));
            Parent root = loader.load();
            
            UpdateLocationController controller = loader.getController();
            controller.setLocation(location);
            
            Stage stage = new Stage();
            stage.setTitle("Modifier la location");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            // Refresh the view after updating
            loadLocationData();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre de modification.");
            e.printStackTrace();
        }
    }

    private void deleteLocation(Location location) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation");
        confirmDialog.setHeaderText("Supprimer la location");
        confirmDialog.setContentText("Êtes-vous sûr de vouloir supprimer cette location ?");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                locationService.supprimer(location.getId_location());
                loadLocationData();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "La location a été supprimée avec succès.");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer la location.");
                e.printStackTrace();
            }
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 