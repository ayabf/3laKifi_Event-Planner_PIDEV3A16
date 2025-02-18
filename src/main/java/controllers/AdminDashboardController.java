package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import services.ServiceEvent;
import services.ServiceBooking;
import services.ServiceLocation;
import Models.Event;
import Models.Location;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AdminDashboardController {
    @FXML private StackPane contentArea;
    @FXML private Label totalEventsLabel;
    @FXML private Label totalBookingsLabel;
    @FXML private Label totalLocationsLabel;
    
    // Events Table
    @FXML private TableView<Event> eventsTable;
    @FXML private TableColumn<Event, String> eventNameColumn;
    @FXML private TableColumn<Event, String> eventDateColumn;
    @FXML private TableColumn<Event, String> eventCityColumn;
    @FXML private TableColumn<Event, Integer> eventCapacityColumn;
    @FXML private TableColumn<Event, String> eventStatusColumn;
    
    // Locations Table
    @FXML private TableView<Location> locationsTable;
    @FXML private TableColumn<Location, String> locationNameColumn;
    @FXML private TableColumn<Location, String> locationCityColumn;
    @FXML private TableColumn<Location, Integer> locationCapacityColumn;
    @FXML private TableColumn<Location, Double> locationPriceColumn;
    @FXML private TableColumn<Location, String> locationStatusColumn;

    private final ServiceEvent eventService = new ServiceEvent();
    private final ServiceBooking bookingService = new ServiceBooking();
    private final ServiceLocation locationService = new ServiceLocation();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    void initialize() {
        initializeEventTable();
        initializeLocationTable();
        updateDashboardStats();
        loadTableData();
    }
    
    private void initializeEventTable() {
        eventNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        eventDateColumn.setCellValueFactory(cellData -> {
            LocalDateTime date = cellData.getValue().getStart_date();
            return javafx.beans.binding.Bindings.createStringBinding(
                () -> date.format(dateFormatter)
            );
        });
        eventCityColumn.setCellValueFactory(cellData -> 
            javafx.beans.binding.Bindings.createStringBinding(
                () -> cellData.getValue().getCity().name()
            ));
        eventCapacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        eventStatusColumn.setCellValueFactory(cellData -> {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startDate = cellData.getValue().getStart_date();
            LocalDateTime endDate = cellData.getValue().getEnd_date();
            return javafx.beans.binding.Bindings.createStringBinding(() -> {
                if (now.isBefore(startDate)) return "Upcoming";
                if (now.isAfter(endDate)) return "Completed";
                return "In Progress";
            });
        });
    }
    
    private void initializeLocationTable() {
        locationNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        locationCityColumn.setCellValueFactory(cellData ->
            javafx.beans.binding.Bindings.createStringBinding(
                () -> cellData.getValue().getVille().name()
            ));
        locationCapacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        locationPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        locationStatusColumn.setCellValueFactory(cellData -> {
            // You might want to check bookings to determine if the location is currently booked
            return javafx.beans.binding.Bindings.createStringBinding(
                () -> "Available" // Default to available, you can implement actual availability check
            );
        });
    }
    
    private void loadTableData() {
        try {
            // Load Events
            ObservableList<Event> events = FXCollections.observableArrayList(eventService.getAll());
            eventsTable.setItems(events);
            
            // Load Locations
            ObservableList<Location> locations = FXCollections.observableArrayList(locationService.getAll());
            locationsTable.setItems(locations);
        } catch (SQLException e) {
            showError("Error loading table data", e);
        }
    }
    
    private void updateDashboardStats() {
        try {
            int totalEvents = eventService.getTotalCount();
            int totalBookings = bookingService.getTotalCount();
            int totalLocations = locationService.getTotalCount();

            totalEventsLabel.setText(String.valueOf(totalEvents));
            totalBookingsLabel.setText(String.valueOf(totalBookings));
            totalLocationsLabel.setText(String.valueOf(totalLocations));
        } catch (SQLException e) {
            showError("Error updating dashboard statistics", e);
        }
    }

    @FXML
    void handleHome() {
        updateDashboardStats();
        loadTableData();
    }

    private void navigateToView(String fxmlPath, Node sourceNode) {
        try {
            URL url = getClass().getResource(fxmlPath);
            if (url == null) {
                throw new IOException("Cannot find " + fxmlPath);
            }
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/global.css").toExternalForm());
            Stage stage = (Stage) sourceNode.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showError("Error loading view", e);
        }
    }

    @FXML
    void handleBackOffice(ActionEvent event) {
        navigateToView("/Landing.fxml", (Node) event.getSource());
    }

    @FXML
    void handleViewAllEvents() {
        loadView("/EventList.fxml", "Event List");
    }

    @FXML
    void handleManageBookings() {
        loadView("/BookingManagement.fxml", "Booking Management");
    }

    @FXML
    void handleViewAllLocations() {
        loadView("/LocationList.fxml", "Location List");
    }

    @FXML
    void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();
            Stage currentStage = (Stage) contentArea.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/global.css").toExternalForm());
            currentStage.setScene(scene);
            currentStage.show();
        } catch (IOException e) {
            showError("Error loading login view", e);
        }
    }

    private void loadView(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader();
            URL resource = getClass().getResource(fxmlPath);
            if (resource == null) {
                throw new IOException("Cannot find " + fxmlPath);
            }
            loader.setLocation(resource);
            
            Parent view = loader.load();
            if (view == null) {
                throw new IOException("Failed to load view: " + fxmlPath);
            }
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
            
        } catch (IOException e) {
            showError("Error loading view: " + title, e);
        }
    }

    private void showError(String message, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
        e.printStackTrace();
    }
} 