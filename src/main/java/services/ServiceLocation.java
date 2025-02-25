package services;

import Models.Location;
import Models.City;
import utils.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class ServiceLocation {
    private Connection conn;

    public ServiceLocation() {
        this.conn = DataSource.getInstance().getConnection();
    }

    public void ajouter(Location location) throws SQLException {
        String query = "INSERT INTO location (name, address, city, capacity, status, description, dimension, price, " +
                      "image_data, image_filename, has_3d_tour, table_set_count, include_corner_plants, " +
                      "window_style, door_style, include_ceiling_lights, light_color) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
            pst.setBoolean(11, location.getHas3DTour());
            pst.setInt(12, location.getTableSetCount());
            pst.setBoolean(13, location.isIncludeCornerPlants());
            pst.setString(14, location.getWindowStyle());
            pst.setString(15, location.getDoorStyle());
            pst.setBoolean(16, location.isIncludeCeilingLights());
            pst.setString(17, location.getLightColor());

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
        String query = "UPDATE location SET name=?, address=?, city=?, capacity=?, status=?, description=?, " +
                      "dimension=?, price=?, image_data=?, image_filename=?, has_3d_tour=?, table_set_count=?, " +
                      "include_corner_plants=?, window_style=?, door_style=?, include_ceiling_lights=?, light_color=? " +
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
            pst.setBoolean(11, location.getHas3DTour());
            pst.setInt(12, location.getTableSetCount());
            pst.setBoolean(13, location.isIncludeCornerPlants());
            pst.setString(14, location.getWindowStyle());
            pst.setString(15, location.getDoorStyle());
            pst.setBoolean(16, location.isIncludeCeilingLights());
            pst.setString(17, location.getLightColor());
            pst.setInt(18, location.getId_location());

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

    public boolean isLocationAvailable(int locationId, LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        String query = "SELECT COUNT(*) FROM booking WHERE location_id = ? AND " +
                      "((start_date BETWEEN ? AND ?) OR " +
                      "(end_date BETWEEN ? AND ?) OR " +
                      "(start_date <= ? AND end_date >= ?))";
        
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, locationId);
            ps.setTimestamp(2, Timestamp.valueOf(startDate));
            ps.setTimestamp(3, Timestamp.valueOf(endDate));
            ps.setTimestamp(4, Timestamp.valueOf(startDate));
            ps.setTimestamp(5, Timestamp.valueOf(endDate));
            ps.setTimestamp(6, Timestamp.valueOf(startDate));
            ps.setTimestamp(7, Timestamp.valueOf(endDate));
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0; // Return true if no overlapping bookings found
            }
        }
        return false;
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
            rs.getString("image_filename"),
            rs.getBoolean("has_3d_tour"),
            rs.getInt("table_set_count"),
            rs.getBoolean("include_corner_plants"),
            rs.getString("window_style"),
            rs.getString("door_style"),
            rs.getBoolean("include_ceiling_lights"),
            rs.getString("light_color")
        );
    }

    public void refreshConnection() {
        this.conn = DataSource.getInstance().getConnection();
    }
} 