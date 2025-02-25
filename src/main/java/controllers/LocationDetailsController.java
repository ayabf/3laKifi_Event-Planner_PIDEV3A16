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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import java.io.IOException;

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
    @FXML private Button tour3DButton;

    private Location location;
    private final ServiceBooking bookingService = new ServiceBooking();

    public void setLocation(Location location) {
        this.location = location;
        updateUI();
    }

    @FXML
    public void initialize() {
        tour3DButton.setOnAction(e -> launch3DTour());
    }

    private void updateUI() {
        if (location != null) {
            locationTitle.setText(location.getName());

            if (location.getImageData() != null && location.getImageData().length > 0) {
                try {
                    Image image = new Image(new ByteArrayInputStream(location.getImageData()));
                    locationImage.setImage(image);
                } catch (Exception e) {
                    System.err.println("Error loading location image: " + e.getMessage());
                }
            }

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

    private void launch3DTour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LocationTour3D.fxml"));
            Parent root = loader.load();
            
            LocationTour3DController controller = loader.getController();
            controller.setLocation(location);
            
            Stage stage = new Stage();
            stage.setTitle("3D Tour - " + location.getName());
            Scene scene = new Scene(root);
            stage.setScene(scene);

            stage.setMaximized(true);

            stage.setOnCloseRequest(e -> controller.cleanup());
            
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error launching 3D tour", e);
        }
    }

    private void showError(String message, Exception e) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
} 