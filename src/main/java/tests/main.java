package tests;

import Models.Role;
import Models.User;
import services.UserService;

import java.util.List;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class main {
    public static void main(String[] args) {

        // Créez un objet User pour les tests
        User testUser = new User("ayachi", "zeinedddb", "happppp588", "password123",
                Role.ADMIN, "A short bio", "123 Main St", "/path/to/image.jpg", "hasssssmrouni.hajer@esprit.tn",20454874);

        User testUser2= new User("aya1111", "aya11", "aya11", "password123",
                Role.CLIENT, "A short bio", "123 Main St", "/path/to/image.jpg", "aya11@esprit.tn",21454874);


        User client = new User("client", "client", "client", "password123",
                Role.CLIENT, "A short bio", "123client", "/path/to/image.jpg", "client@esprit.tn",20457777);





        User test = new User("test", "test", "test", "password123",
                Role.ADMIN, "A short test", "123 test St", "/path/to/image.jpg", "dtest2r@esprit.tn",29454874);

        User test2 = new User("test2", "test2", "test2", "password123",
                Role.ADMIN, "A short test", "123 test St", "/path/to/image.jpg", "test2@esprit.tn",24454874);

        User test44 = new User("test", "test2788", "test2788", "password123",
                Role.ADMIN, "A short test", "123 test St", "/path/to/image.jpg", "dtest2rtest2788@esprit.tn",24254874);

        User test244 = new User("test2788", "test2788", "test2788", "password123",
                Role.ADMIN, "A short test", "123 test St", "/path/to/image.jpg", "test2test2788@esprit.tn",26465874);


        User dheker = new User("dheker1", "dheker1", "dheker1", "password123",
                Role.ADMIN, "A short test", "123 test St", "/path/to/image.jpg", "dhekerlaadhibii@gmail.com",20159874);

        User aya = new User("aya", "touta", "aya", "password123",
                Role.ADMIN, "A short test", "123 test St", "/path/to/image.jpg", "ayabenfraj@gmail.com",95513380);


//        // Créez un service utilisateur
        UserService userService = new UserService();
//
//        // Ajoutez l'utilisateur à la base de données
        userService.ajouter4(testUser);
        userService.ajouter4(testUser2);

        userService.ajouter4(test244);
        userService.ajouter4(dheker);


        //Affichez la liste des utilisateurs pour vérifier l'ajout
        List<User> users = userService.afficher();
        System.out.println("Liste des utilisateurs après ajout :");
        for (User user : users) {
            System.out.println(user);
        }

        // Testez l'authentification avec un nom d'utilisateur ou une adresse e-mail et un mot de passe
        String usernameOrEmail = "hasssssmrouni.hajer@esprit.tn"; // Remplacez par le nom d'utilisateur ou l'adresse e-mail à tester
        String password = "passwor23"; // Remplacez par le mot de passe à tester

        if (userService.verifierUtilisateur(usernameOrEmail, password)) {
            System.out.println("Authentification réussie pour " + usernameOrEmail);
        } else {
            System.out.println("Échec de l'authentification pour " + usernameOrEmail);
        }

    }

/**
 * **************************************************************
 */


}



