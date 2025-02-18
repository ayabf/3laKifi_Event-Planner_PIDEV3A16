package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardContentController {
    @FXML
    private Label totalEventsLabel;
    
    @FXML
    private Label totalBookingsLabel;
    
    @FXML
    private Label totalLocationsLabel;

    @FXML
    void initialize() {
        // Initialize will be handled by the parent controller
    }

    @FXML
    void handleManageEvents() {
        // These will be handled by the parent controller
    }

    @FXML
    void handleManageBookings() {
        // These will be handled by the parent controller
    }

    @FXML
    void handleManageLocations() {
        // These will be handled by the parent controller
    }

    public void updateStatistics(String events, String bookings, String locations) {
        totalEventsLabel.setText(events);
        totalBookingsLabel.setText(bookings);
        totalLocationsLabel.setText(locations);
    }
} 