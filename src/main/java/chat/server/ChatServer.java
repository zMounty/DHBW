package chat.server;

import chat.network.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.*;
import io.netty.util.AttributeKey;

import java.util.*;

public class ChatServer {
    public static final AttributeKey<String> USER_ATTRIBUTE_KEY = AttributeKey.valueOf("user");
    public static final AttributeKey<String> PEER_ATTRIBUTE_KEY = AttributeKey.valueOf("peer");
    public static final EventLoopGroup BOSS_GROUP = new NioEventLoopGroup();
    public static final EventLoopGroup WORKER_GROUP = new NioEventLoopGroup();

    public static final int PORT = 12345;

    private final List<ChannelFuture> endpoints = Collections.synchronizedList(new ArrayList<>());
    private final List<Connection> connections = Collections.synchronizedList(new ArrayList<>());
    private final ChatDatabase chatDatabase;

    private final Map<String, Connection> peers = new HashMap<>();

    public ChatServer(ChatDatabase chatDatabase) {
        this.chatDatabase = chatDatabase;
    }

    public void addEndpoint(int port) throws Exception {
        synchronized (endpoints) {
            this.endpoints.add(new ServerBootstrap().group(BOSS_GROUP, WORKER_GROUP)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel channel) {
                            Connection connection = new Connection();
                            ChatServer.this.connections.add(connection);
                            channel.pipeline()
                                    .addLast("splitter", new FrameDecoder())
                                    .addLast("decoder", new PacketDecoder(PacketDirection.SERVERBOUND))
                                    .addLast("prepender", new FrameEncoder())
                                    .addLast("encoder", new PacketEncoder(PacketDirection.CLIENTBOUND))
                                    .addLast("packet_handler", connection);
                            connection.setPacketHandler(new PacketHandlerLoginServer(ChatServer.this, connection));
                        }
                    }).bind(port).syncUninterruptibly());
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Starte chat.server.ChatServer...");

        ChatDatabase chatDatabase = new ChatDatabase();
        chatDatabase.connect("jdbc:mariadb://198.186.130.154:1521", "chatsystem", "chatsystem", "!*WA!Cbk8JFc8MICR7X6");
        chatDatabase.createTableUsers();
        chatDatabase.createTableMessages();

        ChatServer chatServer = new ChatServer(chatDatabase);
        chatServer.addEndpoint(PORT);
    }

    public void terminateEndpoints() {
        for(ChannelFuture channelFuture : this.endpoints) {
            try {
                channelFuture.channel().close().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        BOSS_GROUP.shutdownGracefully();
        WORKER_GROUP.shutdownGracefully();
    }

    public ChatDatabase chatDatabase() {
        return chatDatabase;
    }

    public Map<String, Connection> peers() {
        return peers;
    }

    /*private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void handlerAdded(ChannelHandlerContext context) {
        Channel incoming = context.channel();
        channels.writeAndFlush("[SERVER] - " + incoming.remoteAddress() + " hat den Chat betreten\n");
        channels.add(incoming);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext context) {
        Channel incoming = context.channel();
        channels.writeAndFlush("[SERVER] - " + incoming.remoteAddress() + " hat den Chat verlassen\n");
        channels.remove(incoming);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, String message) {
        Channel incoming = context.channel();
        for (Channel channel : channels) {
            if (channel != incoming) {
                channel.writeAndFlush("[" + incoming.remoteAddress() + "] " + message + "\n");
            } else {
                channel.writeAndFlush("[Du] " + message + "\n");
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        context.close();
    }*/
}
