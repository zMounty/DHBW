package chat.network.packets.login;

import chat.User;
import chat.network.IPacketHandler;
import chat.network.Packet;
import chat.network.PacketBuffer;

public class ServerboundLoginPacket implements Packet {

    private User user;
    private String peer;

    public ServerboundLoginPacket() {}

    public ServerboundLoginPacket(User user, String peer) {
        this.user = user;
        this.peer = peer;
    }


    @Override
    public void read(PacketBuffer packetBuffer) {
        user = new User(packetBuffer.readStringFromBuffer(), packetBuffer.readStringFromBuffer());
        peer = packetBuffer.readStringFromBuffer();
    }

    @Override
    public void write(PacketBuffer packetBuffer) {
        packetBuffer.writeString(user.userId()).writeString(user.username());
        packetBuffer.writeString(peer);
    }

    @Override
    public void processPacket(IPacketHandler packetHandler) {
        ((IPacketHandlerLoginServer) packetHandler).handleLogin(this);
    }

    public User user() {
        return user;
    }

    public String peer() {
        return peer;
    }

}
