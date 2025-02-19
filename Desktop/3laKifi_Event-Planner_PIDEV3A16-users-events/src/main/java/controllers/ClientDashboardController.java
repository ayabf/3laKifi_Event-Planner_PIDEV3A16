package controllers;

import Models.Event;
import Models.Booking;
import Models.session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import services.ServiceEvent;
import services.ServiceBooking;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import javafx.event.ActionEvent;

public class ClientDashboardController {
    @FXML private FlowPane myEventsContainer;
    @FXML private FlowPane myReservationsContainer;
    @FXML private StackPane noEventsPane;
    @FXML private StackPane noReservationsPane;
    @FXML private Button logoutButton;
    @FXML private Button refreshButton;

    private final ServiceEvent eventService = new ServiceEvent();
    private final ServiceBooking bookingService = new ServiceBooking();

    @FXML
    public void initialize() {
        System.out.println("Initializing ClientDashboardController with user ID: " + session.id_utilisateur);
        loadMyEvents();
        loadMyReservations();
    }

    private void loadMyEvents() {
        try {
            int currentUserId = session.id_utilisateur;
            System.out.println("Loading events for user ID: " + currentUserId);
            List<Event> events = eventService.getEventsByUser(currentUserId);
            myEventsContainer.getChildren().clear();

            if (events.isEmpty()) {
                showNoEvents(true);
            } else {
                showNoEvents(false);
                for (Event event : events) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/EventCard.fxml"));
                        Parent eventCard = loader.load();
                        EventCardController controller = loader.getController();
                        controller.setEvent(event);
                        controller.setOnEdit(e -> handleEditEvent(event));
                        controller.setOnDelete(e -> handleDeleteEvent(event));
                        myEventsContainer.getChildren().add(eventCard);
                    } catch (IOException e) {
                        e.printStackTrace();
                        showError("Error loading event card", e);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error loading events", e);
        }
    }

    private void loadMyReservations() {
        try {
            List<Booking> bookings = bookingService.getBookingsByUser(session.id_utilisateur);
            myReservationsContainer.getChildren().clear();

            if (bookings.isEmpty()) {
                showNoReservations(true);
            } else {
                showNoReservations(false);
                for (Booking booking : bookings) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/BookingCard.fxml"));
                        Parent bookingCard = loader.load();
                        BookingCardController controller = loader.getController();
                        controller.setBooking(booking);
                        myReservationsContainer.getChildren().add(bookingCard);
                    } catch (IOException e) {
                        e.printStackTrace();
                        showError("Error loading booking card", e);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error loading reservations", e);
        }
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
            loadMyEvents(); // Refresh after creating
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error opening event creation form", e);
        }
    }

    @FXML
    void handleBrowseEvents() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EventGrid.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Browse Events");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/global.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error opening events browser", e);
        }
    }

    private void handleEditEvent(Event event) {
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
            loadMyEvents(); // Refresh after editing
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error opening event edit form", e);
        }
    }

    private void handleDeleteEvent(Event event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Event");
        alert.setHeaderText("Are you sure you want to delete this event?");
        alert.setContentText("This action cannot be undone.");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                eventService.supprimer(event.getId_event());
                loadMyEvents(); // Refresh after deleting
            } catch (SQLException e) {
                e.printStackTrace();
                showError("Error deleting event", e);
            }
        }
    }

    @FXML
    void handleBackToLanding(ActionEvent event) {
        try {
            URL url = getClass().getResource("/Landing.fxml");
            if (url == null) {
                throw new IOException("Cannot find Landing.fxml");
            }
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/global.css").toExternalForm());
            Stage stage = (Stage) ((Button)event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error returning to landing page", e);
        }
    }

    @FXML
    void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/global.css").toExternalForm());
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error logging out", e);
        }
    }

    @FXML
    public void handleRefresh() {
        loadMyEvents();
        loadMyReservations();
    }

    private void showNoEvents(boolean show) {
        noEventsPane.setVisible(show);
        noEventsPane.setManaged(show);
        myEventsContainer.setVisible(!show);
        myEventsContainer.setManaged(!show);
    }

    private void showNoReservations(boolean show) {
        noReservationsPane.setVisible(show);
        noReservationsPane.setManaged(show);
        myReservationsContainer.setVisible(!show);
        myReservationsContainer.setManaged(!show);
    }

    private void showError(String message, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
}