package me.earth.earthhack.impl.event.events.network;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;

/**
 * PacketEvent sent by Minecraft's {@link net.minecraft.server.integrated.IntegratedServer}.
 */
public class IntegratedPacketEvent<T extends Packet<?>> extends PacketEvent<T> {
    protected IntegratedPacketEvent(T packet) {
        super(packet);
    }

    public static class Send<T extends Packet<? extends PacketListener>>
            extends PacketEvent.Send<T>
    {
        public Send(T packet)
        {
            super(packet);
        }
    }

    public static class NoEvent<T extends Packet<? extends PacketListener>>
            extends PacketEvent.NoEvent<T>
    {
        public NoEvent(T packet, boolean post)
        {
            super(packet, post);
        }
    }

    public static class Receive<T extends Packet<? extends PacketListener>>
            extends PacketEvent.Receive<T>
    {
        public Receive(T packet, ClientConnection networkManager)
        {
            super(packet, networkManager);
        }
    }

    public static class Post<T extends Packet<? extends PacketListener>> extends PacketEvent.Post<T>
    {
        public Post(T packet)
        {
            super(packet);
        }
    }

}
