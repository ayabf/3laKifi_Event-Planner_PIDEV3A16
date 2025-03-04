package controllers;

import Models.Publications;
import Models.session;
import javafx.geometry.Insets;
import services.ServicePublications;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class AfficherPublication2Controller {

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox vboxContainer;

    @FXML
    private TextField textRecherchPublication; // ✅ Champ de recherche

    private final ServicePublications servicePublications = new ServicePublications();
    private List<Publications> allPublications; // ✅ Stocke toutes les publications

    @FXML
    void initialize() {
        System.out.println("🔹 Initialisation de l'affichage des publications...");
        try {
            allPublications = servicePublications.getAll(); // ✅ Charge toutes les publications
            afficherPublications(allPublications); // ✅ Affiche les publications
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement des publications : " + e.getMessage());
        }
    }

    /**
     * ✅ Affiche les publications données
     */
    private void afficherPublications(List<Publications> publications) {
        vboxContainer.getChildren().clear(); // Nettoyer l'affichage

        if (publications.isEmpty()) {
            Label noPublications = new Label("Aucune publication disponible.");
            vboxContainer.getChildren().add(noPublications);
            return;
        }

        for (Publications pub : publications) {
            ajouterPublication(pub); // ✅ Afficher chaque publication
        }
    }

    /**
     * ✅ Recherche une publication par titre avec `Stream`
     */
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

        afficherPublications(filteredList); // ✅ Afficher les résultats filtrés
    }

    /**
     * ✅ Ajoute une publication sous forme de carte
     */
    private void ajouterPublication(Publications pub) {
        HBox card = new HBox();
        card.setSpacing(10);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #d1d1d1; -fx-border-radius: 10px; " +
                "-fx-padding: 10px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);");

        // ImageView pour afficher l'image
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

        // VBox pour afficher le titre, description et utilisateur
        VBox textContainer = new VBox();
        textContainer.setSpacing(5);

        Label titleLabel = new Label("📌 " + pub.getTitle());
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label descriptionLabel = new Label(pub.getDescription());
        descriptionLabel.setWrapText(true);
        descriptionLabel.setMaxWidth(300);

        Label userLabel = new Label("Posté par : " + servicePublications.getUsernameById(pub.getId_user()));
        userLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

        Label dateLabel = new Label("🕒 " + pub.getPublication_date().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

        textContainer.getChildren().addAll(titleLabel, descriptionLabel, userLabel, dateLabel);

        // Espaceur pour aligner les boutons à droite
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox buttonsContainer = new HBox();
        buttonsContainer.setSpacing(5);

        int userId = session.id_utilisateur;

        if (pub.getId_user() == userId) {
            // Bouton Modifier
            Button editButton = new Button("\uE02F"); // Dripicons 'pencil'
            editButton.setStyle("-fx-font-family: 'Dripicons-v2'; -fx-font-size: 16px; -fx-background-color: #bca2bf; " +
                    "-fx-text-fill: white; -fx-padding: 4px; -fx-border-radius: 5px;");
            editButton.setOnAction(event -> modifierPublication(pub));

            // Bouton Supprimer
            Button deleteButton = new Button("\uE053"); // Dripicons 'trash'
            deleteButton.setStyle("-fx-font-family: 'Dripicons-v2'; -fx-font-size: 16px; -fx-background-color: #bca2bf; " +
                    "-fx-text-fill: white; -fx-padding: 4px; -fx-border-radius: 5px;");
            deleteButton.setOnAction(event -> supprimerPublication(pub));

            buttonsContainer.getChildren().addAll(editButton, deleteButton);
        } else {
            // ✅ Bouton Traduire (uniquement si ce n'est pas l'utilisateur connecté)
            Button translateButton = new Button("🌍");
            translateButton.setStyle("-fx-background-color: #bca2bf; -fx-text-fill: white; -fx-border-radius: 5px;");
            translateButton.setOnAction(event -> traduirePublication(pub, descriptionLabel));

            buttonsContainer.getChildren().add(translateButton);
        }

        card.getChildren().addAll(imageView, textContainer, spacer, buttonsContainer);
        vboxContainer.getChildren().add(card);
    }

    /**
     * ✅ Fonction de traduction (simulée)
     */
    private void traduirePublication(Publications pub, Label descriptionLabel) {
        System.out.println("🔄 Traduction de la publication : " + pub.getTitle());

        // Simuler une traduction (remplacez ceci par un appel API comme Google Translate)
        String traduction = "[Traduction] " + pub.getDescription();
        descriptionLabel.setText(traduction);
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
