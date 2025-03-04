package services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProfanityCheckerService {

    private static final String PURGOMALUM_API_URL = "https://www.purgomalum.com/service/containsprofanity?text=";

    public static boolean containsProfanity(String text) {
        try {
            String apiUrl = PURGOMALUM_API_URL + java.net.URLEncoder.encode(text, "UTF-8");
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();
            connection.disconnect();

            return Boolean.parseBoolean(content.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
