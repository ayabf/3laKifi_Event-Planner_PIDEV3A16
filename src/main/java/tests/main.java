package tests;

import javafx.application.Application;
import models.Product;
import services.ProductService;

import java.sql.SQLException;
import java.util.List;

public class main {
    public static void main(String[] args) {
        ProductService productService = new ProductService();

        long startTime = System.currentTimeMillis();
        try {
            List<Product> products = productService.getAll(new Product());
            long endTime = System.currentTimeMillis();
            System.out.println("Temps de chargement de la base de données: " + (endTime - startTime) + " ms");
        } catch (SQLException e) {
            System.out.println("Erreur de base de données: " + e.getMessage());
        }

        Application.launch(mainFx.class, args);
    }

}


