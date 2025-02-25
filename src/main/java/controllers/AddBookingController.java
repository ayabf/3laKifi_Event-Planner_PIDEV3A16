package controllers;

import Models.Location;
import Models.Event;
import Models.Booking;
import services.ServiceBooking;
import services.ServiceLocation;
import services.ServiceEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
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
    @FXML private DatePicker endDatePicker;
    @FXML private TextField startTimeField;
    @FXML private TextField endTimeField;

    private Event event;
    private ServiceBooking bookingService;
    private ServiceLocation locationService;
    private ServiceEvent eventService;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void setEvent(Event event) {
        this.event = event;
        updateEventDetails();
    }

    public void setServices(ServiceBooking bookingService, ServiceLocation locationService) {
        this.bookingService = bookingService;
        this.locationService = locationService;
        this.eventService = new ServiceEvent();
        loadLocations();
    }

    public void setLocation(Location location) {
        if (location != null) {
            locationComboBox.setValue(location);
        }
    }

    @FXML
    public void initialize() {
        setupLocationComboBox();
    }

    private void updateEventDetails() {
        if (event != null) {
            eventNameLabel.setText(event.getName());
            eventCityLabel.setText("City: " + event.getCity());
            eventDateLabel.setText(String.format("%s - %s",
                event.getStart_date().format(dateFormatter),
                event.getEnd_date().format(dateFormatter)));
            eventCapacityLabel.setText("Capacity: " + event.getCapacity());
        }
    }

    private void loadLocations() {
        try {
            locationComboBox.getItems().clear();
            locationComboBox.getItems().addAll(locationService.getAll());
        } catch (SQLException e) {
            showError("Error", "Could not load locations: " + e.getMessage());
        }
    }

    private void setupLocationComboBox() {
        locationComboBox.setConverter(new StringConverter<Location>() {
            @Override
            public String toString(Location location) {
                return location != null ? location.getName() : "";
            }

            @Override
            public Location fromString(String string) {
                return null;
            }
        });
    }

    @FXML
    private void handleAdd() {
        if (!validateInputs()) {
            return;
        }

        try {
            LocalDateTime startDateTime = LocalDateTime.of(
                startDatePicker.getValue(),
                LocalTime.parse(startTimeField.getText())
            );
            
            LocalDateTime endDateTime = LocalDateTime.of(
                endDatePicker.getValue(),
                LocalTime.parse(endTimeField.getText())
            );

            if (endDateTime.isBefore(startDateTime)) {
                showError("Invalid dates", "End date/time must be after start date/time");
                return;
            }

            if (startDateTime.isBefore(LocalDateTime.now())) {
                showError("Invalid dates", "Start date/time cannot be in the past");
                return;
            }

            Location selectedLocation = locationComboBox.getValue();
            if (!bookingService.isLocationAvailable(selectedLocation.getId_location(), startDateTime, endDateTime)) {
                showError("Location unavailable", "The selected location is not available for these dates");
                return;
            }

            Booking booking = new Booking(
                event,
                selectedLocation,
                startDateTime,
                endDateTime
            );
            
            bookingService.ajouter(booking);
            showInfo("Success", "Booking created successfully");
            closeWindow();
        } catch (SQLException e) {
            showError("Database error", "Could not create booking: " + e.getMessage());
        } catch (Exception e) {
            showError("Error", "Invalid date/time format");
        }
    }

    private boolean validateInputs() {
        if (locationComboBox.getValue() == null) {
            showError("Validation", "Please select a location");
            return false;
        }
        if (startDatePicker.getValue() == null) {
            showError("Validation", "Please select a start date");
            return false;
        }
        if (endDatePicker.getValue() == null) {
            showError("Validation", "Please select an end date");
            return false;
        }
        if (startTimeField.getText().isEmpty() || endTimeField.getText().isEmpty()) {
            showError("Validation", "Please enter both start and end times");
            return false;
        }
        return true;
    }

    @FXML
    private void handleClose() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) locationComboBox.getScene().getWindow();
        stage.close();
    }

    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showInfo(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 