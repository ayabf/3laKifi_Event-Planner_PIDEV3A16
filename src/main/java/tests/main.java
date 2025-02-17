package tests;

import Models.Comments;
import Models.Publications;
import services.ServiceComments;
import services.ServicePublications;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class main {
    public static void main(String[] args) throws SQLException {
        //test Crud Publication
        /*ServicePublications ps = new ServicePublications();
        Publications publication = new Publications("souhaib", "Description Test", "image.jpg", 1);
        ps.ajouter(publication);
        publication = ps.getOne(5);
        System.out.println(publication);
        Publications new_publication = new Publications(8, 1, "amira", "ffff","");
        ps.modifier(new_publication);
        ps.supprimer(6);
        List<Publications> ListPub = new ArrayList<>();
        ListPub=ps.getAll();
        System.out.println(ListPub);*/
        //test crud Comments
        /*ServiceComments serviceComments = new ServiceComments();
        String titrePublication = "souhaib";
        String username = "Mira197";
       Comments comment = new Comments("Très bon article !");
        serviceComments.ajouter(comment, titrePublication, username);
        String newContent = "Commentaire désagréable!";
        serviceComments.modifier(username, titrePublication, newContent);
        //serviceComments.supprimer(username, titrePublication);
        List<Comments> comments = serviceComments.getAllByPublicationTitle("amal");
        System.out.println(comments);*/



    }
}
