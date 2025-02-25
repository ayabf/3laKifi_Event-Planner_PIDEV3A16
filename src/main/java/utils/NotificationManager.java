package utils;

import Models.Notification;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextAlignment;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;

public class NotificationManager {
    private static NotificationManager instance;
    private static final int NOTIFICATION_SPACING = 10;
    private static final int MAX_NOTIFICATIONS = 5;
    private double currentY = 20; // Starting Y position for notifications

    private NotificationManager() {}

    public static NotificationManager getInstance() {
        if (instance == null) {
            instance = new NotificationManager();
        }
        return instance;
    }

    public void showNotification(String title, String message, Notification.NotificationType type) {
        Platform.runLater(() -> createNotification(title, message, type));
    }

    private void createNotification(String title, String message, Notification.NotificationType type) {
        Stage toastStage = new Stage();
        toastStage.initStyle(StageStyle.TRANSPARENT);

        // Create the notification content
        VBox notificationBox = new VBox(5);
        notificationBox.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 10;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 3);" +
                        "-fx-padding: 15;" +
                        "-fx-min-width: 350;" +
                        "-fx-max-width: 350;"
        );

        // Header with icon
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        FontAwesomeIconView icon = createIcon(type);

        Label titleLabel = new Label(title);
        titleLabel.setStyle(
                "-fx-font-weight: bold;" +
                        "-fx-font-size: 14px;" +
                        "-fx-text-fill: #2C3E50;"
        );

        header.getChildren().addAll(icon, titleLabel);

        // Message
        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(300);
        messageLabel.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-text-fill: #7F8C8D;"
        );

        notificationBox.getChildren().addAll(header, messageLabel);

        Scene scene = new Scene(notificationBox);
        scene.setFill(null);
        toastStage.setScene(scene);

        // Position the notification
        positionNotification(toastStage, notificationBox);

        // Show with animation
        toastStage.show();

        // Slide-in animation
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(500), notificationBox);
        slideIn.setFromX(350); // Start from outside the screen
        slideIn.setToX(0);
        slideIn.play();

        // Auto-hide after 5 seconds
        PauseTransition delay = new PauseTransition(Duration.seconds(5));
        delay.setOnFinished(e -> {
            TranslateTransition slideOut = new TranslateTransition(Duration.millis(500), notificationBox);
            slideOut.setFromX(0);
            slideOut.setToX(350);
            slideOut.setOnFinished(event -> {
                toastStage.close();
                currentY -= (notificationBox.getHeight() + NOTIFICATION_SPACING); // Reset Y position
                if (currentY < 20) currentY = 20; // Reset to initial position
            });
            slideOut.play();
        });
        delay.play();
    }

    private FontAwesomeIconView createIcon(Notification.NotificationType type) {
        FontAwesomeIconView icon = new FontAwesomeIconView();
        icon.setSize("16");

        switch (type) {
            case BOOKING_CREATED:
                icon.setGlyphName("CALENDAR_CHECK_O");
                icon.setStyle("-fx-fill: #4CAF50;");
                break;
            case BOOKING_UPDATED:
                icon.setGlyphName("CALENDAR");
                icon.setStyle("-fx-fill: #2196F3;");
                break;
            case BOOKING_CANCELLED:
                icon.setGlyphName("CALENDAR_TIMES_O");
                icon.setStyle("-fx-fill: #f44336;");
                break;
            case EVENT_CREATED:
                icon.setGlyphName("PLUS_CIRCLE");
                icon.setStyle("-fx-fill: #4CAF50;");
                break;
            case EVENT_UPDATED:
                icon.setGlyphName("EDIT");
                icon.setStyle("-fx-fill: #2196F3;");
                break;
            case EVENT_CANCELLED:
                icon.setGlyphName("TIMES_CIRCLE");
                icon.setStyle("-fx-fill: #FF9800;");
                break;
            case LOCATION_AVAILABLE:
                icon.setGlyphName("MAP_MARKER");
                icon.setStyle("-fx-fill: #9C27B0;");
                break;
            case SYSTEM:
                icon.setGlyphName("INFO_CIRCLE");
                icon.setStyle("-fx-fill: #533C56;");
                break;
            default:
                icon.setGlyphName("BELL");
                icon.setStyle("-fx-fill: #533C56;");
        }

        return icon;
    }

    private void positionNotification(Stage toastStage, VBox notificationBox) {
        Screen screen = Screen.getPrimary();
        double screenWidth = screen.getVisualBounds().getWidth();

        // Position at top-right corner with padding
        toastStage.setX(screenWidth - 370); // 350px width + 20px padding
        toastStage.setY(currentY);

        // Update Y position for next notification
        currentY += notificationBox.getHeight() + NOTIFICATION_SPACING;

        // Reset Y position if it goes too far down
        if (currentY > screen.getVisualBounds().getHeight() - 100) {
            currentY = 20;
        }
    }
} 