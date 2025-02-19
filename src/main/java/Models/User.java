package Models;

public class User {
    private int id_user;
    private String username;
    private String address;
    private String password;
    private String role;

    public User() {}

    public User(int id_user, String username, String address, String password, String role) {
        this.id_user = id_user;
        this.username = username;
        this.address = address;
        this.password = password;
        this.role = role;
    }

    public User(String username, String address, String password, String role) {
        this.username = username;
        this.address = address;
        this.password = password;
        this.role = role;
    }

    // Getters
    public int getId_user() { return id_user; }
    public String getUsername() { return username; }
    public String getAddress() { return address; }
    public String getPassword() { return password; }
    public String getRole() { return role; }

    // Setters
    public void setId_user(int id_user) { this.id_user = id_user; }
    public void setUsername(String username) { this.username = username; }
    public void setAddress(String address) { this.address = address; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }

    @Override
    public String toString() {
        return "User{" +
                "id_user=" + id_user +
                ", username='" + username + '\'' +
                ", address='" + address + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
} 