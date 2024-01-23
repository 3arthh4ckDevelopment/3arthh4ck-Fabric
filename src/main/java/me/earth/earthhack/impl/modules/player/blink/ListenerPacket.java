package me.earth.earthhack.impl.modules.player.blink;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerPacket extends ModuleListener<Blink, PacketEvent.Send<?>>
{
    public ListenerPacket(Blink module)
    {
        super(module, PacketEvent.Send.class);
    }

    @Override
    public void invoke(PacketEvent.Send<?> event)
    {
        if (module.packetMode.getValue().shouldCancel(event.getPacket()))
        {
            mc.execute(() -> module.packets.add(event.getPacket()));
            event.setCancelled(true);
        }
    }

}
