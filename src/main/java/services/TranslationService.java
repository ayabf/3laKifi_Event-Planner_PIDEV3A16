package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class TranslationService {

    private static final String API_URL = "https://api.mymemory.translated.net/get";

    // ✅ Fonction qui détecte la langue source via MyMemory
    private static String detectLanguage(String text) {
        try {
            String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);
            String urlString = String.format("%s?q=%s&langpair=en|fr", API_URL, encodedText); // Traduction test

            HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            Scanner scanner = new Scanner(connection.getInputStream());
            StringBuilder jsonResponse = new StringBuilder();
            while (scanner.hasNext()) {
                jsonResponse.append(scanner.nextLine());
            }
            scanner.close();

            // ✅ Analyser la réponse JSON pour obtenir la langue détectée
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode responseNode = objectMapper.readTree(jsonResponse.toString());

            if (responseNode.has("responseDetails")) {
                String details = responseNode.get("responseDetails").asText();
                if (details.contains("detected")) {
                    return details.split("detected")[1].trim().substring(0, 2); // Extrait le code de langue détecté
                }
            }

        } catch (IOException e) {
            System.err.println("❌ Erreur lors de la détection de langue : " + e.getMessage());
        }

        return "en"; // Si échec, suppose anglais
    }

    // ✅ Fonction qui traduit le texte
    public static String translateText(String text, String targetLang) {
        try {
            String sourceLang = detectLanguage(text); // Détecte la langue avant la traduction
            String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);
            String urlString = String.format("%s?q=%s&langpair=%s|%s", API_URL, encodedText, sourceLang, targetLang);

            HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            Scanner scanner = new Scanner(connection.getInputStream());
            StringBuilder jsonResponse = new StringBuilder();
            while (scanner.hasNext()) {
                jsonResponse.append(scanner.nextLine());
            }
            scanner.close();

            // ✅ Analyser la réponse JSON pour obtenir la traduction
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode responseNode = objectMapper.readTree(jsonResponse.toString());

            if (responseNode.has("responseData") && responseNode.get("responseData").has("translatedText")) {
                return responseNode.get("responseData").get("translatedText").asText();
            } else {
                return "⚠ Erreur : Traduction non trouvée.";
            }

        } catch (IOException e) {
            return "❌ Erreur API : " + e.getMessage();
        }
    }
}
