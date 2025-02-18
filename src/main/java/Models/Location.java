package Models;

public class Location {
    private int id_location;
    private String name;
    private City ville;
    private byte[] imageData;
    private String imageFileName;
    private int capacity;
    private String dimension;
    private double price;

    public Location() {}

    public Location(int id_location, String name, City ville, byte[] imageData, 
                   String imageFileName, int capacity, String dimension, double price) {
        this.id_location = id_location;
        this.name = name;
        this.ville = ville;
        this.imageData = imageData;
        this.imageFileName = imageFileName;
        this.capacity = capacity;
        this.dimension = dimension;
        this.price = price;
    }

    public Location(String name, City ville, byte[] imageData, 
                   String imageFileName, int capacity, String dimension, double price) {
        this.name = name;
        this.ville = ville;
        this.imageData = imageData;
        this.imageFileName = imageFileName;
        this.capacity = capacity;
        this.dimension = dimension;
        this.price = price;
    }

    public int getId_location() {
        return id_location;
    }

    public void setId_location(int id_location) {
        this.id_location = id_location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public City getVille() {
        return ville;
    }

    public void setVille(City ville) {
        this.ville = ville;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Location{" +
                "id_location=" + id_location +
                ", name='" + name + '\'' +
                ", ville=" + ville +
                ", imageFileName='" + imageFileName + '\'' +
                ", capacity=" + capacity +
                ", dimension='" + dimension + '\'' +
                ", price=" + price +
                '}';
    }
} 