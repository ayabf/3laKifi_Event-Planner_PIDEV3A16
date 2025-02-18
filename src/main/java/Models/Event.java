package Models;

import java.time.LocalDateTime;

public class Event {
    private int id_event;
    private String name;
    private String description;
    private byte[] imageData;
    private String imageFileName;
    private LocalDateTime start_date;
    private LocalDateTime end_date;
    private int capacity;
    private City city;
    private int id_user;


    public Event() {}

    public Event(int id_event, String name, String description, byte[] imageData, String imageFileName,
                 LocalDateTime start_date, LocalDateTime end_date, int capacity,
                 City city, int id_user) {
        this.id_event = id_event;
        this.name = name;
        this.description = description;
        this.imageData = imageData;
        this.imageFileName = imageFileName;
        this.start_date = start_date;
        this.end_date = end_date;
        this.capacity = capacity;
        this.city = city;
        this.id_user = id_user;
    }

    public Event(String name, String description, byte[] imageData, String imageFileName,
                 LocalDateTime start_date, LocalDateTime end_date, int capacity,
                 City city, int id_user) {
        this.name = name;
        this.description = description;
        this.imageData = imageData;
        this.imageFileName = imageFileName;
        this.start_date = start_date;
        this.end_date = end_date;
        this.capacity = capacity;
        this.city = city;
        this.id_user = id_user;
    }

    public int getId_event() {
        return id_event;
    }

    public void setId_event(int id_event) {
        this.id_event = id_event;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public LocalDateTime getStart_date() {
        return start_date;
    }

    public void setStart_date(LocalDateTime start_date) {
        this.start_date = start_date;
    }

    public LocalDateTime getEnd_date() {
        return end_date;
    }

    public void setEnd_date(LocalDateTime end_date) {
        this.end_date = end_date;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id_event=" + id_event +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", imageFileName='" + imageFileName + '\'' +
                ", start_date=" + start_date +
                ", end_date=" + end_date +
                ", capacity=" + capacity +
                ", city=" + city +
                ", id_user=" + id_user +
                '}';
    }
}
