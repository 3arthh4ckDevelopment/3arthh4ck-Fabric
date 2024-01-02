package me.earth.earthhack.impl.modules.misc.announcer;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.misc.announcer.util.AnnouncementType;
import net.minecraft.block.Block;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.text.Text;

final class ListenerDigging extends
        ModuleListener<Announcer, PacketEvent.Post<PlayerActionC2SPacket>>
{
    public ListenerDigging(Announcer module)
    {
        super(module, PacketEvent.Post.class, PlayerActionC2SPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Post<PlayerActionC2SPacket> event)
    {
        if (module.mine.getValue())
        {
            PlayerActionC2SPacket p = event.getPacket();
            if (p.getAction() == PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK)
            {
                Block block = mc.world.getBlockState(p.getPos())
                                      .getBlock();

                module.addWordAndIncrement(AnnouncementType.Mine,
                        Text.translatable(block.getTranslationKey()).getString());
            }
        }
    }

}
