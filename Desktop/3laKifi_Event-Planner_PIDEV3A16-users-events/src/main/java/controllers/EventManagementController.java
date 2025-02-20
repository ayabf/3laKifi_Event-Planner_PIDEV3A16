package controllers;

import Models.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import services.ServiceEvent;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EventManagementController {
    @FXML
    private FlowPane eventsContainer;
    @FXML
    private TextField searchField;

    private final ServiceEvent eventService = new ServiceEvent();

    @FXML
    void initialize() {
        loadEventData();
        setupSearchFunctionality();
    }

    private void loadEventData() {
        try {
            List<Event> events = eventService.getAll();
            displayEvents(events);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les événements.");
            e.printStackTrace();
        }
    }

    private void displayEvents(List<Event> events) {
        eventsContainer.getChildren().clear();
        for (Event event : events) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/EventCard.fxml"));
                Parent eventCard = loader.load();
                
                EventCardController controller = loader.getController();
                controller.setEvent(event);
                
                // Add event handlers for edit and delete
                controller.setOnEdit(e -> updateEvent(event));
                controller.setOnDelete(e -> deleteEvent(event));
                
                eventsContainer.getChildren().add(eventCard);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setupSearchFunctionality() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                List<Event> allEvents = eventService.getAll();
                List<Event> filteredEvents = allEvents.stream()
                    .filter(event -> 
                        event.getName().toLowerCase().contains(newValue.toLowerCase()) ||
                        event.getDescription().toLowerCase().contains(newValue.toLowerCase()) ||
                        event.getCity().name().toLowerCase().contains(newValue.toLowerCase()))
                    .collect(Collectors.toList());
                displayEvents(filteredEvents);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    void addEvent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddEvent.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter un événement");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            // Refresh the view after adding
            loadEventData();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre d'ajout.");
            e.printStackTrace();
        }
    }

    private void updateEvent(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateEvent.fxml"));
            Parent root = loader.load();
            
            UpdateEventController controller = loader.getController();
            controller.setEvent(event);
            
            Stage stage = new Stage();
            stage.setTitle("Modifier l'événement");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            // Refresh the view after updating
            loadEventData();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre de modification.");
            e.printStackTrace();
        }
    }

    private void deleteEvent(Event event) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation");
        confirmDialog.setHeaderText("Supprimer l'événement");
        confirmDialog.setContentText("Êtes-vous sûr de vouloir supprimer cet événement ?");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                eventService.supprimer(event.getId_event());
                loadEventData();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "L'événement a été supprimé avec succès.");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer l'événement.");
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