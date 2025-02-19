package Models;

public class Location {
    private int id_location;
    private String name;
    private String address;
    private City ville;
    private int capacity;
    private String status;
    private String description;
    private String dimension;
    private double price;
    private byte[] imageData;
    private String imageFileName;

    public Location() {}

    public Location(int id_location, String name, String address, City ville, int capacity, 
                   String status, String description, String dimension, double price, 
                   byte[] imageData, String imageFileName) {
        this.id_location = id_location;
        this.name = name;
        this.address = address;
        this.ville = ville;
        this.capacity = capacity;
        this.status = status;
        this.description = description;
        this.dimension = dimension;
        this.price = price;
        this.imageData = imageData;
        this.imageFileName = imageFileName;
    }

    // Getters
    public int getId_location() { return id_location; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public City getVille() { return ville; }
    public int getCapacity() { return capacity; }
    public String getStatus() { return status; }
    public String getDescription() { return description; }
    public String getDimension() { return dimension; }
    public double getPrice() { return price; }
    public byte[] getImageData() { return imageData; }
    public String getImageFileName() { return imageFileName; }

    // Setters
    public void setId_location(int id_location) { this.id_location = id_location; }
    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
    public void setVille(City ville) { this.ville = ville; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public void setStatus(String status) { this.status = status; }
    public void setDescription(String description) { this.description = description; }
    public void setDimension(String dimension) { this.dimension = dimension; }
    public void setPrice(double price) { this.price = price; }
    public void setImageData(byte[] imageData) { this.imageData = imageData; }
    public void setImageFileName(String imageFileName) { this.imageFileName = imageFileName; }

    @Override
    public String toString() {
        return "Location{" +
                "id_location=" + id_location +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", ville=" + ville +
                ", capacity=" + capacity +
                ", status='" + status + '\'' +
                ", dimension='" + dimension + '\'' +
                ", price=" + price +
                '}';
    }
} 