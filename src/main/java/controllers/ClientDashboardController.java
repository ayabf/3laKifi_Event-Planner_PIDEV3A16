package controllers;

import Models.Event;
import Models.Booking;
import Models.Location;
import Models.session;
import Models.Notification.NotificationType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import services.ServiceEvent;
import services.ServiceBooking;
import services.ServiceLocation;
import utils.NotificationManager;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.io.File;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import javafx.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javax.imageio.ImageIO;
import java.net.URL;
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
    private final ServiceLocation locationService = new ServiceLocation();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final NotificationManager notificationManager = NotificationManager.getInstance();

    @FXML
    public void initialize() {
        System.out.println("Initializing ClientDashboardController with user ID: " + session.id_utilisateur);
        loadMyEvents();
        loadMyReservations();
        checkUpcomingEvents();
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
                        controller.setOnExport(this::exportBookingToPDF);
                        myReservationsContainer.getChildren().add(bookingCard);
                    } catch (IOException e) {
                        e.printStackTrace();
                        notificationManager.showNotification(
                                "Loading Error",
                                "Could not load one of your bookings",
                                NotificationType.SYSTEM
                        );
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error loading reservations", e);
            notificationManager.showNotification(
                    "Loading Error",
                    "Could not load your reservations",
                    NotificationType.SYSTEM
            );
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
            loadMyEvents();
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
            loadMyEvents();

            notificationManager.showNotification(
                    "Event Updated",
                    "Event '" + event.getName() + "' has been updated successfully",
                    NotificationType.EVENT_UPDATED
            );
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
                loadMyEvents();

                notificationManager.showNotification(
                        "Event Deleted",
                        "Event '" + event.getName() + "' has been deleted",
                        NotificationType.EVENT_CANCELLED
                );
            } catch (SQLException e) {
                e.printStackTrace();
                showError("Error deleting event", e);
            }
        }
    }

    @FXML
    void handleBackToLanding(ActionEvent event) {
        try {
            LandingController.setPreviousPage("/ClientDashboard.fxml");
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
            URL loginUrl = getClass().getResource("/log.fxml");
            if (loginUrl == null) {
                throw new IOException("Cannot find log.fxml");
            }
            FXMLLoader loader = new FXMLLoader(loginUrl);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/global.css").toExternalForm());
            Stage stage = (Stage) myEventsContainer.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            showError("Error during logout", e);
        }
    }

    @FXML
    public void handleRefresh() {
        try {
            loadMyEvents();
            loadMyReservations();
            checkUpcomingEvents();

            notificationManager.showNotification(
                    "Refresh Complete",
                    "Your dashboard has been updated",
                    NotificationType.SYSTEM
            );
        } catch (Exception e) {
            e.printStackTrace();
            notificationManager.showNotification(
                    "Refresh Failed",
                    "Could not refresh the dashboard. Please try again.",
                    NotificationType.SYSTEM
            );
        }
    }

    @FXML
    private void exportBookingToPDF(Booking booking) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Booking PDF");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );
            fileChooser.setInitialFileName("booking_" + booking.getBooking_id() + ".pdf");

            File file = fileChooser.showSaveDialog(myEventsContainer.getScene().getWindow());
            if (file != null) {
                generateBookingPDF(file, booking);
                showSuccess("PDF generated successfully!");

                notificationManager.showNotification(
                        "PDF Generated",
                        "Booking confirmation PDF has been generated successfully",
                        NotificationType.SYSTEM
                );
            }
        } catch (Exception e) {
            showError("Error generating PDF", e);
        }
    }

    private void generateBookingPDF(File file, Booking booking) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            generateBookingPDF(document, page, booking);

            document.save(file);
        }
    }

    private void generateBookingPDF(PDDocument document, PDPage page, Booking booking) throws IOException {
        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            PDType1Font titleFont = PDType1Font.HELVETICA_BOLD;
            PDType1Font normalFont = PDType1Font.HELVETICA;
            PDType1Font italicFont = PDType1Font.HELVETICA_OBLIQUE;

            float margin = 50;
            float width = page.getMediaBox().getWidth() - 2 * margin;
            float yStart = page.getMediaBox().getHeight() - margin;
            float yPosition = yStart;

            contentStream.beginText();
            contentStream.setFont(titleFont, 24);
            contentStream.setNonStrokingColor(83, 60, 86);
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("3laKifi Events");
            contentStream.endText();

            yPosition -= 30;
            contentStream.beginText();
            contentStream.setFont(titleFont, 18);
            contentStream.setNonStrokingColor(102, 102, 102);
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("Booking Confirmation");
            contentStream.endText();

            contentStream.setStrokingColor(83, 60, 86);
            contentStream.setLineWidth(2);
            contentStream.moveTo(margin, yPosition - 10);
            contentStream.lineTo(width + margin, yPosition - 10);
            contentStream.stroke();

            yPosition -= 60;

            contentStream.beginText();
            contentStream.setFont(titleFont, 16);
            contentStream.setNonStrokingColor(51, 51, 51);
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("Event Details");
            contentStream.endText();

            yPosition -= 25;

            drawStyledBox(contentStream, margin, yPosition - 100, width, 90);

            Event event = null;
            Location location = null;
            try {
                event = eventService.getById(booking.getEvent_id());
                location = locationService.getById(booking.getLocation_id());
            } catch (SQLException e) {
                System.err.println("Error retrieving event or location data: " + e.getMessage());
                event = new Event();
                location = new Location();
            }

            contentStream.beginText();
            contentStream.setFont(normalFont, 12);
            contentStream.setNonStrokingColor(51, 51, 51);
            contentStream.newLineAtOffset(margin + 15, yPosition - 20);
            contentStream.showText("Event: " + event.getName());
            contentStream.newLineAtOffset(0, -25);
            contentStream.showText("Location: " + location.getName() + " (" + location.getVille() + ")");
            contentStream.newLineAtOffset(0, -25);
            contentStream.showText("Start: " + booking.getStart_date().format(dateFormatter));
            contentStream.newLineAtOffset(0, -25);
            contentStream.showText("End: " + booking.getEnd_date().format(dateFormatter));
            contentStream.endText();

            yPosition -= 130;

            float imageWidth = (width - 30) / 2;
            float imageHeight = 150;

            if (event != null && event.getImageData() != null) {
                PDImageXObject eventImage = PDImageXObject.createFromByteArray(document, event.getImageData(), "Event Image");
                contentStream.drawImage(eventImage, margin, yPosition - imageHeight, imageWidth, imageHeight);

                contentStream.beginText();
                contentStream.setFont(italicFont, 10);
                contentStream.newLineAtOffset(margin, yPosition - imageHeight - 15);
                contentStream.showText("Event: " + event.getName());
                contentStream.endText();
            }

            if (location != null && location.getImageData() != null) {
                PDImageXObject locationImage = PDImageXObject.createFromByteArray(document, location.getImageData(), "Location Image");
                contentStream.drawImage(locationImage, margin + imageWidth + 30, yPosition - imageHeight, imageWidth, imageHeight);

                contentStream.beginText();
                contentStream.setFont(italicFont, 10);
                contentStream.newLineAtOffset(margin + imageWidth + 30, yPosition - imageHeight - 15);
                contentStream.showText("Location: " + location.getName());
                contentStream.endText();
            }

            yPosition -= (imageHeight + 50);

            contentStream.beginText();
            contentStream.setFont(titleFont, 16);
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("Booking Reference");
            contentStream.endText();

            yPosition -= 25;

            drawStyledBox(contentStream, margin, yPosition - 40, width, 30);

            contentStream.beginText();
            contentStream.setFont(normalFont, 12);
            contentStream.newLineAtOffset(margin + 15, yPosition - 25);
            contentStream.showText("Booking ID: " + booking.getBooking_id());
            contentStream.endText();

            yPosition -= 80;

            try {
                String qrContent = String.format(
                        "3laKifi Events - Booking Details\n" +
                                "-------------------------\n" +
                                "Booking ID: %d\n" +
                                "Event: %s\n" +
                                "Location: %s (%s)\n" +
                                "Start Date: %s\n" +
                                "End Date: %s\n" +
                                "-------------------------\n" +
                                "Generated: %s",
                        booking.getBooking_id(),
                        event.getName(),
                        location.getName(),
                        location.getVille(),
                        booking.getStart_date().format(dateFormatter),
                        booking.getEnd_date().format(dateFormatter),
                        LocalDateTime.now().format(dateFormatter)
                );

                QRCodeWriter qrCodeWriter = new QRCodeWriter();
                BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 200, 200);

                BufferedImage qrImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
                for (int x = 0; x < 200; x++) {
                    for (int y = 0; y < 200; y++) {
                        qrImage.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                    }
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(qrImage, "png", baos);
                PDImageXObject qrCodeImage = PDImageXObject.createFromByteArray(document, baos.toByteArray(), "QR Code");

                float qrSize = 150;
                float xQR = (page.getMediaBox().getWidth() - qrSize) / 2;
                contentStream.drawImage(qrCodeImage, xQR, yPosition - qrSize, qrSize, qrSize);

                contentStream.beginText();
                contentStream.setFont(italicFont, 12);
                contentStream.setNonStrokingColor(128, 128, 128);
                float textWidth = italicFont.getStringWidth("Scan for booking details") / 1000 * 12;
                contentStream.newLineAtOffset(xQR + (qrSize - textWidth) / 2, yPosition - qrSize - 20);
                contentStream.showText("Scan for booking details");
                contentStream.endText();
            } catch (Exception e) {
                System.err.println("Error generating QR code: " + e.getMessage());
            }

            float footerY = 50;
            contentStream.beginText();
            contentStream.setFont(italicFont, 10);
            contentStream.setNonStrokingColor(128, 128, 128);
            contentStream.newLineAtOffset(margin, footerY);
            contentStream.showText("Generated on: " + LocalDateTime.now().format(dateFormatter));
            contentStream.endText();

            contentStream.setStrokingColor(200, 200, 200);
            contentStream.setLineWidth(1);
            contentStream.moveTo(margin, footerY + 20);
            contentStream.lineTo(width + margin, footerY + 20);
            contentStream.stroke();
        }
    }

    private void drawStyledBox(PDPageContentStream contentStream, float x, float y, float width, float height) throws IOException {
        contentStream.setNonStrokingColor(245, 246, 250);
        contentStream.addRect(x, y, width, height);
        contentStream.fill();

        contentStream.setStrokingColor(220, 220, 220);
        contentStream.setLineWidth(1);
        contentStream.addRect(x, y, width, height);
        contentStream.stroke();

        contentStream.setNonStrokingColor(240, 240, 240);
        contentStream.addRect(x + 2, y - 2, width, height);
        contentStream.fill();
    }

    private String getEventName(int eventId) {
        try {
            Event event = eventService.getById(eventId);
            return event != null ? event.getName() : "Unknown Event";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Unknown Event";
        }
    }

    private String getLocationName(int locationId) {
        try {
            Location location = locationService.getById(locationId);
            return location != null ? location.getName() : "Unknown Location";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Unknown Location";
        }
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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

    private void checkUpcomingEvents() {
        try {
            List<Event> events = eventService.getEventsByUser(session.id_utilisateur);
            LocalDateTime now = LocalDateTime.now();

            for (Event event : events) {
                if (event.getStart_date().isAfter(now) &&
                        event.getStart_date().isBefore(now.plusHours(24))) {
                    notificationManager.showNotification(
                            "Upcoming Event",
                            "Your event '" + event.getName() + "' starts in " +
                                    getTimeUntil(event.getStart_date()),
                            NotificationType.EVENT_CREATED
                    );
                }
            }

            List<Booking> bookings = bookingService.getBookingsByUser(session.id_utilisateur);
            for (Booking booking : bookings) {
                if (booking.getStart_date().isAfter(now) &&
                        booking.getStart_date().isBefore(now.plusHours(24))) {
                    Event event = eventService.getById(booking.getEvent_id());
                    Location location = locationService.getById(booking.getLocation_id());
                    notificationManager.showNotification(
                            "Upcoming Booking",
                            "Your booking for '" + event.getName() + "' at '" +
                                    location.getName() + "' starts in " +
                                    getTimeUntil(booking.getStart_date()),
                            NotificationType.BOOKING_CREATED
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getTimeUntil(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        long hours = java.time.Duration.between(now, dateTime).toHours();
        if (hours < 1) {
            long minutes = java.time.Duration.between(now, dateTime).toMinutes();
            return minutes + " minutes";
        }
        return hours + " hours";
    }

    private void handleLocationReservation(Location location, Event event) {
        try {
            if (!locationService.isLocationAvailable(
                    location.getId_location(),
                    event.getStart_date(),
                    event.getEnd_date())) {
                notificationManager.showNotification(
                        "Location Unavailable",
                        "Sorry, " + location.getName() + " is not available for the selected dates",
                        NotificationType.SYSTEM
                );
                return;
            }

            Booking booking = new Booking(
                    event.getId_event(),
                    location.getId_location(),
                    session.id_utilisateur,
                    event.getStart_date(),
                    event.getEnd_date()
            );

            bookingService.ajouter(booking);

            notificationManager.showNotification(
                    "Booking Confirmed",
                    "Successfully booked " + location.getName() + " for " + event.getName(),
                    NotificationType.BOOKING_CREATED
            );

            loadMyReservations();

        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error making reservation", e);

            notificationManager.showNotification(
                    "Booking Failed",
                    "Could not complete the booking. Please try again.",
                    NotificationType.SYSTEM
            );
        }
    }
} 