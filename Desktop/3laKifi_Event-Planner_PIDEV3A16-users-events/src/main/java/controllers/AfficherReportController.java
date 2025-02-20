package controllers;

import Models.Reports;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import services.ServiceReports;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AfficherReportController {

    @FXML private VBox reportContainer;
    @FXML private Button btnRefresh;

    private final ServiceReports serviceReports = new ServiceReports();

    @FXML
    public void initialize() {
        afficherReports();
        btnRefresh.setOnAction(event -> afficherReports());
    }

    /**
     * 📌 Charger et afficher tous les reports dans un `VBox`
     */
    private void afficherReports() {
        reportContainer.getChildren().clear(); // Nettoyage avant ajout

        try {
            List<Reports> reportsList = serviceReports.getAll();

            for (Reports report : reportsList) {
                HBox reportBox = new HBox(15);
                reportBox.setStyle("-fx-padding: 10; -fx-border-color: #ddd; -fx-background-color: #fff; -fx-border-radius: 5px;");
                reportBox.setPrefHeight(100);

                // 🔹 Infos du report
                VBox textContainer = new VBox(5);
                Label titleLabel = new Label("📌 " + report.getPublicationTitle());
                titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
                Label usernameLabel = new Label("👤 Signalé par : " + report.getUsername());
                Label reasonLabel = new Label("📝 Description : " + report.getDescription());
                Label statusLabel = new Label("🏷️ Statut : " + report.getStatus());
                Label dateLabel = new Label("📅 Date : " + report.getReport_date());

                textContainer.getChildren().addAll(titleLabel, usernameLabel, reasonLabel, statusLabel, dateLabel);

                // 🔹 Bouton Supprimer
                Button btnDelete = new Button("🗑 Delete");
                btnDelete.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
                btnDelete.setOnAction(event -> supprimerReport(report));

                // 🔹 Bouton Modifier
                Button btnUpdate = new Button("✏ Update");
                btnUpdate.setStyle("-fx-background-color: #533c56; -fx-text-fill: #ffffff;");
                btnUpdate.setOnAction(event -> ouvrirModifierReport(report));

                // 🔹 Conteneur des boutons
                HBox buttonContainer = new HBox(10);
                buttonContainer.getChildren().addAll(btnUpdate, btnDelete);

                // 🔹 Ajouter tout au HBox principal
                reportBox.getChildren().addAll(textContainer, buttonContainer);
                reportContainer.getChildren().add(reportBox);
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les reports : " + e.getMessage());
        }
    }


    private void ouvrirModifierReport(Reports report) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierReport.fxml"));
            Parent root = loader.load();

            ModifierReportController controller = loader.getController();
            controller.initData(report); // ✅ Correction : "report" au lieu de "Report"

            // ✅ Vérifier si l'interface actuelle doit être fermée avant d'ouvrir ModifierReport
            Stage currentStage = (Stage) reportContainer.getScene().getWindow();
            if (currentStage != null) {
                currentStage.close();
            }

            // ✅ Ouvrir la nouvelle interface ModifierReport
            Stage stage = new Stage();
            stage.setTitle("Update status report");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            System.err.println("❌ Erreur lors de l'ouverture de ModifierReport.fxml : " + e.getMessage());
            e.printStackTrace();
        }
    }




    /**
     * 📌 Supprimer un report
     */
    private void supprimerReport(Reports report) {
        try {
            serviceReports.supprimer(report.getPublicationTitle(), report.getUsername());
            showAlert(Alert.AlertType.INFORMATION, "Suppression", "Report supprimé avec succès.");
            afficherReports();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer le report : " + e.getMessage());
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
