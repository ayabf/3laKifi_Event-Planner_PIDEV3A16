package services;

import Models.Location;
import Models.City;
import utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceLocation implements IService<Location> {
    private Connection conn;
    private Statement ste;
    private PreparedStatement pste;

    public ServiceLocation() {
        conn = DataSource.getInstance().getConnection();
    }

    private City normalizeAndParseCity(String cityName) {
        // Remove any accents and special characters, convert to uppercase
        String normalizedCity = cityName.toUpperCase()
            .replace("É", "E")
            .replace("È", "E")
            .replace("Ê", "E")
            .replace("Ë", "E")
            .replace(" ", "_");
        return City.valueOf(normalizedCity);
    }

    @Override
    public void ajouter(Location location) throws SQLException {
        String req = "INSERT INTO location (name, city, image_data, image_filename, capacity, dimension, price) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        pste = conn.prepareStatement(req);
        pste.setString(1, location.getName());
        pste.setString(2, location.getVille().name());
        pste.setBytes(3, location.getImageData());
        pste.setString(4, location.getImageFileName());
        pste.setInt(5, location.getCapacity());
        pste.setString(6, location.getDimension());
        pste.setDouble(7, location.getPrice());
        pste.executeUpdate();
    }

    @Override
    public void modifier(Location location) throws SQLException {
        String req = "UPDATE location SET name=?, city=?, image_data=?, image_filename=?, capacity=?, dimension=?, price=? " +
                    "WHERE id_location=?";
        pste = conn.prepareStatement(req);
        pste.setString(1, location.getName());
        pste.setString(2, location.getVille().name());
        pste.setBytes(3, location.getImageData());
        pste.setString(4, location.getImageFileName());
        pste.setInt(5, location.getCapacity());
        pste.setString(6, location.getDimension());
        pste.setDouble(7, location.getPrice());
        pste.setInt(8, location.getId_location());
        pste.executeUpdate();
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM location WHERE id_location=?";
        pste = conn.prepareStatement(req);
        pste.setInt(1, id);
        pste.executeUpdate();
    }

    @Override
    public Location getOne(Location location) throws SQLException {
        String req = "SELECT * FROM location WHERE id_location=?";
        pste = conn.prepareStatement(req);
        pste.setInt(1, location.getId_location());
        ResultSet rs = pste.executeQuery();
        if (rs.next()) {
            return new Location(
                rs.getInt("id_location"),
                rs.getString("name"),
                normalizeAndParseCity(rs.getString("city")),
                rs.getBytes("image_data"),
                rs.getString("image_filename"),
                rs.getInt("capacity"),
                rs.getString("dimension"),
                rs.getDouble("price")
            );
        }
        return null;
    }

    @Override
    public List<Location> getAll() throws SQLException {
        List<Location> list = new ArrayList<>();
        String req = "SELECT * FROM location";
        ste = conn.createStatement();
        ResultSet rs = ste.executeQuery(req);
        while (rs.next()) {
            Location location = new Location(
                rs.getInt("id_location"),
                rs.getString("name"),
                normalizeAndParseCity(rs.getString("city")),
                rs.getBytes("image_data"),
                rs.getString("image_filename"),
                rs.getInt("capacity"),
                rs.getString("dimension"),
                rs.getDouble("price")
            );
            list.add(location);
        }
        return list;
    }

    public int getTotalCount() throws SQLException {
        String req = "SELECT COUNT(*) FROM location";
        ste = conn.createStatement();
        ResultSet rs = ste.executeQuery(req);
        if (rs.next()) {
            return rs.getInt(1);
        }
        return 0;
    }
} 