package Controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import Models.Role;
import Models.User;
import services.UserService;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static utils.EmailsUtils.sendVerificationEmail;

public class inscription {


    @FXML
    private TextField nom;

    @FXML
    private TextField prenom;

    @FXML
    private TextField email;

    @FXML
    private PasswordField mdp;

    @FXML
    private RadioButton clientRadioButton;

    @FXML
    private RadioButton FournisseurRadioButton;
    @FXML
    private PasswordField mdp1;

    @FXML
    private TextField numtel;

    @FXML
    private TextField username;

    @FXML
    private Button inscriptionButton;
   public String verificationCode1;
    private UserService userService = new UserService(); // Injection du service
    @FXML
    private ProgressBar loadingIndicator;
    @FXML
    private ProgressIndicator progressIndicator;
    public void adduser(ActionEvent event) {
        // Vérification de la saisie
        if (nom.getText().isEmpty() || prenom.getText().isEmpty() || mdp.getText().isEmpty() || mdp1.getText().isEmpty()
                || email.getText().isEmpty() || username.getText().isEmpty() || numtel.getText().isEmpty() ||
                (!clientRadioButton.isSelected() && !FournisseurRadioButton.isSelected())) {
            showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "Remplissez tous les champs.");
        } else
        if (mdp.getText().length() < 8) {
            showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "Le mot de passe doit contenir au moins 8 caractères.");
            return;
        }

        // Vérification de l'adresse e-mail avec une regex
        if (!validateEmail(email.getText())) {
            showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "Adresse e-mail invalide.");
            return;
        }

        if (userService.emailExists(email.getText())) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "L'adresse e-mail existe déjà.");
            return;
        }

        // Vérification si le username existe déjà dans la base de données
        if (userService.usernameExists(username.getText())) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le nom d'utilisateur existe déjà.");
            return;
        }
            if (!mdp.getText().equals(mdp1.getText())) {
            showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "Les mots de passe ne correspondent pas.");
        } else if (!isNumeric(numtel.getText()) || numtel.getText().length() != 8) {
            showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "Le numéro de téléphone doit être numérique et contenir 8 chiffres.");
        } else if (clientRadioButton.isSelected() && FournisseurRadioButton.isSelected()) {
            showAlert(Alert.AlertType.ERROR, "Erreur de sélection", "Veuillez choisir un seul rôle.");}
        else {
            // Show the loading indicator
            loadingIndicator.setVisible(true);

                // Move email sending to a separate thread
                new Thread(() -> {
                    String x = generateVerificationCode();
                    sendVerificationEmail(email.getText(), x);
                    // Update UI on the JavaFX Application thread after email sending is complete
                    Platform.runLater(() -> {
                        // Hide the loading indicator
                        loadingIndicator.setVisible(false);

                        // Continue avec le reste de votre logique
                        userService.ajouter4(new User(nom.getText(), prenom.getText(), username.getText(), mdp.getText(),
                                Role.valueOf(getSelectedRole()), "", "", "", email.getText(), Integer.parseInt(numtel.getText())));

                        // Afficher une confirmation
                        redirectToLoginPage(event);


                    });
                }).start();

            }
    }


    private boolean isNumeric(String str) {
        return str.matches("\\d+");
    }

    private String generateVerificationCode() {
        // Générez un code aléatoire (vous pouvez personnaliser la logique selon vos besoins)
        // Dans cet exemple, un code à 6 chiffres
        int code = 100000 + (int) (Math.random() * 900000);
        return String.valueOf(code);
    }


    private void redirectToLoginPage(ActionEvent event) {
        try {
            // Charger la page de connexion à partir du fichier FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/log.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène
            Scene scene = new Scene(root);

            // Obtenir la scène actuelle à partir de l'événement
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            // Changer la scène actuelle vers la nouvelle scène de connexion
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            // Gérer les erreurs de chargement de la page de connexion
        }
    }



    private void redirectToSidebarFournisseur(ActionEvent event) {
        try {
            // Charger la sidebar du fournisseur
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sidebarFournisseur.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène
            Scene scene = new Scene(root);

            // Obtenir la scène actuelle
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            // Changer la scène actuelle vers la sidebar du fournisseur
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            // Gérer les erreurs de chargement
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la page du fournisseur.");
        }
    }



    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private static final String EMAIL_REGEX =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    private static final Pattern pattern = Pattern.compile(EMAIL_REGEX);

    private boolean validateEmail(String email) {
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public void directToLogin(MouseEvent mouseEvent) {

        try {
            // Charger la page de connexion à partir du fichier FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/log.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène
            Scene scene = new Scene(root);

            // Obtenir la scène actuelle à partir de l'événement
            Stage stage = (Stage) ((javafx.scene.Node) mouseEvent .getSource()).getScene().getWindow();

            // Changer la scène actuelle vers la nouvelle scène de connexion
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            // Gérer les erreurs de chargement de la page de connexion
        }
    }

    private String getSelectedRole() {
        return clientRadioButton.isSelected() ? "CLIENT" : (FournisseurRadioButton.isSelected() ? "FOURNISSEUR" : "");
    }

}
