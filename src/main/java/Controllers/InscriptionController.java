/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import Models.Inscription;
import services.ServiceInscription;
import utils.Mail;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author mahmo
 */
public class InscriptionController implements Initializable {

    @FXML
    private TextField tf_nom;
    @FXML
    private TextField tf_prenom;
    @FXML
    private TextField tf_email;
    @FXML
    private Label id;
    private Inscription inscrit = new Inscription();
    private int idE;

    @FXML
    private Button id_abondonner;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void inscrire(ActionEvent event) {
        
             
         ServiceInscription postService = new ServiceInscription();
    inscrit.setNom(tf_nom.getText());
    inscrit.setPrenom(tf_prenom.getText());
    inscrit.setEmail(tf_email.getText());

   //  Integer.valueOf(id_post.getText());
  
    postService.Inscrire(inscrit, idE);
    
    
           String recipient = tf_email.getText();
                 try {
                 Mail.envoyer(recipient);
                 System.out.println("Le message a été envoyé avec succès.");
                 } catch (Exception e) {
                 System.err.println("Erreur lors de l'envoi du message : " + e.getMessage());
                 e.printStackTrace();
               }
                 
                 
    }
    
    



    @FXML
    private void abodonner(ActionEvent event) throws IOException {
        Parent page1 = FXMLLoader.load(getClass().getResource("/AffichageFormation.fxml"));
        Scene scene = new Scene(page1);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
