package me.earth.earthhack.impl.core.ducks.entity;

import me.earth.earthhack.impl.util.minecraft.MotionTracker;

/**
 * Duck interface for {@link net.minecraft.entity.player.PlayerEntity}
 */
public interface IPlayerEntity {
    void earthhack$setMotionTracker(MotionTracker motionTracker);

    MotionTracker earthhack$getMotionTracker();

    void earthhack$setBreakMotionTracker(MotionTracker motionTracker);

    MotionTracker earthhack$getBreakMotionTracker();

    void earthhack$setBlockMotionTracker(MotionTracker motionTracker);

    MotionTracker earthhack$getBlockMotionTracker();

    int earthhack$getTicksWithoutMotionUpdate();

    void earthhack$setTicksWithoutMotionUpdate(int ticksWithoutMotionUpdate);

}
