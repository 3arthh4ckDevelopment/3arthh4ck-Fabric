package me.earth.earthhack.impl.modules.misc.tracker;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;

final class ListenerUseItem extends
        ModuleListener<Tracker, PacketEvent.Post<PlayerInteractBlockC2SPacket>>
{
    public ListenerUseItem(Tracker module)
    {
        super(module, PacketEvent.Post.class, PlayerInteractBlockC2SPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Post<PlayerInteractBlockC2SPacket> event)
    {
        if (mc.player.getStackInHand(event.getPacket().getHand())
                     .getItem() == Items.EXPERIENCE_BOTTLE)
        {
            module.awaitingExp.incrementAndGet();
        }
    }

}
