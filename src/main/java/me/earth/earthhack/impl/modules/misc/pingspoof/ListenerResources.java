package me.earth.earthhack.impl.modules.misc.pingspoof;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassModule;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;

final class ListenerResources
        extends ModuleListener<PingSpoof,
            PacketEvent.Send<ResourcePackStatusC2SPacket>>
{
    private static final ModuleCache<PingBypassModule> PINGBYPASS =
            Caches.getModule(PingBypassModule.class);

    public ListenerResources(PingSpoof module)
    {
        super(module, PacketEvent.Send.class, ResourcePackStatusC2SPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Send<ResourcePackStatusC2SPacket> event)
    {
        if (!PINGBYPASS.isEnabled() && module.resources.getValue())
        {
            module.onPacket(event.getPacket());
            event.setCancelled(true);
        }
    }

}
