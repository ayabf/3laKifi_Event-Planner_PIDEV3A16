package controllers;

import Models.Booking;
import Models.Event;
import Models.Location;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import services.ServiceBooking;
import services.ServiceEvent;
import services.ServiceLocation;
import utils.NotificationManager;
import Models.Notification.NotificationType;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class BookingCardController {
    @FXML private HBox statusContainer;
    @FXML private FontAwesomeIconView statusIcon;
    @FXML private Label statusText;
    @FXML private Label eventName;
    @FXML private Label locationName;
    @FXML private Label startDate;
    @FXML private Label endDate;
    @FXML private Button viewButton;
    @FXML private Button cancelButton;
    @FXML private Button exportButton;

    private Booking booking;
    private Event event;
    private Location location;
    private final ServiceBooking bookingService = new ServiceBooking();
    private final ServiceEvent eventService = new ServiceEvent();
    private final ServiceLocation locationService = new ServiceLocation();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private Consumer<Booking> onExport;
    private final NotificationManager notificationManager = NotificationManager.getInstance();

    @FXML
    public void initialize() {
        exportButton.setOnAction(e -> {
            if (onExport != null && booking != null) {
                onExport.accept(booking);
            }
        });
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
        loadEventAndLocation();
        updateCard();
    }

    public void setOnExport(Consumer<Booking> onExport) {
        this.onExport = onExport;
    }

    private void loadEventAndLocation() {
        try {
            event = eventService.getById(booking.getEvent_id());
            location = locationService.getById(booking.getLocation_id());
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error loading booking details", e);
        }
    }

    private void updateCard() {
        if (booking != null && event != null && location != null) {
            eventName.setText(event.getName());
            locationName.setText(location.getName() + " (" + location.getVille()+ ")");
            startDate.setText(booking.getStart_date().format(dateFormatter));
            endDate.setText("Until " + booking.getEnd_date().format(dateFormatter));
            updateStatus();
        }
    }

    private void updateStatus() {
        LocalDateTime now = LocalDateTime.now();
        String status;
        String iconName;
        String styleClass;

        if (now.isBefore(booking.getStart_date())) {
            status = "Upcoming";
            iconName = "CLOCK";
            styleClass = "upcoming";
        } else if (now.isAfter(booking.getEnd_date())) {
            status = "Completed";
            iconName = "CHECK_CIRCLE";
            styleClass = "completed";
            cancelButton.setDisable(true);
        } else {
            status = "In Progress";
            iconName = "HOURGLASS_HALF";
            styleClass = "in-progress";
            cancelButton.setDisable(true);
        }

        statusContainer.getStyleClass().removeAll("upcoming", "completed", "in-progress");
        statusContainer.getStyleClass().add(styleClass);
        statusIcon.setGlyphName(iconName);
        statusText.setText(status);
    }

    @FXML
    private void handleView() {
        if (event != null) {
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
                e.printStackTrace();
                showError("Error showing event details", e);
            }
        }
    }

    @FXML
    private void handleCancel() {
        if (LocalDateTime.now().isAfter(booking.getStart_date())) {
            notificationManager.showNotification(
                    "Cannot Cancel Booking",
                    "Cannot cancel a booking that has already started",
                    NotificationType.BOOKING_CANCELLED
            );
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel Booking");
        alert.setHeaderText("Are you sure you want to cancel this booking?");
        alert.setContentText("This action cannot be undone.");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                Event event = eventService.getById(booking.getEvent_id());
                Location location = locationService.getById(booking.getLocation_id());

                bookingService.supprimer(booking.getBooking_id());

                notificationManager.showNotification(
                        "Booking Cancelled",
                        "Your booking for " + event.getName() + " at " + location.getName() + " has been cancelled",
                        NotificationType.BOOKING_CANCELLED
                );

                notificationManager.showNotification(
                        "Location Available",
                        location.getName() + " is now available for booking",
                        NotificationType.LOCATION_AVAILABLE
                );

                refreshParentScene();
            } catch (SQLException e) {
                e.printStackTrace();
                showError("Error canceling booking", e);
            }
        }
    }

    private void refreshParentScene() {
        try {
            Scene scene = statusContainer.getScene();
            if (scene != null) {
                Window window = scene.getWindow();
                if (window instanceof Stage) {
                    Stage stage = (Stage) window;
                    Parent root = scene.getRoot();

                    if (root.getId().equals("clientDashboard")) {
                        ClientDashboardController controller = (ClientDashboardController) scene.getUserData();
                        if (controller != null) {
                            controller.handleRefresh();
                        }
                    } else if (root.getId().equals("myBookings")) {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MyBookings.fxml"));
                        root = loader.load();
                        MyBookingsController controller = loader.getController();

                        Scene newScene = new Scene(root);
                        newScene.getStylesheets().add(getClass().getResource("/styles/global.css").toExternalForm());
                        stage.setScene(newScene);

                        controller.initialize();
                    } else if (root.getId().equals("myEvents")) {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MyEvents.fxml"));
                        root = loader.load();
                        MyEventsController controller = loader.getController();

                        Scene newScene = new Scene(root);
                        newScene.getStylesheets().add(getClass().getResource("/styles/global.css").toExternalForm());
                        stage.setScene(newScene);

                        controller.initialize();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error refreshing scene: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String message, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        alert.setContentText(e.getMessage());
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