package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import Models.Location;
import Models.City;
import services.LocationService;
import java.io.IOException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.binding.Bindings;

public class LocationManagementController implements LocationRefreshable {
    @FXML private TableView<Location> locationsTable;
    @FXML private TableColumn<Location, String> nameColumn;
    @FXML private TableColumn<Location, String> addressColumn;
    @FXML private TableColumn<Location, String> cityColumn;
    @FXML private TableColumn<Location, Integer> capacityColumn;
    @FXML private TableColumn<Location, String> statusColumn;
    @FXML private TableColumn<Location, Double> priceColumn;
    @FXML private TableColumn<Location, Void> actionsColumn;
    @FXML private TextField searchField;
    @FXML private Label totalLocationsLabel;
    @FXML private Label activeLocationsLabel;
    @FXML private VBox filtersContainer;

    private LocationService locationService;
    private ObservableList<Location> locationsList;
    private LocationFiltersController filtersController;

    @FXML
    public void initialize() {
        System.out.println("Initializing LocationManagementController");
        locationService = new LocationService();
        locationsList = FXCollections.observableArrayList();

        // Initialize table columns
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        cityColumn.setCellValueFactory(cellData ->
                Bindings.createStringBinding(
                        () -> cellData.getValue().getVille().name()
                ));
        capacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        setupActionsColumn();

        // Load and initialize filters
        initializeFilters();

        // Set up search field listener
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });

        // Initial data load
        refreshLocations();
    }

    private void initializeFilters() {
        try {
            System.out.println("Loading filters view...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LocationFilters.fxml"));
            VBox filtersView = loader.load();
            filtersController = loader.getController();

            if (filtersContainer != null) {
                filtersContainer.getChildren().clear();
                filtersContainer.getChildren().add(filtersView);
                System.out.println("Filters view added to container");

                // Set up filter change callback
                filtersController.setOnFiltersChanged(() -> {
                    System.out.println("Filter change callback triggered");
                    applyFilters();
                });
            } else {
                System.out.println("ERROR: filtersContainer is null!");
            }
        } catch (IOException e) {
            System.out.println("ERROR loading filters: " + e.getMessage());
            e.printStackTrace();
            showError("Error loading filters", e.getMessage());
        }
    }

    private void setupActionsColumn() {
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("");
            private final Button deleteBtn = new Button("");
            private final HBox actionButtons = new HBox(2);

            {
                FontAwesomeIconView editIcon = new FontAwesomeIconView(de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon.PENCIL);
                editIcon.setSize("14");
                editBtn.setGraphic(editIcon);
                editBtn.getStyleClass().add("action-button");
                editBtn.setStyle("-fx-min-width: 24px; -fx-max-width: 24px; -fx-min-height: 24px; -fx-max-height: 24px; -fx-padding: 2;");
                editBtn.setTooltip(new Tooltip("Edit Location"));
                editBtn.setOnAction(event -> {
                    Location location = getTableView().getItems().get(getIndex());
                    handleEditLocation(location);
                });

                FontAwesomeIconView deleteIcon = new FontAwesomeIconView(de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon.TRASH);
                deleteIcon.setSize("14");
                deleteBtn.setGraphic(deleteIcon);
                deleteBtn.getStyleClass().addAll("action-button", "delete-button");
                deleteBtn.setStyle("-fx-min-width: 24px; -fx-max-width: 24px; -fx-min-height: 24px; -fx-max-height: 24px; -fx-padding: 2;");
                deleteBtn.setTooltip(new Tooltip("Delete Location"));
                deleteBtn.setOnAction(event -> {
                    Location location = getTableView().getItems().get(getIndex());
                    handleDeleteLocation(location);
                });

                actionButtons.setAlignment(javafx.geometry.Pos.CENTER);
                actionButtons.getChildren().addAll(editBtn, deleteBtn);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(actionButtons);
                }
            }
        });
    }

    @FXML
    private void handleAddLocation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddLocation.fxml"));
            VBox addLocationView = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add New Location");
            stage.setScene(new Scene(addLocationView));

            AddLocationController controller = loader.getController();
            controller.setLocationService(locationService);
            controller.setParentController(this);

            stage.showAndWait();
        } catch (IOException e) {
            showError("Error opening add location window", e.getMessage());
        }
    }

    private void handleEditLocation(Location location) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddLocation.fxml"));
            VBox editLocationView = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Edit Location");
            stage.setScene(new Scene(editLocationView));

            AddLocationController controller = loader.getController();
            controller.setLocationService(locationService);
            controller.setParentController(this);
            controller.setLocationForEdit(location);

            stage.showAndWait();
        } catch (IOException e) {
            showError("Error opening edit location window", e.getMessage());
        }
    }

    private void handleDeleteLocation(Location location) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Location");
        alert.setHeaderText("Delete " + location.getName());
        alert.setContentText("Are you sure you want to delete this location? This action cannot be undone.");

        if (alert.showAndWait().get() == ButtonType.OK) {
            if (locationService.delete(location.getId_location())) {
                refreshLocations();
                showInfo("Location deleted successfully");
            } else {
                showError("Error", "Could not delete location");
            }
        }
    }

    @FXML
    private void handleRefresh() {
        refreshLocations();
    }

    @Override
    public void refreshLocations() {
        System.out.println("\nRefreshing locations from database");
        List<Location> locations = locationService.getAll();
        System.out.println("Retrieved " + locations.size() + " locations");

        locationsList.setAll(locations);
        System.out.println("Updated locationsList with " + locationsList.size() + " items");

        // Apply filters to the new data
        applyFilters();
    }

    private void applyFilters() {
        if (locationsList == null) {
            System.out.println("ERROR: locationsList is null!");
            return;
        }

        System.out.println("\nApplying filters to " + locationsList.size() + " locations");
        ObservableList<Location> filteredList = FXCollections.observableArrayList();
        String searchText = searchField.getText().toLowerCase();

        for (Location location : locationsList) {
            boolean matchesSearch = matchesSearch(location, searchText);
            boolean matchesFilters = true;

            if (filtersController != null) {
                try {
                    matchesFilters = filtersController.getFilterPredicate().test(location);
                } catch (Exception e) {
                    System.out.println("Error applying filters to location " + location.getName() + ": " + e.getMessage());
                    e.printStackTrace();
                    matchesFilters = false;
                }
            }

            System.out.println("\nLocation: " + location.getName());
            System.out.println("Matches search: " + matchesSearch);
            System.out.println("Matches filters: " + matchesFilters);

            if (matchesSearch && matchesFilters) {
                filteredList.add(location);
                System.out.println("Added to filtered list");
            }
        }

        System.out.println("\nFiltered list size: " + filteredList.size());
        locationsTable.setItems(filteredList);
        updateStatistics();
    }

    private boolean matchesSearch(Location location, String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            return true;
        }

        boolean matches = location.getName().toLowerCase().contains(searchText) ||
                location.getAddress().toLowerCase().contains(searchText) ||
                location.getVille().name().toLowerCase().contains(searchText);

        System.out.println("Search text '" + searchText + "' matches location " + location.getName() + ": " + matches);
        return matches;
    }

    private void updateStatistics() {
        totalLocationsLabel.setText(String.valueOf(locationService.getTotalCount()));
        activeLocationsLabel.setText(String.valueOf(locationService.getActiveCount()));
    }

    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadLocations() {
        List<Location> locations = locationService.getAll();
        locationsList.setAll(locations);
        locationsTable.setItems(locationsList);
    }
} 