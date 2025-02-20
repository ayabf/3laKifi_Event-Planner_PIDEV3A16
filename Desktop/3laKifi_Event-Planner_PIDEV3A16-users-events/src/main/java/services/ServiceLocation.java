package services;

import Models.Location;
import Models.City;
import utils.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceLocation {
    private Connection conn;

    public ServiceLocation() {
        this.conn = DataSource.getInstance().getConnection();
    }

    public void ajouter(Location location) throws SQLException {
        String query = "INSERT INTO location (name, address, city, capacity, status, description, dimension, price, image_data, image_filename) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, location.getName());
            pst.setString(2, location.getAddress());
            pst.setString(3, location.getVille().name());
            pst.setInt(4, location.getCapacity());
            pst.setString(5, location.getStatus());
            pst.setString(6, location.getDescription());
            pst.setString(7, location.getDimension());
            pst.setDouble(8, location.getPrice());
            pst.setBytes(9, location.getImageData());
            pst.setString(10, location.getImageFileName());

            int affectedRows = pst.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    location.setId_location(rs.getInt(1));
                }
            }
        }
    }

    public void modifier(Location location) throws SQLException {
        String query = "UPDATE location SET name=?, address=?, city=?, capacity=?, " +
                      "status=?, description=?, dimension=?, price=?, image_data=?, image_filename=? " +
                      "WHERE id_location=?";
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setString(1, location.getName());
            pst.setString(2, location.getAddress());
            pst.setString(3, location.getVille().name());
            pst.setInt(4, location.getCapacity());
            pst.setString(5, location.getStatus());
            pst.setString(6, location.getDescription());
            pst.setString(7, location.getDimension());
            pst.setDouble(8, location.getPrice());
            pst.setBytes(9, location.getImageData());
            pst.setString(10, location.getImageFileName());
            pst.setInt(11, location.getId_location());

            pst.executeUpdate();
        }
    }

    public void supprimer(int id) throws SQLException {
        String query = "DELETE FROM location WHERE id_location=?";
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, id);
            pst.executeUpdate();
        }
    }

    public Location getOne(Location location) throws SQLException {
        String query = "SELECT * FROM location WHERE id_location=?";
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, location.getId_location());
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return extractLocationFromResultSet(rs);
            }
        }
        return null;
    }

    public Location getById(int id) throws SQLException {
        String query = "SELECT * FROM location WHERE id_location=?";
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return extractLocationFromResultSet(rs);
            }
        }
        return null;
    }

    public List<Location> getAll() throws SQLException {
        List<Location> locations = new ArrayList<>();
        String query = "SELECT * FROM location ORDER BY name";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                locations.add(extractLocationFromResultSet(rs));
            }
        }
        return locations;
    }

    public int getTotalCount() throws SQLException {
        String query = "SELECT COUNT(*) FROM location";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public int getActiveCount() throws SQLException {
        String query = "SELECT COUNT(*) FROM location WHERE status='Active'";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private Location extractLocationFromResultSet(ResultSet rs) throws SQLException {
        return new Location(
            rs.getInt("id_location"),
            rs.getString("name"),
            rs.getString("address"),
            City.valueOf(rs.getString("city")),
            rs.getInt("capacity"),
            rs.getString("status"),
            rs.getString("description"),
            rs.getString("dimension"),
            rs.getDouble("price"),
            rs.getBytes("image_data"),
            rs.getString("image_filename")
        );
    }

    public void refreshConnection() {
        this.conn = DataSource.getInstance().getConnection();
    }
} 