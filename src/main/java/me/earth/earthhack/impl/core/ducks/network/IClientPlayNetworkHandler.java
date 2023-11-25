package me.earth.earthhack.impl.core.ducks.network;

import me.earth.earthhack.impl.event.events.network.DisconnectEvent;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.MutableText;

/**
 * Duck interface for {@link net.minecraft.client.network.ClientPlayNetworkHandler}.
 */
public interface IClientPlayNetworkHandler
{
    /**
     * Invokes {@link IClientPlayNetworkHandler#sendPacketNoEvent(Packet, boolean)},
     * for the packet and <tt>true</tt>;
     *
     * @param packetIn the packet to send.
     * @return the packet sent, or <tt>null</tt> if the channel is closed.
     */
    default Packet<?> sendPacketNoEvent(Packet<?> packetIn) {
        return packetIn;
    }

    /**
     * Sends a Packet without creating PacketEvent.Send.
     * A PacketEvent.Post will only be created if post is true.
     *
     * @param packetIn the packet to send.
     * @param post if you want to fire a post event.
     * @return the packet sent, or <tt>null</tt> if the channel is closed.
     */
    default Packet<?> sendPacketNoEvent(Packet<?> packetIn, boolean post) {
        return packetIn;
    }


    default <T extends Packet<?>> PacketEvent.Send<T> getSendEvent(T packet) {
        return new PacketEvent.Send<>(packet);
    }

    default <T extends Packet<?>> PacketEvent.Receive<T> getReceive(T packet) {
        return new PacketEvent.Receive<>(packet, (ClientPlayNetworkHandler) this);
    }

    default <T extends Packet<?>> PacketEvent.Post<T> getPost(T packet) {
        return new PacketEvent.Post<>(packet);
    }

    default <T extends Packet<?>> PacketEvent.NoEvent<T> getNoEvent(T packet, boolean post) {
        return new PacketEvent.NoEvent<>(packet, post);
    }

    default DisconnectEvent getDisconnect(MutableText component) {
        return new DisconnectEvent(component, (ClientPlayNetworkHandler) this);
    }
}
