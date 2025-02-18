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
        try {
            ServiceEvent se = new ServiceEvent();
            
            // Create a new event with null image data for testing
            Event e1 = new Event(
                "Test Event",
                "Test Description",
                null, // image data
                null, // image filename
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(2),
                100,
                City.TUNIS,
                1
            );
            
            // Add the event
            se.ajouter(e1);
            System.out.println("Event added successfully!");
            
            // Test getting all events
            System.out.println("All events:");
            se.getAll().forEach(System.out::println);
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


