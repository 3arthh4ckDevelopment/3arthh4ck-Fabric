package me.earth.earthhack.impl.modules.player.norotate;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.network.PacketUtil;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

final class ListenerPosLook extends
        ModuleListener<NoRotate, PacketEvent.Receive<PlayerPositionLookS2CPacket>>
{
    public ListenerPosLook(NoRotate module)
    {
        super(module,
                PacketEvent.Receive.class,
                -5,
                PlayerPositionLookS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<PlayerPositionLookS2CPacket> event)
    {
        if (module.noForceLook.getValue() && !event.isCancelled())
        {
            event.setCancelled(true);
            if (module.async.getValue())
            {
                PacketUtil.handlePosLook(event.getPacket(), mc.player, true);
            }
            else
            {
                mc.execute(() ->
                        PacketUtil.handlePosLook(event.getPacket(), mc.player, true));
            }
        }
    }

}