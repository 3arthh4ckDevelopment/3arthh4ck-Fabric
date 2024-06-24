package me.earth.earthhack.impl.core.mixins.entity.living.player;

import me.earth.earthhack.impl.core.ducks.entity.IPlayerEntity;
import me.earth.earthhack.impl.core.mixins.entity.living.MixinLivingEntity;
import me.earth.earthhack.impl.util.minecraft.MotionTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends MixinLivingEntity implements IPlayerEntity {

    // private static final ModuleCache<TpsSync> TPS_SYNC =
    //         Caches.getModule(TpsSync.class);
    // private static final SettingCache<Boolean, BooleanSetting, TpsSync> ATTACK =
    //         Caches.getSetting(TpsSync.class, BooleanSetting.class, "Attack", false);

    @Shadow
    public void tick()
    {
        throw new IllegalStateException("onUpdate was not shadowed!");
    }

    @Shadow @Final public PlayerInventory inventory;
    @Unique private MotionTracker motionTracker;
    @Unique private MotionTracker breakMotionTracker;
    @Unique private MotionTracker blockMotionTracker;
    @Unique private int ticksWithoutMotionUpdate;

    @Override
    public void earthhack$setMotionTracker(MotionTracker motionTracker) {
        this.motionTracker = motionTracker;
    }

    @Override
    public MotionTracker earthhack$getMotionTracker() {
        return motionTracker;
    }

    @Override
    public MotionTracker earthhack$getBreakMotionTracker() {
        return breakMotionTracker;
    }

    @Override
    public void earthhack$setBreakMotionTracker(MotionTracker breakMotionTracker) {
        this.breakMotionTracker = breakMotionTracker;
    }

    @Override
    public MotionTracker earthhack$getBlockMotionTracker() {
        return blockMotionTracker;
    }

    @Override
    public void earthhack$setBlockMotionTracker(MotionTracker blockMotionTracker) {
        this.blockMotionTracker = blockMotionTracker;
    }

    @Override
    public int earthhack$getTicksWithoutMotionUpdate() {
        return ticksWithoutMotionUpdate;
    }

    @Override
    public void earthhack$setTicksWithoutMotionUpdate(int ticksWithoutMotionUpdate) {
        this.ticksWithoutMotionUpdate = ticksWithoutMotionUpdate;
    }
}
