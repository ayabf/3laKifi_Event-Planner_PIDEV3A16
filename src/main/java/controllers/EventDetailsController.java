package controllers;

import Models.Event;
import Models.Location;
import Models.Booking;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import services.ServiceLocation;
import services.ServiceBooking;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

public class EventDetailsController {
    @FXML
    private ImageView eventImage;
    @FXML
    private Label eventName;
    @FXML
    private TextArea eventDescription;
    @FXML
    private Label eventCity;
    @FXML
    private Label eventCapacity;
    @FXML
    private Label eventStartDate;
    @FXML
    private Label eventEndDate;
    @FXML
    private VBox locationsContainer;

    @FXML
    private Label noPlacesHint;

    private Event event;
    private final ServiceLocation locationService = new ServiceLocation();
    private final ServiceBooking bookingService = new ServiceBooking();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void setEvent(Event event) {
        this.event = event;
        updateUI();
        loadAvailableLocations();
    }

    private void updateUI() {
        if (event != null) {
            eventName.setText(event.getName());
            eventDescription.setText(event.getDescription());
            eventCity.setText("City: " + event.getCity().name());
            eventCapacity.setText("Capacity: " + event.getCapacity() + " people");
            eventStartDate.setText("Start: " + event.getStart_date().format(dateFormatter));
            eventEndDate.setText("End: " + event.getEnd_date().format(dateFormatter));

            // Load image from byte array if available
            if (event.getImageData() != null && event.getImageData().length > 0) {
                try {
                    Image image = new Image(new ByteArrayInputStream(event.getImageData()));
                    eventImage.setImage(image);
                } catch (Exception e) {
                    System.err.println("Error loading image: " + e.getMessage());
                }
            }
        }
    }

    private void loadAvailableLocations() {
        try {
            // Get all locations
            List<Location> locations = locationService.getAll();
            System.out.println("Total locations found: " + locations.size());
            System.out.println("Event city: " + event.getCity());
            
            // Clear existing locations
            locationsContainer.getChildren().clear();
            
            // Filter locations based on city, capacity, and availability
            List<Location> availableLocations = locations.stream()
                    .peek(location -> System.out.println("Checking location: " + location.getName() 
                        + " (City: " + location.getVille() 
                        + ", Capacity: " + location.getCapacity() + ")"))
                    .filter(location -> {
                        boolean sameCity = location.getVille().toString().equals(event.getCity().toString());
                        boolean sufficientCapacity = location.getCapacity() >= event.getCapacity();
                        boolean isAvailable = isLocationAvailable(location);
                        
                        System.out.println("Location " + location.getName() 
                            + " - Same city: " + sameCity 
                            + " (" + location.getVille() + " vs " + event.getCity() + ")"
                            + ", Sufficient capacity: " + sufficientCapacity 
                            + ", Available: " + isAvailable);
                            
                        return sameCity && sufficientCapacity && isAvailable;
                    })
                    .collect(Collectors.toList());

            // Show or hide the hint based on available locations
            if (availableLocations.isEmpty()) {
                noPlacesHint.setText("No available venues for this event. Please check:\n" +
                                   "• Required capacity: " + event.getCapacity() + " people\n" +
                                   "• Location: " + event.getCity() + "\n" +
                                   "• Event period: " + dateFormatter.format(event.getStart_date()) + " to " + 
                                   dateFormatter.format(event.getEnd_date()));
                noPlacesHint.setVisible(true);
                noPlacesHint.setManaged(true);
            } else {
                noPlacesHint.setVisible(false);
                noPlacesHint.setManaged(false);
                
                // Add available location cards
                availableLocations.forEach(this::addLocationCard);
            }
            
            System.out.println("Total location cards added: " + locationsContainer.getChildren().size());
            
        } catch (SQLException e) {
            System.err.println("Error loading locations: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isLocationAvailable(Location location) {
        try {
            return bookingService.isLocationAvailable(
                location.getId_location(),
                event.getStart_date(),
                event.getEnd_date()
            );
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // If there's an error, consider the location as unavailable
        }
    }

    private void handleLocationSelection(Location location) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddBooking.fxml"));
            Parent root = loader.load();
            
            AddBookingController controller = loader.getController();
            controller.setEvent(event);
            controller.preSelectLocation(location);
            
            Stage stage = new Stage();
            stage.setTitle("Book Location - " + location.getName());
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/global.css").toExternalForm());
            stage.setScene(scene);
            stage.showAndWait();
            
            // Refresh locations after booking
            loadAvailableLocations();
        } catch (IOException e) {
            showError("Error opening booking form", e);
        }
    }

    private void addLocationCard(Location location) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LocationCard.fxml"));
            Parent locationCard = loader.load();
            
            LocationCardController controller = loader.getController();
            controller.setLocation(location);
            controller.hideManagementButtons();
            
            // Check if location is available
            boolean isAvailable = isLocationAvailable(location);
            controller.setAvailabilityStatus(isAvailable);
            
            if (isAvailable) {
                // Create Reserve Venue button
                Button reserveButton = new Button("Reserve Venue");
                reserveButton.getStyleClass().addAll("action-button", "primary");
                
                FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.TICKET);
                icon.setSize("1.2em");
                reserveButton.setGraphic(icon);
                
                reserveButton.setOnAction(e -> handleLocationSelection(location));
                
                // Add button to the card
                VBox cardContent = (VBox) locationCard.lookup(".card");
                if (cardContent != null) {
                    // Create a container for the button
                    HBox buttonContainer = new HBox();
                    buttonContainer.setAlignment(javafx.geometry.Pos.CENTER);
                    buttonContainer.getChildren().add(reserveButton);
                    buttonContainer.setSpacing(10);
                    buttonContainer.setPadding(new javafx.geometry.Insets(10, 0, 0, 0));
                    
                    cardContent.getChildren().add(buttonContainer);
                }
            }
            
            locationsContainer.getChildren().add(locationCard);
            
        } catch (IOException e) {
            showError("Error creating location card", e);
        }
    }

    @FXML
    void handleClose() {
        Stage stage = (Stage) eventName.getScene().getWindow();
        stage.close();
    }

    public static void showEventDetails(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(EventDetailsController.class.getResource("/EventDetails.fxml"));
            Parent root = loader.load();
            
            EventDetailsController controller = loader.getController();
            controller.setEvent(event);
            
            Stage stage = new Stage();
            stage.setTitle("Détails de l'événement - " + event.getName());
            stage.setScene(new Scene(root));
            
            // Set preferred and maximum size
            stage.setWidth(800);
            stage.setHeight(600);
            stage.setMinWidth(600);
            stage.setMinHeight(500);
            stage.setMaxWidth(1000);
            stage.setMaxHeight(800);
            
            stage.initStyle(StageStyle.DECORATED);
            stage.show();
            
            // Center the window on the screen
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
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