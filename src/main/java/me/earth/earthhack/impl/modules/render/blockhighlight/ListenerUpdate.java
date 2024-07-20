package me.earth.earthhack.impl.modules.render.blockhighlight;

import me.earth.earthhack.impl.event.events.misc.UpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.minecraft.entity.EntityNames;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;

final class ListenerUpdate extends ModuleListener<BlockHighlight, UpdateEvent>
{
    public ListenerUpdate(BlockHighlight module)
    {
        super(module, UpdateEvent.class);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void invoke(UpdateEvent event)
    {
        if (mc.crosshairTarget != null)
        {
            switch (mc.crosshairTarget.getType())
            {
                case BLOCK:
                    BlockPos pos = BlockPos.ofFloored(mc.crosshairTarget.getPos());
                    if (mc.world.getWorldBorder().contains(pos))
                    {
                        BlockState state = mc.world.getBlockState(pos);
                        if (state.isAir())
                        {
                            ItemStack stack = state
                                    .getBlock()
                                    .asItem().getDefaultStack();

                            module.current = stack.getItem().getName().getString();
                            return;
                        }
                    }
                    break;
                case ENTITY:
                    Entity entity = ((EntityHitResult) mc.crosshairTarget).getEntity();
                    if (entity != null)
                    {
                        module.current = EntityNames.getName(entity);
                        return;
                    }
                    break;
                default:
            }
        }

        module.current = null;
    }

}
