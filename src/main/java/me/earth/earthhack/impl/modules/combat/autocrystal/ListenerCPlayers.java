package me.earth.earthhack.impl.modules.combat.autocrystal;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.PlayerMoveC2SPacketListener;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.ACRotate;
import me.earth.earthhack.impl.modules.combat.autocrystal.modes.RotationThread;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

final class ListenerCPlayers extends PlayerMoveC2SPacketListener
{
    private final AutoCrystal module;

    public ListenerCPlayers(AutoCrystal module)
    {
        this.module = module;
    }

    @Override
    protected void onPacket
            (PacketEvent.Send<PlayerMoveC2SPacket> event)
    {
        update(event);
    }

    @Override
    protected void onPosition
            (PacketEvent.Send<PlayerMoveC2SPacket.PositionAndOnGround> event)
    {
        update(event);
    }

    @Override
    protected void onRotation
            (PacketEvent.Send<PlayerMoveC2SPacket.LookAndOnGround> event)
    {
        update(event);
    }

    @Override
    protected void onPositionRotation
            (PacketEvent.Send<PlayerMoveC2SPacket.Full> event)
    {
        update(event);
    }

    private void update(PacketEvent.Send<? extends PlayerMoveC2SPacket> event)
    {
        if (module.multiThread.getValue()
            && !module.isSpoofing
            && module.rotate.getValue() != ACRotate.None
            && module.rotationThread.getValue() == RotationThread.Cancel)
        {
            module.rotationCanceller.onPacket(event);
        }
        else
        {
            module.rotationCanceller.reset();
        }
    }

}
