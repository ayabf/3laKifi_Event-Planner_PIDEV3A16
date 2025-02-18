package controllers;

import Models.Booking;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.ServiceBooking;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class UpdateBookingController {
    @FXML
    private TextField eventIdField;
    @FXML
    private TextField locationIdField;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private TextField startTimeField;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private TextField endTimeField;

    private Booking booking;
    private final ServiceBooking bookingService = new ServiceBooking();

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
            startTimeField.setText(booking.getStart_date().toLocalTime().toString());
            endTimeField.setText(booking.getEnd_date().toLocalTime().toString());
        }
    }

    @FXML
    void handleUpdate() {
        try {
            // Validate input fields
            if (!validateInputs()) {
                return;
            }

            // Update booking properties
            booking.setEvent_id(Integer.parseInt(eventIdField.getText()));
            booking.setLocation_id(Integer.parseInt(locationIdField.getText()));
            booking.setStart_date(LocalDateTime.of(
                startDatePicker.getValue(),
                LocalTime.parse(startTimeField.getText())
            ));
            booking.setEnd_date(LocalDateTime.of(
                endDatePicker.getValue(),
                LocalTime.parse(endTimeField.getText())
            ));

            // Update booking in database
            bookingService.modifier(booking);
            
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Réservation modifiée avec succès.");
            closeWindow();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de modifier la réservation.");
            e.printStackTrace();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Les IDs doivent être des nombres valides.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Format de date ou heure invalide.");
        }
    }

    private boolean validateInputs() {
        if (eventIdField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation", "L'ID de l'événement est requis.");
            return false;
        }
        if (locationIdField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation", "L'ID de la location est requis.");
            return false;
        }
        if (startDatePicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation", "La date de début est requise.");
            return false;
        }
        if (endDatePicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation", "La date de fin est requise.");
            return false;
        }
        
        try {
            Integer.parseInt(eventIdField.getText());
            Integer.parseInt(locationIdField.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Les IDs doivent être des nombres valides.");
            return false;
        }

        // Validate that end date is after start date
        LocalDateTime startDateTime = LocalDateTime.of(
            startDatePicker.getValue(),
            LocalTime.parse(startTimeField.getText())
        );
        LocalDateTime endDateTime = LocalDateTime.of(
            endDatePicker.getValue(),
            LocalTime.parse(endTimeField.getText())
        );
        
        if (endDateTime.isBefore(startDateTime)) {
            showAlert(Alert.AlertType.WARNING, "Validation", "La date de fin doit être après la date de début.");
            return false;
        }

        return true;
    }

    @FXML
    void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) eventIdField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 