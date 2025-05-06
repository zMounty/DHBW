package chat.network.packets.login;

import chat.network.IPacketHandler;

public interface IPacketHandlerLoginClient extends IPacketHandler {

    void handleLoginSuccess(ClientboundLoginSuccessPacket packet);

}
