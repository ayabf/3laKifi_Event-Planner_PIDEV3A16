package controllers;

import Models.Event;
import Models.City;
import Models.User;
import Models.session;
import Models.Notification.NotificationType;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.ServiceEvent;
import services.UserService;
import utils.NotificationManager;

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
    private final UserService userService = new UserService();
    private int userId = session.id_utilisateur;
    private File selectedImageFile;
    private Event eventToEdit;
    private User currentUser;

    @FXML
    void initialize() {
        cityComboBox.getItems().addAll(City.values());

        imageButton.setOnAction(event -> handleImageSelection());

        imagePreview.setImage(null);

        userId = session.id_utilisateur;
        System.out.println("Initializing AddEventController with user ID: " + userId);
        currentUser = userService.getById(userId);
        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load user information");
        }
    }

    public void setUserId(int userId) {
        this.userId = userId;
        currentUser = userService.getById(userId);
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

        nameField.setText(event.getName());
        descriptionArea.setText(event.getDescription());
        startDatePicker.setValue(event.getStart_date().toLocalDate());
        startTimeField.setText(event.getStart_date().toLocalTime().toString());
        endDatePicker.setValue(event.getEnd_date().toLocalDate());
        endTimeField.setText(event.getEnd_date().toLocalTime().toString());
        capacityField.setText(String.valueOf(event.getCapacity()));
        cityComboBox.setValue(event.getCity());

        if (event.getImageData() != null) {
            try {
                File tempFile = File.createTempFile("event_image", event.getImageFileName());
                java.nio.file.Files.write(tempFile.toPath(), event.getImageData());
                selectedImageFile = tempFile;
                imagePreview.setImage(new Image(tempFile.toURI().toString()));
            } catch (IOException e) {
                showAlert(Alert.AlertType.WARNING, "Image", "Impossible de charger l'image de l'événement.");
            }
        }

        if (addButton != null) {
            addButton.setText("Update");
        }
    }

    @FXML
    void handleAdd() {
        try {
            if (!validateInputs()) {
                return;
            }

            userId = session.id_utilisateur;
            currentUser = userService.getById(userId);
            if (currentUser == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "No user information available");
                return;
            }
            System.out.println("Creating event for user ID: " + userId);

            byte[] imageData = null;
            String imageFileName = null;
            if (selectedImageFile != null) {
                try (FileInputStream fis = new FileInputStream(selectedImageFile)) {
                    imageData = new byte[(int) selectedImageFile.length()];
                    fis.read(imageData);
                    imageFileName = selectedImageFile.getName();
                } catch (IOException e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Could not read image file.");
                    return;
                }
            }

            Event event;
            NotificationManager notificationManager = NotificationManager.getInstance();

            if (eventToEdit != null) {
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
                showAlert(Alert.AlertType.INFORMATION, "Success", "Event updated successfully.");

                notificationManager.showNotification(
                        "Event Updated",
                        "Event '" + event.getName() + "' has been updated successfully",
                        NotificationType.EVENT_UPDATED
                );
            } else {
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
                        currentUser
                );

                eventService.ajouter(event);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Event added successfully.");

                notificationManager.showNotification(
                        "Event Created",
                        "Event '" + event.getName() + "' has been created successfully",
                        NotificationType.EVENT_CREATED
                );
            }

            closeWindow();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not save event: " + e.getMessage());
            e.printStackTrace();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter a valid number for capacity.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid date or time format.");
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