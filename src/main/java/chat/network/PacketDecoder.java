package chat.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {

    private final PacketDirection direction;

    public PacketDecoder(PacketDirection direction) {
        this.direction = direction;
    }

    @Override
    protected void decode(ChannelHandlerContext context, ByteBuf byteBuf, List<Object> output) throws Exception {
        PacketBuffer packetBuffer = new PacketBuffer(byteBuf);
        int packetId = packetBuffer.readShort();
        Packet packet = context.channel().attr(ConnectionState.CONNECTION_STATE_ATTRIBUTE_KEY).get().getPacket(this.direction, packetId);
        packet.read(packetBuffer);
        output.add(packet);
    }

}