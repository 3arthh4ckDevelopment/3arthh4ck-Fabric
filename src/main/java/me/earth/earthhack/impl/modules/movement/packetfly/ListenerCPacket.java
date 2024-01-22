package me.earth.earthhack.impl.modules.movement.packetfly;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.PlayerMoveC2SPacketListener;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

final class ListenerCPacket extends PlayerMoveC2SPacketListener
{
    private final PacketFly packetFly;

    public ListenerCPacket(PacketFly packetFly)
    {
        this.packetFly = packetFly;
    }

    @Override
    protected void onPacket(PacketEvent.Send<PlayerMoveC2SPacket> event)
    {
        packetFly.onPacketSend(event);
    }

    @Override
    protected void onPosition(PacketEvent.Send<PlayerMoveC2SPacket.PositionAndOnGround> event)
    {
        packetFly.onPacketSend(event);
    }

    @Override
    protected void onRotation(PacketEvent.Send<PlayerMoveC2SPacket.LookAndOnGround> event)
    {
        packetFly.onPacketSend(event);
    }

    @Override
    protected void onPositionRotation(
            PacketEvent.Send<PlayerMoveC2SPacket.Full> event)
    {
        packetFly.onPacketSend(event);
    }

}
