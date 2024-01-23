package me.earth.earthhack.impl.modules.player.blink;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

final class ListenerPosLook extends
        ModuleListener<Blink, PacketEvent.Receive<PlayerPositionLookS2CPacket>>
{
    public ListenerPosLook(Blink module)
    {
        super(module, PacketEvent.Receive.class, PlayerPositionLookS2CPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Receive<PlayerPositionLookS2CPacket> event)
    {
        if (module.lagDisable.getValue())
        {
            mc.execute(module::disable);
        }
    }

}
