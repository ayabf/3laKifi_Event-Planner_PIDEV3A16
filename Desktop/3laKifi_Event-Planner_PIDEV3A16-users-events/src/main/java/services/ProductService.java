package services;

import Models.Product;
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
        String sql = "INSERT INTO product (reference, name, description, price, stock_id, image_url, category, id_user) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, product.getReference());
        preparedStatement.setString(2, product.getName());
        preparedStatement.setString(3, product.getDescription());
        preparedStatement.setFloat(4, product.getPrice());
        preparedStatement.setInt(5, product.getStockId());
        preparedStatement.setString(6, product.getImageUrl());
        preparedStatement.setString(7, product.getCategory());
        preparedStatement.setInt(8, product.getIdUser());
        preparedStatement.executeUpdate();
    }

    @Override
    public void modifier(Product product) throws SQLException {
        String sql = "UPDATE product SET name = ?, description = ?, price = ?, stock_id = ?, image_url = ?, category = ?, id_user = ? WHERE reference = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, product.getName());
        preparedStatement.setString(2, product.getDescription());
        preparedStatement.setFloat(3, product.getPrice());
        preparedStatement.setInt(4, product.getStockId());
        preparedStatement.setString(5, product.getImageUrl());
        preparedStatement.setString(6, product.getCategory());
        preparedStatement.setInt(7, product.getIdUser());
        preparedStatement.setString(8, product.getReference());
        preparedStatement.executeUpdate();
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM product WHERE product_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
    }

    /**
     * ✅ Reference-based deletion (calls `supprimer(int id)` but with reference)
     */
    public void supprimerByReference(String reference) throws SQLException {
        String sql = "DELETE FROM product WHERE reference = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, reference);
        preparedStatement.executeUpdate();
    }
    public List<Product> getByCategory(String category) throws SQLException {
        String sql = "SELECT * FROM product WHERE category = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, category);
        ResultSet rs = preparedStatement.executeQuery();

        List<Product> products = new ArrayList<>();
        while (rs.next()) {
            products.add(mapResultSetToProduct(rs));
        }
        return products;
    }

    @Override
    public Product getOne(Product product) throws SQLException {
        String sql = "SELECT * FROM product WHERE product_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, product.getProductId());
        ResultSet rs = preparedStatement.executeQuery();
        if (rs.next()) {
            return mapResultSetToProduct(rs);
        }
        return null;
    }

    @Override
    public List<Product> getAll() throws SQLException {
        return List.of();
    }

    /**
     * ✅ Retrieves product by reference
     */
    public Product getByReference(String reference) throws SQLException {
        String sql = "SELECT * FROM product WHERE reference = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, reference);
        ResultSet rs = preparedStatement.executeQuery();
        if (rs.next()) {
            return mapResultSetToProduct(rs);
        }
        return null;
    }

    @Override
    public List<Product> getAll1(Product product) throws SQLException {
        String sql = "SELECT * FROM product";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        List<Product> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapResultSetToProduct(rs));
        }
        return list;
    }

    /**
     * ✅ Helper method to map `ResultSet` to `Product` object
     */
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        return new Product(
                rs.getInt("product_id"),
                rs.getString("reference"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getFloat("price"),
                rs.getInt("stock_id"),
                rs.getString("image_url"),
                rs.getString("category"),
                rs.getInt("id_user")
        );
    }
}