package me.earth.earthhack.impl.modules.movement.entityspeed;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.event.events.misc.TickEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.movement.boatfly.BoatFly;
import me.earth.earthhack.impl.util.math.position.PositionUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

final class ListenerTick extends ModuleListener<EntitySpeed, TickEvent>
{
    private static final ModuleCache<BoatFly> BOAT_FLY =
            Caches.getModule(BoatFly.class);

    public ListenerTick(EntitySpeed module)
    {
        super(module, TickEvent.class);
    }

    @Override
    public void invoke(TickEvent event)
    {
        if (!event.isSafe())
        {
            return;
        }

        Entity riding = mc.player.getVehicle();
        if (riding == null)
        {
            return;
        }

        double cosYaw =
                Math.cos(Math.toRadians(mc.player.yaw + 90.0f));
        double sinYaw =
                Math.sin(Math.toRadians(mc.player.yaw + 90.0f));

        BlockPos pos   = new BlockPos((int) (mc.player.getX() + 2.0 * cosYaw + 0.0 * sinYaw),
                (int) mc.player.getY(),
                (int) (mc.player.getZ() + (2.0 * sinYaw - 0.0 * cosYaw)));
        BlockPos down  = new BlockPos((int) (mc.player.getX() + 2.0 * cosYaw + 0.0 * sinYaw),
                (int) (mc.player.getY() - 1.0),
                (int) (mc.player.getZ() + (2.0 * sinYaw - 0.0 * cosYaw)));
        if (!riding.onGround
                && !mc.world.getBlockState(pos).blocksMovement()
                && !mc.world.getBlockState(down).blocksMovement()
                && module.noStuck.getValue())
        {
            EntitySpeed.strafe(0.0);
            module.stuckTimer.reset();
            return;
        }

        pos = new BlockPos((int) (mc.player.getX() + 2.0 * cosYaw + 0.0 * sinYaw),
                (int) mc.player.getY(),
                (int) (mc.player.getZ() + (2.0 * sinYaw - 0.0 * cosYaw)));
        if (mc.world.getBlockState(pos).blocksMovement()
                && module.noStuck.getValue())
        {
            EntitySpeed.strafe(0.0);
            module.stuckTimer.reset();
            return;
        }

        pos = new BlockPos((int) (mc.player.getX() + cosYaw + 0.0 * sinYaw),
                (int) (mc.player.getY() + 1.0),
                (int) (mc.player.getZ() + (sinYaw - 0.0 * cosYaw)));
        if (mc.world.getBlockState(pos).blocksMovement()
                && module.noStuck.getValue())
        {
            EntitySpeed.strafe(0.0);
            module.stuckTimer.reset();
            return;
        }

        if (mc.player.input.jumping)
        {
            module.jumpTimer.reset();
        }

        if (module.stuckTimer.passed(module.stuckTime.getValue())
                || !module.noStuck.getValue())
        {
            if (!riding.isInFluid()
                    && !BOAT_FLY.isEnabled()
                    && !mc.player.input.jumping
                    && module.jumpTimer.passed(1000)
                    && !PositionUtil.inLiquid())
            {
                if (riding.onGround)
                {
                    riding.getVelocity().y = 0.4;
                }

                riding.getVelocity().y = -0.4;
            }

            EntitySpeed.strafe(module.speed.getValue());
            if (module.resetStuck.getValue())
            {
                module.stuckTimer.reset();
            }
        }
    }

}