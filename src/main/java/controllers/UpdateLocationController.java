package controllers;
import Models.Location;
import Models.City;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.ServiceLocation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;

public class UpdateLocationController {
    @FXML
    private TextField nameField;
    @FXML
    private ComboBox<City> villeComboBox;
    @FXML
    private Button imageButton;
    @FXML
    private Label imageLabel;
    @FXML
    private TextField capacityField;
    @FXML
    private TextField dimensionField;
    @FXML
    private TextField priceField;

    private Location location;
    private final ServiceLocation locationService = new ServiceLocation();
    private File selectedImageFile;
    private boolean imageChanged = false;

    @FXML
    void initialize() {
        villeComboBox.getItems().addAll(City.values());

        imageButton.setOnAction(event -> handleImageSelection());
    }

    private void handleImageSelection() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Location Image");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        
        File file = fileChooser.showOpenDialog(imageButton.getScene().getWindow());
        if (file != null) {
            selectedImageFile = file;
            imageLabel.setText(file.getName());
            imageChanged = true;
        }
    }

    public void setLocation(Location location) {
        this.location = location;
        populateFields();
    }

    private void populateFields() {
        if (location != null) {
            nameField.setText(location.getName());
            villeComboBox.setValue(location.getVille());
            imageLabel.setText(location.getImageFileName() != null ? location.getImageFileName() : "No image selected");
            capacityField.setText(String.valueOf(location.getCapacity()));
            dimensionField.setText(location.getDimension());
            priceField.setText(String.valueOf(location.getPrice()));
        }
    }

    @FXML
    void handleUpdate() {
        try {
            location.setName(nameField.getText());
            location.setVille(villeComboBox.getValue());

            if (imageChanged && selectedImageFile != null) {
                try (FileInputStream fis = new FileInputStream(selectedImageFile)) {
                    byte[] imageData = new byte[(int) selectedImageFile.length()];
                    fis.read(imageData);
                    location.setImageData(imageData);
                    location.setImageFileName(selectedImageFile.getName());
                } catch (IOException e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Could not read image file.");
                    return;
                }
            }
            
            location.setCapacity(Integer.parseInt(capacityField.getText()));
            location.setDimension(dimensionField.getText());
            location.setPrice(Double.parseDouble(priceField.getText()));

            locationService.modifier(location);
            
            showAlert(Alert.AlertType.INFORMATION, "Success", "Location updated successfully.");
            closeWindow();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not update location.");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter valid numeric values for capacity and price.");
        }
    }

    @FXML
    void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
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