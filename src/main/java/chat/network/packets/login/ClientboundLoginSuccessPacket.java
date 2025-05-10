package chat.network.packets.login;

import chat.User;
import chat.network.IPacketHandler;
import chat.network.Packet;
import chat.network.PacketBuffer;

public class ClientboundLoginSuccessPacket implements Packet {

    private User peer;

    public ClientboundLoginSuccessPacket() {}

    public ClientboundLoginSuccessPacket(User peer) {
        this.peer = peer;
    }

    @Override
    public void read(PacketBuffer packetBuffer) {
        peer = new User(packetBuffer.readStringFromBuffer(), packetBuffer.readStringFromBuffer());
    }

    @Override
    public void write(PacketBuffer packetBuffer) {
        packetBuffer.writeString(peer.userId()).writeString(peer.username());
    }

    @Override
    public void processPacket(IPacketHandler packetHandler) {
        ((IPacketHandlerLoginClient) packetHandler).handleLoginSuccess(this);
    }

    public User peer() {
        return peer;
    }
}
