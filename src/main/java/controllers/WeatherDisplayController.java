package controllers;

import Models.WeatherInfo;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import services.WeatherService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class WeatherDisplayController {
    @FXML private VBox weatherContainer;
    @FXML private Label errorLabel;
    
    private final WeatherService weatherService = new WeatherService();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy");

    public void updateWeather(String city, LocalDateTime startDate, LocalDateTime endDate) {
        weatherContainer.getChildren().clear();
        errorLabel.setVisible(false);
        
        try {
            List<WeatherInfo> weatherInfoList = weatherService.getWeatherForecast(city, startDate, endDate);
            
            if (weatherInfoList.isEmpty()) {
                showError("No weather data available for the specified dates");
                return;
            }
            
            for (WeatherInfo info : weatherInfoList) {
                HBox weatherRow = createWeatherRow(info);
                weatherContainer.getChildren().add(weatherRow);
            }
        } catch (Exception e) {
            showError("Error fetching weather data: " + e.getMessage());
        }
    }
    
    private HBox createWeatherRow(WeatherInfo info) {
        HBox row = new HBox(15);
        row.getStyleClass().add("weather-row");
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label dateLabel = new Label(info.getDateTime().format(formatter));
        dateLabel.getStyleClass().add("weather-date");

        ImageView weatherIcon = new ImageView(new Image(info.getIconCode(), true));
        weatherIcon.setFitHeight(40);
        weatherIcon.setFitWidth(40);

        VBox weatherDetails = new VBox(5);
        weatherDetails.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label tempLabel = new Label(String.format("%.1f°C", info.getTemperature()));
        tempLabel.getStyleClass().add("weather-temp");
        
        Label descLabel = new Label(info.getDescription());
        descLabel.getStyleClass().add("weather-desc");
        
        weatherDetails.getChildren().addAll(tempLabel, descLabel);

        VBox additionalInfo = new VBox(5);
        additionalInfo.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label feelsLikeLabel = new Label(String.format("Feels like: %.1f°C", info.getFeelsLike()));
        Label humidityLabel = new Label(String.format("Humidity: %d%%", info.getHumidity()));
        
        feelsLikeLabel.getStyleClass().add("weather-additional");
        humidityLabel.getStyleClass().add("weather-additional");
        
        additionalInfo.getChildren().addAll(feelsLikeLabel, humidityLabel);

        row.getChildren().addAll(dateLabel, weatherIcon, weatherDetails, additionalInfo);
        
        return row;
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
} 