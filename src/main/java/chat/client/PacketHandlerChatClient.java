package chat.client;

import GUI.App;
import chat.network.packets.chat.ClientboundMessageHistoryPacket;
import chat.network.packets.chat.IPacketHandlerChatClient;

public record PacketHandlerChatClient() implements IPacketHandlerChatClient {

    @Override
    public void handleChatHistory(ClientboundMessageHistoryPacket packet) {
        App.getApp().updateMessages(packet.messages());
    }

    @Override
    public void onDisconnect() {
        App.getApp().onDisconnect();
    }
}
