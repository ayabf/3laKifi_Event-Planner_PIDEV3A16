package controllers;

import Models.Booking;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.ServiceBooking;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class UpdateBookingController {
    @FXML private TextField eventIdField;
    @FXML private TextField locationIdField;
    @FXML private DatePicker startDatePicker;
    @FXML private TextField startTimeField;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField endTimeField;

    private Booking booking;
    private final ServiceBooking bookingService = new ServiceBooking();
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    public void initialize() {
        // Add time format validation
        startTimeField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d{0,2}:\\d{0,2}")) {
                startTimeField.setText(oldValue);
            }
        });
        
        endTimeField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d{0,2}:\\d{0,2}")) {
                endTimeField.setText(oldValue);
            }
        });
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
        populateFields();
    }

    private void populateFields() {
        if (booking != null) {
            eventIdField.setText(String.valueOf(booking.getEvent_id()));
            locationIdField.setText(String.valueOf(booking.getLocation_id()));
            
            // Set date pickers
            startDatePicker.setValue(booking.getStart_date().toLocalDate());
            endDatePicker.setValue(booking.getEnd_date().toLocalDate());
            
            // Set time fields (format: HH:mm)
            startTimeField.setText(booking.getStart_date().toLocalTime().format(timeFormatter));
            endTimeField.setText(booking.getEnd_date().toLocalTime().format(timeFormatter));
        }
    }

    @FXML
    private void handleUpdate() {
        if (!validateInput()) {
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

            // Validate date/time logic
            if (startDateTime.isAfter(endDateTime)) {
                showError("Invalid dates", "Start date/time must be before end date/time");
                return;
            }

            if (startDateTime.isBefore(LocalDateTime.now())) {
                showError("Invalid dates", "Start date/time cannot be in the past");
                return;
            }

            // Update booking object
            booking.setStart_date(startDateTime);
            booking.setEnd_date(endDateTime);

            // Save to database
            bookingService.modifier(booking);
            showInfo("Booking updated successfully");
            handleClose();
        } catch (IllegalArgumentException e) {
            showError("Invalid time format", "Please enter time in HH:mm format");
        } catch (SQLException e) {
            showError("Database error", "Could not update booking: " + e.getMessage());
        }
    }

    private boolean validateInput() {
        if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
            showError("Missing data", "Please select both start and end dates");
            return false;
        }

        if (startTimeField.getText().isEmpty() || endTimeField.getText().isEmpty()) {
            showError("Missing data", "Please enter both start and end times");
            return false;
        }

        try {
            LocalTime.parse(startTimeField.getText());
            LocalTime.parse(endTimeField.getText());
        } catch (Exception e) {
            showError("Invalid time format", "Please enter time in HH:mm format");
            return false;
        }

        return true;
    }

    @FXML
    private void handleClose() {
        ((Stage) eventIdField.getScene().getWindow()).close();
    }

    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
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