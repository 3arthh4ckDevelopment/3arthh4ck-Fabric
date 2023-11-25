package me.earth.earthhack.impl.modules.movement.step;

// import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
/*
final class ListenerRender extends ModuleListener<Step, Render3DEvent>
{
    public ListenerRender(Step module)
    {
        super(module, Render3DEvent.class);
    }

    @Override
    public void invoke(Render3DEvent event)
    {
        StepESP esp = module.esp.getValue();
        if (esp != StepESP.None)
        {
            BlockPos pos = PositionUtil.getPosition(mc.player, 1.0);
            BlockPos up2 = pos.up(2);
            if (mc.world.getBlockState(up2).getMaterial().blocksMovement())
            {
                if (esp == StepESP.Good)
                {
                    return;
                }

                module.renderPos(up2);
            }

            for (Direction facing : Direction.HORIZONTAL)
            {
                BlockPos off = pos.offset(facing);
                if (!mc.world.getBlockState(off).getMaterial().blocksMovement())
                {
                    continue;
                }

                off = off.up();
                IBlockState state = mc.world.getBlockState(off);
                if (state.getMaterial().blocksMovement()
                    && state.getBoundingBox(mc.world, off)
                        == Block.FULL_BLOCK_AABB)
                {
                    if (esp == StepESP.Bad)
                    {
                        module.renderPos(off);
                    }

                    continue;
                }

                IBlockState up = mc.world.getBlockState(off.up());
                if (up.getMaterial().blocksMovement())
                {
                    if (esp == StepESP.Bad)
                    {
                        module.renderPos(off);
                    }

                    continue;
                }

                if (esp == StepESP.Good)
                {
                    module.renderPos(off);
                }
            }
        }
    }

}
*/