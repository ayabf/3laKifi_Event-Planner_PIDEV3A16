package controllers;

import Models.Event;
import services.ServiceEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Separator;
import javafx.event.ActionEvent;
import java.sql.SQLException;
import java.util.List;

public class EventsClientController {

    @FXML
    private GridPane eventsGridPane;

    @FXML
    private Button filterButton;

    private final ServiceEvent serviceEvent = new ServiceEvent();

    @FXML
    void initialize() {
        loadEvents();

    }


    private void loadEvents() {
        try {
            List<Event> events = serviceEvent.getAll();

            eventsGridPane.getChildren().clear();
            int column = 0;
            int row = 0;

            for (Event event : events) {
                VBox eventCard = createEventCard(event);

                eventsGridPane.add(eventCard, column, row);

                column++;
                if (column == 3) { // 3 événements par ligne
                    column = 0;
                    row++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox createEventCard(Event event) {
        VBox card = new VBox();
        card.setSpacing(10);
        card.setStyle("-fx-background-color: #cebec9; -fx-padding: 10px; -fx-border-radius: 10px; -fx-alignment: center;");
        card.setPrefWidth(250);
        card.setPrefHeight(280);

        ImageView eventImage = new ImageView();
        eventImage.setFitWidth(230);
        eventImage.setFitHeight(130);
        eventImage.setImage(new Image("file:" + event.getImagepath()));

        Label eventName = new Label(event.getName());
        eventName.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #533C56;");

        Label eventDescription = new Label(event.getDescription());
        eventDescription.setWrapText(true);
        eventDescription.setStyle("-fx-text-fill: #533C56;");

        Button reserveButton = new Button("Reserve");
        reserveButton.setStyle("-fx-background-color: #99707E; -fx-text-fill: white; -fx-font-weight: bold;");
        reserveButton.setOnAction(eventAction -> reserveEvent(event));

        card.getChildren().addAll(eventImage, eventName, new Separator(), eventDescription, reserveButton);

        return card;
    }

    private void reserveEvent(Event event) {
        System.out.println("Event reserved: " + event.getName());
        // Ici, rediriger vers EventFormClient.fxml avec l'événement sélectionné
    }
}
