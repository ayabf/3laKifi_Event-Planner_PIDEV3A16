package controllers;

import Models.Location;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import java.util.List;
import java.util.function.Predicate;
import services.LocationService;

public class LocationsViewController {
    @FXML private LocationFiltersController filtersPaneController;
    @FXML private FlowPane locationsContainer;
    @FXML private TextField searchField;
    @FXML private StackPane noLocationsPane;

    private LocationService locationService;
    private ObservableList<Location> allLocations;
    private FilteredList<Location> filteredLocations;

    @FXML
    public void initialize() {
        locationService = new LocationService();
        allLocations = FXCollections.observableArrayList();
        filteredLocations = new FilteredList<>(allLocations);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });

        filtersPaneController.setOnFiltersChanged(() -> {
            applyFilters();
        });

        refreshLocations();
    }

    private void applyFilters() {
        Predicate<Location> searchPredicate = location -> {
            if (searchField.getText() == null || searchField.getText().isEmpty()) {
                return true;
            }
            String searchText = searchField.getText().toLowerCase();
            return location.getName().toLowerCase().contains(searchText) ||
                    location.getAddress().toLowerCase().contains(searchText) ||
                    location.getVille().toString().toLowerCase().contains(searchText);
        };

        Predicate<Location> filtersPredicate = filtersPaneController.getFilterPredicate();

        filteredLocations.setPredicate(searchPredicate.and(filtersPredicate));
        updateLocationCards();
    }

    private void updateLocationCards() {
        locationsContainer.getChildren().clear();

        if (filteredLocations.isEmpty()) {
            noLocationsPane.setVisible(true);
            noLocationsPane.setManaged(true);
        } else {
            noLocationsPane.setVisible(false);
            noLocationsPane.setManaged(false);

            for (Location location : filteredLocations) {
                VBox locationCard = createLocationCard(location);
                locationsContainer.getChildren().add(locationCard);
            }
        }
    }

    private VBox createLocationCard(Location location) {
        VBox card = new VBox(10);
        card.getStyleClass().add("location-card");

        Label nameLabel = new Label(location.getName());
        nameLabel.getStyleClass().add("card-title");

        VBox details = new VBox(5);
        details.getChildren().addAll(
                new Label("Address: " + location.getAddress()),
                new Label("City: " + location.getVille()),
                new Label("Capacity: " + location.getCapacity()),
                new Label("Price: " + location.getPrice() + " DT")
        );

        HBox actions = new HBox(10);
        Button editButton = new Button("Edit");
        editButton.setOnAction(e -> handleEditLocation(location));
        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> handleDeleteLocation(location));
        actions.getChildren().addAll(editButton, deleteButton);

        card.getChildren().addAll(nameLabel, details, actions);
        return card;
    }

    @FXML
    private void handleAddLocation() {
    }

    @FXML
    private void handleRefresh() {
        refreshLocations();
    }

    private void handleEditLocation(Location location) {
    }

    private void handleDeleteLocation(Location location) {
    }

    private void refreshLocations() {
        List<Location> locations = locationService.getAll();
        allLocations.setAll(locations);
        applyFilters();
    }
} 