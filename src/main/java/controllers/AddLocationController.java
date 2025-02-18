package controllers;

import Models.Location;
import Models.City;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.ServiceLocation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;

public class AddLocationController {
    @FXML
    private TextField nameField;
    @FXML
    private ComboBox<City> villeComboBox;
    @FXML
    private Button imageButton;
    @FXML
    private ImageView imagePreview;
    @FXML
    private TextField capacityField;
    @FXML
    private TextField dimensionField;
    @FXML
    private TextField priceField;

    private final ServiceLocation locationService = new ServiceLocation();
    private File selectedImageFile;

    @FXML
    void initialize() {
        // Initialize the city combo box
        villeComboBox.getItems().addAll(City.values());
        
        // Initialize the image selection button
        imageButton.setOnAction(event -> handleImageSelection());
        
        // Set default image for preview
        imagePreview.setImage(null);
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
            // Update the image preview
            try {
                Image image = new Image(file.toURI().toString());
                imagePreview.setImage(image);
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger l'aperçu de l'image.");
            }
        }
    }

    @FXML
    void handleAdd() {
        try {
            // Validate input fields
            if (!validateInputs()) {
                return;
            }

            // Read the image file into a byte array
            byte[] imageData = null;
            String imageFileName = null;
            if (selectedImageFile != null) {
                try (FileInputStream fis = new FileInputStream(selectedImageFile)) {
                    imageData = new byte[(int) selectedImageFile.length()];
                    fis.read(imageData);
                    imageFileName = selectedImageFile.getName();
                } catch (IOException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de lire le fichier image.");
                    return;
                }
            }

            // Create new location
            Location location = new Location(
                nameField.getText(),
                villeComboBox.getValue(),
                imageData,
                imageFileName,
                Integer.parseInt(capacityField.getText()),
                dimensionField.getText(),
                Double.parseDouble(priceField.getText())
            );

            // Add location to database
            locationService.ajouter(location);
            
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Location ajoutée avec succès.");
            closeWindow();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ajouter la location.");
            e.printStackTrace();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez entrer des valeurs numériques valides pour la capacité et le prix.");
        }
    }

    private boolean validateInputs() {
        if (nameField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Le nom est requis.");
            return false;
        }
        if (villeComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation", "La ville est requise.");
            return false;
        }
        if (selectedImageFile == null) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Une image est requise.");
            return false;
        }
        if (dimensionField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation", "La dimension est requise.");
            return false;
        }
        try {
            int capacity = Integer.parseInt(capacityField.getText());
            if (capacity <= 0) {
                showAlert(Alert.AlertType.WARNING, "Validation", "La capacité doit être supérieure à 0.");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation", "La capacité doit être un nombre valide.");
            return false;
        }
        try {
            double price = Double.parseDouble(priceField.getText());
            if (price <= 0) {
                showAlert(Alert.AlertType.WARNING, "Validation", "Le prix doit être supérieur à 0.");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Le prix doit être un nombre valide.");
            return false;
        }
        return true;
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