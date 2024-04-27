package me.earth.earthhack.impl.modules.combat.bowkill;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.PlayerMoveC2SPacketListener;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

final class ListenerCPacket extends PlayerMoveC2SPacketListener
{

    private final BowKiller module;

    public ListenerCPacket(BowKiller module)
    {
        this.module = module;
    }

    @Override
    protected void onPacket(PacketEvent.Send<PlayerMoveC2SPacket> event)
    {
        module.onPacket(event);
    }

    @Override
    protected void onPosition(PacketEvent.Send<PlayerMoveC2SPacket.PositionAndOnGround> event)
    {
        module.onPacket(event);
    }

    @Override
    protected void onRotation(PacketEvent.Send<PlayerMoveC2SPacket.LookAndOnGround> event)
    {
        if (module.cancelRotate.getValue()) module.onPacket(event);
    }

    @Override
    protected void onPositionRotation(PacketEvent.Send<PlayerMoveC2SPacket.Full> event)
    {
        module.onPacket(event);
    }

}
