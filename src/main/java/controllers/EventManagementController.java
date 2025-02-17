package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class EventManagementController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TableColumn<?, ?> capacityColumn;

    @FXML
    private TableColumn<?, ?> cityColumn;

    @FXML
    private TableColumn<?, ?> descriptionColumn;

    @FXML
    private TableColumn<?, ?> endDateColumn;

    @FXML
    private TableView<?> eventTableView;

    @FXML
    private TableColumn<?, ?> imagePathColumn;

    @FXML
    private AnchorPane main_form;

    @FXML
    private TableColumn<?, ?> nameColumn;

    @FXML
    private TextField searchField;

    @FXML
    private TableColumn<?, ?> startDateColumn;

    @FXML
    void addEvent(ActionEvent event) {
        try {
            // Load the new FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddEvent.fxml"));
            Parent root = loader.load();

            // Create a new stage
            Stage stage = new Stage();
            stage.setScene(new Scene(root));

            // Show the stage
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    void deleteEvent(ActionEvent event) {

    }

    @FXML
    void updateEvent(ActionEvent event) {

    }

    @FXML
    void initialize() {

    }

}
