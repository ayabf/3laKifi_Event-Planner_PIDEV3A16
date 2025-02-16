package Models;

public class User {
    private int id_user;
    private String numTel;
    private boolean blocked;
    private String blockEndDate;
    private String lastName;
    private String firstName;
    private String username;
    private String password;
    private String role;
    private String address;
    private String profileImagePath;

    // Constructeur
    public User(int id_user, String numTel, boolean blocked, String blockEndDate, String lastName,
                String firstName, String username, String password, String role,
                String address, String profileImagePath) {
        this.id_user = id_user;
        this.numTel = numTel;
        this.blocked = blocked;
        this.blockEndDate = blockEndDate;
        this.lastName = lastName;
        this.firstName = firstName;
        this.username = username;
        this.password = password;
        this.role = role;
        this.address = address;
        this.profileImagePath = profileImagePath;
    }

    // Getters et Setters
    public int getUserId() { return id_user; }
    public void setUserId(int userId) { this.id_user = userId; }

    public String getNumTel() { return numTel; }
    public void setNumTel(String numTel) { this.numTel = numTel; }

    public boolean isBlocked() { return blocked; }
    public void setBlocked(boolean blocked) { this.blocked = blocked; }

    public String getBlockEndDate() { return blockEndDate; }
    public void setBlockEndDate(String blockEndDate) { this.blockEndDate = blockEndDate; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getProfileImagePath() { return profileImagePath; }
    public void setProfileImagePath(String profileImagePath) { this.profileImagePath = profileImagePath; }

    // Méthode d'affichage
    @Override
    public String toString() {
        return "User{" +
                "userId=" + id_user +
                ", numTel='" + numTel + '\'' +
                ", blocked=" + blocked +
                ", blockEndDate='" + blockEndDate + '\'' +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", address='" + address + '\'' +
                ", profileImagePath='" + profileImagePath + '\'' +
                '}';
    }
}
