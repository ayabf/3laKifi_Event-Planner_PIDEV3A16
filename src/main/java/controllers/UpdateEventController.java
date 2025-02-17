package controllers;

import Models.City;
import Models.Event;
import services.ServiceEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class UpdateEventController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField capacityTextField;

    @FXML
    private Button changeImageButton;

    @FXML
    private ComboBox<City> cityComboBox;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private ComboBox<Integer> endHourComboBox;

    @FXML
    private ComboBox<Integer> endMinuteComboBox;

    @FXML
    private ImageView eventImageView;

    @FXML
    private TextField nameTextField;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private ComboBox<Integer> startHourComboBox;

    @FXML
    private ComboBox<Integer> startMinuteComboBox;

    @FXML
    private Button submitButton;

    private File selectedImageFile;  // Stocke la nouvelle image
    private final ServiceEvent serviceEvent = new ServiceEvent();
    private Event selectedEvent;  // Stocke l'événement à modifier

    @FXML
    void initialize() {
        populateCityComboBox();
        populateTimeComboBoxes();
    }

    // 📌 Charge l'événement sélectionné dans les champs
    public void initData(Event event) {
        this.selectedEvent = event;

        // Remplir les champs avec les données existantes
        nameTextField.setText(event.getName());
        descriptionTextArea.setText(event.getDescription());
        cityComboBox.setValue(event.getCity());
        startDatePicker.setValue(event.getStart_date().toLocalDate());
        endDatePicker.setValue(event.getEnd_date().toLocalDate());
        startHourComboBox.setValue(event.getStart_date().getHour());
        startMinuteComboBox.setValue(event.getStart_date().getMinute());
        endHourComboBox.setValue(event.getEnd_date().getHour());
        endMinuteComboBox.setValue(event.getEnd_date().getMinute());
        capacityTextField.setText(String.valueOf(event.getCapacity()));

        // Charger l'image actuelle de l'événement
        if (event.getImagepath() != null && !event.getImagepath().isEmpty()) {
            eventImageView.setImage(new Image("file:" + event.getImagepath()));
        }
    }

    // 📌 Remplir les ComboBox des heures et minutes
    private void populateTimeComboBoxes() {
        ObservableList<Integer> hours = FXCollections.observableArrayList();
        ObservableList<Integer> minutes = FXCollections.observableArrayList();

        for (int i = 0; i < 24; i++) {
            hours.add(i);
        }

        for (int i = 0; i < 60; i++) {
            minutes.add(i);
        }

        startHourComboBox.setItems(hours);
        startMinuteComboBox.setItems(minutes);
        endHourComboBox.setItems(hours);
        endMinuteComboBox.setItems(minutes);
    }

    // 📌 Remplissage de la ComboBox des villes avec l'énumération City
    private void populateCityComboBox() {
        List<City> cities = Arrays.asList(City.values());
        ObservableList<City> cityList = FXCollections.observableArrayList(cities);
        cityComboBox.setItems(cityList);
    }

    // 📌 Modifier l'image de l'événement
    @FXML
    void changeImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une nouvelle image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));

        selectedImageFile = fileChooser.showOpenDialog(new Stage());

        if (selectedImageFile != null) {
            eventImageView.setImage(new Image(selectedImageFile.toURI().toString()));
        }
    }

    // 📌 Mise à jour de l'événement dans la base de données
    @FXML
    void updateEvent(ActionEvent event) {
        try {
            // ✅ Vérification des champs
            String name = nameTextField.getText().trim();
            String description = descriptionTextArea.getText().trim();
            City city = cityComboBox.getValue();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            Integer startHour = startHourComboBox.getValue();
            Integer startMinute = startMinuteComboBox.getValue();
            Integer endHour = endHourComboBox.getValue();
            Integer endMinute = endMinuteComboBox.getValue();
            String capacityText = capacityTextField.getText().trim();

            if (name.isEmpty() || description.isEmpty() || city == null || startDate == null || endDate == null ||
                    startHour == null || startMinute == null || endHour == null || endMinute == null || capacityText.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Champs obligatoires", "Veuillez remplir tous les champs.");
                return;
            }

            int capacity;
            try {
                capacity = Integer.parseInt(capacityText);
                if (capacity <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Capacité invalide", "La capacité doit être un nombre entier positif.");
                return;
            }

            // ✅ Vérification des dates
            LocalDateTime startDateTime = startDate.atTime(startHour, startMinute);
            LocalDateTime endDateTime = endDate.atTime(endHour, endMinute);

            if (startDateTime.isBefore(LocalDateTime.now())) {
                showAlert(Alert.AlertType.ERROR, "Date invalide", "La date de début ne peut pas être dans le passé.");
                return;
            }
            if (endDateTime.isBefore(startDateTime)) {
                showAlert(Alert.AlertType.ERROR, "Date invalide", "La date de fin doit être après la date de début.");
                return;
            }

            // ✅ Mise à jour de l'événement sélectionné
            selectedEvent.setName(name);
            selectedEvent.setDescription(description);
            selectedEvent.setCity(city);
            selectedEvent.setStart_date(startDateTime);
            selectedEvent.setEnd_date(endDateTime);
            selectedEvent.setCapacity(capacity);

            // ✅ Mise à jour de l'image si une nouvelle est sélectionnée
            if (selectedImageFile != null) {
                selectedEvent.setImagepath(selectedImageFile.getAbsolutePath());
            }

            // ✅ Mise à jour dans la base de données
            serviceEvent.modifier(selectedEvent);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Événement mis à jour avec succès !");

            // ✅ Fermer la fenêtre après la mise à jour
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de la mise à jour de l'événement.");
        }
    }

    // 📌 Fonction pour afficher des alertes
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
