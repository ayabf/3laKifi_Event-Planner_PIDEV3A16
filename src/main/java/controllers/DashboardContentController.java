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
    }

    @FXML
    void handleManageEvents() {
    }

    @FXML
    void handleManageBookings() {
    }

    @FXML
    void handleManageLocations() {
    }

    public void updateStatistics(String events, String bookings, String locations) {
        totalEventsLabel.setText(events);
        totalBookingsLabel.setText(bookings);
        totalLocationsLabel.setText(locations);
    }
} 