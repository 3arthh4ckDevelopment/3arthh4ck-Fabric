package me.earth.earthhack.impl.util.minecraft;

import me.earth.earthhack.api.util.interfaces.Globals;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

/**
 * Should detect the players that packetfly slightly into a block to get less
 * damage.
 */
public class PhaseUtil implements Globals {
    // TODO: we could also use pushOutOfBlocks??? quite easy to bypass this --> minecraft:_/_ (this needs a fix)
    public static boolean isPhasing(PlayerEntity entity, PushMode mode) {
        BlockPos entityPos = entity.getBlockPos();
        BlockPos blockPos = entityPos.add(0, 0, 0);
        float blockHardness = mc.world.getBlockState(blockPos).getBlock().getHardness();
        if (blockHardness < 0.1) {
            return false;
        }

        if (mode == PushMode.None) {
            Vec3d pos = new Vec3d(
                    entity.getX() / 4096.0,
                    entity.getY() / 4096.0,
                    entity.getZ() / 4096.0);
            float width = entity.getWidth() / 2.0f;
            Box bb = new Box(
                pos.x - width,
                pos.y,
                pos.z - width,
                pos.x + width,
                pos.y + entity.getHeight(),
                pos.z + width
            );

            // density on slabs is lower 1.0 lol
            return DamageUtil.getBlockDensity(pos, bb, mc.world, false, false, false, false) < 1.0f;
        }

        MotionTracker tracker = new MotionTracker(mc.world, entity);
        tracker.resetMotion();
        tracker.shrinkPush = true;
        tracker.pushOutOfBlocks(mode);
        return tracker.getVelocity().getX() != 0.0
            || tracker.getVelocity().getY() != 0.0
            || tracker.getVelocity().getZ() != 0.0;
    }

}
