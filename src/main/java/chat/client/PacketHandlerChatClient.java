package chat.client;

import chat.ChatMessage;
import chat.network.packets.chat.ClientboundMessageHistoryPacket;
import chat.network.packets.chat.IPacketHandlerChatClient;

public record PacketHandlerChatClient(ChatClient chatClient) implements IPacketHandlerChatClient {

    @Override
    public void handleChatHistory(ClientboundMessageHistoryPacket packet) {
        System.out.println("unpacking");
        System.out.println(packet.peer());
        System.out.println(packet.messages());
        for (ChatMessage message : packet.messages()) {
            System.out.printf("[%s] %s -> %s: %s%n]", message.getTimestamp(), message.getSender(), message.getReceiver(), message.getMessage());
        }
        chatClient.chatHistory().clear();
        chatClient.chatHistory().addAll(packet.messages());
    }

    @Override
    public void onDisconnect() {
        //TODO
    }
}
