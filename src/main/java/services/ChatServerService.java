package services;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ChatServerService {
    private static final String HISTORY_FILE = "chat_history.txt";
    private static Set<ClientHandler> clients = new HashSet<>();
    static final Map<String, ClientHandler> clientMap = new HashMap<>();

    private static boolean isRunning = false;

    public static void startServer() {
        if (isRunning) {
            System.out.println("Le serveur de chat est déjà en cours d'exécution.");
            return;
        }

        isRunning = true;

        Thread serverThread = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(5000)) {
                System.out.println("✅ Serveur de chat lancé sur le port 5000...");
                while (isRunning) {
                    Socket socket = serverSocket.accept();
                    ClientHandler clientHandler = new ClientHandler(socket);
                    clients.add(clientHandler);
                    new Thread(clientHandler).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        serverThread.setDaemon(true); // S'arrête quand l'application se ferme
        serverThread.start();
    }

    public static void stopServer() {
        isRunning = false;
        for (ClientHandler client : clients) {
            client.sendMessage("⚠️ Le serveur de chat est arrêté.");
            client.closeConnection();
        }
        clients.clear();
        clientMap.clear();
        System.out.println("⛔ Serveur de chat arrêté.");
    }

    static void broadcastMessage(String message, ClientHandler sender) {
        String timestampedMessage = sender.clientName + ": " + message;

        // 🔹 Sauvegarder le message proprement
        saveChatHistory(timestampedMessage);

        // 🔹 Envoyer aux clients connectés
        for (ClientHandler client : clients) {
            client.sendMessage(timestampedMessage);
        }
    }

    // Fonction pour sauvegarder l'historique des messages
    private static void saveChatHistory(String message) {
        try (PrintWriter out = new PrintWriter(new FileWriter(HISTORY_FILE, true))) {
            out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void updateUserList() {
        String userList = "@users " + String.join(",", clientMap.keySet());
        for (ClientHandler client : clients) {
            client.sendMessage(userList);
        }
    }

    static void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        clientMap.remove(clientHandler.clientName);
        updateUserList();
    }

    private static String getTimestamp() {
        return "[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "]";
    }
}

// ✅ Correction: Les méthodes `sendMessage()` et `closeConnection()` doivent être **dans** `ClientHandler`
class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    String clientName;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            clientName = in.readLine().trim();

            if (ChatServerService.clientMap.containsKey(clientName)) {
                sendMessage("⚠️ Ce nom d'utilisateur est déjà pris !");
                closeConnection();
                return;
            }

            System.out.println("👤 " + clientName + " a rejoint la messagerie.");
            ChatServerService.clientMap.put(clientName, this);
            ChatServerService.updateUserList();

            String message;
            while ((message = in.readLine()) != null) {
                ChatServerService.broadcastMessage(message, this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ChatServerService.removeClient(this);
            closeConnection();
        }
    }

    // ✅ Correction: Ces méthodes doivent être dans `ClientHandler`
    void sendMessage(String message) {
        out.println(message);
    }

    void closeConnection() {
        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }
}
