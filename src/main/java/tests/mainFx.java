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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterPublication.fxml"));
        try {
            Parent root = loader.load();
            Scene sc = new Scene(root);
            stage.setScene(sc);
            stage.setTitle("Ajouter");
            stage.show();
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
