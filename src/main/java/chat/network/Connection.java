package chat.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.SocketAddress;

public class Connection extends SimpleChannelInboundHandler<Packet> { //MODEL

    public static final EventLoopGroup EVENT_LOOP_GROUP = new NioEventLoopGroup();

    private Channel channel;
    private IPacketHandler packetHandler;

    public static Connection connect(String host, int port) {
        Connection connection = new Connection();
        new Bootstrap().group(EVENT_LOOP_GROUP)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel channel) {
                        channel.pipeline()
                                .addLast("splitter", new FrameDecoder())
                                .addLast("decoder", new PacketDecoder(PacketDirection.CLIENTBOUND))
                                .addLast("prepender", new FrameEncoder())
                                .addLast("encoder", new PacketEncoder(PacketDirection.SERVERBOUND))
                                .addLast("packet_handler", connection);
                    }
                }).connect(host, port).syncUninterruptibly();
        return connection;
    }

    @Override
    public void channelActive(ChannelHandlerContext context) throws Exception {
        super.channelActive(context);
        this.channel = context.channel();
        this.setConnectionState(ConnectionState.LOGIN);
    }

    @Override
    public void channelInactive(ChannelHandlerContext context) throws Exception {
        super.channelInactive(context);
        packetHandler.onDisconnect();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, Packet packet) {
        packet.processPacket(packetHandler);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        cause.printStackTrace();
        System.out.println("Verbindung unterbrochen: " + cause.getMessage());
        context.close();
    }

    public void sendPacket(Packet packet) {
        ConnectionState packetConnectionState = ConnectionState.getByPacket(packet);
        if(this.channel.eventLoop().inEventLoop()) {
            if(packetConnectionState != connectionState()) {
                Connection.this.setConnectionState(packetConnectionState);
            }
            ChannelFuture channelFuture = this.channel.writeAndFlush(packet);
            channelFuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        }else {
            this.channel.eventLoop().execute(() -> {
                if(packetConnectionState != connectionState()) {
                    Connection.this.setConnectionState(packetConnectionState);
                }
                ChannelFuture channelFuture = Connection.this.channel.writeAndFlush(packet);
                channelFuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            });
        }
    }

    public void closeChannel() {
        if(this.channel.isOpen()) {
            this.channel.close().syncUninterruptibly();
        }
    }

    public Channel channel() {
        return channel;
    }

    public static void shutdown() {
        EVENT_LOOP_GROUP.shutdownGracefully();
    }

    public IPacketHandler packetHandler() {
        return this.packetHandler;
    }

    public void setPacketHandler(IPacketHandler packetHandler) {
        this.packetHandler = packetHandler;
    }

    public ConnectionState connectionState() {
        return this.channel.attr(ConnectionState.CONNECTION_STATE_ATTRIBUTE_KEY).get();
    }

    public void setConnectionState(ConnectionState connectionState) {
        this.channel.attr(ConnectionState.CONNECTION_STATE_ATTRIBUTE_KEY).set(connectionState);
    }

    public SocketAddress remoteAddress() {
        return this.channel.remoteAddress();
    }

}