package services;

import Models.Location;
import Models.City;
import utils.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LocationService {
    private Connection conn;

    public LocationService() {
        this.conn = DataSource.getInstance().getConnection();
    }

    public boolean add(Location location) {
        String query = "INSERT INTO location (name, address, city, capacity, status, description, dimension, price, image_data, image_filename, has_3d_tour) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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

            int affectedRows = pst.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    location.setId_location(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.out.println("❌ Error adding location: " + e.getMessage());
        }
        return false;
    }

    public boolean update(Location location) {
        String query = "UPDATE location SET name=?, address=?, city=?, capacity=?, " +
                      "status=?, description=?, dimension=?, price=?, image_data=?, image_filename=?, has_3d_tour=? " +
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
            pst.setInt(12, location.getId_location());

            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Error updating location: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(int id) {
        String query = "DELETE FROM location WHERE id_location=?";
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Error deleting location: " + e.getMessage());
        }
        return false;
    }

    public Location getById(int id) {
        String query = "SELECT * FROM location WHERE id_location=?";
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return extractLocationFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error getting location: " + e.getMessage());
        }
        return null;
    }

    public List<Location> getAll() {
        List<Location> locations = new ArrayList<>();
        String query = "SELECT * FROM location ORDER BY name";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                locations.add(extractLocationFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error getting locations: " + e.getMessage());
        }
        return locations;
    }

    public int getTotalCount() {
        String query = "SELECT COUNT(*) FROM location";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error getting total count: " + e.getMessage());
        }
        return 0;
    }

    public int getActiveCount() {
        String query = "SELECT COUNT(*) FROM location WHERE status='Active'";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error getting active count: " + e.getMessage());
        }
        return 0;
    }

    private Location extractLocationFromResultSet(ResultSet rs) throws SQLException {
        Location location = new Location();
        location.setId_location(rs.getInt("id_location"));
        location.setName(rs.getString("name"));
        location.setAddress(rs.getString("address"));
        location.setVille(City.valueOf(rs.getString("city")));
        location.setCapacity(rs.getInt("capacity"));
        location.setStatus(rs.getString("status"));
        location.setDescription(rs.getString("description"));
        location.setDimension(rs.getString("dimension"));
        location.setPrice(rs.getDouble("price"));
        location.setImageData(rs.getBytes("image_data"));
        location.setImageFileName(rs.getString("image_filename"));
        location.setHas3DTour(rs.getBoolean("has_3d_tour"));
        return location;
    }

    public void refreshConnection() {
        this.conn = DataSource.getInstance().getConnection();
    }

    public void reserveLocation(Location location) {
        String query = "UPDATE location SET status = 'Reserved' WHERE id_location = ?";
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, location.getId_location());
            pst.executeUpdate();
        } catch (SQLException e) {
            System.out.println("❌ Error reserving location: " + e.getMessage());
        }
    }
} 