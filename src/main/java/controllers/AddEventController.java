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

    private File selectedImageFile; // Stocke l'image sélectionnée

    private final ServiceEvent serviceEvent = new ServiceEvent(); // Service pour l'ajout en BD

    @FXML
    void initialize() {
        populateCityComboBox();
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
            // Récupération des valeurs des champs
            String name = nameTextField.getText().trim();
            String description = descriptionTextArea.getText().trim();
            City city = cityComboBox.getValue();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            String capacityText = capacityTextField.getText().trim();

            // Validation des champs
            if (name.isEmpty() || description.isEmpty() || city == null || startDate == null || endDate == null || capacityText.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Champs obligatoires", "Veuillez remplir tous les champs.");
                return;
            }
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atStartOfDay();

            // Vérification des dates
            if (startDate.isBefore(LocalDate.now())) {
                showAlert(Alert.AlertType.ERROR, "Date invalide", "La date de début ne peut pas être dans le passé.");
                return;
            }
            if (endDate.isBefore(startDate)) {
                showAlert(Alert.AlertType.ERROR, "Date invalide", "La date de fin doit être après la date de début.");
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

            // Gestion de l'image (par défaut, une image par défaut sera utilisée si aucune n'est choisie)
            String imagePath = selectedImageFile != null ? selectedImageFile.getAbsolutePath() : "default_event.png";

            // Création de l'objet Event
            Event newEvent = new Event(name, description, imagePath, startDateTime, endDateTime, capacity, city, 1);

            // Enregistrement dans la BD
            serviceEvent.ajouter(newEvent);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Événement ajouté avec succès !");

            // Fermer la fenêtre après l'ajout
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
