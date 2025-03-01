package controllers;

import Models.Location;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import services.LocationService;
import javafx.scene.layout.HBox;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.binding.Bindings;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class LocationListController implements LocationRefreshable {
    @FXML private TableView<Location> locationsTable;
    @FXML private TableColumn<Location, String> nameColumn;
    @FXML private TableColumn<Location, String> addressColumn;
    @FXML private TableColumn<Location, String> cityColumn;
    @FXML private TableColumn<Location, Integer> capacityColumn;
    @FXML private TableColumn<Location, String> dimensionColumn;
    @FXML private TableColumn<Location, Double> priceColumn;
    @FXML private TableColumn<Location, String> statusColumn;
    @FXML private TableColumn<Location, Void> actionsColumn;
    @FXML private TextField searchField;
    @FXML private Label totalLocationsLabel;
    @FXML private Label activeLocationsLabel;

    private final LocationService locationService = new LocationService();
    private ObservableList<Location> locationList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        initializeColumns();
        loadLocations();
        setupSearch();
        updateStatistics();
    }

    private void initializeColumns() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        cityColumn.setCellValueFactory(cellData -> 
            Bindings.createStringBinding(
                () -> cellData.getValue().getVille().name()
            ));
        capacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        dimensionColumn.setCellValueFactory(new PropertyValueFactory<>("dimension"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Setup actions column with edit and delete buttons
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button();
            private final Button deleteBtn = new Button();
            private final HBox actionButtons = new HBox(5);

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
            Parent root = loader.load();
            
            AddLocationController controller = loader.getController();
            controller.setLocationService(locationService);
            controller.setParentController(this);
            
            Stage stage = new Stage();
            stage.setTitle("Add New Location");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/global.css").toExternalForm());
            stage.setScene(scene);
            
            stage.showAndWait();
            refreshLocations();
        } catch (IOException e) {
            showError("Error opening add location window", e);
        }
    }

    private void handleEditLocation(Location location) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddLocation.fxml"));
            Parent root = loader.load();
            
            AddLocationController controller = loader.getController();
            controller.setLocationService(locationService);
            controller.setParentController(this);
            controller.setLocationForEdit(location);
            
            Stage stage = new Stage();
            stage.setTitle("Edit Location");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/global.css").toExternalForm());
            stage.setScene(scene);
            
            stage.showAndWait();
            refreshLocations();
        } catch (IOException e) {
            showError("Error opening edit location window", e);
        }
    }

    private void handleDeleteLocation(Location location) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Delete Location");
        confirmDialog.setHeaderText("Delete " + location.getName());
        confirmDialog.setContentText("Are you sure you want to delete this location? This action cannot be undone.");
        confirmDialog.getDialogPane().getStyleClass().add("dialog-pane");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (locationService.delete(location.getId_location())) {
                refreshLocations();
                showInfo("Location deleted successfully");
            } else {
                showError("Error", "Could not delete location");
            }
        }
    }

    private void loadLocations() {
        locationList.clear();
        locationList.addAll(locationService.getAll());
        locationsTable.setItems(locationList);
    }

    private void setupSearch() {
        FilteredList<Location> filteredData = new FilteredList<>(locationList, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(location -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();
                return location.getName().toLowerCase().contains(lowerCaseFilter) ||
                       location.getVille().name().toLowerCase().contains(lowerCaseFilter) ||
                       location.getDimension().toLowerCase().contains(lowerCaseFilter);
            });
        });
        locationsTable.setItems(filteredData);
    }

    private void updateStatistics() {
        totalLocationsLabel.setText(String.valueOf(locationService.getTotalCount()));
        activeLocationsLabel.setText(String.valueOf(locationService.getActiveCount()));
    }

    @FXML
    private void handleRefresh() {
        refreshLocations();
    }

    @Override
    public void refreshLocations() {
        loadLocations();
        updateStatistics();
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminDashboard.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/global.css").toExternalForm());
            Stage stage = (Stage) locationsTable.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            showError("Error returning to dashboard", e);
        }
    }

    private void showError(String header, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
        e.printStackTrace();
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
} 