package controllers;

import Models.Publications;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import services.ServiceReports;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.sql.SQLException;

public class AjouterReportController {

    @FXML private TextField UsernameTextfield;
    @FXML private TextField descriptionReportTextfield;
    @FXML private TextField reasonReportTextfield;
    @FXML private TextField titlePublicationTextfield;
    @FXML private Button btnSubmitReport;
    @FXML private Button btnCancel;

    private final ServiceReports serviceReports = new ServiceReports();
    private Publications publication;

    /**
     * 📌 Initialisation des données de la publication (l'utilisateur entre manuellement son username)
     */
    public void initData(Publications publication) {
        this.publication = publication;

        // Récupérer le titre de la publication (non modifiable)
        titlePublicationTextfield.setText(publication.getTitle());
        titlePublicationTextfield.setEditable(false);

        // Laisser l'utilisateur entrer son username
        UsernameTextfield.setPromptText("Entrez votre username");
    }

    /**
     * 📌 Ajouter un report dans la base de données
     */
    @FXML
    private void ajouterReport() {
        String username = UsernameTextfield.getText().trim(); // Saisie manuelle
        String reason = reasonReportTextfield.getText().trim();
        String description = descriptionReportTextfield.getText().trim();

        // Vérifier si les champs sont remplis
        if (username.isEmpty() || reason.isEmpty() || description.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champs vides", "Veuillez remplir tous les champs.");
            return;
        }

        try {
            // Enregistrer le report avec l'username saisi
            serviceReports.ajouter(publication.getTitle(), username, reason, description);
            showAlert(Alert.AlertType.INFORMATION, "Signalement envoyé", "Votre signalement a bien été enregistré.");

            // Fermer la fenêtre après enregistrement
            closeWindow();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'envoyer le signalement : " + e.getMessage());
        }
    }

    /**
     * 📌 Annuler l'ajout et fermer la fenêtre
     */
    @FXML
    private void cancelReport() {
        closeWindow();
    }

    /**
     * 📌 Fermer la fenêtre actuelle
     */
    private void closeWindow() {
        try {
            // Fermer la fenêtre actuelle
            Stage currentStage = (Stage) btnCancel.getScene().getWindow();
            currentStage.close();

            // Charger l'interface AfficherPublication.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherPublication.fxml"));
            Parent root = loader.load();

            // Ouvrir la nouvelle fenêtre
            Stage stage = new Stage();
            stage.setTitle("Liste des Publications");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            System.err.println("❌ Erreur lors de l'ouverture de AfficherPublication.fxml : " + e.getMessage());
        }
    }


    /**
     * 📌 Afficher une alerte pour informer l'utilisateur
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
            stage.setTitle("Liste des Publications");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            System.err.println("❌ Erreur lors de l'ouverture de AfficherPublication.fxml : " + e.getMessage());
        }
    }



}
