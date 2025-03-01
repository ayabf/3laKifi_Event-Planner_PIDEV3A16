package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import Models.Reunion;
import javafx.stage.Stage;
import services.ReunionService;

import java.io.File;
import java.time.LocalDate;

public class ReunionController {

    @FXML
    private Button annulerReunionBtn;
    @FXML
    private Button uploadMediaBtn; // Renommé

    @FXML
    private TextField objectifTF;
    @FXML
    private DatePicker dateReunionDP;
    @FXML
    private TextArea descriptionTA;

    private File selectedFile;
    private ReunionService reunionService = new ReunionService();

    @FXML
    void enregistrerReunion(ActionEvent event) {
        String objectif = objectifTF.getText();
        LocalDate dateReunion = dateReunionDP.getValue();
        String description = descriptionTA.getText();
        String fichierPv = (selectedFile != null) ? selectedFile.getName() : null;

        if (objectif.isEmpty() || dateReunion == null || dateReunion.isBefore(LocalDate.now())) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Veuillez entrer des informations valides et choisir une date future.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        // Ajouter la réunion
        Reunion reunion = new Reunion(0, objectif, dateReunion, description, fichierPv);
        reunionService.ajouterReunion(reunion);

        // Afficher une confirmation
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Réunion ajoutée avec succès !", ButtonType.OK);
        alert.showAndWait();

        // Fermer la fenêtre après l'ajout
        ((Stage) ((Button) event.getSource()).getScene().getWindow()).close();
    }




    @FXML
    void uploaderMedia(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            uploadMediaBtn.setText("Fichier sélectionné : " + selectedFile.getName());
        }
    }

    @FXML
    void annulerReunion(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Annuler une réunion");
        dialog.setHeaderText("Veuillez entrer l'ID de la réunion à annuler");
        dialog.setContentText("ID:");

        dialog.showAndWait().ifPresent(idStr -> {
            try {
                int id = Integer.parseInt(idStr);
                reunionService.annulerReunion(id);

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Réunion annulée avec succès !", ButtonType.OK);
                alert.showAndWait();
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Veuillez entrer un ID valide.", ButtonType.OK);
                alert.showAndWait();
            }
        });
    }




}
