package me.earth.earthhack.impl.modules.misc.tracker;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.math.BlockPos;

final class ListenerUseItemOnBlock extends
    ModuleListener<Tracker, PacketEvent.Post<PlayerInteractBlockC2SPacket>>
{
    public ListenerUseItemOnBlock(Tracker module)
    {
        super(module,
                PacketEvent.Post.class,
                PlayerInteractBlockC2SPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Post<PlayerInteractBlockC2SPacket> event)
    {
        PlayerInteractBlockC2SPacket packet = event.getPacket();
        if (mc.player.getStackInHand(packet.getHand())
                     .getItem() == Items.END_CRYSTAL)
        {
            BlockPos pos = packet.getBlockHitResult().getBlockPos();
            module.placed.add(new BlockPos((int) (pos.getX() + 0.5f),
                                           pos.getY() + 1,
                                            (int) (pos.getZ() + 0.5f)));
        }
    }

}
