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

    private LocationService locationService;
    private ObservableList<Location> locationsList;

    @FXML
    public void initialize() {
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
        
        // Setup actions column
        setupActionsColumn();

        // Load locations
        refreshLocations();

        // Setup search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterLocations(newValue);
        });
    }

    private void setupActionsColumn() {
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("");
            private final Button deleteBtn = new Button("");
            private final HBox actionButtons = new HBox(2);

            {
                // Edit button
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

                // Delete button
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
        List<Location> locations = locationService.getAll();
        locationsList.setAll(locations);
        locationsTable.setItems(locationsList);
        updateStatistics();
    }

    private void filterLocations(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            locationsTable.setItems(locationsList);
        } else {
            ObservableList<Location> filteredList = FXCollections.observableArrayList();
            locationsList.forEach(location -> {
                if (matchesSearch(location, searchText.toLowerCase())) {
                    filteredList.add(location);
                }
            });
            locationsTable.setItems(filteredList);
        }
    }

    private boolean matchesSearch(Location location, String searchText) {
        return location.getName().toLowerCase().contains(searchText) ||
               location.getAddress().toLowerCase().contains(searchText) ||
               location.getVille().name().toLowerCase().contains(searchText);
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