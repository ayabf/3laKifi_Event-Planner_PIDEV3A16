package Models;

import java.time.LocalDateTime;

public class WeatherInfo {
    private LocalDateTime dateTime;
    private double temperature;
    private double feelsLike;
    private int humidity;
    private String description;
    private String iconCode;

    public WeatherInfo(LocalDateTime dateTime, double temperature, double feelsLike, 
                      int humidity, String description, String iconCode) {
        this.dateTime = dateTime;
        this.temperature = temperature;
        this.feelsLike = feelsLike;
        this.humidity = humidity;
        this.description = description;
        this.iconCode = iconCode;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getFeelsLike() {
        return feelsLike;
    }

    public int getHumidity() {
        return humidity;
    }

    public String getDescription() {
        return description;
    }

    public String getIconCode() {
        return iconCode;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public void setFeelsLike(double feelsLike) {
        this.feelsLike = feelsLike;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIconCode(String iconCode) {
        this.iconCode = iconCode;
    }

    @Override
    public String toString() {
        return String.format("Weather on %s: %.1f°C (Feels like: %.1f°C), Humidity: %d%%, %s",
            dateTime.toString(), temperature, feelsLike, humidity, description);
    }
} 