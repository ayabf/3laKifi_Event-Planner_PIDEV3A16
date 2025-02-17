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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EventManagement.fxml"));
            Parent root = loader.load();
            Scene sc = new Scene(root);
            //stage.setTitle("Ajouter");
            stage.setScene(sc);
            stage.show();
        }catch (IOException e){
            System.out.println(e.getMessage());
        }

    }
}
