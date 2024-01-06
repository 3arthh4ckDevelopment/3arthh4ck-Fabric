package me.earth.earthhack.impl.modules.player.fastplace;

import me.earth.earthhack.impl.event.events.network.PacketEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.minecraft.blocks.SpecialBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.math.BlockPos;

final class ListenerUseOnBlock extends
     ModuleListener<FastPlace, PacketEvent.Send<PlayerInteractBlockC2SPacket>>
{
    public ListenerUseOnBlock(FastPlace module)
    {
        super(module,
                PacketEvent.Send.class,
                PlayerInteractBlockC2SPacket.class);
    }

    @Override
    public void invoke(PacketEvent.Send<PlayerInteractBlockC2SPacket> event)
    {
        if (module.bypass.getValue()
                && mc.player.getStackInHand(event.getPacket().getHand())
                            .getItem() == Items.EXPERIENCE_BOTTLE
            || module.foodBypass.getValue()
                && mc.player.getStackInHand(event.getPacket().getHand())
                            .getItem().isFood())
        {
            if (Managers.ACTION.isSneaking()
                || module.bypassContainers.getValue())
            {
                event.setCancelled(true);
            }
            else
            {
                BlockPos pos = event.getPacket().getBlockHitResult().getBlockPos();
                BlockState state = mc.world.getBlockState(pos);
                if (!SpecialBlocks.BAD_BLOCKS.contains(state.getBlock())
                    && !SpecialBlocks.SHULKERS.contains(state.getBlock()))
                {
                    event.setCancelled(true);
                }
            }
        }
    }

}
