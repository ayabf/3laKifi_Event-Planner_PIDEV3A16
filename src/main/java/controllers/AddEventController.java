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

public class AddEventController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button addImageButton;

    @FXML
    private TextField capacityTextField;

    @FXML
    private ComboBox<City> cityComboBox;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private DatePicker endDatePicker;

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
    private ComboBox<Integer> endHourComboBox;

    @FXML
    private ComboBox<Integer> endMinuteComboBox;


    private File selectedImageFile; // Stocke l'image sélectionnée

    private final ServiceEvent serviceEvent = new ServiceEvent(); // Service pour l'ajout en BD

    @FXML
    void initialize() {
        populateCityComboBox();
        populateTimeComboBoxes();
    }
    // Méthode pour remplir les ComboBox des heures et minutes
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

        // Sélectionner une valeur par défaut
        startHourComboBox.setValue(12);
        startMinuteComboBox.setValue(0);
        endHourComboBox.setValue(12);
        endMinuteComboBox.setValue(0);
    }

    // Remplissage de la ComboBox des villes avec l'énumération City
    private void populateCityComboBox() {
        List<City> cities = Arrays.asList(City.values());
        ObservableList<City> cityList = FXCollections.observableArrayList(cities);
        cityComboBox.setItems(cityList);
    }

    @FXML
    void addEvent(ActionEvent event) {
        try {
            String name = nameTextField.getText().trim();
            String description = descriptionTextArea.getText().trim();
            City city = cityComboBox.getValue();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            int startHour = startHourComboBox.getValue();
            int startMinute = startMinuteComboBox.getValue();
            int endHour = endHourComboBox.getValue();
            int endMinute = endMinuteComboBox.getValue();
            String capacityText = capacityTextField.getText().trim();

            // Vérification des champs obligatoires
            if (name.isEmpty() || description.isEmpty() || city == null || startDate == null || endDate == null || capacityText.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Champs obligatoires", "Veuillez remplir tous les champs.");
                return;
            }

            // Vérification de la capacité
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

            // Vérification des dates
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

            // Gestion de l'image
            String imagePath = selectedImageFile != null ? selectedImageFile.getAbsolutePath() : "default_event.png";

            // Création de l'objet Event
            Event newEvent = new Event(name, description, imagePath, startDateTime, endDateTime, capacity, city, 1);

            // Enregistrement en BD
            serviceEvent.ajouter(newEvent);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Événement ajouté avec succès !");

            // Fermer la fenêtre après ajout
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de l'ajout de l'événement.");
        }
    }


    @FXML
    void cancel(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    void chooseImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));

        selectedImageFile = fileChooser.showOpenDialog(new Stage());

        if (selectedImageFile != null) {
            Image image = new Image(selectedImageFile.toURI().toString());
            eventImageView.setImage(image);
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
