package me.earth.earthhack.impl.core.ducks.network;

import net.minecraft.network.packet.Packet;

/**
 * Duck interface for {@link net.minecraft.client.network.ClientPlayNetworkHandler}.
 * Currently WIP because PacketDirection is not available in Fabric.
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

    /* TODO
    default boolean isPingBypass() {
        // this statement is correct but is also overridden in the Mixin and the PbClientPlayNetworkHandler
        return this instanceof PbClientPlayNetworkHandler;
    }
    */
    /*
    default EnumPacketDirection getPacketDirection() {
        // dummy, this is overridden in the Mixin
        return EnumPacketDirection.CLIENTBOUND;
    }

    default boolean isIntegratedServerClientPlayNetworkHandler() {
        return !isPingBypass() && getPacketDirection() == PacketDirection.SERVERBOUND;
    }

    default <T extends Packet<?>> PacketEvent.Send<T> getSendEvent(T packet) {
        if (isIntegratedServerClientPlayNetworkHandler()) {
            return new IntegratedPacketEvent.Send<>(packet);
        }

        return new PacketEvent.Send<>(packet);
    }

    default <T extends Packet<?>> PacketEvent.Receive<T> getReceive(T packet) {
        if (isIntegratedServerClientPlayNetworkHandler()) {
            return new IntegratedPacketEvent.Receive<>(packet, (ClientPlayNetworkHandler) this);
        }

        return new PacketEvent.Receive<>(packet, (ClientPlayNetworkHandler) this);
    }

    default <T extends Packet<?>> PacketEvent.Post<T> getPost(T packet) {
        if (isIntegratedServerClientPlayNetworkHandler()) {
            return new IntegratedPacketEvent.Post<>(packet);
        }

        return new PacketEvent.Post<>(packet);
    }

    default <T extends Packet<?>> PacketEvent.NoEvent<T> getNoEvent(T packet, boolean post) {
        if (isIntegratedServerClientPlayNetworkHandler()) {
            return new IntegratedPacketEvent.NoEvent<>(packet, post);
        }

        return new PacketEvent.NoEvent<>(packet, post);
    }

    default DisconnectEvent getDisconnect(MutableText component) {
        if (isIntegratedServerClientPlayNetworkHandler()) {
            return new IntegratedDisconnectEvent(component, (ClientPlayNetworkHandler) this);
        }

        return new DisconnectEvent(component, (ClientPlayNetworkHandler) this);
    }
    */
}
