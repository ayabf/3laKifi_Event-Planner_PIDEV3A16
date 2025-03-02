package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import Models.User;
import Models.session;
import services.ChatServerService;
import services.UserService;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class sidebarAdmin {
    @FXML
    private Label name;
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;
    @FXML
    private Label labelFX;
UserService userService=new UserService();
    @FXML
    private ListView<String> userList;






    @FXML
    void initialize() {
        updateLabels();
    }

    public void redirectVersAcceuil(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sidebarAdmin.fxml"));
            Parent root = loader.load();
            sidebarAdmin controller = loader.getController();
            labelFX.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
        @FXML
        public void diectutuilisateur(ActionEvent event) {
            try {
                // Charger la page de connexion à partir du fichier FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/listUserOriginale.fxml"));
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







        //mahdi
        @FXML
        public void reddirectverstock(ActionEvent event) {
            try {
                // Charger la page de connexion à partir du fichier FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherStock.fxml"));
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















    public void ProfileClient(MouseEvent mouseEvent) {
        try {
            // Charger la page de connexion à partir du fichier FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProfileAdmin.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène
            Scene scene = new Scene(root);

            // Obtenir la scène actuelle à partir de l'événement
            Stage stage = (Stage) ((javafx.scene.Node) mouseEvent.getSource()).getScene().getWindow();

            // Changer la scène actuelle vers la nouvelle scène de connexion
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Gérer les erreurs de chargement de la page de connexion
        }

    }



    @FXML
    private BorderPane mainContainer; // 🔹 Assurez-vous que le `BorderPane` du sidebar est bien lié au FXML

    @FXML
    public void openChatScene(ActionEvent event) {
        try {
            // Charger le fichier FXML du chat
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Chat.fxml"));
            Parent chatView = loader.load();

            // Obtenir la scène actuelle depuis l'événement
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Changer la scène complète vers la vue du Chat
            stage.setScene(new Scene(chatView));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("⚠️ ERREUR : Impossible de charger Chat.fxml !");
        }
    }




    @FXML
    public void stopChatServer() {
        ChatServerService.stopServer();
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Serveur de Chat");
        alert.setHeaderText("Chat Server Arrêté !");
        alert.setContentText("Le serveur de chat a été arrêté.");
        alert.show();
    }





    @FXML
    public void diectoevent(ActionEvent event) {
        try {
            // Charger la page de connexion à partir du fichier FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Landing.fxml"));
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

    @FXML
    public void directToCart(ActionEvent event) {
        try {
            // Charger la page de connexion à partir du fichier FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cart.fxml"));
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



    private void openNewWindow(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (loader.getLocation() == null) {
                throw new IOException("❌ Le fichier FXML n'a pas été trouvé : " + fxmlPath);
            }

            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("❌ Erreur lors du chargement du fichier FXML : " + e.getMessage());
            e.printStackTrace();
        }
    }



    private void updateLabels() {
        User utilisateur = userService.getById(session.id_utilisateur);


        // Mettez à jour les labels avec les données de l'utilisateur
        name.setText(utilisateur.getUsername());



    }

    /*@FXML
    public void redirectToReunion(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterReunion.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            // Changer la scène vers la gestion des réunions
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement de la page Réunion.");
        }
    }*/



    public void directToLogin(ActionEvent event) {
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
        session.id_utilisateur = 0; // Assurez-vous de réinitialiser la session ou l'utilisateur approprié

    }

    @FXML
    public void reddirectversafficherreunions(ActionEvent event) {
        try {
            // Charger la page de connexion à partir du fichier FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ReunionList.fxml"));
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










    @FXML
    public void redirectversproduct(ActionEvent event) {
        try {
            // Charger la page de connexion à partir du fichier FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherProducts.fxml"));
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


    @FXML
    private void goToWelcomePage() {
        openNewWindow("/welcomeC.fxml", "Bienvenue !");
    }
























}






