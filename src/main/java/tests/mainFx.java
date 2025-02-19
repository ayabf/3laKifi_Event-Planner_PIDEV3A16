// MainFx.java (JavaFX Application Entry Point)
package tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;

public class mainFx extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {
            System.out.println("Chargement du fichier FXML...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierProduct.fxml"));
            Parent root = loader.load();
            System.out.println("FXML chargé avec succès !");

            Scene scene = new Scene(root);
            stage.setTitle("Afficher Produits");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.out.println("Erreur de chargement FXML : " + e.getMessage());
        }
    }


}
