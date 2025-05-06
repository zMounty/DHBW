package chat.network.packets.chat;

import chat.network.Packet;
import chat.network.PacketBuffer;
import chat.network.IPacketHandler;

public class ServerboundMessagePacket implements Packet {

    private String userId;
    private String message;

    public ServerboundMessagePacket() {}

    public ServerboundMessagePacket(String userId, String message) {
        this.userId = userId;
        this.message = message;
    }

    @Override
    public void read(PacketBuffer packetBuffer) {
        this.userId =  packetBuffer.readStringFromBuffer();
        this.message =  packetBuffer.readStringFromBuffer();
    }

    @Override
    public void write(PacketBuffer packetBuffer) {
        packetBuffer.writeString(userId);
        packetBuffer.writeString(message);
    }

    @Override
    public void processPacket(IPacketHandler packetHandler) {
        ((IPacketHandlerChatServer) packetHandler).handleMessage(this);
    }

    public String userId() {
        return userId;
    }

    public String message() {
        return message;
    }
}
