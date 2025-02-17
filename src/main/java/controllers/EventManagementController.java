package controllers;

import Models.Event;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import services.ServiceEvent;
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
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

public class EventManagementController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TableView<Event> eventTableView;

    @FXML
    private TableColumn<Event, String> nameColumn;

    @FXML
    private TableColumn<Event, String> descriptionColumn;

    @FXML
    private TableColumn<Event, String> cityColumn;

    @FXML
    private TableColumn<Event, LocalDateTime> startDateColumn;

    @FXML
    private TableColumn<Event, LocalDateTime> endDateColumn;

    @FXML
    private TableColumn<Event, Integer> capacityColumn;

    @FXML
    private TableColumn<Event, String> imagePathColumn;

    @FXML
    private TextField searchField;

    @FXML
    private AnchorPane main_form;

    private final ServiceEvent eventServices = new ServiceEvent();
    private final ObservableList<Event> eventList = FXCollections.observableArrayList();

    /**
     * Méthode appelée automatiquement lors du chargement de l'interface.
     * Initialise les colonnes du `TableView` et charge les événements depuis la base de données.
     */
    @FXML
    void initialize() {
        setupTableColumns();
        loadEventData();
        setupSearchFunctionality();
    }

    /**
     * Associe chaque colonne du `TableView` avec les attributs de la classe `Event`.
     */
    private void setupTableColumns() {
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        cityColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCity().name())); // Solution sans property
        startDateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getStart_date()));
        endDateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getEnd_date()));
        capacityColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCapacity()).asObject());
        imagePathColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getImagepath()));
    }


    /**
     * Charge les événements depuis la base de données et les affiche dans le `TableView`.
     */
    private void loadEventData() {
        try {
            List<Event> events = eventServices.getAll();
            eventList.setAll(events);
            eventTableView.setItems(eventList);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les événements.");
        }
    }

    /**
     * Configure la barre de recherche pour filtrer les événements dynamiquement.
     */

    private void setupSearchFunctionality() {
        FilteredList<Event> filteredData = new FilteredList<>(eventList, b -> true);


        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(event -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return event.getName().toLowerCase().contains(lowerCaseFilter) ||
                        event.getDescription().toLowerCase().contains(lowerCaseFilter) ||
                        event.getCity().name().toLowerCase().contains(lowerCaseFilter); // Solution ici
            });
        });

        SortedList<Event> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(eventTableView.comparatorProperty());
        eventTableView.setItems(sortedData);
    }


    /**
     * Ouvre la fenêtre d'ajout d'un événement et rafraîchit la table après ajout.
     */
    @FXML
    void addEvent(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddEvent.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.showAndWait(); // Attendre que l'utilisateur ferme la fenêtre

            // Rafraîchir la table après ajout
            loadEventData();
            //NEW
            // 🔥 Nouvelle ligne : Réinitialiser la recherche après mise à jour des données
            setupSearchFunctionality();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Affiche une boîte de dialogue d'alerte.
     */
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void deleteEvent(ActionEvent actionEvent) {
    }

    public void updateEvent(ActionEvent actionEvent) {

    }
}
