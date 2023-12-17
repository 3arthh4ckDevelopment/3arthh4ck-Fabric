package me.earth.earthhack.impl.modules.misc.pingspoof;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassModule;
import net.minecraft.network.packet.c2s.play.PlayPongC2SPacket;

final class ListenerTransaction extends
        ModuleListener<PingSpoof, PacketEvent.Send<PlayPongC2SPacket>>
{
    private static final ModuleCache<PingBypassModule> PINGBYPASS =
            Caches.getModule(PingBypassModule.class);

    public ListenerTransaction(PingSpoof module)
    {
        super(module, PacketEvent.Send.class, PlayPongC2SPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Send<PlayPongC2SPacket> event)
    {
        if (!PINGBYPASS.isEnabled() && module.transactions.getValue())
        {
            if (module.transactionIDs.remove((short) event.getPacket().getParameter()))
            {
                return;
            }

            module.onPacket(event.getPacket());
            event.setCancelled(true);
        }
    }

}
