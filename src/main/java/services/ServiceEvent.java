package services;

import Models.City;
import Models.Event;
import utils.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceEvent implements IService<Event> {

    private Connection conn;
    private Statement ste;
    private PreparedStatement pste;

    public ServiceEvent() {
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

    // ✅ Ajouter un événement
    @Override
    public void ajouter(Event event) throws SQLException {
        String req = "INSERT INTO event (name, description, image_data, image_filename, start_date, end_date, capacity, city, id_user) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        pste = conn.prepareStatement(req);
        pste.setString(1, event.getName());
        pste.setString(2, event.getDescription());
        pste.setBytes(3, event.getImageData());
        pste.setString(4, event.getImageFileName());
        pste.setTimestamp(5, Timestamp.valueOf(event.getStart_date()));
        pste.setTimestamp(6, Timestamp.valueOf(event.getEnd_date()));
        pste.setInt(7, event.getCapacity());
        pste.setString(8, event.getCity().name());
        pste.setInt(9, event.getId_user());
        pste.executeUpdate();
        System.out.println("✅ Événement ajouté avec succès !");
    }

    //  Modifier un événement
    @Override
    public void modifier(Event event) throws SQLException {
        String req = "UPDATE event SET name=?, description=?, image_data=?, image_filename=?, start_date=?, end_date=?, capacity=?, city=?, id_user=? " +
                    "WHERE id_event=?";
        pste = conn.prepareStatement(req);
        pste.setString(1, event.getName());
        pste.setString(2, event.getDescription());
        pste.setBytes(3, event.getImageData());
        pste.setString(4, event.getImageFileName());
        pste.setTimestamp(5, Timestamp.valueOf(event.getStart_date()));
        pste.setTimestamp(6, Timestamp.valueOf(event.getEnd_date()));
        pste.setInt(7, event.getCapacity());
        pste.setString(8, event.getCity().name());
        pste.setInt(9, event.getId_user());
        pste.setInt(10, event.getId_event());
        pste.executeUpdate();
        System.out.println("✅ Événement modifié avec succès !");
    }

    // Supprimer un événement
    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM event WHERE id_event=?";
        pste = conn.prepareStatement(req);
        pste.setInt(1, id);
        pste.executeUpdate();
        System.out.println("✅ Événement supprimé avec succès !");
    }

    // Récupérer un événement par son ID
    @Override
    public Event getOne(Event event) throws SQLException {
        String req = "SELECT * FROM event WHERE id_event=?";
        pste = conn.prepareStatement(req);
        pste.setInt(1, event.getId_event());
        ResultSet rs = pste.executeQuery();
        if (rs.next()) {
            return new Event(
                rs.getInt("id_event"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getBytes("image_data"),
                rs.getString("image_filename"),
                rs.getTimestamp("start_date").toLocalDateTime(),
                rs.getTimestamp("end_date").toLocalDateTime(),
                rs.getInt("capacity"),
                normalizeAndParseCity(rs.getString("city")),
                rs.getInt("id_user")
            );
        }
        return null;
    }

    // Récupérer tous les événements
    @Override
    public List<Event> getAll() throws SQLException {
        List<Event> list = new ArrayList<>();
        String req = "SELECT * FROM event";
        ste = conn.createStatement();
        ResultSet rs = ste.executeQuery(req);
        while (rs.next()) {
            Event event = new Event(
                rs.getInt("id_event"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getBytes("image_data"),
                rs.getString("image_filename"),
                rs.getTimestamp("start_date").toLocalDateTime(),
                rs.getTimestamp("end_date").toLocalDateTime(),
                rs.getInt("capacity"),
                normalizeAndParseCity(rs.getString("city")),
                rs.getInt("id_user")
            );
            list.add(event);
        }
        return list;
    }

    public int getTotalCount() throws SQLException {
        String req = "SELECT COUNT(*) FROM event";
        ste = conn.createStatement();
        ResultSet rs = ste.executeQuery(req);
        if (rs.next()) {
            return rs.getInt(1);
        }
        return 0;
    }

    public List<Event> searchEvents(String query) throws SQLException {
        List<Event> list = new ArrayList<>();
        String req = "SELECT * FROM event WHERE name LIKE ? OR description LIKE ? OR city LIKE ?";
        pste = conn.prepareStatement(req);
        String searchPattern = "%" + query.toLowerCase() + "%";
        pste.setString(1, searchPattern);
        pste.setString(2, searchPattern);
        pste.setString(3, searchPattern);
        ResultSet rs = pste.executeQuery();
        while (rs.next()) {
            Event event = new Event(
                rs.getInt("id_event"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getBytes("image_data"),
                rs.getString("image_filename"),
                rs.getTimestamp("start_date").toLocalDateTime(),
                rs.getTimestamp("end_date").toLocalDateTime(),
                rs.getInt("capacity"),
                normalizeAndParseCity(rs.getString("city")),
                rs.getInt("id_user")
            );
            list.add(event);
        }
        return list;
    }

    public List<Event> getEventsByUser(int userId) throws SQLException {
        List<Event> list = new ArrayList<>();
        String req = "SELECT * FROM event WHERE id_user = ?";
        pste = conn.prepareStatement(req);
        pste.setInt(1, userId);
        ResultSet rs = pste.executeQuery();
        while (rs.next()) {
            Event event = new Event(
                rs.getInt("id_event"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getBytes("image_data"),
                rs.getString("image_filename"),
                rs.getTimestamp("start_date").toLocalDateTime(),
                rs.getTimestamp("end_date").toLocalDateTime(),
                rs.getInt("capacity"),
                normalizeAndParseCity(rs.getString("city")),
                rs.getInt("id_user")
            );
            list.add(event);
        }
        return list;
    }
}
