package chat.server;

import chat.ChatMessage;
import chat.User;
import chat.network.Connection;
import chat.network.packets.chat.ClientboundMessageHistoryPacket;
import chat.network.packets.chat.IPacketHandlerChatServer;
import chat.network.packets.chat.ServerboundMessagePacket;

import java.util.List;

public record PacketHandlerChatServer(ChatServer chatServer, Connection connection) implements IPacketHandlerChatServer {

    @Override
    public void handleMessage(ServerboundMessagePacket packet) {
        String userId = connection.channel().attr(ChatServer.USER_ATTRIBUTE_KEY).get();
        String peerId = connection.channel().attr(ChatServer.PEER_ATTRIBUTE_KEY).get();
        chatServer.chatDatabase().saveMessage(userId, peerId, packet.message());

        List<ChatMessage> chatMessages = chatServer.chatDatabase().fetchChatHistory(userId, peerId);
        Connection peerConnection = chatServer.peers().get(peerId);
        if(peerConnection != null) {
        User peer = chatServer.chatDatabase().cachedUserById(peerId);
        peerConnection.sendPacket(new ClientboundMessageHistoryPacket(peer != null ? peer : new User(peerId, "Unknown"), chatMessages));
        }
        User user = chatServer.chatDatabase().cachedUserById(userId);
        connection.sendPacket(new ClientboundMessageHistoryPacket(user != null ? user : new User(userId, "Unknown"), chatMessages));
    }

    @Override
    public void onDisconnect() {
        String userId = connection.channel().attr(ChatServer.USER_ATTRIBUTE_KEY).get();
        chatServer().peers().remove(userId);
    }
}
