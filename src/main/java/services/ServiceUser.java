package services;

import Models.User;
import utils.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceUser implements IService<User> {
    private Connection conn;
    private Statement ste;
    private PreparedStatement pste;

    public ServiceUser() {
        conn = DataSource.getInstance().getConnection();
    }

    @Override
    public void ajouter(User user) throws SQLException {
        String req = "INSERT INTO user (username, address, password, role) VALUES (?, ?, ?, ?)";
        pste = conn.prepareStatement(req);
        pste.setString(1, user.getUsername());
        pste.setString(2, user.getAddress());
        pste.setString(3, user.getPassword());
        pste.setString(4, user.getRole());
        pste.executeUpdate();
    }

    @Override
    public void modifier(User user) throws SQLException {
        String req = "UPDATE user SET username=?, address=?, password=?, role=? WHERE id_user=?";
        pste = conn.prepareStatement(req);
        pste.setString(1, user.getUsername());
        pste.setString(2, user.getAddress());
        pste.setString(3, user.getPassword());
        pste.setString(4, user.getRole());
        pste.setInt(5, user.getId_user());
        pste.executeUpdate();
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String req = "DELETE FROM user WHERE id_user=?";
        pste = conn.prepareStatement(req);
        pste.setInt(1, id);
        pste.executeUpdate();
    }

    @Override
    public User getOne(User user) throws SQLException {
        return getById(user.getId_user());
    }

    public User getById(int id) throws SQLException {
        String req = "SELECT * FROM user WHERE id_user=?";
        pste = conn.prepareStatement(req);
        pste.setInt(1, id);
        ResultSet rs = pste.executeQuery();
        if (rs.next()) {
            return new User(
                rs.getInt("id_user"),
                rs.getString("username"),
                rs.getString("address"),
                rs.getString("password"),
                rs.getString("role")
            );
        }
        return null;
    }

    @Override
    public List<User> getAll() throws SQLException {
        List<User> list = new ArrayList<>();
        String req = "SELECT * FROM user";
        ste = conn.createStatement();
        ResultSet rs = ste.executeQuery(req);
        while (rs.next()) {
            User user = new User(
                rs.getInt("id_user"),
                rs.getString("username"),
                rs.getString("address"),
                rs.getString("password"),
                rs.getString("role")
            );
            list.add(user);
        }
        return list;
    }
} 