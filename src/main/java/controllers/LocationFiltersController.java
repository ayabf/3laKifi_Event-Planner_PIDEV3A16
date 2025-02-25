package controllers;

import Models.City;
import Models.Location;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import java.util.function.Predicate;

public class LocationFiltersController {
    @FXML private TextField minPriceField;
    @FXML private TextField maxPriceField;
    @FXML private TextField minCapacityField;
    @FXML private TextField maxCapacityField;
    @FXML private ComboBox<City> cityComboBox;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private CheckBox has3DTourCheckbox;
    @FXML private CheckBox hasCornerPlantsCheckbox;
    @FXML private CheckBox hasCeilingLightsCheckbox;
    @FXML private Slider tableSetSlider;
    @FXML private Button applyFiltersButton;
    @FXML private Button clearFiltersButton;

    private Runnable onFiltersChanged;

    @FXML
    public void initialize() {
        System.out.println("Initializing LocationFiltersController");

        cityComboBox.getItems().addAll(City.values());
        statusComboBox.getItems().addAll("Active", "Inactive", "Under Maintenance");

        setupNumericValidation(minPriceField);
        setupNumericValidation(maxPriceField);
        setupNumericValidation(minCapacityField);
        setupNumericValidation(maxCapacityField);

        // Set initial slider value
        tableSetSlider.setMin(0);
        tableSetSlider.setMax(50);
        tableSetSlider.setValue(0);

        tableSetSlider.setLabelFormatter(new StringConverter<Double>() {
            @Override
            public String toString(Double value) {
                return String.format("%d tables", value.intValue());
            }

            @Override
            public Double fromString(String string) {
                return null;
            }
        });
    }

    private void setupNumericValidation(TextField field) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                field.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    public Predicate<Location> getFilterPredicate() {
        return location -> {
            if (location == null) {
                System.out.println("WARNING: Null location object received!");
                return false;
            }

            System.out.println("\nChecking filters for location: " + location.getName());
            System.out.println("Location details: " + location);
            boolean passes = true;

            try {
                // Price filters
                if (!minPriceField.getText().isEmpty()) {
                    double minPrice = Double.parseDouble(minPriceField.getText());
                    passes = location.getPrice() >= minPrice;
                    System.out.println("Min Price Filter: " + minPrice + " <= " + location.getPrice() + " = " + passes);
                    if (!passes) return false;
                }

                if (!maxPriceField.getText().isEmpty()) {
                    double maxPrice = Double.parseDouble(maxPriceField.getText());
                    passes = location.getPrice() <= maxPrice;
                    System.out.println("Max Price Filter: " + location.getPrice() + " <= " + maxPrice + " = " + passes);
                    if (!passes) return false;
                }

                // Capacity filters
                if (!minCapacityField.getText().isEmpty()) {
                    int minCapacity = Integer.parseInt(minCapacityField.getText());
                    passes = location.getCapacity() >= minCapacity;
                    System.out.println("Min Capacity Filter: " + minCapacity + " <= " + location.getCapacity() + " = " + passes);
                    if (!passes) return false;
                }

                if (!maxCapacityField.getText().isEmpty()) {
                    int maxCapacity = Integer.parseInt(maxCapacityField.getText());
                    passes = location.getCapacity() <= maxCapacity;
                    System.out.println("Max Capacity Filter: " + location.getCapacity() + " <= " + maxCapacity + " = " + passes);
                    if (!passes) return false;
                }

                // City filter
                if (cityComboBox.getValue() != null) {
                    City selectedCity = cityComboBox.getValue();
                    City locationCity = location.getVille();
                    passes = locationCity == selectedCity;
                    System.out.println("City Filter: comparing " + locationCity + " == " + selectedCity + " = " + passes);
                    if (!passes) return false;
                }

                // Status filter
                if (statusComboBox.getValue() != null) {
                    String selectedStatus = statusComboBox.getValue();
                    String locationStatus = location.getStatus();
                    passes = locationStatus != null && locationStatus.equalsIgnoreCase(selectedStatus);
                    System.out.println("Status Filter: comparing '" + locationStatus + "' with '" + selectedStatus + "' = " + passes);
                    if (!passes) return false;
                }

                // Feature filters
                if (has3DTourCheckbox.isSelected()) {
                    passes = location.getHas3DTour();
                    System.out.println("3D Tour Filter: " + location.getHas3DTour() + " = " + passes);
                    if (!passes) return false;
                }

                if (hasCornerPlantsCheckbox.isSelected()) {
                    passes = location.isIncludeCornerPlants();
                    System.out.println("Corner Plants Filter: " + location.isIncludeCornerPlants() + " = " + passes);
                    if (!passes) return false;
                }

                if (hasCeilingLightsCheckbox.isSelected()) {
                    passes = location.isIncludeCeilingLights();
                    System.out.println("Ceiling Lights Filter: " + location.isIncludeCeilingLights() + " = " + passes);
                    if (!passes) return false;
                }

                // Table set filter
                double sliderValue = tableSetSlider.getValue();
                if (sliderValue > tableSetSlider.getMin()) {
                    int minTables = (int) sliderValue;
                    passes = location.getTableSetCount() >= minTables;
                    System.out.println("Table Set Filter: " + location.getTableSetCount() + " >= " + minTables + " = " + passes);
                    if (!passes) return false;
                }

                System.out.println("Location " + location.getName() + " passed all filters");
                return true;
            } catch (Exception e) {
                System.out.println("ERROR while filtering location " + location.getName() + ": " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        };
    }

    @FXML
    private void handleApplyFilters() {
        System.out.println("\n=== Applying filters ===");
        System.out.println("Min Price: '" + minPriceField.getText() + "'");
        System.out.println("Max Price: '" + maxPriceField.getText() + "'");
        System.out.println("Min Capacity: '" + minCapacityField.getText() + "'");
        System.out.println("Max Capacity: '" + maxCapacityField.getText() + "'");
        System.out.println("Selected City: " + cityComboBox.getValue());
        System.out.println("Selected Status: '" + statusComboBox.getValue() + "'");
        System.out.println("3D Tour Required: " + has3DTourCheckbox.isSelected());
        System.out.println("Corner Plants Required: " + hasCornerPlantsCheckbox.isSelected());
        System.out.println("Ceiling Lights Required: " + hasCeilingLightsCheckbox.isSelected());
        System.out.println("Min Table Sets: " + tableSetSlider.getValue());

        if (onFiltersChanged != null) {
            onFiltersChanged.run();
        } else {
            System.out.println("WARNING: No filter change callback set!");
        }
    }

    @FXML
    private void handleClearFilters() {
        minPriceField.clear();
        maxPriceField.clear();
        minCapacityField.clear();
        maxCapacityField.clear();
        cityComboBox.setValue(null);
        statusComboBox.setValue(null);
        has3DTourCheckbox.setSelected(false);
        hasCornerPlantsCheckbox.setSelected(false);
        hasCeilingLightsCheckbox.setSelected(false);
        tableSetSlider.setValue(tableSetSlider.getMin());

        if (onFiltersChanged != null) {
            onFiltersChanged.run();
        }
    }

    public void setOnFiltersChanged(Runnable callback) {
        this.onFiltersChanged = callback;
    }
} 