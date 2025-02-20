package controllers;

import Models.Event;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.FadeTransition;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import services.ServiceEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventCardController {
    @FXML private HBox statusContainer;
    @FXML private FontAwesomeIconView statusIcon;
    @FXML private Label statusText;
    @FXML private ImageView eventImage;
    @FXML private VBox imageOverlay;
    @FXML private Label overlayTitle;
    @FXML private Label overlayDate;
    @FXML private Label eventName;
    @FXML private Label eventDescription;
    @FXML private Label eventCity;
    @FXML private Label eventCapacity;
    @FXML private Label eventStartDate;
    @FXML private Label eventEndDate;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button detailsButton;

    private Event event;
    private final ServiceEvent eventService = new ServiceEvent();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    private EventHandler<ActionEvent> onEdit;
    private EventHandler<ActionEvent> onDelete;

    @FXML
    public void initialize() {
        setupImageHoverEffect();
        setupButtonHoverEffects();
    }

    private void setupImageHoverEffect() {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), imageOverlay);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), imageOverlay);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        eventImage.setOnMouseEntered(e -> {
            imageOverlay.setVisible(true);
            fadeIn.playFromStart();
        });

        eventImage.setOnMouseExited(e -> {
            fadeOut.setOnFinished(event -> imageOverlay.setVisible(false));
            fadeOut.playFromStart();
        });
    }

    private void setupButtonHoverEffects() {
        setupButtonAnimation(editButton);
        setupButtonAnimation(deleteButton);
        setupButtonAnimation(detailsButton);
    }

    private void setupButtonAnimation(Button button) {
        button.setOnMouseEntered(e -> {
            button.setScaleX(1.1);
            button.setScaleY(1.1);
        });

        button.setOnMouseExited(e -> {
            button.setScaleX(1.0);
            button.setScaleY(1.0);
        });
    }

    public void setEvent(Event event) {
        this.event = event;
        updateCard();
    }

    public void setOnEdit(EventHandler<ActionEvent> handler) {
        this.onEdit = handler;
    }

    public void setOnDelete(EventHandler<ActionEvent> handler) {
        this.onDelete = handler;
    }

    private void updateCard() {
        if (event != null) {
            // Set event details
            eventName.setText(event.getName());
            eventDescription.setText(event.getDescription());
            eventCity.setText(event.getCity().name());
            eventCapacity.setText(String.format("%d attendees", event.getCapacity()));
            eventStartDate.setText(event.getStart_date().format(dateFormatter));
            eventEndDate.setText("Until " + event.getEnd_date().format(dateFormatter));

            // Set overlay content
            overlayTitle.setText(event.getName());
            overlayDate.setText(event.getStart_date().format(dateFormatter));

            // Load image
            if (event.getImageData() != null && event.getImageData().length > 0) {
                try {
                    Image image = new Image(new ByteArrayInputStream(event.getImageData()));
                    eventImage.setImage(image);
                } catch (Exception e) {
                    System.err.println("Error loading event image: " + e.getMessage());
                }
            }

            // Update status
            updateStatus();
        }
    }

    private void updateStatus() {
        LocalDateTime now = LocalDateTime.now();
        String status;
        String iconName;
        String styleClass;

        if (now.isBefore(event.getStart_date())) {
            status = "Upcoming";
            iconName = "CALENDAR";
            styleClass = "upcoming";
        } else if (now.isAfter(event.getEnd_date())) {
            status = "Completed";
            iconName = "CHECK";
            styleClass = "completed";
        } else {
            status = "In Progress";
            iconName = "CLOCK_O";
            styleClass = "in-progress";
        }

        statusContainer.getStyleClass().removeAll("upcoming", "completed", "in-progress");
        statusContainer.getStyleClass().add(styleClass);
        statusIcon.setGlyphName(iconName);
        statusText.setText(status);
    }

    @FXML
    private void handleEdit() {
        if (onEdit != null) {
            onEdit.handle(new ActionEvent());
        }
    }

    @FXML
    private void handleDelete() {
        if (onDelete != null) {
            onDelete.handle(new ActionEvent());
        }
    }

    @FXML
    private void handleDetails() {
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
            System.err.println("Error showing event details: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Event getEvent() {
        return event;
    }
} 