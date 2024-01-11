package me.earth.earthhack.impl.core.ducks.entity;

import me.earth.earthhack.impl.util.minecraft.MotionTracker;

/**
 * Duck interface for {@link net.minecraft.entity.player.PlayerEntity}
 */
public interface IPlayerEntity {
    void setMotionTracker(MotionTracker motionTracker);

    MotionTracker getMotionTracker();

    void setBreakMotionTracker(MotionTracker motionTracker);

    MotionTracker getBreakMotionTracker();

    void setBlockMotionTracker(MotionTracker motionTracker);

    MotionTracker getBlockMotionTracker();

    int getTicksWithoutMotionUpdate();

    void setTicksWithoutMotionUpdate(int ticksWithoutMotionUpdate);

}
