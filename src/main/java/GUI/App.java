package GUI;

import chat.ChatMessage;
import chat.User;
import chat.client.ChatClient;
import chat.client.PacketHandlerChatClient;
import chat.client.PacketHandlerLoginClient;
import chat.network.Connection;
import chat.network.ConnectionState;
import chat.network.packets.chat.ServerboundMessagePacket;
import chat.network.packets.login.ServerboundLoginPacket;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class App {

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

    public String userId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    /*public void start() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        User self = new User(in.readLine(), in.readLine());
        System.out.printf("constructed user: %s; %s", self.userId(), self.username());
        String peerId = in.readLine();
        System.out.printf("peer user is: %s", peerId);
        connection.sendPacket(new ServerboundLoginPacket(self, peerId));
        while (true) {
            String line = in.readLine();
            if (line.equalsIgnoreCase("exit")) break;
            System.out.printf("sending message: %s; %s", peerId, line);
            connection.sendPacket(new ServerboundMessagePacket(peerId, line));
        }
    }*/
}