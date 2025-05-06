package chat.network.packets.chat;

import chat.network.IPacketHandler;

public interface IPacketHandlerChatServer extends IPacketHandler {

    void handleMessage(ServerboundMessagePacket packet);

}
