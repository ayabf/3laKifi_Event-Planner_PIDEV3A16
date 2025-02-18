package controllers;

import Models.Booking;
import Models.Event;
import Models.Location;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import services.ServiceBooking;
import services.ServiceEvent;
import services.ServiceLocation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BookingCardController {
    @FXML private HBox statusContainer;
    @FXML private FontAwesomeIconView statusIcon;
    @FXML private Label statusText;
    @FXML private Label eventName;
    @FXML private Label locationName;
    @FXML private Label startDate;
    @FXML private Label endDate;
    @FXML private Button viewButton;
    @FXML private Button cancelButton;

    private Booking booking;
    private Event event;
    private Location location;
    private final ServiceBooking bookingService = new ServiceBooking();
    private final ServiceEvent eventService = new ServiceEvent();
    private final ServiceLocation locationService = new ServiceLocation();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        // Initialization code if needed
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
        loadEventAndLocation();
        updateCard();
    }

    private void loadEventAndLocation() {
        try {
            Event tempEvent = new Event();
            tempEvent.setId_event(booking.getEvent_id());
            event = eventService.getOne(tempEvent);

            Location tempLocation = new Location();
            tempLocation.setId_location(booking.getLocation_id());
            location = locationService.getOne(tempLocation);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error loading booking details", e);
        }
    }

    private void updateCard() {
        if (booking != null && event != null && location != null) {
            eventName.setText(event.getName());
            locationName.setText(location.getName() + " (" + location.getVille().name() + ")");
            startDate.setText(booking.getStart_date().format(dateFormatter));
            endDate.setText("Until " + booking.getEnd_date().format(dateFormatter));
            updateStatus();
        }
    }

    private void updateStatus() {
        LocalDateTime now = LocalDateTime.now();
        String status;
        String iconName;
        String styleClass;

        if (now.isBefore(booking.getStart_date())) {
            status = "Upcoming";
            iconName = "CLOCK";
            styleClass = "upcoming";
        } else if (now.isAfter(booking.getEnd_date())) {
            status = "Completed";
            iconName = "CHECK_CIRCLE";
            styleClass = "completed";
            cancelButton.setDisable(true);
        } else {
            status = "In Progress";
            iconName = "PLAY_CIRCLE";
            styleClass = "in-progress";
            cancelButton.setDisable(true);
        }

        statusContainer.getStyleClass().removeAll("upcoming", "completed", "in-progress");
        statusContainer.getStyleClass().add(styleClass);
        statusIcon.setGlyphName(iconName);
        statusText.setText(status);
    }

    @FXML
    private void handleView() {
        // TODO: Implement view booking details
    }

    @FXML
    private void handleCancel() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel Booking");
        alert.setHeaderText("Are you sure you want to cancel this booking?");
        alert.setContentText("This action cannot be undone.");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                bookingService.supprimer(booking.getBooking_id());
                // Refresh the parent view
                statusContainer.getScene().getWindow().hide();
            } catch (Exception e) {
                e.printStackTrace();
                showError("Error canceling booking", e);
            }
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