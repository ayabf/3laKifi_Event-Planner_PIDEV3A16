package Models;

public class User {
    private int id_user;
    private int numTel;


    public int getNumTel() {
        return numTel;
    }

    public void setNumTel(int numTel) {
        this.numTel = numTel;
    }

    private String lastName;
    private String firstName;
    private String username;
    private String password;
    private Role role;

    private String address;
    private String profileImagePath;
    private String email;

    public User(String lastName, String firstName, String username, String password, Role role,
                String biography, String address, String profileImagePath, String email, int num) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.username = username;
        this.password = password;
        this.role = role;

        this.address = address;
        this.profileImagePath = profileImagePath;
        this.email = email;
        this.numTel = num;


    }

    public User(int s, String lastName, String firstName, String username, String password, Role role,
                String biography, String address, String profileImagePath, String email, int num) {
        this.id_user = s;
        this.lastName = lastName;
        this.firstName = firstName;
        this.username = username;
        this.password = password;
        this.role = role;

        this.address = address;
        this.profileImagePath = profileImagePath;
        this.email = email;
        this.numTel = num;

    }
    // Constructors, getters, setters, toString, etc.
//(last_name, first_name, email, password, role, address)


    public User(String lastName, String firstName,String username, Role role,String password ,String address, String email,int numTel ) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.password = password;
        this.role = role;
        this.address = address;
        this.email = email;
        this.username = username;
        this.numTel = numTel;
    }


    public User() {
    }

    @Override
    public String toString() {
        return "User{" +
                "id_user=" + id_user +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +

                ", address='" + address + '\'' +
                ", profileImagePath='" + profileImagePath + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return id_user;
    }

    public void setId(int id) {
        this.id_user = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }





    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProfileImagePath() {
        return profileImagePath;
    }

    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }


}