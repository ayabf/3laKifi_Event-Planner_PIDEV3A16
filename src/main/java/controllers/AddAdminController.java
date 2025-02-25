package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import Models.Role;
import Models.User;
import services.UserService;

public class AddAdminController {
    private UserListContoller userListController;
    @FXML
    private TextField numTelField;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField addressField;

    private UserService userService = new UserService();

    @FXML
    private void handleAddAdmin() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String address = addressField.getText();
        String username=firstName + ""+lastName;
        String numTelString = numTelField.getText();
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || address.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs.");
            return;
        }


        int numTel = 0;
        if (!numTelString.isEmpty()) {
            try {
                numTel = Integer.parseInt(numTelString);
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Le numéro de téléphone doit contenir uniquement des chiffres.");
                return;
            }
        }


        if (!email.contains("@")) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez entrer une adresse e-mail valide.");
            return;
        }

        User newAdmin = new User(lastName, firstName, username, Role.ADMIN, password, address, email, numTel);

        userService.ajouterAdmin(newAdmin);

        if (userListController != null) {
            userListController.updateListView();
        }

        showAlert(Alert.AlertType.INFORMATION, "Succès", "Administrateur ajouté avec succès !");

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

    public void setUserListController(UserListContoller userListController) {
        this.userListController = userListController;
    }
}
