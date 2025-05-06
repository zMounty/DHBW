package chat.network.packets.login;

import chat.network.IPacketHandler;
import chat.network.Packet;
import chat.network.PacketBuffer;

public class ClientboundLoginSuccessPacket implements Packet {
    @Override
    public void read(PacketBuffer packetBuffer) {

    }

    @Override
    public void write(PacketBuffer packetBuffer) {

    }

    @Override
    public void processPacket(IPacketHandler packetHandler) {
        ((IPacketHandlerLoginClient) packetHandler).handleLoginSuccess(this);
    }
}
