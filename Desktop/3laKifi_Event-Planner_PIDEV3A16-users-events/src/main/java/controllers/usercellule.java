package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import Interfaces.LocalUserStateObserver;
import Models.Role;
import Models.User;
import services.UserService;

public class usercellule {

    private UserListContoller userListController;

    // Observer to notify about local changes in user state
    private LocalUserStateObserver localObserver;

    // UserService instance for user-related operations
    private UserService userService = new UserService();



    // FXML elements
    @FXML
    private Label usernam;

    @FXML
    private Label email;

    @FXML
    private Label num;

    public Label getRole() {
        return role;
    }

    public void setRole(String roles) {
        role.setText(roles);
    }

    @FXML
    private Label role;

    @FXML
    private Button supprimer;

    // User ID associated with this cell
    private int id;

    // Setter for the UserListController
    public void setUserListController(UserListContoller userListController) {
        this.userListController = userListController;
    }

    // Setter for the LocalUserStateObserver
    public void setLocalObserver(LocalUserStateObserver localObserver) {
        this.localObserver = localObserver;
    }

    // Getter for the User ID
    public int getId() {
        return id;
    }

    // Setter for the User ID
    public void setId(int id) {
        this.id = id;
    }

    // Setter for the Label of the username1
    public void setUsername1Label(String username) {
        usernam.setText(username);
    }

    // Setter for the Label of the email
    public void setEmailLabel(String userEmail) {
        email.setText(userEmail);
    }

    // Setter for the Label of the num
    public void setNumLabel(String userNum) {
        num.setText(userNum);
    }

    private void afficherAlerteErreur(String titre, String contenu) {
        Alert alerte = new Alert(Alert.AlertType.ERROR);
        alerte.setTitle(titre);
        alerte.setHeaderText(null);
        alerte.setContentText(contenu);
        alerte.showAndWait();
    }
    @FXML
    private ImageView userPhoto;  // Add ImageView for displaying user photo

    // Setter method to set user photo based on the role
    public void setUserPhoto(Role userRole) {
        if (userRole == Role.FOURNISSEUR) {
            // Set artist photo
            userPhoto.setImage(new Image(getClass().getResource("/images/fournisseur.PNG").toExternalForm()));
        } else {
            if (userRole == Role.CLIENT)
            // Set default photo for other roles
            {
                userPhoto.setImage(new Image(getClass().getResource("/images/img_4.png").toExternalForm()));
            } else {
                userPhoto.setImage(new Image(getClass().getResource("/images/UserIcon.png").toExternalForm()));
            }
        }
    }

    private void afficherAlerte(String titre, String contenu) {
        Alert alerte = new Alert(Alert.AlertType.INFORMATION);
        alerte.setTitle(titre);
        alerte.setHeaderText(null);
        alerte.setContentText(contenu);
        alerte.showAndWait();
    }

    @FXML
    private void supprimerUtilisateur(ActionEvent event) {
        if (id > 0) {
            userService.supprimer(id); // Supprime l'utilisateur de la base de données
            afficherAlerte("Succès", "Utilisateur supprimé avec succès.");

            // Appeler la mise à jour de la liste des utilisateurs après suppression
            if (userListController != null) {
                userListController.updateListView();
            }
        } else {
            afficherAlerteErreur("Erreur", "ID utilisateur non valide.");
        }
    }

    public Button getSupprimerButton() {
        return supprimer;
    }
    public void setUserData(User user) {
        this.id = user.getId(); // Associe l'ID de l'utilisateur à la cellule
        setUsername1Label(user.getUsername());
        setEmailLabel(user.getEmail());
        setNumLabel(String.valueOf(user.getNumTel()));
        setRole(user.getRole().toString());

        // Vérifie si l'utilisateur est ADMIN, cache le bouton "Supprimer"
        if (user.getRole() == Role.ADMIN) {
            supprimer.setVisible(false); // Cache complètement le bouton
            // supprimer.setDisable(true); // Alternative : désactiver sans cacher
        }
    }

}



