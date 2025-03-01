    package controllers;

    import javafx.event.ActionEvent;
    import javafx.fxml.FXML;
    import javafx.fxml.FXMLLoader;
    import javafx.scene.Parent;
    import javafx.scene.Scene;
    import javafx.scene.control.Alert;
    import javafx.scene.control.Button;
    import javafx.scene.control.PasswordField;
    import javafx.scene.control.TextField;
    import javafx.stage.Stage;
    import Models.session;
    import services.ChatServerService;
    import services.UserService;

    import java.io.IOException;
    import java.sql.SQLException;

    public class  LoginController {




        @FXML
        private TextField loginId;

        @FXML
        private PasswordField motpasse;

        @FXML
        private Button button;




        @FXML
        private void initialize() {


            // Initialisation du contrôleur (peut être utilisé pour effectuer des actions lors du chargement du FXML)


                startChatServer();


        }

        @FXML
        private void submit(ActionEvent event) throws SQLException {
            // Récupérer les valeurs des champs
            System.out.println("hh");
            if (loginId.getText().isEmpty() || motpasse.getText().isEmpty()) {
                motpasse.getScene().getWindow();
                System.out.println("u should type something");
            }
            UserService S = new UserService();

            // Appeler la fonction de vérification dans AuthentificationManager
            boolean authentifie = S.verifierUtilisateur(loginId.getText(), motpasse.getText());
            System.out.println("connecté " + authentifie);

            // Ajouter le reste de la logique selon le résultat de l'authentification
            if (authentifie) {
                int userId = S.getUtilisateurid(loginId.getText(), motpasse.getText());
                session.id_utilisateur = userId;
                String userRole = S.getUtilisateurRole(loginId.getText());
              // Aya a ajouté ca en cas ou tekhdhet
                session.loadUserRole(userId);
                System.out.println("🎯 Rôle de l'utilisateur connecté : " + session.role_utilisateur);

                switch (userRole) {
                    case "ADMIN":
                        redirectToAdminPage(event);
                        break;
                    case "CLIENT":
                        redirectToCLIENTPage(event);
                        break;
                    case "FOURNISSEUR":
                        redirectToFournisseurPage(event);
                        break;
                }

                System.out.println("Utilisateur connecté : " + userId);
            } else {
                // L'authentification a échoué, afficher une alerte d'erreur
                afficherAlerteErreur("Authentification échouée", "Veuillez vérifier vos identifiants.");
            }
        }

        private void redirectToFournisseurPage(ActionEvent event) {
            try {
                // Charger la page de connexion à partir du fichier FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/sidebarFournisseur.fxml"));
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

        private void redirectToCLIENTPage(ActionEvent event) {
            try {
                // Charger la page de connexion à partir du fichier FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProfileClient.fxml"));
                Parent root = loader.load();

                // Créer une nouvelle scène
                Scene scene = new Scene(root);

                // Obtenir la scène actuelle à partir de l'événement
                Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

                // Changer la scène actuelle vers la nouvelle scène de connexion
                stage.setScene(scene);
                stage.show();
                profileUser controller=loader.getController();
                controller.setStage(stage);

            } catch (IOException e) {
                e.printStackTrace();
                // Gérer les erreurs de chargement de la page de connexion
            }
        }

        private void redirectToAdminPage(ActionEvent event) {
            try {
                // Charger la page de connexion à partir du fichier FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProfileAdmin.fxml"));
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


        private void redirectToLoginPage(ActionEvent event) {
            try {
                // Charger la page de connexion à partir du fichier FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/CarteList.fxml"));
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
        private void afficherAlerteErreur(String titre, String contenu) {
            Alert alerte = new Alert(Alert.AlertType.ERROR);
            alerte.setTitle(titre);
            alerte.setHeaderText(null);
            alerte.setContentText(contenu);
            alerte.showAndWait();
        }


        private void chargerNouvelleScene(String fxml) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();

                // Fermer la scène actuelle (si nécessaire)
                Stage currentStage = (Stage) button.getScene().getWindow();
                currentStage.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        private void redirect(ActionEvent event) {
            try {
                // Charger la page de connexion à partir du fichier FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/inscription.fxml"));
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

        public void redirectto(ActionEvent actionEvent) {
            try {
                // Charger la page de connexion à partir du fichier FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/inscription.fxml"));
                Parent root = loader.load();

                // Créer une nouvelle scène
                Scene scene = new Scene(root);

                // Obtenir la scène actuelle à partir de l'événement
                Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();

                // Changer la scène actuelle vers la nouvelle scène de connexion
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                // Gérer les erreurs de chargement de la page de connexion
            }
        }

        public void motpasseoublié(ActionEvent actionEvent) {
            try {
                // Charger la page de connexion à partir du fichier FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/emailForPassword.fxml"));
                Parent root = loader.load();

                // Créer une nouvelle scène
                Scene scene = new Scene(root);

                // Obtenir la scène actuelle à partir de l'événement
                Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();

                // Changer la scène actuelle vers la nouvelle scène de connexion
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                // Gérer les erreurs de chargement de la page de connexion
            }
        }


        @FXML
        public void startChatServer() {
            Thread serverThread = new Thread(() -> {
                ChatServerService.startServer();
            });

            serverThread.setDaemon(true); // Assure que le serveur s'arrête avec l'application
            serverThread.start();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Serveur de Chat");
            alert.setHeaderText("Chat Server Démarré !");
            alert.setContentText("Le serveur de chat est maintenant actif.");
            alert.show();
        }

    }
