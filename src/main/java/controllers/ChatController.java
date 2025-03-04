package controllers;

import Models.User;
import Models.session;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import services.UserService;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ChatController {
    @FXML
    private VBox chatList;
    @FXML
    private TextField messageField;
    @FXML
    private ListView<String> userList;
    @FXML
    private Label dateLabel;

    private final ObservableList<String> users = FXCollections.observableArrayList();
    private final Map<String, String> userAvatars = new HashMap<>();
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String clientName;
    private final UserService userService = new UserService();

    private static final String HISTORY_FILE = "chat_history.txt";

    public void initialize() {
        userList.setItems(users);
        loadUserAvatars();

        // ✅ Récupérer l'utilisateur connecté
        User currentUser = userService.getById(session.id_utilisateur);
        clientName = (currentUser != null) ? currentUser.getUsername() : "Inconnu";

        // ✅ Afficher la date
        dateLabel.setText(getFormattedTimestamp());

        loadChatHistory();
        connectToServer();
    }

    private void loadUserAvatars() {
        userAvatars.put("Admin_Homme", "/images/admin2.PNG");
        userAvatars.put("Admin_Femme", "/images/adminFemme.PNG");
        userAvatars.put("ADMIN", "/images/admin3.png");
        userAvatars.put("Default", "/images/default.png");
    }

    private void loadChatHistory() {
        File file = new File(HISTORY_FILE);
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    final String chatLine = line; // ✅ Création d'une copie locale finale
                    Platform.runLater(() -> processMessage(chatLine));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void connectToServer() {
        try {
            socket = new Socket("127.0.0.1", 5000);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println(clientName);


                new Thread(() -> {
                    try {
                        String msg;
                        while ((msg = in.readLine()) != null) {
                            final String finalMsg = msg; // ✅ Rend la variable "effectivement finale"
                            Platform.runLater(() -> processMessage(finalMsg));
                        }
                    } catch (IOException ignored) {
                    }
                }).start();

            } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processMessage(String msg) {
        if (msg.startsWith("@users ")) {
            users.setAll(Arrays.asList(msg.substring(7).split(",")));
        } else {
            int firstColon = msg.indexOf(":");
            if (firstColon != -1) {
                final String sender = msg.substring(0, firstColon).trim();
                final String message = msg.substring(firstColon + 1).trim();

                Platform.runLater(() -> {
                    String avatarPath = userAvatars.getOrDefault(sender, "/images/default.png");
                    ImageView avatarView = new ImageView(new Image(getClass().getResourceAsStream(avatarPath)));
                    avatarView.setFitWidth(30);
                    avatarView.setFitHeight(30);

                    Text messageText = new Text(sender + " : " + message);
                    TextFlow textFlow = new TextFlow(messageText);

                    HBox messageBox = new HBox(10, avatarView, textFlow);
                    chatList.getChildren().add(messageBox);
                });
            }
        }
    }

    @FXML
    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            out.println(message);
            processMessage(clientName + ": " + message);
            messageField.clear();
        }
    }

    @FXML
    public void backToSidebar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sidebarAdmin.fxml"));
            Parent sidebarView = loader.load();

            Stage stage = (Stage) userList.getScene().getWindow();
            stage.setScene(new Scene(sidebarView));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getFormattedTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy, hh:mm a", Locale.FRENCH);
        return now.format(formatter);
    }
}
