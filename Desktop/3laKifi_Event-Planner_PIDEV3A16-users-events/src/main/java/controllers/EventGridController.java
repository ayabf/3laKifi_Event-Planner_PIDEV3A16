package controllers;

import Models.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import services.ServiceEvent;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class EventGridController {
    @FXML private FlowPane eventContainer;
    @FXML private TextField searchField;
    @FXML private StackPane noEventsPane;

    private final ServiceEvent eventService = new ServiceEvent();

    @FXML
    void initialize() {
        loadEvents();
        setupSearch();
    }

    private void loadEvents() {
        try {
            List<Event> events = eventService.getAll();
            eventContainer.getChildren().clear();
            
            if (events.isEmpty()) {
                showNoEvents(true);
                return;
            }
            
            showNoEvents(false);
            for (Event event : events) {
                addEventCard(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error loading events", e);
        }
    }

    private void addEventCard(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EventCard.fxml"));
            Node eventCard = loader.load();
            EventCardController controller = loader.getController();
            controller.setEvent(event);
            
            // Set preferred width for consistent card size
            eventCard.setStyle("-fx-pref-width: 300;");
            eventContainer.getChildren().add(eventCard);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error creating event card", e);
        }
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                List<Event> events = eventService.getAll();
                eventContainer.getChildren().clear();
                
                String searchText = newValue.toLowerCase();
                boolean hasResults = false;
                
                for (Event event : events) {
                    if (matchesSearch(event, searchText)) {
                        addEventCard(event);
                        hasResults = true;
                    }
                }
                
                showNoEvents(!hasResults);
            } catch (SQLException e) {
                e.printStackTrace();
                showError("Error searching events", e);
            }
        });
    }

    private boolean matchesSearch(Event event, String searchText) {
        return event.getName().toLowerCase().contains(searchText) ||
               event.getDescription().toLowerCase().contains(searchText) ||
               event.getCity().name().toLowerCase().contains(searchText);
    }

    @FXML
    void handleRefresh() {
        loadEvents();
    }

    private void showNoEvents(boolean show) {
        noEventsPane.setVisible(show);
        noEventsPane.setManaged(show);
        eventContainer.setVisible(!show);
        eventContainer.setManaged(!show);
    }

    private void showError(String message, Exception e) {
        // You can implement your error showing logic here
        System.err.println(message + ": " + e.getMessage());
    }
} 