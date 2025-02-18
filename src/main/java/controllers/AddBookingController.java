package controllers;

import Models.Location;
import Models.Event;
import Models.Booking;
import services.ServiceBooking;
import services.ServiceLocation;
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
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void setEvent(Event event) {
        this.event = event;
        updateEventDetails();
    }

    public void setServices(ServiceBooking bookingService, ServiceLocation locationService) {
        this.bookingService = bookingService;
        this.locationService = locationService;
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
        // Location cell factory
        locationComboBox.setCellFactory(new Callback<ListView<Location>, ListCell<Location>>() {
            @Override
            public ListCell<Location> call(ListView<Location> param) {
                return new ListCell<Location>() {
                    @Override
                    protected void updateItem(Location location, boolean empty) {
                        super.updateItem(location, empty);
                        if (empty || location == null) {
                            setText(null);
                        } else {
                            setText(location.getName() + " (" + location.getVille().name() + ")");
                        }
                    }
                };
            }
        });

        // Location string converter
        locationComboBox.setConverter(new StringConverter<Location>() {
            @Override
            public String toString(Location location) {
                return location != null ? location.getName() + " (" + location.getVille().name() + ")" : "";
            }

            @Override
            public Location fromString(String string) {
                return null;
            }
        });
    }

    @FXML
    private void handleBook() {
        if (!validateInput()) {
            return;
        }

        try {
            Location selectedLocation = locationComboBox.getValue();
            Booking booking = new Booking(
                0, // id will be set by database
                event.getId_event(),
                selectedLocation.getId_location(),
                LocalDateTime.of(startDatePicker.getValue(), 
                    LocalTime.parse(startTimeField.getText())),
                LocalDateTime.of(endDatePicker.getValue(), 
                    LocalTime.parse(endTimeField.getText()))
            );

            bookingService.ajouter(booking);
            showInfo("Booking created successfully");
            handleClose();
        } catch (Exception e) {
            showError("Error", "Could not create booking: " + e.getMessage());
        }
    }

    @FXML
    private void handleClose() {
        ((Stage) eventNameLabel.getScene().getWindow()).close();
    }

    private boolean validateInput() {
        if (locationComboBox.getValue() == null) {
            showError("Invalid Input", "Please select a location");
            return false;
        }
        if (startDatePicker.getValue() == null) {
            showError("Invalid Input", "Please select a start date");
            return false;
        }
        if (endDatePicker.getValue() == null) {
            showError("Invalid Input", "Please select an end date");
            return false;
        }
        if (startTimeField.getText().isEmpty()) {
            showError("Invalid Input", "Please enter a start time");
            return false;
        }
        if (endTimeField.getText().isEmpty()) {
            showError("Invalid Input", "Please enter an end time");
            return false;
        }
        try {
            LocalTime.parse(startTimeField.getText());
            LocalTime.parse(endTimeField.getText());
        } catch (Exception e) {
            showError("Invalid Input", "Please enter valid times in HH:mm format");
            return false;
        }
        return true;
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