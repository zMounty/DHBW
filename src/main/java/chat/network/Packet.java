package chat.network;

public interface Packet {

    void read(PacketBuffer packetBuffer);
    void write(PacketBuffer packetBuffer);
    void processPacket(IPacketHandler packetHandler);

}
