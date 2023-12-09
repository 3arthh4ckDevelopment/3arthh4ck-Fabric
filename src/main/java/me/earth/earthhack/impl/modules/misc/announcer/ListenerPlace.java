package me.earth.earthhack.impl.modules.misc.announcer;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.announcer.util.AnnouncementType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.EndCrystalItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.text.Text;

final class ListenerPlace extends ModuleListener<Announcer,
        PacketEvent.Post<PlayerInteractBlockC2SPacket>>
{
    public ListenerPlace(Announcer module)
    {
        super(module,
                PacketEvent.Post.class,
                PlayerInteractBlockC2SPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Post<PlayerInteractBlockC2SPacket> event)
    {
        if (module.place.getValue())
        {

            PlayerInteractBlockC2SPacket packet = event.getPacket();
            ItemStack stack = mc.player.getStackInHand(packet.getHand());
            if (stack.getItem() instanceof BlockItem
                    || stack.getItem() instanceof EndCrystalItem)
            {
                module.addWordAndIncrement(AnnouncementType.Place,
                        Text.translatable(stack.getTranslationKey()).getString()); // simplify?
            }
        }
    }

}
