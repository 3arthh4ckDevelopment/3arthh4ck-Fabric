package me.earth.earthhack.impl.modules.movement.jesus;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

final class ListenerMotion extends ModuleListener<Jesus, MotionUpdateEvent>
{
    public ListenerMotion(Jesus module)
    {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        Entity entity = PositionUtil.getPositionEntity();
        if (entity == null
                || !entity.isAlive()
                || entity.isSneaking()
                || !module.timer.passed(800))
        {
            return;
        }

        switch (module.mode.getValue())
        {
            case Dolphin:
                if (PositionUtil.inLiquid()
                        && entity.fallDistance < 3.0f
                        && !entity.isSneaking())
                {
                    entity.setVelocity(entity.getVelocity().x, 0.1, entity.getVelocity().z);
                }

                return;
            case Trampoline:
                if (event.getStage() == Stage.PRE)
                {
                    if (PositionUtil.inLiquid(false) && !entity.isSneaking())
                    {
                        entity.onGround = false;
                    }

                    Block block =
                            mc.world.getBlockState(new BlockPos((int) entity.getX(),
                                            (int) entity.getY(),
                                            (int) entity.getZ()))
                                    .getBlock();

                    if (module.jumped
                            && !mc.player.getAbilities().flying
                            && !entity.isTouchingWater())
                    {
                        if (entity.getVelocity().getY() < -0.3
                                || entity.onGround
                                || mc.player.isHoldingOntoLadder())
                        {
                            module.jumped = false;
                            return;
                        }

                        entity.setVelocity(entity.getVelocity().x, entity.getVelocity().getY() / 0.9800000190734863 + 0.08, entity.getVelocity().z);
                        entity.setVelocity(entity.getVelocity().x, entity.getVelocity().getY() - 0.03120000000005, entity.getVelocity().z);
                    }

                    // TODO: better way is to check if block below is liquid / easy fix / but for now just port
                    if (entity.isTouchingWater() || entity.isInLava())
                    {
                        entity.setVelocity(entity.getVelocity().x, 0.1, entity.getVelocity().z);
                        break;
                    }

                    if (!entity.isInLava()
                            && block.equals(Blocks.WATER)
                            && entity.getVelocity().getY() < 0.2)
                    {
                        entity.setVelocity(entity.getVelocity().x, 0.5, entity.getVelocity().z);
                        module.jumped = true;
                    }
                }

                break;
            default:
        }

        if (event.getStage() == Stage.PRE
                && !PositionUtil.inLiquid()
                && PositionUtil.inLiquid(true)
                && !PositionUtil.isMovementBlocked())
        {
            event.setY(event.getY() + 0.02);
        }
    }

}