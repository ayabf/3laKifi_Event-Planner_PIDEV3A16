// ProductService.java (Service Layer for Product)
package services;
import models.Product;
import utils.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductService implements IService<Product> {
    private Connection connection;

    public ProductService() {
        connection = DataSource.getInstance().getConnection();
    }

    @Override
    public void ajouter(Product product) throws SQLException {
        String sql = "INSERT INTO product (name, description, price, stock_id, image_url, category, id_user) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, product.getName());
        preparedStatement.setString(2, product.getDescription());
        preparedStatement.setFloat(3, product.getPrice());
        preparedStatement.setInt(4, product.getStockId());
        preparedStatement.setString(5, product.getImageUrl());
        preparedStatement.setString(6, product.getCategory());
        preparedStatement.setInt(7, product.getIdUser());
        preparedStatement.executeUpdate();
    }

    @Override
    public void modifier(Product product) throws SQLException {
        String sql = "UPDATE product SET name = ?, description = ?, price = ?, stock_id = ?, image_url = ?, category = ?, id_user = ? WHERE product_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, product.getName());
        preparedStatement.setString(2, product.getDescription());
        preparedStatement.setFloat(3, product.getPrice());
        preparedStatement.setInt(4, product.getStockId());
        preparedStatement.setString(5, product.getImageUrl());
        preparedStatement.setString(6, product.getCategory());
        preparedStatement.setInt(7, product.getIdUser());
        preparedStatement.setInt(8, product.getProductId());
        preparedStatement.executeUpdate();
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM product WHERE product_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
    }

    public List<Product> getAll(Product product) throws SQLException {
        long startTime = System.currentTimeMillis(); // Démarrer le chrono

        String sql = "SELECT * FROM product";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        List<Product> list = new ArrayList<>();

        while (rs.next()) {
            list.add(new Product(
                    rs.getInt("product_id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getFloat("price"),
                    rs.getInt("stock_id"),
                    rs.getString("image_url"),
                    rs.getString("category"),
                    rs.getInt("id_user")
            ));
        }

        long endTime = System.currentTimeMillis(); // Fin du chrono
        System.out.println("Temps d'exécution de getAll(): " + (endTime - startTime) + "ms");

        return list;
    }


    public List<Product> recupererParCategorie(String category) throws SQLException {
        String sql = "SELECT * FROM product WHERE category = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, category);
        ResultSet rs = preparedStatement.executeQuery();
        List<Product> list = new ArrayList<>();
        while (rs.next()) {
            list.add(new Product(
                    rs.getInt("product_id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getFloat("price"),
                    rs.getInt("stock_id"),
                    rs.getString("image_url"),
                    rs.getString("category"),
                    rs.getInt("id_user")
            ));
        }
        return list;
    }
    @Override
    public Product getOne(Product product) throws SQLException {
        String sql = "SELECT * FROM product WHERE product_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, product.getProductId());
        ResultSet rs = preparedStatement.executeQuery();
        if (rs.next()) {
            return new Product(
                    rs.getInt("product_id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getFloat("price"),
                    rs.getInt("stock_id"),
                    rs.getString("image_url"),
                    rs.getString("category"),
                    rs.getInt("id_user")
            );
        }
        return null;
    }

}
