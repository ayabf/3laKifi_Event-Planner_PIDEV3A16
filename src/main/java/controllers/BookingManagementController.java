package controllers;

import Models.Booking;
import Models.Event;
import Models.Location;
import services.ServiceBooking;
import services.ServiceEvent;
import services.ServiceLocation;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Pos;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

public class BookingManagementController {
    @FXML private ResourceBundle resources;
    @FXML private URL location;
    @FXML private VBox bookingsContainer;
    @FXML private TextField searchField;
    @FXML private AnchorPane main_form;

    private final ServiceBooking bookingService = new ServiceBooking();
    private final ServiceEvent eventService = new ServiceEvent();
    private final ServiceLocation locationService = new ServiceLocation();
    private final ObservableList<Booking> bookingList = FXCollections.observableArrayList();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private Map<Integer, Event> eventCache = new HashMap<>();
    private Map<Integer, Location> locationCache = new HashMap<>();

    @FXML
    public void initialize() {
        loadEventAndLocationData();
        loadBookingData();
        setupSearchFunctionality();
    }

    private void loadEventAndLocationData() {
        try {
            List<Event> events = eventService.getAll();
            for (Event event : events) {
                eventCache.put(event.getId_event(), event);
            }

            List<Location> locations = locationService.getAll();
            for (Location location : locations) {
                locationCache.put(location.getId_location(), location);
            }
        } catch (SQLException e) {
            showError("Error loading events and locations", e);
        }
    }

    private HBox createBookingRow(Booking booking) {
        HBox row = new HBox(5);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("booking-row");

        String eventName = getEventName(booking.getEvent_id());
        String locationName = getLocationName(booking.getLocation_id());

        Label idLabel = createLabel(String.valueOf(booking.getBooking_id()), 80);
        Label eventLabel = createLabel(eventName, 200);
        Label locationLabel = createLabel(locationName, 200);
        Label startDateLabel = createLabel(booking.getStart_date().format(dateFormatter), 150);
        Label endDateLabel = createLabel(booking.getEnd_date().format(dateFormatter), 150);

        HBox actionsBox = new HBox(5);
        actionsBox.setAlignment(Pos.CENTER);
        actionsBox.setMinWidth(200);
        actionsBox.setMaxWidth(200);

        Button editButton = createActionButton("Modifier", "EDIT", () -> updateBooking(booking));
        Button deleteButton = createActionButton("Supprimer", "TRASH", () -> deleteBooking(booking));
        deleteButton.getStyleClass().add("secondary");

        actionsBox.getChildren().addAll(editButton, deleteButton);

        row.getChildren().addAll(
                idLabel, eventLabel, locationLabel,
                startDateLabel, endDateLabel, actionsBox
        );

        return row;
    }

    private String getEventName(int eventId) {
        Event event = eventCache.get(eventId);
        return event != null ? event.getName() : "Unknown Event";
    }

    private String getLocationName(int locationId) {
        Location location = locationCache.get(locationId);
        return location != null ? location.getName() : "Unknown Location";
    }

    private Label createLabel(String text, double width) {
        Label label = new Label(text);
        label.setMinWidth(width);
        label.setMaxWidth(width);
        label.getStyleClass().add("cell");
        return label;
    }

    private Button createActionButton(String text, String icon, Runnable action) {
        Button button = new Button(text);
        button.getStyleClass().add("action-button");

        FontAwesomeIconView iconView = new FontAwesomeIconView();
        iconView.setGlyphName(icon);
        iconView.setSize("1.2em");
        button.setGraphic(iconView);

        button.setOnAction(event -> action.run());
        return button;
    }

    @FXML
    private void loadBookingData() {
        try {
            List<Booking> bookings = bookingService.getAll();
            bookingList.setAll(bookings);
            displayBookings(bookings);
        } catch (SQLException e) {
            showError("Error loading bookings", e);
        }
    }

    private void displayBookings(List<Booking> bookings) {
        bookingsContainer.getChildren().clear();
        for (Booking booking : bookings) {
            bookingsContainer.getChildren().add(createBookingRow(booking));
        }
    }

    private void setupSearchFunctionality() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                displayBookings(bookingList);
            } else {
                String searchText = newValue.toLowerCase();
                List<Booking> filteredBookings = bookingList.stream()
                        .filter(booking -> {
                            String eventName = getEventName(booking.getEvent_id()).toLowerCase();
                            String locationName = getLocationName(booking.getLocation_id()).toLowerCase();
                            return eventName.contains(searchText) ||
                                    locationName.contains(searchText) ||
                                    String.valueOf(booking.getBooking_id()).contains(searchText);
                        })
                        .collect(Collectors.toList());
                displayBookings(filteredBookings);
            }
        });
    }

    @FXML
    void handleHome() {
        try {
            URL dashboardUrl = getClass().getResource("/AdminDashboard.fxml");
            if (dashboardUrl == null) {
                throw new IOException("Cannot find AdminDashboard.fxml");
            }
            FXMLLoader loader = new FXMLLoader(dashboardUrl);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/global.css").toExternalForm());
            Stage stage = (Stage) bookingsContainer.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            showError("Error loading dashboard", e);
        }
    }

    @FXML
    void handleLogout() {
        try {
            URL loginUrl = getClass().getResource("/log.fxml");
            if (loginUrl == null) {
                throw new IOException("Cannot find log.fxml");
            }
            FXMLLoader loader = new FXMLLoader(loginUrl);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/global.css").toExternalForm());
            Stage stage = (Stage) bookingsContainer.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            showError("Error during logout", e);
        }
    }

    private void updateBooking(Booking booking) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateBooking.fxml"));
            Parent root = loader.load();

            UpdateBookingController controller = loader.getController();
            controller.setBooking(booking);

            Stage stage = new Stage();
            stage.setTitle("Modifier la réservation");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/global.css").toExternalForm());
            stage.setScene(scene);
            stage.showAndWait();

            loadBookingData();
        } catch (IOException e) {
            showError("Error loading update booking form", e);
        }
    }

    private void deleteBooking(Booking booking) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer la réservation");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette réservation ?");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                bookingService.supprimer(booking.getBooking_id());
                loadBookingData();
                showInfo("Réservation supprimée avec succès");
            } catch (SQLException e) {
                showError("Error deleting booking", e);
            }
        }
    }

    private void showError(String message, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(message);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
        e.printStackTrace();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 