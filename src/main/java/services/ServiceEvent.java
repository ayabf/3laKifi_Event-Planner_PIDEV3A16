package services;

import Models.City;
import Models.Event;
import utils.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceEvent implements IService<Event> {

    private Connection con;
    private PreparedStatement pst;

    public ServiceEvent() {
        con = DataSource.getInstance().getConnection(); // Connexion à la base
    }

    // ✅ Ajouter un événement
    @Override
    public void ajouter(Event e) throws SQLException {
        String query = "INSERT INTO event (name, description, imagepath, start_date, end_date, capacity, city, id_user) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        pst = con.prepareStatement(query);
        pst.setString(1, e.getName());
        pst.setString(2, e.getDescription());
        pst.setString(3, e.getImagepath());
        pst.setTimestamp(4, Timestamp.valueOf(e.getStart_date()));
        pst.setTimestamp(5, Timestamp.valueOf(e.getEnd_date()));
        pst.setInt(6, e.getCapacity());
        pst.setString(7, e.getCity().name()); // Stocke le ENUM comme String
        pst.setInt(8, e.getId_user());

        pst.executeUpdate();
        System.out.println("✅ Événement ajouté avec succès !");
    }

    //  Modifier un événement
    @Override
    public void modifier(Event e) throws SQLException {
        String query = "UPDATE event SET name=?, description=?, imagepath=?, start_date=?, end_date=?, capacity=?, city=?, id_user=? WHERE id_event=?";
        pst = con.prepareStatement(query);
        pst.setString(1, e.getName());
        pst.setString(2, e.getDescription());
        pst.setString(3, e.getImagepath());
        pst.setTimestamp(4, Timestamp.valueOf(e.getStart_date()));
        pst.setTimestamp(5, Timestamp.valueOf(e.getEnd_date()));
        pst.setInt(6, e.getCapacity());
        pst.setString(7, e.getCity().name());
        pst.setInt(8, e.getId_user());
        pst.setInt(9, e.getId_event());

        pst.executeUpdate();
        System.out.println("✅ Événement modifié avec succès !");
    }

    // Supprimer un événement
    @Override
    public void supprimer(int id) throws SQLException {
        String query = "DELETE FROM event WHERE id_event=?";
        pst = con.prepareStatement(query);
        pst.setInt(1, id);

        pst.executeUpdate();
        System.out.println("✅ Événement supprimé avec succès !");
    }

    // Récupérer un événement par son ID
    @Override
    public Event getOne(Event e) throws SQLException {
        String query = "SELECT * FROM event WHERE id_event=?";
        pst = con.prepareStatement(query);
        pst.setInt(1, e.getId_event());

        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            return new Event(
                    rs.getInt("id_event"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getString("imagepath"),
                    rs.getTimestamp("start_date").toLocalDateTime(),
                    rs.getTimestamp("end_date").toLocalDateTime(),
                    rs.getInt("capacity"),
                    City.valueOf(rs.getString("city")),
                    rs.getInt("id_user")
            );
        }
        return null;
    }

    // Récupérer tous les événements
    @Override
    public List<Event> getAll() throws SQLException {
        List<Event> list = new ArrayList<>();
        String query = "SELECT * FROM event";
        pst = con.prepareStatement(query);
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            list.add(new Event(
                    rs.getInt("id_event"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getString("imagepath"),
                    rs.getTimestamp("start_date").toLocalDateTime(),
                    rs.getTimestamp("end_date").toLocalDateTime(),
                    rs.getInt("capacity"),
                    City.valueOf(rs.getString("city").toUpperCase()),
                    rs.getInt("id_user")
            ));
        }
        return list;
    }
}
