package Models;

import java.time.LocalDateTime;

public class Notification {
    private int id;
    private String title;
    private String message;
    private NotificationType type;
    private LocalDateTime timestamp;
    private boolean isRead;
    private int userId;

    public enum NotificationType {
        BOOKING_CREATED,
        BOOKING_UPDATED,
        BOOKING_CANCELLED,
        EVENT_CREATED,
        EVENT_UPDATED,
        EVENT_CANCELLED,
        LOCATION_AVAILABLE,
        SYSTEM
    }

    public Notification() {
        this.timestamp = LocalDateTime.now();
        this.isRead = false;
    }

    public Notification(String title, String message, NotificationType type, int userId) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.userId = userId;
        this.timestamp = LocalDateTime.now();
        this.isRead = false;
    }

    public Notification(int id, String title, String message, NotificationType type, 
                       LocalDateTime timestamp, boolean isRead, int userId) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.type = type;
        this.timestamp = timestamp;
        this.isRead = isRead;
        this.userId = userId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", type=" + type +
                ", timestamp=" + timestamp +
                ", isRead=" + isRead +
                ", userId=" + userId +
                '}';
    }
} 