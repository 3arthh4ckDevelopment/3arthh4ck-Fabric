package me.earth.earthhack.impl.modules.misc.portals;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;

final class ListenerTeleport extends
        ModuleListener<Portals, PacketEvent.Send<TeleportConfirmC2SPacket>>
{
    public ListenerTeleport(Portals module)
    {
        super(module, PacketEvent.Send.class, TeleportConfirmC2SPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Send<TeleportConfirmC2SPacket> event)
    {
        if (module.godMode.getValue())
        {
            event.setCancelled(true);
        }
    }

}