package me.earth.earthhack.impl.modules.movement.flight;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.mixins.network.client.ICPacketPlayer;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.CPacketPlayerListener;
import me.earth.earthhack.impl.modules.movement.flight.mode.FlightMode;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

final class ListenerPlayerPacket extends CPacketPlayerListener implements Globals
{
    private final Flight module;

    public ListenerPlayerPacket(Flight module)
    {
        this.module = module;
    }

    @Override
    protected void onPacket(PacketEvent.Send<PlayerMoveC2SPacket> event)
    {
        onCPacket(event);
    }

    @Override
    protected void onPosition(PacketEvent.Send<PlayerMoveC2SPacket.PositionAndOnGround> event)
    {
        onCPacket(event);
    }

    @Override
    protected void onRotation(PacketEvent.Send<PlayerMoveC2SPacket.LookAndOnGround> event)
    {
        onCPacket(event);
    }

    @Override
    protected void onPositionRotation(PacketEvent.Send<PlayerMoveC2SPacket.Full> event)
    {
        onCPacket(event);
    }

    private void onCPacket(PacketEvent.Send<? extends PlayerMoveC2SPacket> packet)
    {
        if (module.mode.getValue() == FlightMode.AAC)
        {
            if (mc.player.fallDistance > 3.8f)
            {
                ((ICPacketPlayer) packet.getPacket()).setOnGround(true);
                mc.player.fallDistance = 0.0f;
            }
        } else if (module.mode.getValue() == FlightMode.ConstantiamNew
                && module.constNewStage == 0)
        {
            packet.setCancelled(true);
        }

        if (module.mode.getValue() == FlightMode.Constantiam
                && module.clipped)
        {
            // mc.player.setPosition(mc.player.posX, mc.player.posY + 0.032, mc.player.posZ);
            // module.clipped = false;
        }
    }

}