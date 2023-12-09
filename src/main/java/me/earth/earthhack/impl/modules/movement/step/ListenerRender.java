package me.earth.earthhack.impl.modules.movement.step;

// import me.earth.earthhack.impl.event.events.render.Render3DEvent;
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
                BlockState state = mc.world.getBlockState(off);
                if (state.getMaterial().blocksMovement()
                    && state.getCollisionShape(mc.world, off)
                        == Block.FULL_BLOCK_AABB)
                {
                    if (esp == StepESP.Bad)
                    {
                        module.renderPos(off);
                    }

                    continue;
                }

                BlockState up = mc.world.getBlockState(off.up());
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