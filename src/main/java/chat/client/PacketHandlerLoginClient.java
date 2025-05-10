package chat.client;

import GUI.App;
import chat.network.packets.login.ClientboundLoginSuccessPacket;
import chat.network.packets.login.IPacketHandlerLoginClient;

public record PacketHandlerLoginClient() implements IPacketHandlerLoginClient {

    @Override
    public void handleLoginSuccess(ClientboundLoginSuccessPacket packet) {
        App.getApp().acceptPeer(packet.peer());
    }

    @Override
    public void onDisconnect() {
        App.getApp().onDisconnect();
    }

}
