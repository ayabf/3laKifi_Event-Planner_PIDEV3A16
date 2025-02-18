package controllers;

import Models.Event;
import Models.Location;
import Models.Booking;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.ServiceBooking;
import services.ServiceLocation;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class AddBookingController {
    @FXML private Label eventNameLabel;
    @FXML private Label eventCityLabel;
    @FXML private Label eventDateLabel;
    @FXML private Label eventCapacityLabel;
    @FXML private ComboBox<Location> locationComboBox;
    @FXML private DatePicker startDatePicker;
    @FXML private TextField startTimeField;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField endTimeField;

    private Event event;
    private final ServiceBooking bookingService = new ServiceBooking();
    private final ServiceLocation locationService = new ServiceLocation();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    void initialize() {
        // Initialize location combo box
        try {
            locationComboBox.getItems().addAll(locationService.getAll());
            locationComboBox.setConverter(new javafx.util.StringConverter<Location>() {
                @Override
                public String toString(Location location) {
                    return location != null ? location.getName() + " (" + location.getVille().name() + ")" : "";
                }

                @Override
                public Location fromString(String string) {
                    return null;
                }
            });
        } catch (SQLException e) {
            showError("Error loading locations", e);
        }
    }

    public void setEvent(Event event) {
        this.event = event;
        updateEventDetails();
        
        // Pre-fill dates with event dates
        startDatePicker.setValue(event.getStart_date().toLocalDate());
        startTimeField.setText(event.getStart_date().toLocalTime().toString());
        endDatePicker.setValue(event.getEnd_date().toLocalDate());
        endTimeField.setText(event.getEnd_date().toLocalTime().toString());
    }

    private void updateEventDetails() {
        if (event != null) {
            eventNameLabel.setText(event.getName());
            eventCityLabel.setText(event.getCity().name());
            eventDateLabel.setText(String.format("%s - %s",
                event.getStart_date().format(dateFormatter),
                event.getEnd_date().format(dateFormatter)));
            eventCapacityLabel.setText(String.format("%d attendees", event.getCapacity()));
        }
    }

    @FXML
    private void handleBook() {
        if (!validateInputs()) {
            return;
        }

        try {
            Location selectedLocation = locationComboBox.getValue();
            LocalDateTime startDateTime = LocalDateTime.of(
                startDatePicker.getValue(),
                LocalTime.parse(startTimeField.getText())
            );
            LocalDateTime endDateTime = LocalDateTime.of(
                endDatePicker.getValue(),
                LocalTime.parse(endTimeField.getText())
            );

            // Check if the location is available for the selected time period
            if (!bookingService.isLocationAvailable(selectedLocation.getId_location(), startDateTime, endDateTime)) {
                showAlert(Alert.AlertType.WARNING, "Location Unavailable", 
                    "The selected location is not available for the specified time period.");
                return;
            }

            // Create and save the booking
            Booking booking = new Booking(
                event.getId_event(),
                selectedLocation.getId_location(),
                startDateTime,
                endDateTime
            );
            
            bookingService.ajouter(booking);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Event booked successfully!");
            closeWindow();
        } catch (SQLException e) {
            showError("Error creating booking", e);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid date/time format. Please use HH:mm format for time.");
        }
    }

    private boolean validateInputs() {
        if (locationComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Please select a location.");
            return false;
        }
        if (startDatePicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Start date is required.");
            return false;
        }
        if (endDatePicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation", "End date is required.");
            return false;
        }
        if (startTimeField.getText().isEmpty() || !startTimeField.getText().matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Please enter a valid start time (HH:mm).");
            return false;
        }
        if (endTimeField.getText().isEmpty() || !endTimeField.getText().matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Please enter a valid end time (HH:mm).");
            return false;
        }

        // Validate that booking time is within event time
        LocalDateTime bookingStart = LocalDateTime.of(
            startDatePicker.getValue(),
            LocalTime.parse(startTimeField.getText())
        );
        LocalDateTime bookingEnd = LocalDateTime.of(
            endDatePicker.getValue(),
            LocalTime.parse(endTimeField.getText())
        );

        if (bookingStart.isBefore(event.getStart_date()) || bookingEnd.isAfter(event.getEnd_date())) {
            showAlert(Alert.AlertType.WARNING, "Validation", 
                "Booking time must be within the event's time period.");
            return false;
        }

        if (bookingEnd.isBefore(bookingStart)) {
            showAlert(Alert.AlertType.WARNING, "Validation", 
                "End time must be after start time.");
            return false;
        }

        return true;
    }

    @FXML
    private void handleClose() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) eventNameLabel.getScene().getWindow();
        stage.close();
    }

    private void showError(String message, Exception e) {
        showAlert(Alert.AlertType.ERROR, "Error", message + ": " + e.getMessage());
        e.printStackTrace();
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void preSelectLocation(Location location) {
        locationComboBox.setValue(location);
    }
} 