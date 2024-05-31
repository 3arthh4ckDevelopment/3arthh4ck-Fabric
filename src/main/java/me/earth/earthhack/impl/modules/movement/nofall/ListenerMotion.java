package me.earth.earthhack.impl.modules.movement.nofall;

import me.earth.earthhack.api.event.events.Stage;
import me.earth.earthhack.impl.event.events.network.MotionUpdateEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.movement.nofall.mode.FallMode;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import me.earth.earthhack.impl.util.minecraft.InventoryUtil;
import me.earth.earthhack.impl.util.thread.Locks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

final class ListenerMotion extends ModuleListener<NoFall, MotionUpdateEvent>
{
    public ListenerMotion(NoFall module)
    {
        super(module, MotionUpdateEvent.class);
    }

    @Override
    public void invoke(MotionUpdateEvent event)
    {
        if (module.mode.getValue() == FallMode.Bucket)
        {
            int slot = InventoryUtil.findHotbarItem(Items.WATER_BUCKET);
            if (slot != -1)
            {
                Vec3d positionVector = mc.player.getPos();
                BlockHitResult rayTraceBlocks =
                        mc.world.raycast(
                                new RaycastContext(positionVector,
                                        new Vec3d(positionVector.x, positionVector.y - 3.0, positionVector.z),
                                        RaycastContext.ShapeType.COLLIDER,
                                        RaycastContext.FluidHandling.NONE,
                                        ShapeContext.absent()));

                if (mc.player.fallDistance < 5.0f
                        || rayTraceBlocks == null
                        || rayTraceBlocks.getType() != HitResult.Type.BLOCK
                        || mc.world.getBlockState(
                                rayTraceBlocks.getBlockPos()).getBlock() instanceof FluidBlock
                        || PositionUtil.inLiquid()
                        || PositionUtil.inLiquid(false))
                {
                    return;
                }

                if (event.getStage() == Stage.PRE)
                {
                    event.setPitch(90.0f);
                }
                else
                {
                    BlockHitResult rayTraceBlocks2 =
                            mc.world.raycast(
                                    new RaycastContext(positionVector,
                                            new Vec3d(positionVector.x, positionVector.y - 5.0, positionVector.z),
                                            RaycastContext.ShapeType.COLLIDER,
                                            RaycastContext.FluidHandling.NONE,
                                            ShapeContext.absent()));

                    if (rayTraceBlocks2 != null
                            && rayTraceBlocks2.getType() ==
                                                    HitResult.Type.BLOCK
                            && !(mc.world.getBlockState(
                                    rayTraceBlocks2.getBlockPos()).getBlock()
                                                        instanceof FluidBlock)
                            && module.timer.passed(1000))
                    {
                        Locks.acquire(Locks.PLACE_SWITCH_LOCK, () ->
                        {
                            InventoryUtil.switchTo(slot);
                            mc.interactionManager
                                    .interactItem(mc.player,
                                                 slot == -2
                                                    ? Hand.OFF_HAND
                                                    : Hand.MAIN_HAND);
                        });

                        module.timer.reset();
                    }
                }
            }
        }
    }

}
