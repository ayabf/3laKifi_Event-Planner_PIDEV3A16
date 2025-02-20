package services;

import Models.Stock;
import utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StockService implements IService<Stock> {
    private final Connection connection;

    public StockService() {
        connection = DataSource.getInstance().getConnection();
    }

    @Override
    public void ajouter(Stock stock) throws SQLException {
        String sql = "INSERT INTO stock (available_quantity, minimum_quantity, last_update, id_user) VALUES (?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, stock.getAvailableQuantity());
        preparedStatement.setInt(2, stock.getMinimumQuantity());
        preparedStatement.setTimestamp(3, stock.getLastUpdate()); // Timestamp est utilisé ici
        preparedStatement.setInt(4, stock.getIdUser());
        preparedStatement.executeUpdate();
    }

    @Override
    public void modifier(Stock stock) throws SQLException {
        String sql = "UPDATE stock SET available_quantity = ?, minimum_quantity = ?, last_update = ?, id_user = ? WHERE stock_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, stock.getAvailableQuantity());
        preparedStatement.setInt(2, stock.getMinimumQuantity());
        preparedStatement.setTimestamp(3, stock.getLastUpdate()); // Timestamp
        preparedStatement.setInt(4, stock.getIdUser());
        preparedStatement.setInt(5, stock.getStockId());
        preparedStatement.executeUpdate();
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM stock WHERE stock_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
    }

    @Override
    public List<Stock> getAll(Stock stock) throws SQLException {
        String sql = "SELECT * FROM stock";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        List<Stock> stocks = new ArrayList<>();

        while (rs.next()) {
            stocks.add(new Stock(
                    rs.getInt("stock_id"),
                    rs.getInt("available_quantity"),
                    rs.getInt("minimum_quantity"),
                    rs.getTimestamp("last_update"), // On récupère Timestamp
                    rs.getInt("id_user")
            ));
        }
        return stocks;
    }

    @Override
    public Stock getOne(Stock stock) throws SQLException {
        String sql = "SELECT * FROM stock WHERE stock_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, stock.getStockId());
        ResultSet rs = preparedStatement.executeQuery();

        if (rs.next()) {
            return new Stock(
                    rs.getInt("stock_id"),
                    rs.getInt("available_quantity"),
                    rs.getInt("minimum_quantity"),
                    rs.getTimestamp("last_update"), // Timestamp
                    rs.getInt("id_user")
            );
        }
        return null;
    }

    // Récupérer les stocks d'un utilisateur spécifique
    public List<Stock> getStocksByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM stock WHERE id_user = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, userId);
        ResultSet rs = preparedStatement.executeQuery();
        List<Stock> stocks = new ArrayList<>();

        while (rs.next()) {
            stocks.add(new Stock(
                    rs.getInt("stock_id"),
                    rs.getInt("available_quantity"),
                    rs.getInt("minimum_quantity"),
                    rs.getTimestamp("last_update"), // Timestamp
                    rs.getInt("id_user")
            ));
        }
        return stocks;
    }
}
