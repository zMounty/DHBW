package chat.network;

import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

public class PacketBuffer {

    private final ByteBuf buffer;

    public PacketBuffer(ByteBuf buffer) {
        this.buffer = buffer;
    }

    public PacketBuffer ensureWritable(int bytes) {
        buffer.ensureWritable(bytes);
        return this;
    }

    public void release() {
        buffer.release();
    }

    public void writeBytes(ByteBuf source, int index, int length) {
        buffer.writeBytes(source, index, length);
    }

    public boolean readBoolean() {
        return buffer.readBoolean();
    }

    public PacketBuffer writeBoolean(boolean value) {
        buffer.writeBoolean(value);
        return this;
    }

    public short readShort() {
        return buffer.readShort();
    }

    public PacketBuffer writeShort(short value) {
        buffer.writeShort(value);
        return this;
    }

    public int readInt() {
        return buffer.readInt();
    }

    public PacketBuffer writeInt(int value) {
        buffer.writeInt(value);
        return this;
    }

    public float readFloat() {
        return buffer.readFloat();
    }

    public PacketBuffer writeFloat(float value) {
        buffer.writeFloat(value);
        return this;
    }

    public long readLong() {
        return buffer.readLong();
    }

    public PacketBuffer writeLong(long value) {
        buffer.writeLong(value);
        return this;
    }

    public String readStringFromBuffer() {
        return buffer.readCharSequence(buffer.readShort(), StandardCharsets.UTF_8).toString();
    }

    public PacketBuffer writeString(String value) {
        buffer.writeShort(value.getBytes(StandardCharsets.UTF_8).length);
        buffer.writeCharSequence(value, StandardCharsets.UTF_8);
        return this;
    }


}
