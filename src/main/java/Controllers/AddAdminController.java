package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import Models.Role;
import Models.User;
import services.UserService;

public class AddAdminController {
    private UserListContoller userListController; // Pour mettre à jour la liste après ajout
    @FXML
    private TextField numTelField;
    // Ajout du champ pour le numéro de téléphone
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField addressField; // Champ pour l'adresse

    private UserService userService = new UserService();

    @FXML
    private void handleAddAdmin() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String address = addressField.getText(); // Récupère l'adresse entrée par l'utilisateur
 String username=firstName + ""+lastName;
        String numTelString = numTelField.getText();
        // Vérifier si tous les champs sont remplis
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || address.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs.");
            return;
        }


        int numTel = 0; // Par défaut, 0 si vide
        if (!numTelString.isEmpty()) {
            try {
                numTel = Integer.parseInt(numTelString);
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Le numéro de téléphone doit contenir uniquement des chiffres.");
                return;
            }
        }



        // Vérifier si l'email est valide
        if (!email.contains("@")) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez entrer une adresse e-mail valide.");
            return;
        }

        User newAdmin = new User(lastName, firstName, username, Role.ADMIN, password, address, email, numTel);

//(last_name, first_name, email, password, role, address)
        // Ajouter l'admin à la base de données
        userService.ajouterAdmin(newAdmin);

        // Mettre à jour la liste des utilisateurs
        if (userListController != null) {
            userListController.updateListView();
        }

        showAlert(Alert.AlertType.INFORMATION, "Succès", "Administrateur ajouté avec succès !");

        // Fermer la fenêtre après l'ajout
        closeWindow();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) firstNameField.getScene().getWindow();
        stage.close();
    }

    // Setter pour le UserListContoller
    public void setUserListController(UserListContoller userListController) {
        this.userListController = userListController;
    }
}
