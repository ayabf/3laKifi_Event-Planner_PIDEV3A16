package controllers;

import Models.Publications;
import services.ServicePublications;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AfficherPublicationController {

    @FXML private VBox publicationContainer;
    @FXML private Button btnRefresh;
    @FXML private Button btnAddPublication;


    private final ServicePublications servicePublications = new ServicePublications();

    @FXML
    public void initialize() {
        afficherPublications(); // Charger les publications au démarrage

        // Bouton Actualiser
        btnRefresh.setOnAction(event -> afficherPublications());

        // Bouton Ajouter une publication
        btnAddPublication.setOnAction(event -> ouvrirAjouterPublication());
    }

    /**
     * 📌 Charge et affiche toutes les publications avec images et actions
     */
    private void afficherPublications() {
        publicationContainer.getChildren().clear(); // Nettoyage avant d'ajouter

        try {
            List<Publications> publications = servicePublications.getAll();

            for (Publications pub : publications) {
                HBox publicationBox = new HBox(15);
                publicationBox.setStyle("-fx-padding: 10; -fx-border-color: #ddd; -fx-background-color: #fff; -fx-border-radius: 5px;");
                publicationBox.setPrefHeight(120);

                // 🔹 Image de la publication
                ImageView imageView = new ImageView();
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);

                try {
                    if (pub.getImage_url() != null && !pub.getImage_url().isEmpty()) {
                        Image image = new Image(pub.getImage_url(), true);
                        imageView.setImage(image);
                    } else {
                        throw new Exception("Image invalide");
                    }
                } catch (Exception e) {
                    System.err.println("❌ Image invalide pour " + pub.getTitle() + ", chargement de l'image par défaut.");
                    imageView.setImage(new Image(getClass().getResource("/images/default.png").toString())); // Image par défaut
                }

                // 🔹 Infos Publication
                VBox textContainer = new VBox(5);
                Label titleLabel = new Label("📌 " + pub.getTitle());
                titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
                Label descLabel = new Label("📝 " + pub.getDescription());
                Label usernameLabel = new Label("👤 " + servicePublications.getUsernameById(pub.getId_user()));
                Label dateLabel = new Label("📅 " + pub.getPublication_date());

                textContainer.getChildren().addAll(titleLabel, descLabel, usernameLabel, dateLabel);

                // 🔹 Boutons Modifier, Supprimer et Signaler
                VBox actionButtons = new VBox(5);

                Button btnUpdate = new Button("✏ Update");
                btnUpdate.setStyle("-fx-background-color: #533c56; -fx-text-fill: #ffffff;");
                btnUpdate.setOnAction(event -> ouvrirModifierPublication(pub));

                Button btnDelete = new Button("🗑 Delete");
                btnDelete.setStyle("-fx-background-color: #533c56; -fx-text-fill: white;");
                btnDelete.setOnAction(event -> supprimerPublication(pub.getPublication_id()));

                Button btnSignal = new Button("🚨 Report");
                btnSignal.setStyle("-fx-background-color: #533c56; -fx-text-fill: white;");
                btnSignal.setOnAction(event -> signalerPublication(pub));

                actionButtons.getChildren().addAll(btnUpdate, btnDelete, btnSignal);

                // 🔹 Ajouter tout au HBox principal
                publicationBox.getChildren().addAll(imageView, textContainer, actionButtons);
                publicationContainer.getChildren().add(publicationBox);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'affichage des publications : " + e.getMessage());
        }
    }

    /**
     * 📌 Supprime une publication avec confirmation
     */
    private void supprimerPublication(int publicationId) {
        try {
            servicePublications.supprimer(publicationId);
            afficherPublications(); // Recharger la liste après suppression
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression de la publication : " + e.getMessage());
        }
    }

    /**
     * 📌 Ouvre la fenêtre de modification de la publication
     */
    private void ouvrirModifierPublication(Publications publication) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierPublication.fxml"));
            Parent root = loader.load();

            ModifierPublicationController controller = loader.getController();
            controller.initData(publication); // Passer les données à ModifierPublicationController

            // Récupérer la scène actuelle et la fermer
            Stage currentStage = (Stage) publicationContainer.getScene().getWindow();
            currentStage.close();

            // Ouvrir ModifierPublication.fxml dans une nouvelle fenêtre
            Stage stage = new Stage();
            stage.setTitle("Update publication");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            System.err.println("❌ Erreur lors de l'ouverture de ModifierPublication.fxml : " + e.getMessage());
        }
    }


    /**
     * 📌 Ouvre AjouterPublication.fxml et ferme la fenêtre actuelle
     */
    private void ouvrirAjouterPublication() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterPublication.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Add publication");
            stage.setScene(new Scene(root));
            stage.show();

            // Fermer la fenêtre actuelle
            Stage currentStage = (Stage) btnAddPublication.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            System.err.println("❌ Erreur lors de l'ouverture de la fenêtre AjouterPublication.fxml : " + e.getMessage());
        }
    }

    /**
     * 📌 Ouvre AjouterReport.fxml pour signaler une publication
     */
    private void signalerPublication(Publications publication) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterReport.fxml"));
            Parent root = loader.load();

            AjouterReportController controller = loader.getController();
            controller.initData(publication); // Passer les données de la publication

            // Fermer la fenêtre actuelle (AfficherPublication.fxml)
            Stage currentStage = (Stage) publicationContainer.getScene().getWindow();
            currentStage.close();

            // Ouvrir AjouterReport.fxml dans une nouvelle fenêtre
            Stage stage = new Stage();
            stage.setTitle("Report publication");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            System.err.println("❌ Erreur lors de l'ouverture de AjouterReport.fxml : " + e.getMessage());
        }
    }
    @FXML
    void cancelReport(ActionEvent event) {
        try {
            // Fermer la fenêtre actuelle (AjouterReport.fxml)
            Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            currentStage.close();

            // Charger et ouvrir AfficherPublication.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherPublication.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("List of publications");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            System.err.println("❌ Erreur lors de l'ouverture de AfficherPublication.fxml : " + e.getMessage());
        }
    }









}
