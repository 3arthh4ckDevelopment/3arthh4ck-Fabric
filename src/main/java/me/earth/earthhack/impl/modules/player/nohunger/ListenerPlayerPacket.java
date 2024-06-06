package me.earth.earthhack.impl.modules.player.nohunger;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.mixins.network.client.IPlayerMoveC2SPacket;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.PlayerMoveC2SPacketListener;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

final class ListenerPlayerPacket
        extends PlayerMoveC2SPacketListener implements Globals
{
    private final NoHunger module;

    public ListenerPlayerPacket(NoHunger module)
    {
        this.module = module;
    }

    @Override
    protected void onPacket(PacketEvent.Send<PlayerMoveC2SPacket> event)
    {
        onPacket(event.getPacket());
    }

    @Override
    protected void onPosition(PacketEvent.Send<PlayerMoveC2SPacket.PositionAndOnGround> event)
    {
        onPacket(event.getPacket());
    }

    @Override
    protected void onRotation(PacketEvent.Send<PlayerMoveC2SPacket.LookAndOnGround> event)
    {
        onPacket(event.getPacket());
    }

    @Override
    protected void onPositionRotation(PacketEvent.Send<PlayerMoveC2SPacket.Full> event)
    {
        onPacket(event.getPacket());
    }

    // Can improve with block break and liquid checks
    private void onPacket(PlayerMoveC2SPacket packet)
    {
        if (module.ground.getValue()
                && module.onGround
                && mc.player.isOnGround()
                && packet.getY(0.0) ==
                (!((IPlayerMoveC2SPacket) packet).isMoving() ? 0.0 : mc.player.getY()))
        {
            ((IPlayerMoveC2SPacket) packet).setOnGround(false);
        }

        module.onGround = mc.player.onGround;
    }

}