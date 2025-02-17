package tests;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import Models.City;
import Models.Event;
import services.ServiceEvent;
import utils.DataSource;

public class main {
    public static void main(String[] args) {
        //DataSource.getInstance();
        ServiceEvent service = new ServiceEvent();
        Event event = new Event("Mariage Bohème", "Un mariage élégant", "mariage.png",
                LocalDateTime.now(), LocalDateTime.now().plusHours(4),
                150, City.TUNIS, 1);
        //ajouter
        try {
            service.ajouter(event);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        //modifier
        try {
            service.modifier(new Event(2, "Mariage orientale", "Un mariage tres élégant", "mariage.png",
                    LocalDateTime.now(), LocalDateTime.now().plusHours(4),
                    150, City.TUNIS, 1));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        //afficher tout
        try {
            System.out.println(service.getAll());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        //supprimer
        try {
            service.supprimer(2);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


    }
    }


