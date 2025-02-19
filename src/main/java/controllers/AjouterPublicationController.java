package controllers;

import Models.Publications;
import services.ServicePublications;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.SQLException;

public class AjouterPublicationController {

    @FXML
    private TextField titlePublicationTextfield;

    @FXML
    private TextField descriptionPublicationTextfield;

    @FXML
    private TextField UrltitlePublicationTextfield;

    @FXML
    private TextField UsernameTextfield;

    @FXML
    private ImageView imageView;

    private final ServicePublications servicePublications = new ServicePublications();

    @FXML
    void ajouterPublication(ActionEvent event) {
        String title = titlePublicationTextfield.getText().trim();
        String description = descriptionPublicationTextfield.getText().trim();
        String imageUrl = UrltitlePublicationTextfield.getText().trim();
        String username = UsernameTextfield.getText().trim();

        // ✅ Vérification des champs obligatoires
        if (title.isEmpty() || description.isEmpty() || imageUrl.isEmpty() || username.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Tous les champs sont obligatoires !");
            return;
        }

        if (!servicePublications.utilisateurExiste(username)) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "❌ L'utilisateur '" + username + "' n'existe pas !");
            return; // ❌ Arrêter l'ajout si l'utilisateur n'existe pas
        }

        try {

            Publications newPublication = new Publications(title, description, imageUrl, 1);
            servicePublications.ajouter(newPublication);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "✅ Publication ajoutée avec succès !");
            clearFields(); // Effacer les champs après l'ajout
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de l'ajout de la publication !");
            e.printStackTrace();
        }
    }

    @FXML
    void importPublication(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();


        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        // 🔹 Ouvrir l'explorateur de fichiers
        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {
            String imagePath = file.toURI().toString();
            UrltitlePublicationTextfield.setText(imagePath);
            imageView.setImage(new Image(imagePath));
        } else {
            System.out.println("Aucune image sélectionnée.");
        }
    }


    private void clearFields() {
        titlePublicationTextfield.clear();
        descriptionPublicationTextfield.clear();
        UrltitlePublicationTextfield.clear();
        UsernameTextfield.clear();
        imageView.setImage(null);
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    void initialize() {
        assert UrltitlePublicationTextfield != null : "fx:id=\"UrltitlePublicationTextfield\" was not injected: check your FXML file 'AjouterPublication.fxml'.";
        assert UsernameTextfield != null : "fx:id=\"UsernameTextfield\" was not injected: check your FXML file 'AjouterPublication.fxml'.";
        assert descriptionPublicationTextfield != null : "fx:id=\"descriptionPublicationTextfield\" was not injected: check your FXML file 'AjouterPublication.fxml'.";
        assert titlePublicationTextfield != null : "fx:id=\"titlePublicationTextfield\" was not injected: check your FXML file 'AjouterPublication.fxml'.";
        assert imageView != null : "fx:id=\"imageView\" was not injected: check your FXML file 'AjouterPublication.fxml'.";
    }
}
