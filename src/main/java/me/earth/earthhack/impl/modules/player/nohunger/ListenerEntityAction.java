package me.earth.earthhack.impl.modules.player.nohunger;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

final class ListenerEntityAction extends
        ModuleListener<NoHunger, PacketEvent.Send<ClientCommandC2SPacket>>
{
    public ListenerEntityAction(NoHunger module)
    {
        super(module, PacketEvent.Send.class, ClientCommandC2SPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Send<ClientCommandC2SPacket> event)
    {
        if (module.sprint.getValue())
        {
            ClientCommandC2SPacket p = event.getPacket();
            if (p.getMode() == ClientCommandC2SPacket.Mode.START_SPRINTING
                    || p.getMode() ==
                    ClientCommandC2SPacket.Mode.STOP_SPRINTING)
            {
                event.setCancelled(true);
            }
        }
    }

}