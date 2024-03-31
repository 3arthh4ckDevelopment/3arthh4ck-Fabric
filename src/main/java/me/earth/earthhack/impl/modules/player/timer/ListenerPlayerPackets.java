package me.earth.earthhack.impl.modules.player.timer;

import me.earth.earthhack.api.util.interfaces.Globals;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.PlayerMoveC2SPacketListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.player.timer.mode.TimerMode;
import me.earth.earthhack.impl.util.minecraft.MovementUtil;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

final class ListenerPlayerPackets extends PlayerMoveC2SPacketListener
        implements Globals
{
    private final Timer timer;

    public ListenerPlayerPackets(Timer timer)
    {
        this.timer = timer;
    }

    @Override
    protected void onPacket(PacketEvent.Send<PlayerMoveC2SPacket> event)
    {
        onEvent(event);
    }

    @Override
    protected void onPosition(PacketEvent.Send<PlayerMoveC2SPacket.PositionAndOnGround> event)
    {
        if (!Managers.POSITION.isBlocking())
        {
            onEvent(event);
        }
    }

    @Override
    protected void onRotation(PacketEvent.Send<PlayerMoveC2SPacket.LookAndOnGround> event)
    {
        if (!Managers.ROTATION.isBlocking())
        {
            onEvent(event);
        }
    }

    @Override
    protected void onPositionRotation
            (PacketEvent.Send<PlayerMoveC2SPacket.Full> event)
    {
        if (!Managers.ROTATION.isBlocking() && !Managers.POSITION.isBlocking())
        {
            onEvent(event);
        }
    }

    private void onEvent(PacketEvent<?> event)
    {
        if (timer.mode.getValue() == TimerMode.Blink
                && Managers.NCP.passed(timer.lagTime.getValue()))
        {
            if (timer.packets != 0
                    && timer.letThrough.getValue() != 0
                    && timer.packets % timer.letThrough.getValue() == 0)
            {
                timer.packets++;
                return;
            }

            if (MovementUtil.noMovementKeys()
                    && mc.player.getVelocity().getX() < 0.001
                    && mc.player.getVelocity().getY() < 0.001
                    && mc.player.getVelocity().getZ() < 0.001)
            {
                event.setCancelled(true);
                timer.pSpeed = 1.0f;
                timer.packets++;
                return;
            }
            else if (timer.packets > timer.offset.getValue()
                        && timer.sent < timer.maxPackets.getValue())
            {
                timer.pSpeed = timer.speed.getValue();
                timer.packets--;
                timer.sent++;
                return;
            }
        }

        timer.pSpeed  = 1.0f;
        timer.sent    = 0;
        timer.packets = 0;
    }

}
