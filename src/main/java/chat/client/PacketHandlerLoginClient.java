package chat.client;

import chat.network.ConnectionState;
import chat.network.packets.login.ClientboundLoginSuccessPacket;
import chat.network.packets.login.IPacketHandlerLoginClient;

public record PacketHandlerLoginClient(ChatClient chatClient) implements IPacketHandlerLoginClient {

    @Override
    public void handleLoginSuccess(ClientboundLoginSuccessPacket packet) {
        System.out.println("Login successful");
        chatClient.connection().setConnectionState(ConnectionState.CHAT);
        chatClient.connection().setPacketHandler(new PacketHandlerChatClient(chatClient));
    }

    @Override
    public void onDisconnect() {
        //TODO
    }

}
