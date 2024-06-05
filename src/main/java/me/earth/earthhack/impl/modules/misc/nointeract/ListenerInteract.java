package me.earth.earthhack.impl.modules.misc.nointeract;

import me.earth.earthhack.impl.event.events.misc.ClickBlockEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;

final class ListenerInteract extends
        ModuleListener<NoInteract, ClickBlockEvent.Right>
{
    public ListenerInteract(NoInteract module)
    {
        super(module, ClickBlockEvent.Right.class);
    }

    @Override
    public void invoke(ClickBlockEvent.Right event)
    {
        if (module.sneak.getValue() && Managers.ACTION.isSneaking())
        {
            return;
        }

        BlockState state = mc.world.getBlockState(event.getPos());

        if (module.tileOnly.getValue()
                && state.getBlock().getClass().isAssignableFrom(BlockEntityProvider.class)
                || state.getBlock() instanceof AnvilBlock)
            // Extra check for anvils because they categorize as FallingBlocks instead of BlockEntities
        {
            event.setCancelled(true);
        }
        else
        {
            if (module.isValid(state.getBlock().getName().getString()))
            {
                event.setCancelled(true);
            }
        }
    }

}
