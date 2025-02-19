package tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class mainFx extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherPublication.fxml"));
            Parent root = loader.load();

            primaryStage.setTitle("Gestion des Publications");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Erreur de chargement du FXML : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
