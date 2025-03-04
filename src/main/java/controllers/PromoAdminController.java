package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import services.SMSService;
import services.ServiceCodePromo;
import Models.CodePromo;
import utils.DataSource;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.sql.SQLException;

public class PromoAdminController {

    @FXML private TextField promoCodeField;
    @FXML private Label errorCodeLabel;

    @FXML private TextField percentageField;
    @FXML private Label errorPercentageLabel;

    @FXML private DatePicker expirationDatePicker;
    @FXML private Label errorDateLabel;

    private final ServiceCodePromo serviceCodePromo = new ServiceCodePromo();
    @FXML private Button addPromoButton;

    @FXML
    private void initialize() {
        // Ajoute les contrôles en temps réel
        promoCodeField.setOnKeyReleased(this::validatePromoCode);
        percentageField.setOnKeyReleased(this::validatePercentage);
        expirationDatePicker.setOnAction(event -> validateExpirationDate());
    }

    /** 🟢 Vérifie si le code promo est alphanumérique et <= 20 caractères **/
    private void validatePromoCode(KeyEvent event) {
        String code = promoCodeField.getText().trim();
        if (code.isEmpty()) {
            errorCodeLabel.setText("⚠️ Le code promo est obligatoire.");
        } else if (!code.matches("^[a-zA-Z0-9]{1,20}$")) {
            errorCodeLabel.setText("⚠️ Seuls les lettres et chiffres (max 20) sont autorisés.");
        } else {
            errorCodeLabel.setText(""); // ✅ Efface l'erreur si OK
        }
    }

    /** 🟢 Vérifie si le pourcentage est un nombre valide et entre 1-100% **/
    private void validatePercentage(KeyEvent event) {
        String percentageText = percentageField.getText().trim();
        try {
            double pourcentage = Double.parseDouble(percentageText);
            if (pourcentage < 1 || pourcentage > 100) {
                errorPercentageLabel.setText("⚠️ La réduction doit être entre 1% et 100%.");
            } else {
                errorPercentageLabel.setText(""); // ✅ Efface l'erreur si OK
            }
        } catch (NumberFormatException e) {
            errorPercentageLabel.setText("⚠️ Le pourcentage doit être un nombre valide.");
        }
    }

    /** 🟢 Vérifie si la date est future **/
    private void validateExpirationDate() {
        LocalDate expirationDate = expirationDatePicker.getValue();
        if (expirationDate == null) {
            errorDateLabel.setText("⚠️ Veuillez choisir une date.");
        } else if (expirationDate.isBefore(LocalDate.now())) {
            errorDateLabel.setText("⚠️ La date doit être future.");
        } else {
            errorDateLabel.setText(""); // ✅ Efface l'erreur si OK
        }
    }
    @FXML
    private void addPromo() {
        String code = promoCodeField.getText().trim();
        String percentageText = percentageField.getText().trim();
        LocalDate expirationDate = expirationDatePicker.getValue();

        if (!errorCodeLabel.getText().isEmpty() || !errorPercentageLabel.getText().isEmpty() || !errorDateLabel.getText().isEmpty()) {
            return;
        }

        if (code.isEmpty() || percentageText.isEmpty() || expirationDate == null) {
            if (code.isEmpty()) errorCodeLabel.setText("⚠️ Ce champ est obligatoire.");
            if (percentageText.isEmpty()) errorPercentageLabel.setText("⚠️ Ce champ est obligatoire.");
            if (expirationDate == null) errorDateLabel.setText("⚠️ Ce champ est obligatoire.");
            return;
        }

        try {
            double pourcentage = Double.parseDouble(percentageText);
            SMSService smsService = new SMSService(); // ✅ Service SMS

            if (promoToUpdate == null) {
                // ✅ Mode Ajout
                if (serviceCodePromo.existeDeja(code)) {
                    showErrorAlert("Code Promo Existant", "⚠️ Ce code promo existe déjà !");
                    return;
                }

                CodePromo promo = new CodePromo(code, pourcentage, Date.valueOf(expirationDate));
                CodePromo savedPromo = serviceCodePromo.ajouterCodePromo(promo);

                if (savedPromo != null) {
                    adminDashboardCController.addPromoCard(savedPromo);
                    showSuccessAlert("Ajout Réussi", "✅ Le code promo a été ajouté avec succès !");

                    // 📲 Envoi du SMS après l'ajout
                    String smsMessage = "🎉 Nouveau Code Promo : " + code + " | -" + pourcentage + "% jusqu'au " + expirationDate + ". Profitez-en !";
                    smsService.sendSMS(smsMessage);

                    clearFields();
                } else {
                    showErrorAlert("Erreur", "❌ Une erreur est survenue.");
                }

            } else {
                // 🔄 Mode Mise à Jour
                promoToUpdate.setCode(code);
                promoToUpdate.setPourcentage(pourcentage);
                promoToUpdate.setDateExpiration(Date.valueOf(expirationDate));

                if (serviceCodePromo.updatePromo(promoToUpdate)) {
                    showSuccessAlert("Mise à Jour Réussie", "✅ Le code promo a été mis à jour !");
                    adminDashboardCController.loadPromoCards();

                    // 📲 Envoi du SMS après la mise à jour
                    String smsMessage = "🔄 Code Promo mis à jour : " + code + " | -" + pourcentage + "% jusqu'au " + expirationDate + ". Ne le ratez pas !";
                    smsService.sendSMS(smsMessage);

                    clearFields();
                    promoToUpdate = null;
                    addPromoButton.setText("➕ Ajouter Promo");
                } else {
                    showErrorAlert("Erreur", "❌ Impossible de mettre à jour.");
                }
            }

        } catch (NumberFormatException e) {
            showErrorAlert("Valeur Invalide", "⚠️ Le pourcentage doit être un nombre valide.");
        }
    }






    private AdminDashboardCController adminDashboardCController;

    public void setAdminDashboardCController(AdminDashboardCController controller) {
        this.adminDashboardCController = controller;
    }


    /** ✅ Affiche une alerte de succès */
    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    /** ❌ Affiche une alerte d'erreur */
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }
    public boolean ajouterCodePromo(CodePromo promo) {
        String query = "INSERT INTO code_promo (code_promo, pourcentage, date_expiration) VALUES (?, ?, ?)";

        try (Connection conn = DataSource.getInstance().getConnection()) {
            if (conn == null || conn.isClosed()) {
                System.err.println("⚠️ Connexion fermée ! Impossible d'ajouter le code promo.");
                return false;
            }

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, promo.getCode());
                stmt.setDouble(2, promo.getPourcentage());
                stmt.setDate(3, new java.sql.Date(promo.getDateExpiration().getTime()));

                int rowsInserted = stmt.executeUpdate();
                return rowsInserted > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    private CodePromo promoToUpdate = null;
    public void updatePromo(CodePromo promo) {
        promoToUpdate = promo;
        promoCodeField.setText(promo.getCode());
        percentageField.setText(String.valueOf(promo.getPourcentage()));

        // Correction ici :
        expirationDatePicker.setValue(promo.getDateExpirationAsLocalDate());

        addPromoButton.setText("🔄 Modifier Promo");
    }


    private void clearFields() {
        promoCodeField.clear();
        percentageField.clear();
        expirationDatePicker.setValue(null);
        errorCodeLabel.setText(""); // Effacer les erreurs
        errorPercentageLabel.setText("");
        errorDateLabel.setText("");
    }
}