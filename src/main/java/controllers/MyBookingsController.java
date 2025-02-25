package controllers;

import Models.Booking;
import Models.Event;
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
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import services.ServiceBooking;
import services.ServiceEvent;
import services.ServiceLocation;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.binding.Bindings;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyBookingsController {
    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking, String> eventNameColumn;
    @FXML private TableColumn<Booking, String> locationNameColumn;
    @FXML private TableColumn<Booking, String> startDateColumn;
    @FXML private TableColumn<Booking, String> endDateColumn;
    @FXML private TableColumn<Booking, String> statusColumn;
    @FXML private TableColumn<Booking, Void> actionsColumn;
    @FXML private TextField searchField;
    @FXML private StackPane noBookingsPane;

    private final ServiceBooking bookingService = new ServiceBooking();
    private final ServiceEvent eventService = new ServiceEvent();
    private final ServiceLocation locationService = new ServiceLocation();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private ObservableList<Booking> bookingList = FXCollections.observableArrayList();
    private Map<Integer, Event> eventCache = new HashMap<>();
    private Map<Integer, Location> locationCache = new HashMap<>();

    @FXML
    void initialize() {
        initializeColumns();
        loadBookings();
        setupSearch();
        updateNoBookingsPane();
    }

    private void initializeColumns() {
        eventNameColumn.setCellValueFactory(cellData -> {
            int eventId = cellData.getValue().getEvent_id();
            Event event = eventCache.get(eventId);
            return Bindings.createStringBinding(
                () -> event != null ? event.getName() : "Unknown Event"
            );
        });

        locationNameColumn.setCellValueFactory(cellData -> {
            int locationId = cellData.getValue().getLocation_id();
            Location location = locationCache.get(locationId);
            return Bindings.createStringBinding(
                () -> location != null ? location.getName() : "Unknown Location"
            );
        });

        startDateColumn.setCellValueFactory(cellData -> 
            Bindings.createStringBinding(
                () -> cellData.getValue().getStart_date().format(dateFormatter)
            ));

        endDateColumn.setCellValueFactory(cellData -> 
            Bindings.createStringBinding(
                () -> cellData.getValue().getEnd_date().format(dateFormatter)
            ));

        statusColumn.setCellValueFactory(cellData -> {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startDate = cellData.getValue().getStart_date();
            LocalDateTime endDate = cellData.getValue().getEnd_date();
            return Bindings.createStringBinding(() -> {
                if (now.isBefore(startDate)) return "Upcoming";
                if (now.isAfter(endDate)) return "Completed";
                return "Active";
            });
        });

        setupActionsColumn();
    }

    private void setupActionsColumn() {
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button viewButton = new Button();
            private final Button cancelButton = new Button();

            {
                viewButton.setGraphic(new FontAwesomeIconView(de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon.INFO_CIRCLE));
                cancelButton.setGraphic(new FontAwesomeIconView(de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon.TIMES));

                viewButton.getStyleClass().add("action-button");
                cancelButton.getStyleClass().addAll("action-button", "secondary");

                viewButton.setOnAction(event -> handleView(getTableRow().getItem()));
                cancelButton.setOnAction(event -> handleCancel(getTableRow().getItem()));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Booking booking = getTableRow().getItem();
                    if (booking != null) {
                        boolean isUpcoming = LocalDateTime.now().isBefore(booking.getStart_date());
                        cancelButton.setVisible(isUpcoming);
                        cancelButton.setManaged(isUpcoming);
                        
                        javafx.scene.layout.HBox buttons = new javafx.scene.layout.HBox(5, viewButton);
                        if (isUpcoming) {
                            buttons.getChildren().add(cancelButton);
                        }
                        buttons.setAlignment(javafx.geometry.Pos.CENTER);
                        setGraphic(buttons);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
    }

    private void loadBookings() {
        try {
            bookingList.clear();
            eventCache.clear();
            locationCache.clear();

            List<Booking> bookings = bookingService.getAll();

            for (Booking booking : bookings) {
                if (!eventCache.containsKey(booking.getEvent_id())) {
                    Event event = new Event();
                    event.setId_event(booking.getEvent_id());
                    event = eventService.getOne(event);
                    if (event != null) {
                        eventCache.put(booking.getEvent_id(), event);
                    }
                }
                
                if (!locationCache.containsKey(booking.getLocation_id())) {
                    Location location = new Location();
                    location.setId_location(booking.getLocation_id());
                    location = locationService.getOne(location);
                    if (location != null) {
                        locationCache.put(booking.getLocation_id(), location);
                    }
                }
            }
            
            bookingList.addAll(bookings);
            updateNoBookingsPane();
        } catch (SQLException e) {
            showError("Error loading bookings", e);
        }
    }

    private void setupSearch() {
        FilteredList<Booking> filteredData = new FilteredList<>(bookingList, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(booking -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();
                Event event = eventCache.get(booking.getEvent_id());
                Location location = locationCache.get(booking.getLocation_id());
                
                return (event != null && event.getName().toLowerCase().contains(lowerCaseFilter)) ||
                       (location != null && location.getName().toLowerCase().contains(lowerCaseFilter));
            });
        });
        bookingsTable.setItems(filteredData);
    }

    private void updateNoBookingsPane() {
        boolean hasBookings = !bookingList.isEmpty();
        bookingsTable.setVisible(hasBookings);
        bookingsTable.setManaged(hasBookings);
        noBookingsPane.setVisible(!hasBookings);
        noBookingsPane.setManaged(!hasBookings);
    }

    @FXML
    void handleRefresh() {
        loadBookings();
    }

    @FXML
    void handleBrowseEvents() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EventList.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Browse Events");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/global.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showError("Error loading events view", e);
        }
    }

    private void handleView(Booking booking) {
        Event event = eventCache.get(booking.getEvent_id());
        if (event != null) {
            EventDetailsController.showEventDetails(event);
        }
    }

    private void handleCancel(Booking booking) {
        if (LocalDateTime.now().isAfter(booking.getStart_date())) {
            showError("Cannot cancel booking", new Exception("Cannot cancel a booking that has already started"));
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel Booking");
        alert.setHeaderText("Cancel Booking");
        alert.setContentText("Are you sure you want to cancel this booking?");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                bookingService.supprimer(booking.getBooking_id());
                loadBookings();
            } catch (SQLException e) {
                showError("Error canceling booking", e);
            }
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