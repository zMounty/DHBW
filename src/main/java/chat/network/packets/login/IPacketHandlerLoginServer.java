package chat.network.packets.login;

import chat.network.IPacketHandler;

public interface IPacketHandlerLoginServer extends IPacketHandler {

    void handleLogin(ServerboundLoginPacket packet);

}
