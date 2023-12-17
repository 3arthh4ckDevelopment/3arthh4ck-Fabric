package me.earth.earthhack.impl.modules.misc.pingspoof;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.client.pingbypass.PingBypassModule;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;

final class ListenerClickSlot extends
        ModuleListener<PingSpoof, PacketEvent.Post<ClickSlotC2SPacket>>
{
    private static final ModuleCache<PingBypassModule> PINGBYPASS =
            Caches.getModule(PingBypassModule.class);

    public ListenerClickSlot(PingSpoof module)
    {
        super(module, PacketEvent.Post.class, ClickSlotC2SPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Post<ClickSlotC2SPacket> event)
    {
        if (module.transactions.getValue() && !PINGBYPASS.isEnabled())
        {
            module.transactionIDs.add((short) event.getPacket().getActionType().ordinal());
        }
    }

}
