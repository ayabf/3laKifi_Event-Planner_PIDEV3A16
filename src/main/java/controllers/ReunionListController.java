package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import Models.Reunion;
import javafx.stage.Stage;
import services.ReunionService;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;

public class ReunionListController {

    @FXML private ListView<Reunion> reunionsListView;
    @FXML private TextField searchBar;
    @FXML private Button rafraichirBtn;

    private ReunionService reunionService = new ReunionService();

    @FXML
    public void initialize() {
        afficherReunions();

        reunionsListView.setCellFactory(param -> new ListCell<Reunion>() {
            @Override
            protected void updateItem(Reunion reunion, boolean empty) {
                super.updateItem(reunion, empty);
                if (empty || reunion == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Création de la carte de réunion
                    VBox card = new VBox();
                    card.setStyle("-fx-padding: 10; -fx-background-color: white; -fx-border-color: #ddd; "
                            + "-fx-border-radius: 5; -fx-background-radius: 5; -fx-spacing: 5;");

                    Label objectifLabel = new Label("Objectif : " + reunion.getObjectif());
                    objectifLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: black;");

                    Label dateLabel = new Label("Date : " + reunion.getDateReunion());
                    dateLabel.setStyle("-fx-text-fill: #333;");

                    Label descriptionLabel = new Label("Description : " + reunion.getDescription());
                    descriptionLabel.setStyle("-fx-text-fill: #555;");

                    Label fichierLabel = new Label("Fichier : " + reunion.getFichierPv());
                    fichierLabel.setStyle("-fx-text-fill: #777;");

                    card.getChildren().addAll(objectifLabel, dateLabel, descriptionLabel, fichierLabel);
                    setGraphic(card);
                }
            }
        });
    }



    @FXML
    public void rafraichirReunions() {
        afficherReunions();
    }




    private void afficherReunions() {
        List<Reunion> reunions = reunionService.getAllReunions();
        ObservableList<Reunion> observableList = FXCollections.observableArrayList(reunions);
        reunionsListView.setItems(observableList);
    }

    @FXML
    void ouvrirFenetreAjoutReunion() {
        try {
            // Charger la vue `Reunion.fxml`
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterReunion.fxml"));
            Parent root = loader.load();

            // Obtenir le contrôleur de la fenêtre d'ajout
            ReunionController reunionController = loader.getController();

            // Afficher la fenêtre d'ajout
            Stage stage = new Stage();
            stage.setTitle("Ajouter une réunion");
            stage.setScene(new Scene(root));
            stage.showAndWait(); // Attend que l'utilisateur ferme la fenêtre avant de continuer

            // Après la fermeture de la fenêtre, rafraîchir la liste des réunions
            rafraichirReunions();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
