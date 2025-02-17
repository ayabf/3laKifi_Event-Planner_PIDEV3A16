// DataSource.java (Database Connection Utility)
package utils;
import java.sql.*;

public class DataSource {
    private final String URL = "jdbc:mysql://localhost:3306/hackpack";
    private final String USER = "root";
    private final String PASS = "";
    private Connection connection;
    private static DataSource instance;

    private DataSource() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("Connected to database");
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
        }
    }

    public static DataSource getInstance() {
        if (instance == null) {
            instance = new DataSource();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
