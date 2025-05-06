package chat.network.packets.chat;

import chat.ChatMessage;
import chat.User;
import chat.network.IPacketHandler;
import chat.network.Packet;
import chat.network.PacketBuffer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ClientboundMessageHistoryPacket implements Packet {

    private User peer;
    private List<ChatMessage> messages;

    public ClientboundMessageHistoryPacket() {}

    public ClientboundMessageHistoryPacket(User peer, List<ChatMessage> messages) {
        this.peer = peer;
        this.messages = messages;
    }


    @Override
    public void read(PacketBuffer packetBuffer) {
        peer = new User(packetBuffer.readStringFromBuffer(), packetBuffer.readStringFromBuffer());
        int length = packetBuffer.readInt();
        messages = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            messages.add(new ChatMessage(new User(packetBuffer.readStringFromBuffer(), packetBuffer.readStringFromBuffer()), new User(packetBuffer.readStringFromBuffer(), packetBuffer.readStringFromBuffer()), packetBuffer.readStringFromBuffer(), new Date(packetBuffer.readLong())));
        }
    }

    @Override
    public void write(PacketBuffer packetBuffer) {
        packetBuffer.writeString(peer.userId()).writeString(peer.username());
        packetBuffer.writeInt(messages.size());
        for (ChatMessage chatMessage : messages) {
            packetBuffer.writeString(chatMessage.getSender().userId());
            packetBuffer.writeString(chatMessage.getSender().username());
            packetBuffer.writeString(chatMessage.getReceiver().userId());
            packetBuffer.writeString(chatMessage.getReceiver().username());
            packetBuffer.writeString(chatMessage.getMessage());
            packetBuffer.writeLong(chatMessage.getTimestamp().getTime());
        }
    }

    @Override
    public void processPacket(IPacketHandler packetHandler) {
        ((IPacketHandlerChatClient) packetHandler).handleChatHistory(this);
    }

    public User peer() {
        return peer;
    }

    public List<ChatMessage> messages() {
        return messages;
    }
}
