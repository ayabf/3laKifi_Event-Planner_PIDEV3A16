package tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class mainFx extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
       /* try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Ajouter.fxml"));
            Parent root = loader.load();
            Scene sc = new Scene(root);
            stage.setTitle("Ajouter");
            stage.setScene(sc);
            stage.show();
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
*/
        try {
            // Vérifie le bon chemin du fichier FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cart.fxml"));
            Parent root = loader.load();
            root.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

            Scene scene = new Scene(root, 600, 600); // 🌟 Taille plus grande
            stage.setTitle("Shopping Cart");
            stage.setScene(scene);
            stage.setMaximized(false); // 🌟 Plein écran
            stage.show();


        } catch (IOException e) {
            System.err.println("❌ Erreur : Impossible de charger cart.fxml !");
            e.printStackTrace();
        }
    }
}
