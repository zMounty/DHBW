package chat.client;

import chat.ChatMessage;
import chat.User;
import chat.network.*;
import chat.network.packets.chat.ServerboundMessagePacket;
import chat.network.packets.login.ServerboundLoginPacket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ChatClient {

    public static final String HOST = "127.0.0.1";
    public static final int PORT = 12345;

    private final Connection connection;

    private final List<ChatMessage> chatHistory = new ArrayList<ChatMessage>();

    public ChatClient(Connection connection) {
        this.connection = connection;
    }

    public void start() throws IOException {
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
    }

    public List<ChatMessage> chatHistory() {
        return chatHistory;
    }

    public Connection connection() {
        return connection;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Starte chat.client.ChatClient...");

        Connection connection = Connection.connect(HOST, PORT);
        ChatClient chatClient = new ChatClient(connection);
        connection.setPacketHandler(new PacketHandlerLoginClient(chatClient));
        chatClient.start();
    }
}
