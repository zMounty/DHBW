package chat.network.packets.chat;

import chat.network.IPacketHandler;

public interface IPacketHandlerChatClient extends IPacketHandler {

    void handleChatHistory(ClientboundMessageHistoryPacket packet);

}
