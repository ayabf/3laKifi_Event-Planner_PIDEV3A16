package controllers;

import Models.Event;
import Models.City;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.ServiceEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class UpdateEventController {
    @FXML
    private TextField nameField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private Button imageButton;
    @FXML
    private Label imageLabel;
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

    private Event event;
    private final ServiceEvent eventService = new ServiceEvent();
    private File selectedImageFile;
    private boolean imageChanged = false;
    private java.util.function.Consumer<Event> onEventUpdated;

    public void setOnEventUpdated(java.util.function.Consumer<Event> callback) {
        this.onEventUpdated = callback;
    }

    @FXML
    void initialize() {
        cityComboBox.getItems().addAll(City.values());
        
        // Initialize the image selection button
        imageButton.setOnAction(event -> handleImageSelection());
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
            imageLabel.setText(file.getName());
            imageChanged = true;
        }
    }

    public void setEvent(Event event) {
        this.event = event;
        populateFields();
    }

    private void populateFields() {
        if (event != null) {
            nameField.setText(event.getName());
            descriptionArea.setText(event.getDescription());
            imageLabel.setText(event.getImageFileName() != null ? event.getImageFileName() : "Aucune image sélectionnée");
            capacityField.setText(String.valueOf(event.getCapacity()));
            cityComboBox.setValue(event.getCity());

            // Set date and time fields
            startDatePicker.setValue(event.getStart_date().toLocalDate());
            startTimeField.setText(event.getStart_date().toLocalTime().toString());
            endDatePicker.setValue(event.getEnd_date().toLocalDate());
            endTimeField.setText(event.getEnd_date().toLocalTime().toString());
        }
    }

    @FXML
    void handleUpdate() {
        try {
            // Update event properties
            event.setName(nameField.getText());
            event.setDescription(descriptionArea.getText());
            event.setCapacity(Integer.parseInt(capacityField.getText()));
            event.setCity(cityComboBox.getValue());

            // Update image if changed
            if (imageChanged && selectedImageFile != null) {
                try (FileInputStream fis = new FileInputStream(selectedImageFile)) {
                    byte[] imageData = new byte[(int) selectedImageFile.length()];
                    fis.read(imageData);
                    event.setImageData(imageData);
                    event.setImageFileName(selectedImageFile.getName());
                } catch (IOException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de lire le fichier image.");
                    return;
                }
            }

            // Combine date and time for start date
            LocalDateTime startDateTime = LocalDateTime.of(
                startDatePicker.getValue(),
                LocalTime.parse(startTimeField.getText())
            );
            event.setStart_date(startDateTime);

            // Combine date and time for end date
            LocalDateTime endDateTime = LocalDateTime.of(
                endDatePicker.getValue(),
                LocalTime.parse(endTimeField.getText())
            );
            event.setEnd_date(endDateTime);

            // Update event in database
            eventService.modifier(event);
            
            // Notify listeners about the update
            if (onEventUpdated != null) {
                onEventUpdated.accept(event);
            }
            
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Événement modifié avec succès.");
            closeWindow();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de modifier l'événement.");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez entrer un nombre valide pour la capacité.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Format de date ou heure invalide.");
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