package chat.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketEncoder extends MessageToByteEncoder<Packet> {

    private final PacketDirection direction;

    public PacketEncoder(PacketDirection direction) {
        this.direction = direction;
    }

    @Override
    protected void encode(ChannelHandlerContext context, Packet packet, ByteBuf output) throws Exception {
        int packetId = context.channel().attr(ConnectionState.CONNECTION_STATE_ATTRIBUTE_KEY).get().getPacketId(this.direction, packet);
        PacketBuffer packetBuffer = new PacketBuffer(output);
        packetBuffer.writeShort((short) packetId);
        packet.write(packetBuffer);
    }

}