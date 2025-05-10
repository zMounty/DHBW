package GUI;

import chat.ChatMessage;
import chat.User;
import chat.client.PacketHandlerChatClient;
import chat.client.PacketHandlerLoginClient;
import chat.network.Connection;
import chat.network.ConnectionState;
import chat.network.packets.chat.ServerboundMessagePacket;
import chat.network.packets.login.ServerboundLoginPacket;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class App { //Controller

    public static final String HOST = "192.168.0.47";
    public static final int PORT = 12345;

    private static final App INSTANCE = new App();

    private final List<ChatMessage> chatHistory = new ArrayList<>();

    private String userId;
    private User peer;
    private ChatGUI chatGUI;
    private Connection connection;


    public App() {}

    public static void main(String[] args) throws IOException {
        String hardwareID = HardwareIDGenerator.getShortHardwareID();
        System.out.println("Hardware ID: " + hardwareID);

        App app = getApp();
        app.setUserId(hardwareID);
        app.connect(HOST, PORT);
        app.displayGUI();
    }

    public static App getApp() {
        return INSTANCE;
    }

    public void connect(String host, int port) throws IOException {
        connection = Connection.connect(host, port);
        connection.setPacketHandler(new PacketHandlerLoginClient());
    }

    public void displayGUI() {
        chatGUI = new ChatGUI();
        SwingUtilities.invokeLater(() -> {
            chatGUI.setVisible(true);
        });
    }

    public void updateMessages(List<ChatMessage> messages) {
        chatHistory.clear();
        chatHistory.addAll(messages);
        chatGUI.setChatMessages(chatHistory);
    }

    public void selectPeer(String peerId) {
        if(peer != null) {
            JOptionPane.showMessageDialog(chatGUI, "Du bist schon mit jemandem Verbunden");
            return;
        }
        connection.sendPacket(new ServerboundLoginPacket(new User(HardwareIDGenerator.getShortHardwareID(), this.userId), peerId));
    }

    public void acceptPeer(User peer) {
        this.peer = peer;
        connection.setConnectionState(ConnectionState.CHAT);
        connection.setPacketHandler(new PacketHandlerChatClient());
    }

    public void sendMessage(String message) {
        if(peer == null) {
            JOptionPane.showMessageDialog(chatGUI, "Du bist noch mit niemandem Verbunden");
            return;
        }
        //CHECK IF CONNECTED
        if(!message.isEmpty()) {
            connection.sendPacket(new ServerboundMessagePacket(peer.userId(), message));
            chatGUI.resetMessageInputField();
        }
    }

    public void onDisconnect() {
        JOptionPane.showMessageDialog(null, "Deine Verbindung ist abgebrochen.");
        System.exit(1);
    }

    public String userId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}