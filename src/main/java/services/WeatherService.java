package services;

import Models.WeatherInfo;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class WeatherService {
    private static final String API_KEY = "a7f8a184ac7b462fabb112550252302";
    private static final String BASE_URL = "http://api.weatherapi.com/v1/forecast.json";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public List<WeatherInfo> getWeatherForecast(String city, LocalDateTime startDate, LocalDateTime endDate) {
        List<WeatherInfo> weatherInfoList = new ArrayList<>();
        HttpURLConnection conn = null;

        try {
            // Calculate the number of days between start and end date (max 7 days for free tier)
            long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate());
            int daysToForecast = (int) Math.min(daysBetween + 1, 7); // Add 1 to include start date, max 7 days

            String urlString = String.format("%s?key=%s&q=%s&days=%d&aqi=no", 
                BASE_URL, API_KEY, city, daysToForecast);
            

            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                System.out.println("❌ Error fetching weather data. Response code: " + responseCode);
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                StringBuilder errorResponse = new StringBuilder();
                String line;
                while ((line = errorReader.readLine()) != null) {
                    errorResponse.append(line);
                }
                errorReader.close();
                System.out.println("Error details: " + errorResponse.toString());
                return weatherInfoList;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();



            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONObject forecast = jsonResponse.getJSONObject("forecast");
            JSONArray forecastDays = forecast.getJSONArray("forecastday");

            for (int i = 0; i < forecastDays.length(); i++) {
                JSONObject dayForecast = forecastDays.getJSONObject(i);
                String dateStr = dayForecast.getString("date");
                LocalDateTime forecastDate = LocalDateTime.parse(dateStr + " 00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                
                JSONObject day = dayForecast.getJSONObject("day");
                JSONObject condition = day.getJSONObject("condition");

                double avgTemp = day.optDouble("avgtemp_c", Double.NaN);
                double feelsLike = day.optDouble("feelslike_c", Double.NaN);
                int humidity = day.optInt("avghumidity", 0);


                WeatherInfo weatherInfo = new WeatherInfo(
                    forecastDate,
                    avgTemp,
                    feelsLike,
                    humidity,
                    condition.optString("text", "No description available"),
                    condition.optString("icon", "").replace("//", "https://")
                );

                // Check for alternative keys if feelslike_c is missing
                if (!condition.has("feelslike_c") && condition.has("feels_like")) {
                    weatherInfo.setFeelsLike(day.optDouble("feels_like", Double.NaN));
                }

                weatherInfoList.add(weatherInfo);
            }


        } catch (Exception e) {
            System.out.println("❌ Error fetching weather data: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return weatherInfoList;
    }
} 