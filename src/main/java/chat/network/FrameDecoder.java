package chat.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class FrameDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext context, ByteBuf input, List<Object> output) {
        input.markReaderIndex();
        if(!input.isReadable()) {
            System.out.println("Not readable");
            input.resetReaderIndex();
            return;
        }
        short readableBytes = input.readShort();
        if(input.readableBytes() >= readableBytes) {
            output.add(input.readBytes(readableBytes));
            return;
        }
        input.resetReaderIndex();
    }

}
