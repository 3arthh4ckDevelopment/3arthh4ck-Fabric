package me.earth.earthhack.impl.modules.player.timer;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

final class ListenerPosLook extends
        ModuleListener<Timer, PacketEvent.Receive<PlayerPositionLookS2CPacket>>
{
    public ListenerPosLook(Timer module)
    {
        super(module, PacketEvent.Receive.class, PlayerPositionLookS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<PlayerPositionLookS2CPacket> event)
    {
        module.packets = 0;
        module.sent    = 0;
        module.pSpeed  = 1.0f;
    }

}
