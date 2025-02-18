package controllers;

import Models.Event;
import Models.City;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.ServiceEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AddEventController {
    @FXML
    private TextField nameField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private Button imageButton;
    @FXML
    private ImageView imagePreview;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private TextField startTimeField;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private TextField endTimeField;
    @FXML
    private TextField capacityField;
    @FXML
    private ComboBox<City> cityComboBox;
    @FXML
    private Button addButton;

    private final ServiceEvent eventService = new ServiceEvent();
    private int userId = 1;  // Default user ID until user management is integrated
    private File selectedImageFile;
    private Event eventToEdit;

    @FXML
    void initialize() {
        cityComboBox.getItems().addAll(City.values());
        
        // Initialize the image selection button
        imageButton.setOnAction(event -> handleImageSelection());
        
        // Set default image for preview
        imagePreview.setImage(null);
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    private void handleImageSelection() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Event Image");
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

    public void setEventForEdit(Event event) {
        this.eventToEdit = event;
        
        // Populate form fields with event data
        nameField.setText(event.getName());
        descriptionArea.setText(event.getDescription());
        startDatePicker.setValue(event.getStart_date().toLocalDate());
        startTimeField.setText(event.getStart_date().toLocalTime().toString());
        endDatePicker.setValue(event.getEnd_date().toLocalDate());
        endTimeField.setText(event.getEnd_date().toLocalTime().toString());
        capacityField.setText(String.valueOf(event.getCapacity()));
        cityComboBox.setValue(event.getCity());

        // Update image preview if event has an image
        if (event.getImageData() != null) {
            try {
                // Create a temporary file for the image
                File tempFile = File.createTempFile("event_image", event.getImageFileName());
                java.nio.file.Files.write(tempFile.toPath(), event.getImageData());
                selectedImageFile = tempFile;
                imagePreview.setImage(new Image(tempFile.toURI().toString()));
            } catch (IOException e) {
                showAlert(Alert.AlertType.WARNING, "Image", "Impossible de charger l'image de l'événement.");
            }
        }

        // Update the Add button text to "Update"
        if (addButton != null) {
            addButton.setText("Update");
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

            // Create or update event
            Event event;
            if (eventToEdit != null) {
                // Update existing event
                event = eventToEdit;
                event.setName(nameField.getText());
                event.setDescription(descriptionArea.getText());
                if (imageData != null) {
                    event.setImageData(imageData);
                    event.setImageFileName(imageFileName);
                }
                event.setStart_date(LocalDateTime.of(
                    startDatePicker.getValue(),
                    LocalTime.parse(startTimeField.getText())
                ));
                event.setEnd_date(LocalDateTime.of(
                    endDatePicker.getValue(),
                    LocalTime.parse(endTimeField.getText())
                ));
                event.setCapacity(Integer.parseInt(capacityField.getText()));
                event.setCity(cityComboBox.getValue());
                
                eventService.modifier(event);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Événement mis à jour avec succès.");
            } else {
                // Create new event
                event = new Event(
                    nameField.getText(),
                    descriptionArea.getText(),
                    imageData,
                    imageFileName,
                    LocalDateTime.of(
                        startDatePicker.getValue(),
                        LocalTime.parse(startTimeField.getText())
                    ),
                    LocalDateTime.of(
                        endDatePicker.getValue(),
                        LocalTime.parse(endTimeField.getText())
                    ),
                    Integer.parseInt(capacityField.getText()),
                    cityComboBox.getValue(),
                    userId
                );
                
                eventService.ajouter(event);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Événement ajouté avec succès.");
            }
            
            closeWindow();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ajouter l'événement: " + e.getMessage());
            e.printStackTrace();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez entrer un nombre valide pour la capacité.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Format de date ou heure invalide.");
        }
    }

    private boolean validateInputs() {
        if (nameField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Le nom est requis.");
            return false;
        }
        if (descriptionArea.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation", "La description est requise.");
            return false;
        }
        if (selectedImageFile == null) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Une image est requise.");
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
        if (cityComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation", "La ville est requise.");
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