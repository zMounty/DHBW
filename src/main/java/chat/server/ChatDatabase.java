package chat.server;

import chat.ChatMessage;
import chat.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatDatabase {

    private Connection connection;

    private Map<String, User> userCache = new HashMap<>();

    public void connect(String url, String dbName, String username, String password) {
        try {
            connection = DriverManager.getConnection(url + "/" + dbName, username, password);
            System.out.println("Verbindung zur Datenbank erfolgreich hergestellt.");
        } catch (SQLException e) {
            System.out.println("Fehler beim Herstellen der Datenbankverbindung: " + e.getMessage());
        }
    }

    public void createTableMessages() {
        String sql = "CREATE TABLE IF NOT EXISTS messages (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, " +
                "sender_id TEXT NOT NULL, " +
                "receiver_id TEXT NOT NULL, " +
                "message TEXT NOT NULL, " +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabelle 'messages' überprüft/erstellt.");
        } catch (SQLException e) {
            System.out.println("Fehler beim Erstellen der Tabelle: " + e.getMessage());
        }
    }

    public void createTableUsers() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "user_id TEXT NOT NULL, " +
                "user_name TEXT NOT NULL" +
                ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabelle 'Users' überprüft/erstellt.");
        } catch (SQLException e) {
            System.out.println("Fehler beim Erstellen der Tabelle: " + e.getMessage());
        }
    }

    public void saveMessage(String senderId, String receiverId, String message) {
        String sql = "INSERT INTO messages (sender_id, receiver_id, message, timestamp) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, senderId);
            stmt.setString(2, receiverId);
            stmt.setString(3, message);
            stmt.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User cachedUserById(String userId) {
        if (!userCache.containsKey(userId)) {
            User user = fetchUserById(userId);
            if(user != null) {
                userCache.put(userId, user);
                return user;
            }else {
                return new User(userId, userId);
            }
        }
        return userCache.get(userId);
    }

    public User fetchUserById(String userId) {
        String sql = "SELECT user_id, user_name FROM users WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String id = rs.getString("user_id");
                    String name = rs.getString("user_name");
                    return new User(id, name);
                }
            }
        } catch (SQLException e) {
            System.out.println("Fehler beim Abrufen des Users: " + e.getMessage());
        }
        return null;
    }

    public void insertUser(User user) {
        String sql = "INSERT INTO users (user_id, user_name) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.userId());
            pstmt.setString(2, user.username());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Fehler beim Einfügen des Users: " + e.getMessage());
        }
    }

    public void updateUsername(User user) {
        String sql = "UPDATE users SET user_name = ? WHERE user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.username());
            pstmt.setString(2, user.userId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Fehler beim Aktualisieren des Usernames: " + e.getMessage());
        }
    }

    public List<ChatMessage> fetchChatHistory(String senderId, String receiverId) {
        List<ChatMessage> chatHistory = new ArrayList<>();
        String sql = "SELECT sender_id, receiver_id, message, timestamp FROM messages " +
                "WHERE (sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?) " +
                "ORDER BY timestamp ASC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, senderId);
            stmt.setString(2, receiverId);
            stmt.setString(3, receiverId);
            stmt.setString(4, senderId);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                String messageSenderId = resultSet.getString("sender_id");
                String messageReceiverId = resultSet.getString("receiver_id");
                String message = resultSet.getString("message");
                Timestamp timestamp = resultSet.getTimestamp("timestamp");
                ChatMessage chatMessage = new ChatMessage(cachedUserById(messageSenderId), cachedUserById(messageReceiverId), message, timestamp);
                chatHistory.add(chatMessage);
            }
        }catch (SQLException e) {
            System.out.println("Fehler beim Aktualisieren des Chats: " + e.getMessage());
            return new ArrayList<>();
        }
        return chatHistory;
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Datenbankverbindung geschlossen.");
        }
    }
}
