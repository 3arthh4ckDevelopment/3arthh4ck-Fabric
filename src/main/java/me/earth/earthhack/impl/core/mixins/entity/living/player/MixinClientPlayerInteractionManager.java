package me.earth.earthhack.impl.core.mixins.entity.living.player;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.core.ducks.network.IClientPlayerInteractionManager;
import me.earth.earthhack.impl.event.events.misc.DamageBlockEvent;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class MixinClientPlayerInteractionManager implements IClientPlayerInteractionManager {

    @Shadow
    private float currentBreakingProgress;

    @Shadow
    private int blockBreakingCooldown;

    @Override
    @Invoker(value = "syncSelectedSlot")
    public abstract void syncItem();

    @Override
    @Accessor(value = "lastSelectedSlot")
    public abstract int getItem();

    @Override
    @Accessor(value = "blockBreakingCooldown")
    public abstract void setBlockHitDelay(int delay);

    @Override
    @Accessor(value = "blockBreakingCooldown")
    public abstract int getBlockHitDelay();

    @Override
    @Accessor(value = "currentBreakingProgress")
    public abstract float getCurBlockDamageMP();

    @Override
    @Accessor(value = "currentBreakingProgress")
    public abstract void setCurBlockDamageMP(float damage);

    @Override
    @Accessor(value = "breakingBlock")
    public abstract boolean getIsHittingBlock();

    @Override
    @Accessor(value = "breakingBlock")
    public abstract void setIsHittingBlock(boolean hitting);

    @Override
    @Accessor(value = "networkHandler")
    public abstract ClientPlayNetworkHandler getConnection();

    @Inject(
            method = "updateBlockBreakingProgress",
            at = @At("HEAD"),
            cancellable = true)
    public void onPlayerDamageBlock(BlockPos pos,
                                    Direction direction,
                                    CallbackInfoReturnable<Boolean> cir)
    {
        DamageBlockEvent event = new DamageBlockEvent(pos,
                direction,
                this.currentBreakingProgress,
                this.blockBreakingCooldown);
        Bus.EVENT_BUS.post(event);

        this.currentBreakingProgress = event.getDamage();
        this.blockBreakingCooldown = event.getDelay();

        if (event.isCancelled())
        {
            cir.setReturnValue(true);
        }
    }
}
