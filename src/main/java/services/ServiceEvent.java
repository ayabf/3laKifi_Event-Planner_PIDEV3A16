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
        String req = "INSERT INTO event (name, description, image_data, start_date, end_date, capacity, city, id_user) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        pste = conn.prepareStatement(req);
        pste.setString(1, event.getName());
        pste.setString(2, event.getDescription());
        pste.setBytes(3, event.getImageData());
        pste.setTimestamp(4, Timestamp.valueOf(event.getStart_date()));
        pste.setTimestamp(5, Timestamp.valueOf(event.getEnd_date()));
        pste.setInt(6, event.getCapacity());
        pste.setString(7, event.getCity().name());
        pste.setInt(8, event.getId_user());
        pste.executeUpdate();
        System.out.println("✅ Event added successfully!");
    }

    //  Modifier un événement
    @Override
    public void modifier(Event event) throws SQLException {
        String req = "UPDATE event SET name=?, description=?, image_data=?, start_date=?, end_date=?, capacity=?, city=? " +
                    "WHERE id_event=?";
        pste = conn.prepareStatement(req);
        pste.setString(1, event.getName());
        pste.setString(2, event.getDescription());
        pste.setBytes(3, event.getImageData());
        pste.setTimestamp(4, Timestamp.valueOf(event.getStart_date()));
        pste.setTimestamp(5, Timestamp.valueOf(event.getEnd_date()));
        pste.setInt(6, event.getCapacity());
        pste.setString(7, event.getCity().name());
        pste.setInt(8, event.getId_event());
        pste.executeUpdate();
        System.out.println("✅ Event updated successfully!");
    }

    // Supprimer un événement
    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM event WHERE id_event=?";
        pste = conn.prepareStatement(req);
        pste.setInt(1, id);
        pste.executeUpdate();
        System.out.println("✅ Event deleted successfully!");
    }

    // Récupérer un événement par son objet
    @Override
    public Event getOne(Event event) throws SQLException {
        return getById(event.getId_event());
    }

    // Helper method to get event by ID
    public Event getById(int id) throws SQLException {
        String req = "SELECT * FROM event WHERE id_event=?";
        pste = conn.prepareStatement(req);
        pste.setInt(1, id);
        ResultSet rs = pste.executeQuery();
        if (rs.next()) {
            return extractEventFromResultSet(rs);
        }
        return null;
    }

    private Event extractEventFromResultSet(ResultSet rs) throws SQLException {
        Event event = new Event();
        event.setId_event(rs.getInt("id_event"));
        event.setName(rs.getString("name"));
        event.setDescription(rs.getString("description"));
        event.setImageData(rs.getBytes("image_data"));
        event.setStart_date(rs.getTimestamp("start_date").toLocalDateTime());
        event.setEnd_date(rs.getTimestamp("end_date").toLocalDateTime());
        event.setCapacity(rs.getInt("capacity"));
        event.setCity(normalizeAndParseCity(rs.getString("city")));
        return event;
    }

    // Récupérer tous les événements
    @Override
    public List<Event> getAll() throws SQLException {
        List<Event> list = new ArrayList<>();
        String req = "SELECT * FROM event";
        ste = conn.createStatement();
        ResultSet rs = ste.executeQuery(req);
        while (rs.next()) {
            list.add(extractEventFromResultSet(rs));
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
            list.add(extractEventFromResultSet(rs));
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
            list.add(extractEventFromResultSet(rs));
        }
        return list;
    }
}
