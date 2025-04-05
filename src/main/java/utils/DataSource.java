package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSource {
    private static DataSource instance;
    private Connection conn;
    private String url = "jdbc:mysql://localhost:3306/hackpack6";
    private String username = "root";
    private String password = "";

    private DataSource() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, username, password);
            System.out.println("✅ Connected to database successfully!");
        } catch (ClassNotFoundException e) {
            System.out.println("❌ MySQL JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("❌ Error connecting to database: " + e.getMessage());
        }
    }

    public static DataSource getInstance() {
        if (instance == null) {
            instance = new DataSource();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(url, username, password);
                System.out.println("✅ Database connection reestablished!");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error getting database connection: " + e.getMessage());
        }
        return conn;
    }

    public void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("✅ Database connection closed successfully!");
            } catch (SQLException e) {
                System.out.println("❌ Error closing database connection: " + e.getMessage());
            }
        }
    }
}

