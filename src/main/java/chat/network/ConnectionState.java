package chat.network;

import chat.network.packets.chat.ClientboundMessageHistoryPacket;
import chat.network.packets.login.ClientboundLoginSuccessPacket;
import chat.network.packets.login.ServerboundLoginPacket;
import io.netty.util.AttributeKey;
import chat.network.packets.chat.ServerboundMessagePacket;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum ConnectionState {

    LOGIN(0) {
        {
            this.registerPacket(PacketDirection.SERVERBOUND, ServerboundLoginPacket.class);
            this.registerPacket(PacketDirection.CLIENTBOUND, ClientboundLoginSuccessPacket.class);
        }
    },
    CHAT(1) {
        {
            this.registerPacket(PacketDirection.SERVERBOUND, ServerboundMessagePacket.class);
            this.registerPacket(PacketDirection.CLIENTBOUND, ClientboundMessageHistoryPacket.class);
        }
    };

    public static final AttributeKey<ConnectionState> CONNECTION_STATE_ATTRIBUTE_KEY = AttributeKey.valueOf("state");

    private static final Map<Class<? extends Packet>, ConnectionState> STATES_BY_CLASS = new HashMap<Class<? extends Packet>, ConnectionState>();
    private static final Map<Integer, ConnectionState> STATES_BY_ID = new HashMap<Integer, ConnectionState>();

    private final int id;

    private List<Class<? extends Packet>> clientBoundMap;
    private List<Class<? extends Packet>> serverBoundMap;

    ConnectionState(int protocolId) {
        this.id = protocolId;
    }

    protected void registerPacket(PacketDirection direction, Class<? extends Packet> packetClass) {
        if(direction == PacketDirection.CLIENTBOUND) {
            if(this.clientBoundMap == null) {
                this.clientBoundMap = new ArrayList<>();
            }
            clientBoundMap.add(packetClass);
        }else {
            if(this.serverBoundMap == null) {
                this.serverBoundMap = new ArrayList<>();
            }
            serverBoundMap.add(packetClass);
        }
    }

    public static ConnectionState getById(int stateId) {
        return STATES_BY_ID.get(stateId);
    }

    public static ConnectionState getByPacket(Packet packet) {
        return STATES_BY_CLASS.get(packet.getClass());
    }

    public int getPacketId(PacketDirection direction, Packet packet) {
        if(direction == PacketDirection.CLIENTBOUND) {
            return this.clientBoundMap.indexOf(packet.getClass());
        }else if(direction == PacketDirection.SERVERBOUND) {
            return this.serverBoundMap.indexOf(packet.getClass());
        }else {
            return -1;
        }
    }

    public Packet getPacket(PacketDirection direction, int packetId) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if(direction == PacketDirection.CLIENTBOUND) {
            Constructor<? extends Packet> constructor = clientBoundMap.get(packetId).getConstructor();
            return constructor.newInstance();
        }else if(direction == PacketDirection.SERVERBOUND) {
            Constructor<? extends Packet> constructor = serverBoundMap.get(packetId).getConstructor();
            return constructor.newInstance();
        }else {
            return null;
        }
    }

    public int getId() {
        return this.id;
    }

    static {
        ConnectionState[] states = values();

        for (ConnectionState state : states) {
            STATES_BY_ID.put(state.getId(), state);
            if (state.clientBoundMap != null) {
                for (Class<? extends Packet> packetClass : state.clientBoundMap) {
                    if (STATES_BY_CLASS.containsKey(packetClass) && STATES_BY_CLASS.get(packetClass) != state) {
                        System.out.printf("Packet fehler %s%n", STATES_BY_CLASS.get(packetClass));
                    } else {
                        try {
                            packetClass.getConstructor().newInstance();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    STATES_BY_CLASS.put(packetClass, state);
                }
            }

            if (state.serverBoundMap != null) {
                for (Class<? extends Packet> packetClass : state.serverBoundMap) {
                    if (STATES_BY_CLASS.containsKey(packetClass) && STATES_BY_CLASS.get(packetClass) != state) {
                        System.out.println("Packet fehler");
                    } else {
                        try {
                            packetClass.getConstructor().newInstance();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    STATES_BY_CLASS.put(packetClass, state);
                }
            }
        }
    }

}