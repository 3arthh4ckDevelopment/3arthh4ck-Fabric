package me.earth.earthhack.impl.core.mixins.entity.living.player;

import me.earth.earthhack.impl.core.ducks.entity.IPlayerEntity;
import me.earth.earthhack.impl.util.minecraft.MotionTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity implements IPlayerEntity {

    // private static final ModuleCache<TpsSync> TPS_SYNC =
    //         Caches.getModule(TpsSync.class);
    // private static final SettingCache<Boolean, BooleanSetting, TpsSync> ATTACK =
    //         Caches.getSetting(TpsSync.class, BooleanSetting.class, "Attack", false);

    @Shadow
    public void tick()
    {
        throw new IllegalStateException("onUpdate was not shadowed!");
    }

    @Shadow
    public PlayerInventory inventory;
    @Unique
    private MotionTracker motionTracker;
    @Unique
    private MotionTracker breakMotionTracker;
    @Unique
    private MotionTracker blockMotionTracker;
    @Unique
    private int ticksWithoutMotionUpdate;

    @Override
    public void setMotionTracker(MotionTracker motionTracker) {
        this.motionTracker = motionTracker;
    }

    @Override
    public MotionTracker getMotionTracker() {
        return motionTracker;
    }

    @Override
    public MotionTracker getBreakMotionTracker() {
        return breakMotionTracker;
    }

    @Override
    public void setBreakMotionTracker(MotionTracker breakMotionTracker) {
        this.breakMotionTracker = breakMotionTracker;
    }

    @Override
    public MotionTracker getBlockMotionTracker() {
        return blockMotionTracker;
    }

    @Override
    public void setBlockMotionTracker(MotionTracker blockMotionTracker) {
        this.blockMotionTracker = blockMotionTracker;
    }

    @Override
    public int getTicksWithoutMotionUpdate() {
        return ticksWithoutMotionUpdate;
    }

    @Override
    public void setTicksWithoutMotionUpdate(int ticksWithoutMotionUpdate) {
        this.ticksWithoutMotionUpdate = ticksWithoutMotionUpdate;
    }

}
