package chat.server;

import chat.ChatMessage;
import chat.User;
import chat.network.Connection;
import chat.network.packets.chat.ClientboundMessageHistoryPacket;
import chat.network.packets.login.ClientboundLoginSuccessPacket;
import chat.network.packets.login.IPacketHandlerLoginServer;
import chat.network.packets.login.ServerboundLoginPacket;

import java.util.List;

public record PacketHandlerLoginServer(ChatServer chatServer, Connection connection) implements IPacketHandlerLoginServer {


    @Override
    public void handleLogin(ServerboundLoginPacket packet) {
        connection.channel().attr(ChatServer.USER_ATTRIBUTE_KEY).set(packet.user().userId());
        connection.channel().attr(ChatServer.PEER_ATTRIBUTE_KEY).set(packet.peer());
        User user = packet.user();
        User cachedUser = chatServer.chatDatabase().cachedUserById(user.userId());
        if(cachedUser == null) {
            chatServer.chatDatabase().insertUser(user);
        }else if (!cachedUser.username().equals(user.username())) {
            chatServer.chatDatabase().updateUsername(user);
        }
        chatServer.peers().put(user.userId(), connection);
        List<ChatMessage> chatMessages = chatServer.chatDatabase().fetchChatHistory(user.userId(), packet.peer());
        System.out.println("chat messages: " + chatMessages);
        User peer = chatServer.chatDatabase().cachedUserById(packet.peer());
        System.out.println(packet.peer());
        System.out.println("peer messages: " + peer);
        connection.sendPacket(new ClientboundLoginSuccessPacket());
        connection.sendPacket(new ClientboundMessageHistoryPacket(peer != null ? peer : new User(packet.peer(), "Unknown"), chatMessages));
        connection.setPacketHandler(new PacketHandlerChatServer(chatServer, connection));
    }

    @Override
    public void onDisconnect() {
        String userId = connection.channel().attr(ChatServer.USER_ATTRIBUTE_KEY).get();
        chatServer.peers().remove(userId);
    }
}
