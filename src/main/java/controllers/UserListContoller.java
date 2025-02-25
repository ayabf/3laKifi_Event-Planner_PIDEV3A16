package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import Interfaces.LocalUserStateObserver;
import Models.Role;
import Models.User;
import services.UserService;

import java.io.IOException;
import java.util.List;

public class UserListContoller implements LocalUserStateObserver {
    @FXML
    private ListView<VBox> usersListView;

    @FXML
    private TextField searchBar;

    private UserService userService = new UserService();

    @FXML
    public void initialize() {
        // Initialize your ListView here, for example:
        updateListView();

        // Add a listener to the search bar to filter users on text change
        searchBar.textProperty().addListener((observable, oldValue, newValue) ->
                filterUsers(newValue.trim().toLowerCase()));
    }

    @FXML
    private void showAddAdminPopup(MouseEvent event) {
        try {
            System.out.println("Ouverture de la pop-up d'ajout d'admin..."); // Debug

            // Charger le fichier FXML de la pop-up
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/addAdminPopup.fxml"));
            Parent root = loader.load();

            System.out.println("FXML chargé avec succès !"); // Debug

            // Récupérer le contrôleur de la pop-up
            AddAdminController addAdminController = loader.getController();
            addAdminController.setUserListController(this);

            // Créer et afficher la pop-up
            Stage popupStage = new Stage();
            popupStage.setTitle("Ajouter un Administrateur");
            popupStage.setScene(new Scene(root));
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setResizable(false);
            popupStage.show();

            System.out.println("Pop-up affichée avec succès !"); // Debug

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de la pop-up d'ajout d'admin : " + e.getMessage());
        }
    }




    void updateListView() {
        List<User> users = userService.afficher();
        usersListView.getItems().clear();

        for (User user : users) {
            // Rest of the code to add users to the ListView
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/cellule1.fxml"));
            try {
                HBox userCell = loader.load();
                usercellule userController = loader.getController();
                userController.setLocalObserver(this);
                userController.setUserListController(this);

                userController.setRole(user.getRole().name().toLowerCase());
                // Update the labels with user data
                userController.setUserPhoto(user.getRole());

                userController.setUsername1Label(user.getUsername());
                userController.setEmailLabel(user.getEmail());
                userController.setNumLabel(String.valueOf(user.getNumTel()));
                userController.setId(user.getId());

                // Hide buttons for ADMIN users
                if (user.getRole() == Role.ADMIN) {
                    userController.getSupprimerButton().setVisible(false);

                }
                userCell.setSpacing(5);
                VBox userRow = new VBox(userCell);
                userRow.setSpacing(10);

                usersListView.getItems().add(userRow);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Error loading cellule1.fxml: " + e.getMessage());
            }
        }
    }

    private void filterUsers(String keyword) {
        List<User> filteredUsers = userService.searchUsers(keyword);
        updateListView(filteredUsers);
    }

    public void updateListView(List<User> users) {
        // Clear existing content
        usersListView.getItems().clear();

        // Add each user as a cell in a new row
        for (User user : users) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/cellule1.fxml"));
            try {
                HBox userCell = loader.load();
                usercellule userController = loader.getController();
                userController.setLocalObserver(this);
                userController.setUserListController(this);

                userController.setRole(user.getRole().name().toLowerCase());
                userController.setUserPhoto(user.getRole());
                    userController.setUserData(user);
                // Update the labels with user data
                userController.setUsername1Label(user.getUsername());
                userController.setEmailLabel(user.getEmail());
                userController.setNumLabel(String.valueOf(user.getNumTel()));
                userController.setId(user.getId());

                userCell.setSpacing(5);
                VBox userRow = new VBox(userCell);
                userRow.setSpacing(10);

                usersListView.getItems().add(userRow);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Error loading cellule1.fxml: " + e.getMessage());
            }
        }
    }

    @Override
    public void onUserStateChanged(int userId, boolean isBlocked) {
        System.out.println("Local state changed for user " + userId + ". Blocked: " + isBlocked);
    }

    public void reload(MouseEvent mouseEvent) {
        updateListView();
    }

}
