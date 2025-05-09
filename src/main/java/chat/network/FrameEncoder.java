package chat.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class FrameEncoder  extends MessageToByteEncoder<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext context, ByteBuf input, ByteBuf output) {
        int readableBytes = input.readableBytes();
        output.ensureWritable(2 + readableBytes);
        output.writeShort(readableBytes);
        output.writeBytes(input, input.readerIndex(), readableBytes);
    }

}
