package controllers;

import Models.Location;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import services.ServiceBooking;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.ByteArrayInputStream;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class LocationDetailsController {
    @FXML private Label locationTitle;
    @FXML private ImageView locationImage;
    @FXML private HBox statusContainer;
    @FXML private FontAwesomeIconView statusIcon;
    @FXML private Label statusText;
    @FXML private Label cityLabel;
    @FXML private Label capacityLabel;
    @FXML private Label dimensionsLabel;
    @FXML private Label priceLabel;

    private Location location;
    private final ServiceBooking bookingService = new ServiceBooking();

    public void setLocation(Location location) {
        this.location = location;
        updateUI();
    }

    private void updateUI() {
        if (location != null) {
            // Set title
            locationTitle.setText(location.getName());

            // Load image
            if (location.getImageData() != null && location.getImageData().length > 0) {
                try {
                    Image image = new Image(new ByteArrayInputStream(location.getImageData()));
                    locationImage.setImage(image);
                } catch (Exception e) {
                    System.err.println("Error loading location image: " + e.getMessage());
                }
            }

            // Update status
            try {
                boolean isAvailable = bookingService.isLocationAvailable(
                    location.getId_location(),
                    LocalDateTime.now(),
                    LocalDateTime.now().plusHours(1)
                );
                updateStatus(isAvailable);
            } catch (SQLException e) {
                System.err.println("Error checking location availability: " + e.getMessage());
                updateStatus(false);
            }

            // Set details
            cityLabel.setText(location.getVille().name());
            capacityLabel.setText(location.getCapacity() + " people");
            dimensionsLabel.setText(location.getDimension());
            priceLabel.setText(String.format("%.2f DT", location.getPrice()));
        }
    }

    private void updateStatus(boolean isAvailable) {
        if (isAvailable) {
            statusContainer.getStyleClass().removeAll("unavailable");
            statusContainer.getStyleClass().add("available");
            statusIcon.setGlyphName("CHECK_CIRCLE");
            statusText.setText("Available");
        } else {
            statusContainer.getStyleClass().removeAll("available");
            statusContainer.getStyleClass().add("unavailable");
            statusIcon.setGlyphName("TIMES_CIRCLE");
            statusText.setText("Currently Booked");
        }
    }

    @FXML
    void handleClose() {
        Stage stage = (Stage) locationTitle.getScene().getWindow();
        stage.close();
    }
} 