package controllers;

import Models.Publications;
import services.ServicePublications;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

public class ModifierPublicationController {

    @FXML private TextField titlePublicationTextfield;
    @FXML private TextField descriptionPublicationTextfield;
    @FXML private TextField UrltitlePublicationTextfield;
    @FXML private Button btnUpdate;
    @FXML private Button btnCancel;

    private final ServicePublications servicePublications = new ServicePublications();
    private Publications currentPublication;

    /**
     * 📌 Initialise les champs avec les données de la publication sélectionnée
     */
    public void initData(Publications publication) {
        this.currentPublication = publication;
        titlePublicationTextfield.setText(publication.getTitle());
        descriptionPublicationTextfield.setText(publication.getDescription());
        UrltitlePublicationTextfield.setText(publication.getImage_url());
    }

    /**
     * 📌 Met à jour la publication dans la base de données
     */
    @FXML
    private void updatePublication(ActionEvent event) {
        String title = titlePublicationTextfield.getText().trim();
        String description = descriptionPublicationTextfield.getText().trim();
        String imageUrl = UrltitlePublicationTextfield.getText().trim();

        // ✅ Vérifier les champs obligatoires
        if (title.isEmpty() || description.isEmpty() || imageUrl.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Tous les champs sont obligatoires !");
            return;
        }

        try {
            // ✅ Mettre à jour l'objet publication
            currentPublication.setTitle(title);
            currentPublication.setDescription(description);
            currentPublication.setImage_url(imageUrl);

            // ✅ Mise à jour dans la base de données
            servicePublications.modifier(currentPublication);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Publication mise à jour avec succès !");
            fermerFenetre();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la mise à jour de la publication !");
            e.printStackTrace();
        }
    }

    /**
     * 📌 Ferme la fenêtre de modification
     */
    @FXML
    private void fermerFenetre() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    /**
     * 📌 Affichage d'alertes pour informer l'utilisateur
     */
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
