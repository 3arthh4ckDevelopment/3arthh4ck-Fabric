package me.earth.earthhack.impl.modules.player.automine;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;

final class ListenerPlace extends
    ModuleListener<AutoMine, PacketEvent.Post<PlayerInteractBlockC2SPacket>>
{
    public ListenerPlace(AutoMine module)
    {
        super(module,
                PacketEvent.Post.class,
                PlayerInteractBlockC2SPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Post<PlayerInteractBlockC2SPacket> event)
    {
        if (mc.player.getStackInHand(event.getPacket().getHand())
                     .getItem() == Items.END_CRYSTAL)
        {
            module.downTimer.reset();
        }
    }

}
