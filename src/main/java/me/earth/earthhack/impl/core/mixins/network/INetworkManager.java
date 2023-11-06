package me.earth.earthhack.impl.core.mixins.network;

import me.earth.earthhack.impl.event.events.network.DisconnectEvent;
import me.earth.earthhack.impl.event.events.network.IntegratedDisconnectEvent;
import me.earth.earthhack.impl.event.events.network.IntegratedPacketEvent;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.MutableText;

/**
 * Duck interface for {@link net.minecraft.client.network.ClientPlayNetworkHandler}.
 */
public interface INetworkManager
{
    /**
     * Invokes {@link INetworkManager#sendPacketNoEvent(Packet, boolean)},
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

    default boolean isPingBypass() {
        // this statement is correct but is also overridden in the Mixin and the PbNetworkManager
        return this instanceof PbNetworkManager;
    }

    default EnumPacketDirection getPacketDirection() {
        // dummy, this is overridden in the Mixin
        return EnumPacketDirection.CLIENTBOUND;
    }

    default boolean isIntegratedServerNetworkManager() {
        return !isPingBypass() && getPacketDirection() == EnumPacketDirection.SERVERBOUND;
    }

    default <T extends Packet<?>> PacketEvent.Send<T> getSendEvent(T packet) {
        if (isIntegratedServerNetworkManager()) {
            return new IntegratedPacketEvent.Send<>(packet);
        }

        return new PacketEvent.Send<>(packet);
    }

    default <T extends Packet<?>> PacketEvent.Receive<T> getReceive(T packet) {
        if (isIntegratedServerNetworkManager()) {
            return new IntegratedPacketEvent.Receive<>(packet, (ClientPlayNetworkHandler) this);
        }

        return new PacketEvent.Receive<>(packet, (ClientPlayNetworkHandler) this);
    }

    default <T extends Packet<?>> PacketEvent.Post<T> getPost(T packet) {
        if (isIntegratedServerNetworkManager()) {
            return new IntegratedPacketEvent.Post<>(packet);
        }

        return new PacketEvent.Post<>(packet);
    }

    default <T extends Packet<?>> PacketEvent.NoEvent<T> getNoEvent(T packet, boolean post) {
        if (isIntegratedServerNetworkManager()) {
            return new IntegratedPacketEvent.NoEvent<>(packet, post);
        }

        return new PacketEvent.NoEvent<>(packet, post);
    }

    default DisconnectEvent getDisconnect(MutableText component) {
        if (isIntegratedServerNetworkManager()) {
            return new IntegratedDisconnectEvent(component, (ClientPlayNetworkHandler) this);
        }

        return new DisconnectEvent(component, (ClientPlayNetworkHandler) this);
    }

}
