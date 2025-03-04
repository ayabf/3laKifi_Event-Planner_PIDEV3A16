package controllers;

import Models.Publications;
import Models.session;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import services.ServicePublications;
import services.TranslationService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AfficherPublication2Controller {

    @FXML
    private VBox vboxContainer;

    @FXML
    private TextField textRecherchPublication;

    private final ServicePublications servicePublications = new ServicePublications();
    private List<Publications> allPublications;

    @FXML
    void initialize() {
        System.out.println("🔹 Initialisation de l'affichage des publications...");
        try {
            allPublications = servicePublications.getAll();
            afficherPublications(allPublications);
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement des publications : " + e.getMessage());
        }
    }

    private void afficherPublications(List<Publications> publications) {
        vboxContainer.getChildren().clear();

        if (publications.isEmpty()) {
            Label noPublications = new Label("Aucune publication disponible.");
            vboxContainer.getChildren().add(noPublications);
            return;
        }

        for (Publications pub : publications) {
            ajouterPublication(pub);
        }
    }

    @FXML
    void searchPublication(ActionEvent event) {
        if (allPublications == null) {
            System.err.println("⚠ Aucune publication chargée.");
            return;
        }

        String searchText = textRecherchPublication.getText().trim().toLowerCase();
        List<Publications> filteredList = allPublications.stream()
                .filter(pub -> pub.getTitle() != null && pub.getTitle().toLowerCase().contains(searchText))
                .collect(Collectors.toList());

        afficherPublications(filteredList);
    }

    private void ajouterPublication(Publications pub) {
        HBox card = new HBox();
        card.setSpacing(10);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #d1d1d1; -fx-border-radius: 10px; " +
                "-fx-padding: 10px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);");

        ImageView imageView = new ImageView();
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(true);

        try {
            if (pub.getImage_url() != null && !pub.getImage_url().isEmpty()) {
                imageView.setImage(new Image(pub.getImage_url(), true));
            } else {
                imageView.setImage(new Image("https://via.placeholder.com/100"));
            }
        } catch (Exception e) {
            imageView.setImage(new Image("https://via.placeholder.com/100"));
        }

        VBox textContainer = new VBox();
        textContainer.setSpacing(5);

        Label titleLabel = new Label("Title: " + pub.getTitle());
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label descriptionLabel = new Label(pub.getDescription());
        descriptionLabel.setWrapText(true);
        descriptionLabel.setMaxWidth(300);

        Label userLabel = new Label("Posté par : " + servicePublications.getUsernameById(pub.getId_user()));
        userLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

        Label dateLabel = new Label("🕒 " + pub.getPublication_date().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

        textContainer.getChildren().addAll(titleLabel, descriptionLabel, userLabel, dateLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox buttonsContainer = new HBox();
        buttonsContainer.setSpacing(5);

        int userId = session.id_utilisateur;

        if (pub.getId_user() == userId) {
            Button editButton = new Button("\uE02F");
            editButton.setStyle("-fx-font-family: 'Dripicons-v2'; -fx-font-size: 16px; -fx-background-color: #bca2bf; " +
                    "-fx-text-fill: white; -fx-padding: 4px; -fx-border-radius: 5px;");
            editButton.setOnAction(event -> modifierPublication(pub));

            Button deleteButton = new Button("\uE053");
            deleteButton.setStyle("-fx-font-family: 'Dripicons-v2'; -fx-font-size: 16px; -fx-background-color: #bca2bf; " +
                    "-fx-text-fill: white; -fx-padding: 4px; -fx-border-radius: 5px;");
            deleteButton.setOnAction(event -> supprimerPublication(pub));

            buttonsContainer.getChildren().addAll(editButton, deleteButton);
        } else {
            Button translateButton = new Button("\uE064");
            translateButton.setStyle("-fx-font-family: 'Dripicons-v2'; -fx-font-size: 20px; -fx-background-color: #bca2bf; " +
                    "-fx-text-fill: white; -fx-padding: 4px; -fx-border-radius: 8px;");
            translateButton.setOnAction(event -> afficherBoiteTraduction(titleLabel, descriptionLabel, pub));


            Button reportButton = new Button("\uE063"); // Icône de signalement
            reportButton.setStyle("-fx-font-family: 'Dripicons-v2'; -fx-font-size: 20px; -fx-background-color: #bca2bf; " +
                    "-fx-text-fill: white; -fx-padding: 4px; -fx-border-radius: 8px;");
            reportButton.setOnAction(event -> signalerPublication(pub));

            buttonsContainer.getChildren().addAll(translateButton, reportButton);
        }

        card.getChildren().addAll(imageView, textContainer, spacer, buttonsContainer);
        vboxContainer.getChildren().add(card);
    }

    /**
     * ✅ Signale une publication et ouvre un formulaire de signalement.
     */
    private void signalerPublication(Publications pub) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterReport.fxml"));
            Parent root = loader.load();

            AjouterReportController controller = loader.getController();
            controller.initData(pub);

            Stage stage = new Stage();
            stage.setTitle("Report a publication");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            System.err.println("❌ Erreur lors de l'ouverture du formulaire de signalement : " + e.getMessage());
        }
    }


    private void afficherBoiteTraduction(Label titleLabel, Label descriptionLabel, Publications pub) {
        Stage translationStage = new Stage();
        translationStage.setTitle("Choisissez une langue");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #f5f5f5;");

        Scene scene = new Scene(vbox, 300, 150);
        scene.getStylesheets().add(getClass().getResource("/styles/Publication-style.css").toExternalForm());

        Map<String, String> languageMap = Map.of(
                "Anglais", "en", "Français", "fr", "Espagnol", "es",
                "Allemand", "de", "Arabe", "ar",
                "Russe", "ru", "Italien", "it"
        );

        ComboBox<String> languageComboBox = new ComboBox<>();
        languageComboBox.setItems(FXCollections.observableArrayList(languageMap.keySet()));
        languageComboBox.setPromptText("Select a language");
        languageComboBox.setStyle("-fx-font-size: 14px;");
        languageComboBox.getStyleClass().add("combo-box");

        Button translateButton = new Button("Translate");
        translateButton.getStyleClass().add("translate-button");
        translateButton.setOnAction(event -> {
            String selectedLanguage = languageComboBox.getValue();
            if (selectedLanguage != null) {
                String languageCode = languageMap.get(selectedLanguage);
                traduirePublication(pub, titleLabel, descriptionLabel, languageCode);
                translationStage.close();
            }
        });

        vbox.getChildren().addAll(languageComboBox, translateButton);

        translationStage.setScene(scene);
        translationStage.initModality(Modality.APPLICATION_MODAL);
        translationStage.show();
    }


    private void traduirePublication(Publications pub, Label titleLabel, Label descriptionLabel, String languageCode) {
        // Traduire le titre
        String traductionTitre = TranslationService.translateText(pub.getTitle(), languageCode);
        titleLabel.setText("Title: " + traductionTitre);

        // Traduire la description
        String traductionDescription = TranslationService.translateText(pub.getDescription(), languageCode);
        descriptionLabel.setText(traductionDescription);
    }






    /**
     * ✅ Supprime une publication et rafraîchit l'affichage.
     */
    private void supprimerPublication(Publications pub) {
        try {
            servicePublications.supprimer(pub.getPublication_id());
            allPublications.remove(pub); // 🔹 Supprime de la liste
            afficherPublications(allPublications); // 🔹 Rafraîchir l'affichage
            System.out.println("✅ Publication supprimée : " + pub.getTitle());
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la suppression de la publication : " + e.getMessage());
        }
    }

    /**
     * ✅ Modifie une publication.
     */
    private void modifierPublication(Publications pub) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierPublication.fxml"));
            Parent root = loader.load();

            ModifierPublicationController controller = loader.getController();
            controller.initData(pub); // ✅ Appeler initData()

            Stage stage = new Stage();
            stage.setTitle("Modifier la Publication");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            allPublications = servicePublications.getAll(); // 🔄 Recharger les données
            afficherPublications(allPublications); // 🔄 Rafraîchir après modification

        } catch (IOException e) {
            System.err.println("❌ Erreur lors de l'ouverture de ModifierPublication.fxml : " + e.getMessage());
        }
    }

    /**
     * ✅ Ajoute une nouvelle publication en ouvrant l'interface AjouterPublication.
     */
    @FXML
    void addPublication() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterPublication.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter une Publication");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            allPublications = servicePublications.getAll(); // 🔄 Recharger après ajout
            afficherPublications(allPublications);

        } catch (IOException e) {
            System.err.println("❌ Erreur lors de l'ouverture de AjouterPublication.fxml : " + e.getMessage());
        }
    }

    /**
     * ✅ Retour à l'accueil.
     */
    @FXML
    void returnAcceuil(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sidebarClient.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

            System.out.println("✅ Redirection vers l'accueil réussie.");

        } catch (IOException e) {
            System.err.println("❌ Erreur lors du chargement de sideBareClient.fxml : " + e.getMessage());
        }
    }
}
