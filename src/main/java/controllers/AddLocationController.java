package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import Models.Location;
import Models.City;
import services.LocationService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.io.ByteArrayInputStream;
import javafx.scene.paint.Color;

interface LocationRefreshable {
    void refreshLocations();
}

public class AddLocationController {
    @FXML private Label formTitle;
    @FXML private TextField nameField;
    @FXML private TextField addressField;
    @FXML private ComboBox<City> cityComboBox;
    @FXML private TextField capacityField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextArea descriptionArea;
    @FXML private TextField dimensionField;
    @FXML private TextField priceField;
    @FXML private ImageView locationImage;
    @FXML private Button uploadButton;
    @FXML private Button saveButton;
    @FXML private CheckBox has3DTourCheckbox;
    @FXML private VBox tourCustomizationBox;
    @FXML private ComboBox<Integer> tableSetCountComboBox;
    @FXML private CheckBox includeCornerPlantsCheckbox;
    @FXML private ComboBox<String> windowStyleComboBox;
    @FXML private ComboBox<String> doorStyleComboBox;
    @FXML private CheckBox includeCeilingLightsCheckbox;
    @FXML private ColorPicker lightColorPicker;

    private LocationService locationService;
    private LocationRefreshable parentController;
    private Location locationToEdit;
    private byte[] selectedImageData;
    private String selectedImageFileName;

    @FXML
    public void initialize() {
        cityComboBox.getItems().addAll(City.values());

        statusComboBox.getItems().addAll("Active", "Inactive", "Under Maintenance");

        uploadButton.setOnAction(e -> handleImageUpload());

        initializeTourOptions();

        setupValidation();
    }

    public void setLocationService(LocationService locationService) {
        this.locationService = locationService;
    }

    public void setParentController(LocationRefreshable controller) {
        this.parentController = controller;
    }

    public void setLocationForEdit(Location location) {
        this.locationToEdit = location;
        formTitle.setText("Edit Location");
        saveButton.setText("Save Changes");

        nameField.setText(location.getName());
        addressField.setText(location.getAddress());
        cityComboBox.setValue(location.getVille());
        capacityField.setText(String.valueOf(location.getCapacity()));
        statusComboBox.setValue(location.getStatus());
        descriptionArea.setText(location.getDescription());
        dimensionField.setText(location.getDimension());
        priceField.setText(String.format("%.2f", location.getPrice()));
        has3DTourCheckbox.setSelected(location.getHas3DTour());

        if (location.getHas3DTour()) {
            tableSetCountComboBox.setValue(location.getTableSetCount() > 0 ? location.getTableSetCount() : 3);
            includeCornerPlantsCheckbox.setSelected(location.isIncludeCornerPlants());
            windowStyleComboBox.setValue(location.getWindowStyle() != null ? location.getWindowStyle() : "Modern");
            doorStyleComboBox.setValue(location.getDoorStyle() != null ? location.getDoorStyle() : "Modern");
            includeCeilingLightsCheckbox.setSelected(location.isIncludeCeilingLights());

            String lightColor = location.getLightColor();
            if (lightColor != null && !lightColor.isEmpty()) {
                try {
                    lightColorPicker.setValue(Color.valueOf(lightColor));
                } catch (IllegalArgumentException e) {
                    lightColorPicker.setValue(Color.YELLOW);
                }
            } else {
                lightColorPicker.setValue(Color.YELLOW);
            }
        }

        if (location.getImageData() != null && location.getImageData().length > 0) {
            selectedImageData = location.getImageData();
            selectedImageFileName = location.getImageFileName();
            Image image = new Image(new ByteArrayInputStream(selectedImageData));
            locationImage.setImage(image);
        }
    }

    @FXML
    private void handleSave() {
        if (!validateInput()) {
            return;
        }

        try {
            String colorString = has3DTourCheckbox.isSelected() ?
                String.format("#%02X%02X%02X", 
                    (int)(lightColorPicker.getValue().getRed() * 255),
                    (int)(lightColorPicker.getValue().getGreen() * 255),
                    (int)(lightColorPicker.getValue().getBlue() * 255)) : 
                "#FFFFFF";

            Location location = new Location(
                locationToEdit != null ? locationToEdit.getId_location() : 0,
                nameField.getText(),
                addressField.getText(),
                cityComboBox.getValue(),
                Integer.parseInt(capacityField.getText()),
                statusComboBox.getValue(),
                descriptionArea.getText(),
                dimensionField.getText(),
                Double.parseDouble(priceField.getText()),
                selectedImageData,
                selectedImageFileName,
                has3DTourCheckbox.isSelected(),
                has3DTourCheckbox.isSelected() ? tableSetCountComboBox.getValue() : 3,
                has3DTourCheckbox.isSelected() ? includeCornerPlantsCheckbox.isSelected() : true,
                has3DTourCheckbox.isSelected() ? windowStyleComboBox.getValue() : "Modern",
                has3DTourCheckbox.isSelected() ? doorStyleComboBox.getValue() : "Modern",
                has3DTourCheckbox.isSelected() ? includeCeilingLightsCheckbox.isSelected() : true,
                colorString
            );

            boolean success;
            if (locationToEdit != null) {
                success = locationService.update(location);
            } else {
                success = locationService.add(location);
            }

            if (success) {
                parentController.refreshLocations();
                handleClose();
            } else {
                showError("Error", "Could not save location");
            }
        } catch (NumberFormatException e) {
            showError("Invalid Input", "Please enter valid numbers for capacity and price");
        }
    }

    @FXML
    private void handleClose() {
        ((Stage) saveButton.getScene().getWindow()).close();
    }

    private void handleImageUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Location Image");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(uploadButton.getScene().getWindow());
        if (selectedFile != null) {
            try {
                selectedImageData = Files.readAllBytes(selectedFile.toPath());
                selectedImageFileName = selectedFile.getName();

                loadImage(selectedImageData);
            } catch (IOException e) {
                showError("Error", "Could not load image: " + e.getMessage());
            }
        }
    }

    private void loadImage(byte[] imageData) {
        try {
            Image image = new Image(new java.io.ByteArrayInputStream(imageData));
            locationImage.setImage(image);
        } catch (Exception e) {
            showError("Error", "Could not display image: " + e.getMessage());
        }
    }

    private boolean validateInput() {
        if (nameField.getText().isEmpty()) {
            showError("Invalid Input", "Name is required");
            return false;
        }
        if (addressField.getText().isEmpty()) {
            showError("Invalid Input", "Address is required");
            return false;
        }
        if (cityComboBox.getValue() == null) {
            showError("Invalid Input", "City is required");
            return false;
        }
        if (capacityField.getText().isEmpty()) {
            showError("Invalid Input", "Capacity is required");
            return false;
        }
        if (dimensionField.getText().isEmpty()) {
            showError("Invalid Input", "Dimension is required");
            return false;
        }
        if (priceField.getText().isEmpty()) {
            showError("Invalid Input", "Price is required");
            return false;
        }
        try {
            Integer.parseInt(capacityField.getText());
            Double.parseDouble(priceField.getText());
        } catch (NumberFormatException e) {
            showError("Invalid Input", "Please enter valid numbers for capacity and price");
            return false;
        }
        if (statusComboBox.getValue() == null) {
            showError("Invalid Input", "Status is required");
            return false;
        }
        return true;
    }

    private void setupValidation() {
        capacityField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                capacityField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        priceField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d*")) {
                priceField.setText(oldValue);
            }
        });
    }

    private void initializeTourOptions() {
        tableSetCountComboBox.getItems().addAll(1, 2, 3, 4, 5);
        tableSetCountComboBox.setValue(3);

        windowStyleComboBox.getItems().addAll(
            "Modern", "Classic", "Large", "Small"
        );
        windowStyleComboBox.setValue("Modern");

        doorStyleComboBox.getItems().addAll(
            "Modern", "Classic", "Double", "Single"
        );
        doorStyleComboBox.setValue("Modern");

        lightColorPicker.setValue(Color.WHITE);

        tourCustomizationBox.disableProperty().bind(
            has3DTourCheckbox.selectedProperty().not()
        );
    }

    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 