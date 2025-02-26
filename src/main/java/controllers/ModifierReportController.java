package controllers;

import Models.Reports;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import services.ServiceReports;

import java.io.IOException;

public class ModifierReportController {

    @FXML private ChoiceBox<String> statusChoiceBox;
    @FXML private Button btnUpdate;
    @FXML private Button btnCancel;

    private final ServiceReports serviceReports = new ServiceReports();
    private int reportId; // ✅ ID du report à modifier

    @FXML
    public void initialize() {
        // ✅ Ajouter les statuts dans la ChoiceBox
        statusChoiceBox.getItems().addAll("Pending", "Verified", "Rejected");
    }

    /**
     * 📌 Initialisation des données du report
     */
    public void initData(Reports report) {
        this.reportId = report.getReport_id(); // ✅ Stocker l'ID du report
        statusChoiceBox.setValue(report.getStatus().toString()); // ✅ Afficher le statut actuel
    }

    /**
     * 📌 Mettre à jour le statut du report
     */
    @FXML
    void updateReportStatus(ActionEvent event) {
        String newStatus = statusChoiceBox.getValue();
        if (newStatus == null || newStatus.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Erreur", "Veuillez sélectionner un statut.");
            return;
        }

        try {
            // ✅ Mise à jour du statut avec l'ID du report
            serviceReports.modifier(reportId, newStatus);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Statut mis à jour avec succès !");

            // ✅ Retourner vers AfficherReports après la mise à jour
            retournerVersAfficherReports();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de mettre à jour le statut : " + e.getMessage());
        }
    }

    /**
     * 📌 Annuler et retourner vers `AfficherReport.fxml`
     */
    @FXML
    void retournerVersAfficherReports() {
        try {
            // 🔹 Fermer la fenêtre actuelle (ModifierReport.fxml)
            Stage currentStage = (Stage) btnCancel.getScene().getWindow();
            currentStage.close();

            // 🔹 Charger AfficherReport.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherReport.fxml"));
            Parent root = loader.load();

            // 🔹 Créer une nouvelle scène et l'afficher
            Stage stage = new Stage();
            stage.setTitle("Liste des Signalements");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("❌ Erreur lors de l'ouverture de AfficherReport.fxml : " + e.getMessage());
        }
    }

    /**
     * 📌 Afficher une alerte
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
