package me.earth.earthhack.impl.modules.movement.nofall;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.core.mixins.network.client.IPlayerMoveC2SPacket;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.PlayerMoveC2SPacketListener;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

final class ListenerPlayerPackets extends PlayerMoveC2SPacketListener
        implements Globals
{
    public final NoFall module;

    public ListenerPlayerPackets(NoFall module)
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
    protected void onPositionRotation
            (PacketEvent.Send<PlayerMoveC2SPacket.Full> event)
    {
        onPacket(event.getPacket());
    }

    private void onPacket(PlayerMoveC2SPacket packet)
    {
        switch (module.mode.getValue())
        {
            case Packet -> {
                if (mc.player.fallDistance > 3.0F) {
                    ((IPlayerMoveC2SPacket) packet).setOnGround(true);
                    return;
                }
            }
            case Anti -> {
                if (mc.player.fallDistance > 3.0F) {
                    ((IPlayerMoveC2SPacket) packet).setY(
                            mc.player.getY() + 0.10000000149011612);
                    return;
                }
            }
            case AAC -> {
                if (mc.player.fallDistance > 3.0F) {
                    mc.player.onGround = true;
                    mc.player.getAbilities().flying = true;
                    mc.player.getAbilities().allowFlying = true;
                    ((IPlayerMoveC2SPacket) packet).setOnGround(false);
                    mc.player.velocityModified = true;
                    mc.player.getAbilities().flying = false;
                    mc.player.jump();
                }
            }
        }
    }

}
