package controllers;

import Models.Booking;
import Models.Event;
import Models.Location;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import services.ServiceBooking;
import services.ServiceEvent;
import services.ServiceLocation;

import java.io.IOException;
import java.sql.SQLException;
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
            event = eventService.getById(booking.getEvent_id());
            location = locationService.getById(booking.getLocation_id());
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error loading booking details", e);
        }
    }

    private void updateCard() {
        if (booking != null && event != null && location != null) {
            eventName.setText(event.getName());
            locationName.setText(location.getName() + " (" + location.getVille()+ ")");
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
            iconName = "HOURGLASS_HALF";
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
        if (event != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/EventDetails.fxml"));
                Parent root = loader.load();
                
                EventDetailsController controller = loader.getController();
                controller.setEvent(event);
                
                Stage stage = new Stage();
                stage.setTitle("Event Details - " + event.getName());
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/styles/global.css").toExternalForm());
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showError("Error showing event details", e);
            }
        }
    }

    @FXML
    private void handleCancel() {
        if (LocalDateTime.now().isAfter(booking.getStart_date())) {
            showError("Cannot cancel booking", new Exception("Cannot cancel a booking that has already started"));
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel Booking");
        alert.setHeaderText("Are you sure you want to cancel this booking?");
        alert.setContentText("This action cannot be undone.");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                bookingService.supprimer(booking.getBooking_id());
                showInfo("Booking cancelled successfully");
                // Close the booking card
                statusContainer.getScene().getWindow().hide();
            } catch (SQLException e) {
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

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 