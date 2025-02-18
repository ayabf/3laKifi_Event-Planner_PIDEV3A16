package controllers;

import Models.Event;
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
import services.ServiceEvent;
import javafx.scene.layout.HBox;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventListController {
    @FXML private TableView<Event> eventsTable;
    @FXML private TableColumn<Event, String> eventNameColumn;
    @FXML private TableColumn<Event, String> eventDescriptionColumn;
    @FXML private TableColumn<Event, String> eventStartDateColumn;
    @FXML private TableColumn<Event, String> eventEndDateColumn;
    @FXML private TableColumn<Event, String> eventCityColumn;
    @FXML private TableColumn<Event, Integer> eventCapacityColumn;
    @FXML private TableColumn<Event, String> eventStatusColumn;
    @FXML private TableColumn<Event, Void> eventDetailsColumn;
    @FXML private TextField searchField;

    private final ServiceEvent eventService = new ServiceEvent();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private ObservableList<Event> eventList = FXCollections.observableArrayList();

    @FXML
    void initialize() {
        initializeColumns();
        loadEvents();
        setupSearch();
    }

    private void initializeColumns() {
        eventNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        eventDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        eventStartDateColumn.setCellValueFactory(cellData -> 
            javafx.beans.binding.Bindings.createStringBinding(
                () -> cellData.getValue().getStart_date().format(dateFormatter)
            ));
        eventEndDateColumn.setCellValueFactory(cellData -> 
            javafx.beans.binding.Bindings.createStringBinding(
                () -> cellData.getValue().getEnd_date().format(dateFormatter)
            ));
        eventCityColumn.setCellValueFactory(cellData -> 
            javafx.beans.binding.Bindings.createStringBinding(
                () -> cellData.getValue().getCity().name()
            ));
        eventCapacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        eventStatusColumn.setCellValueFactory(cellData -> {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startDate = cellData.getValue().getStart_date();
            LocalDateTime endDate = cellData.getValue().getEnd_date();
            return javafx.beans.binding.Bindings.createStringBinding(() -> {
                if (now.isBefore(startDate)) return "Upcoming";
                if (now.isAfter(endDate)) return "Completed";
                return "In Progress";
            });
        });

        // Details button column
        eventDetailsColumn.setCellFactory(param -> new TableCell<>() {
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
                        Event selectedEvent = getTableView().getItems().get(getIndex());
                        showEventDetails(selectedEvent);
                    });
                }
            }
        });
    }

    private void loadEvents() {
        try {
            eventList.clear();
            eventList.addAll(eventService.getAll());
            eventsTable.setItems(eventList);
        } catch (SQLException e) {
            showError("Error loading events", e);
        }
    }

    private void setupSearch() {
        FilteredList<Event> filteredData = new FilteredList<>(eventList, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(event -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();
                return event.getName().toLowerCase().contains(lowerCaseFilter) ||
                       event.getDescription().toLowerCase().contains(lowerCaseFilter) ||
                       event.getCity().name().toLowerCase().contains(lowerCaseFilter);
            });
        });
        eventsTable.setItems(filteredData);
    }

    private void showEventDetails(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EventDetails.fxml"));
            Parent root = loader.load();
            
            EventDetailsController controller = loader.getController();
            controller.setEvent(event);
            
            Stage stage = new Stage();
            stage.setTitle("Event Details - " + event.getName());
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/global.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showError("Error showing event details", e);
        }
    }

    @FXML
    void handleRefresh() {
        loadEvents();
    }

    @FXML
    void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminDashboard.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/global.css").toExternalForm());
            Stage stage = (Stage) eventsTable.getScene().getWindow();
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