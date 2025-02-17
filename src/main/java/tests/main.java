package tests;

import Models.Publications;
import services.ServicePublications;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class main {
    public static void main(String[] args) throws SQLException {
        //test Crud Publication
        ServicePublications ps = new ServicePublications();
         /* Publications publication = new Publications("souhaib", "Description Test", "image.jpg", 1);
        ps.ajouter(publication);
        publication = ps.getOne(5);
        System.out.println(publication);*/
        Publications new_publication = new Publications(8, 1, "amira", "ffff","");
        ps.modifier(new_publication);
        //ps.supprimer(6);
        List<Publications> ListPub = new ArrayList<>();
        ListPub=ps.getAll();
        System.out.println(ListPub);


    }
}
