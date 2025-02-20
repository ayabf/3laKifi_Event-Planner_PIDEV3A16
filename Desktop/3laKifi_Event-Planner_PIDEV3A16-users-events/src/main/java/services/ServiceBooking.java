package services;

import Models.Booking;
import Models.Event;
import Models.Location;
import utils.DataSource;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServiceBooking implements IService<Booking> {
    private Connection conn;
    private Statement ste;
    private PreparedStatement pste;
    private final ServiceEvent eventService;
    private final ServiceLocation locationService;

    public ServiceBooking() {
        conn = DataSource.getInstance().getConnection();
        eventService = new ServiceEvent();
        locationService = new ServiceLocation();
    }

    public boolean isLocationAvailable(int locationId, LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        String req = "SELECT COUNT(*) FROM booking WHERE id_location = ? AND " +
                    "((start_date BETWEEN ? AND ?) OR " +
                    "(end_date BETWEEN ? AND ?) OR " +
                    "(start_date <= ? AND end_date >= ?))";
        
        pste = conn.prepareStatement(req);
        pste.setInt(1, locationId);
        pste.setTimestamp(2, Timestamp.valueOf(startDate));
        pste.setTimestamp(3, Timestamp.valueOf(endDate));
        pste.setTimestamp(4, Timestamp.valueOf(startDate));
        pste.setTimestamp(5, Timestamp.valueOf(endDate));
        pste.setTimestamp(6, Timestamp.valueOf(startDate));
        pste.setTimestamp(7, Timestamp.valueOf(endDate));
        
        ResultSet rs = pste.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) == 0; // Return true if no overlapping bookings found
        }
        return true;
    }

    @Override
    public void ajouter(Booking booking) throws SQLException {
        String req = "INSERT INTO booking (id_event, id_location, start_date, end_date) " +
                    "VALUES (?, ?, ?, ?)";
        pste = conn.prepareStatement(req);
        pste.setInt(1, booking.getEvent_id());
        pste.setInt(2, booking.getLocation_id());
        pste.setTimestamp(3, Timestamp.valueOf(booking.getStart_date()));
        pste.setTimestamp(4, Timestamp.valueOf(booking.getEnd_date()));
        pste.executeUpdate();
    }

    @Override
    public void modifier(Booking booking) throws SQLException {
        String req = "UPDATE booking SET id_event=?, id_location=?, start_date=?, end_date=? " +
                    "WHERE id_booking=?";
        pste = conn.prepareStatement(req);
        pste.setInt(1, booking.getEvent_id());
        pste.setInt(2, booking.getLocation_id());
        pste.setTimestamp(3, Timestamp.valueOf(booking.getStart_date()));
        pste.setTimestamp(4, Timestamp.valueOf(booking.getEnd_date()));
        pste.setInt(5, booking.getBooking_id());
        pste.executeUpdate();
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM booking WHERE id_booking=?";
        pste = conn.prepareStatement(req);
        pste.setInt(1, id);
        pste.executeUpdate();
    }

    @Override
    public Booking getOne(Booking booking) throws SQLException {
        return getById(booking.getBooking_id());
    }

    public Booking getById(int id) throws SQLException {
        String req = "SELECT * FROM booking WHERE id_booking=?";
        pste = conn.prepareStatement(req);
        pste.setInt(1, id);
        ResultSet rs = pste.executeQuery();
        if (rs.next()) {
            Event event = eventService.getById(rs.getInt("id_event"));
            Location location = locationService.getById(rs.getInt("id_location"));
            return new Booking(
                rs.getInt("id_booking"),
                event,
                location,
                rs.getTimestamp("start_date").toLocalDateTime(),
                rs.getTimestamp("end_date").toLocalDateTime()
            );
        }
        return null;
    }

    @Override
    public List<Booking> getAll() throws SQLException {
        List<Booking> list = new ArrayList<>();
        String req = "SELECT * FROM booking";
        ste = conn.createStatement();
        ResultSet rs = ste.executeQuery(req);
        while (rs.next()) {
            Event event = eventService.getById(rs.getInt("id_event"));
            Location location = locationService.getById(rs.getInt("id_location"));
            Booking booking = new Booking(
                rs.getInt("id_booking"),
                event,
                location,
                rs.getTimestamp("start_date").toLocalDateTime(),
                rs.getTimestamp("end_date").toLocalDateTime()
            );
            list.add(booking);
        }
        return list;
    }

    @Override
    public List<Booking> getAll1(Booking booking) throws SQLException {
        return List.of();
    }

    public int getTotalCount() throws SQLException {
        String req = "SELECT COUNT(*) FROM booking";
        ste = conn.createStatement();
        ResultSet rs = ste.executeQuery(req);
        if (rs.next()) {
            return rs.getInt(1);
        }
        return 0;
    }

    public List<Booking> getBookingsByUser(int userId) throws SQLException {
        List<Booking> list = new ArrayList<>();
        String req = "SELECT b.* FROM booking b " +
                    "JOIN event e ON b.id_event = e.id_event " +
                    "WHERE e.id_user = ?";
        pste = conn.prepareStatement(req);
        pste.setInt(1, userId);
        ResultSet rs = pste.executeQuery();
        while (rs.next()) {
            Event event = eventService.getById(rs.getInt("id_event"));
            Location location = locationService.getById(rs.getInt("id_location"));
            Booking booking = new Booking(
                rs.getInt("id_booking"),
                event,
                location,
                rs.getTimestamp("start_date").toLocalDateTime(),
                rs.getTimestamp("end_date").toLocalDateTime()
            );
            list.add(booking);
        }
        return list;
    }
} 