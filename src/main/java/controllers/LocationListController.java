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
import services.ServiceLocation;
import services.ServiceBooking;
import javafx.scene.layout.HBox;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class LocationListController {
    @FXML private TableView<Location> locationsTable;
    @FXML private TableColumn<Location, String> locationNameColumn;
    @FXML private TableColumn<Location, String> locationCityColumn;
    @FXML private TableColumn<Location, Integer> locationCapacityColumn;
    @FXML private TableColumn<Location, String> locationDimensionColumn;
    @FXML private TableColumn<Location, Double> locationPriceColumn;
    @FXML private TableColumn<Location, String> locationStatusColumn;
    @FXML private TableColumn<Location, Void> locationDetailsColumn;
    @FXML private TextField searchField;

    private final ServiceLocation locationService = new ServiceLocation();
    private final ServiceBooking bookingService = new ServiceBooking();
    private ObservableList<Location> locationList = FXCollections.observableArrayList();

    @FXML
    void initialize() {
        initializeColumns();
        loadLocations();
        setupSearch();
    }

    private void initializeColumns() {
        locationNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        locationCityColumn.setCellValueFactory(cellData -> 
            javafx.beans.binding.Bindings.createStringBinding(
                () -> cellData.getValue().getVille().name()
            ));
        locationCapacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        locationDimensionColumn.setCellValueFactory(new PropertyValueFactory<>("dimension"));
        locationPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        locationStatusColumn.setCellValueFactory(cellData -> {
            Location location = cellData.getValue();
            return javafx.beans.binding.Bindings.createStringBinding(() -> {
                try {
                    return bookingService.isLocationAvailable(
                        location.getId_location(),
                        LocalDateTime.now(),
                        LocalDateTime.now().plusHours(1)
                    ) ? "Available" : "Booked";
                } catch (SQLException e) {
                    e.printStackTrace();
                    return "Unknown";
                }
            });
        });

        // Details button column
        locationDetailsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button detailsButton = new Button();

            {
                FontAwesomeIconView icon = new FontAwesomeIconView();
                icon.setGlyphName("INFO_CIRCLE");
                icon.setSize("1.5em");
                detailsButton.setGraphic(icon);
                detailsButton.getStyleClass().add("action-button");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(detailsButton);
                    detailsButton.setOnAction(event -> {
                        Location selectedLocation = getTableView().getItems().get(getIndex());
                        showLocationDetails(selectedLocation);
                    });
                }
            }
        });
    }

    private void loadLocations() {
        try {
            locationList.clear();
            locationList.addAll(locationService.getAll());
            locationsTable.setItems(locationList);
        } catch (SQLException e) {
            showError("Error loading locations", e);
        }
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

    private void showLocationDetails(Location location) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LocationDetails.fxml"));
            Parent root = loader.load();
            
            LocationDetailsController controller = loader.getController();
            controller.setLocation(location);
            
            Stage stage = new Stage();
            stage.setTitle("Location Details - " + location.getName());
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/global.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showError("Error showing location details", e);
        }
    }

    @FXML
    void handleRefresh() {
        loadLocations();
    }

    @FXML
    void handleBack() {
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

    private void showError(String message, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
        e.printStackTrace();
    }
} 