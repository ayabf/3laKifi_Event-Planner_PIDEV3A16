package controllers;

import Models.Event;
import Models.session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import services.ServiceEvent;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MyEventsController {
    @FXML private TableView<Event> eventsTable;
    @FXML private TableColumn<Event, String> eventNameColumn;
    @FXML private TableColumn<Event, String> eventDescriptionColumn;
    @FXML private TableColumn<Event, String> eventStartDateColumn;
    @FXML private TableColumn<Event, String> eventEndDateColumn;
    @FXML private TableColumn<Event, String> eventCityColumn;
    @FXML private TableColumn<Event, Integer> eventCapacityColumn;
    @FXML private TableColumn<Event, String> eventStatusColumn;
    @FXML private TableColumn<Event, Void> eventActionsColumn;
    @FXML private TextField searchField;
    @FXML private StackPane noEventsPane;

    private final ServiceEvent eventService = new ServiceEvent();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private ObservableList<Event> eventList = FXCollections.observableArrayList();

    @FXML
    void initialize() {
        System.out.println("Initializing MyEventsController with user ID: " + session.id_utilisateur);
        initializeColumns();
        loadEvents();
        setupSearch();
        updateNoEventsPane();
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

        setupActionsColumn();
    }

    private void setupActionsColumn() {
        eventActionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button();
            private final Button deleteButton = new Button();
            private final Button detailsButton = new Button();

            {
                editButton.setGraphic(new FontAwesomeIconView(de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon.EDIT));
                deleteButton.setGraphic(new FontAwesomeIconView(de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon.TRASH));
                detailsButton.setGraphic(new FontAwesomeIconView(de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon.INFO_CIRCLE));

                editButton.getStyleClass().add("action-button");
                deleteButton.getStyleClass().addAll("action-button", "secondary");
                detailsButton.getStyleClass().add("action-button");

                editButton.setOnAction(event -> handleEdit(getTableRow().getItem()));
                deleteButton.setOnAction(event -> handleDelete(getTableRow().getItem()));
                detailsButton.setOnAction(event -> handleDetails(getTableRow().getItem()));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    javafx.scene.layout.HBox buttons = new javafx.scene.layout.HBox(5, editButton, deleteButton, detailsButton);
                    buttons.setAlignment(javafx.geometry.Pos.CENTER);
                    setGraphic(buttons);
                }
            }
        });
    }

    private void loadEvents() {
        try {
            eventList.clear();
            int currentUserId = session.id_utilisateur;
            System.out.println("Loading events for user ID: " + currentUserId);
            List<Event> events = eventService.getEventsByUser(currentUserId);
            System.out.println("Found " + events.size() + " events for user " + currentUserId);
            eventList.addAll(events);
            updateNoEventsPane();
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

    private void updateNoEventsPane() {
        boolean hasEvents = !eventList.isEmpty();
        eventsTable.setVisible(hasEvents);
        eventsTable.setManaged(hasEvents);
        noEventsPane.setVisible(!hasEvents);
        noEventsPane.setManaged(!hasEvents);
    }

    @FXML
    void handleCreateEvent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddEvent.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Create New Event");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/global.css").toExternalForm());
            stage.setScene(scene);
            stage.showAndWait();
            loadEvents();
        } catch (IOException e) {
            showError("Error loading create event form", e);
        }
    }

    @FXML
    void handleRefresh() {
        loadEvents();
    }

    private void handleEdit(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddEvent.fxml"));
            Parent root = loader.load();
            
            AddEventController controller = loader.getController();
            controller.setEventForEdit(event);
            
            Stage stage = new Stage();
            stage.setTitle("Edit Event");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/global.css").toExternalForm());
            stage.setScene(scene);
            stage.showAndWait();
            loadEvents();
        } catch (IOException e) {
            showError("Error loading edit event form", e);
        }
    }

    private void handleDelete(Event event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Event");
        alert.setHeaderText("Delete Event: " + event.getName());
        alert.setContentText("Are you sure you want to delete this event?");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                eventService.supprimer(event.getId_event());
                loadEvents();
            } catch (SQLException e) {
                showError("Error deleting event", e);
            }
        }
    }

    private void handleDetails(Event event) {
        EventDetailsController.showEventDetails(event);
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