package controllers;

import Models.Publications;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
import java.io.IOException;
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
    private ImageView publicationImage; // 🔹 Remplace imageView par publicationImage

    @FXML
    private Button cancelPublication;

    private final ServicePublications servicePublications = new ServicePublications();

    @FXML
    void initialize() {
        System.out.println("🔹 Vérification du chargement du CSS...");

        titlePublicationTextfield.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.getStylesheets().add(
                        getClass().getResource("/styles/AjouterPublication-style.css").toExternalForm()
                );
                System.out.println("✅ Style CSS chargé avec succès !");
            }
        });
    }


    @FXML
    void ajouterPublication(ActionEvent event) {
        String title = titlePublicationTextfield.getText().trim();
        String description = descriptionPublicationTextfield.getText().trim();
        String imageUrl = UrltitlePublicationTextfield.getText().trim();
        String username = UsernameTextfield.getText().trim();

        if (title.isEmpty() || description.isEmpty() || imageUrl.isEmpty() || username.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Tous les champs sont obligatoires !");
            return;
        }

        if (!servicePublications.utilisateurExiste(username)) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "❌ L'utilisateur '" + username + "' n'existe pas !");
            return;
        }

        try {
            Publications newPublication = new Publications(title, description, imageUrl, 1);
            servicePublications.ajouter(newPublication);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "✅ Publication ajoutée avec succès !");
            clearFields();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de l'ajout de la publication !");
            e.printStackTrace();
        }
    }

    @FXML
    void importPublication(ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();

            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );

            File file = fileChooser.showOpenDialog(new Stage());

            if (file != null) {
                String imagePath = file.toURI().toString();
                UrltitlePublicationTextfield.setText(imagePath);
                publicationImage.setImage(new Image(imagePath)); // 🔹 Afficher l'image dans publicationImage
                System.out.println("📸 Image sélectionnée : " + imagePath);
            } else {
                System.out.println("❌ Aucune image sélectionnée.");
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'import de l'image : " + e.getMessage());
        }
    }

    private void clearFields() {
        titlePublicationTextfield.clear();
        descriptionPublicationTextfield.clear();
        UrltitlePublicationTextfield.clear();
        UsernameTextfield.clear();
        publicationImage.setImage(null);
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void cancelPublication(ActionEvent event) {
        Stage stage = (Stage) cancelPublication.getScene().getWindow();
        stage.close();
        System.out.println("🚪 Fenêtre AjouterPublication fermée.");
    }
}
