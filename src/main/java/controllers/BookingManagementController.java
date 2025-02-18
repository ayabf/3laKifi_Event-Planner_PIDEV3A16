package controllers;

import Models.Booking;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import services.ServiceBooking;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Pos;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

public class BookingManagementController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TableView<Booking> bookingTableView;

    @FXML
    private TableColumn<Booking, Integer> idColumn;

    @FXML
    private TableColumn<Booking, Integer> eventIdColumn;

    @FXML
    private TableColumn<Booking, Integer> locationIdColumn;

    @FXML
    private TableColumn<Booking, LocalDateTime> startDateColumn;

    @FXML
    private TableColumn<Booking, LocalDateTime> endDateColumn;

    @FXML
    private TableColumn<Booking, Void> actionsColumn;

    @FXML
    private TextField searchField;

    @FXML
    private AnchorPane main_form;

    @FXML
    private TableColumn<?, ?> eventColumn;
    
    @FXML
    private TableColumn<?, ?> dateColumn;
    
    @FXML
    private TableColumn<?, ?> statusColumn;

    private final ServiceBooking bookingService = new ServiceBooking();
    private final ObservableList<Booking> bookingList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Initialize table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("booking_id"));
        setupTableColumns();
        setupActionsColumn();
        loadBookingData();
        setupSearchFunctionality();
    }

    private void setupTableColumns() {
        eventIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getEvent_id()).asObject());
        locationIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getLocation_id()).asObject());
        startDateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getStart_date()));
        endDateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getEnd_date()));
    }

    private void setupActionsColumn() {
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = createButton("Modifier", "EDIT", this::handleEdit);
            private final Button deleteButton = createButton("Supprimer", "TRASH", this::handleDelete);
            private final HBox buttons = new HBox(5, editButton, deleteButton);

            {
                buttons.setAlignment(Pos.CENTER);
            }

            private Button createButton(String text, String icon, Runnable action) {
                Button button = new Button(text);
                button.getStyleClass().addAll("action-button");
                if ("Supprimer".equals(text)) {
                    button.getStyleClass().add("secondary");
                }
                
                FontAwesomeIconView iconView = new FontAwesomeIconView();
                iconView.setGlyphName(icon);
                iconView.setSize("1.2em");
                button.setGraphic(iconView);
                
                button.setOnAction(event -> action.run());
                return button;
            }

            private void handleEdit() {
                Booking booking = getTableView().getItems().get(getIndex());
                updateBooking(booking);
            }

            private void handleDelete() {
                Booking booking = getTableView().getItems().get(getIndex());
                deleteBooking(booking);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        });
    }

    @FXML
    private void loadBookingData() {
        try {
            List<Booking> bookings = bookingService.getAll();
            bookingList.setAll(bookings);
            bookingTableView.setItems(bookingList);
        } catch (SQLException e) {
            showError("Error loading bookings", e);
        }
    }

    private void setupSearchFunctionality() {
        FilteredList<Booking> filteredData = new FilteredList<>(bookingList, p -> true);
        
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(booking -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                
                String lowerCaseFilter = newValue.toLowerCase();
                return String.valueOf(booking.getEvent_id()).contains(lowerCaseFilter)
                    || String.valueOf(booking.getLocation_id()).contains(lowerCaseFilter);
            });
        });
        
        SortedList<Booking> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(bookingTableView.comparatorProperty());
        bookingTableView.setItems(sortedData);
    }

    @FXML
    void addBooking(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddBooking.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter une réservation");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/global.css").toExternalForm());
            stage.setScene(scene);
            stage.showAndWait();
            loadBookingData();
        } catch (IOException e) {
            showError("Error loading add booking form", e);
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

    @FXML
    void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminDashboard.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/global.css").toExternalForm());
            Stage stage = (Stage) bookingTableView.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            showError("Error returning to dashboard", e);
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