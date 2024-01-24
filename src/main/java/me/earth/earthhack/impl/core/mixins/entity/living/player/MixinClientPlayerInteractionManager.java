package me.earth.earthhack.impl.core.mixins.entity.living.player;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.earthhack.impl.core.ducks.network.IClientPlayerInteractionManager;
import me.earth.earthhack.impl.event.events.misc.ClickBlockEvent;
import me.earth.earthhack.impl.event.events.misc.DamageBlockEvent;
import me.earth.earthhack.impl.event.events.misc.ResetBlockEvent;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class MixinClientPlayerInteractionManager implements IClientPlayerInteractionManager {

    @Shadow
    private float currentBreakingProgress;

    @Shadow
    private int blockBreakingCooldown;

    @Override
    @Invoker(value = "syncSelectedSlot")
    public abstract void earthhack$syncItem();

    @Override
    @Accessor(value = "lastSelectedSlot")
    public abstract int earthhack$getItem();

    @Override
    @Accessor(value = "blockBreakingCooldown")
    public abstract void earthhack$setBlockHitDelay(int delay);

    @Override
    @Accessor(value = "blockBreakingCooldown")
    public abstract int earthhack$getBlockHitDelay();

    @Override
    @Accessor(value = "currentBreakingProgress")
    public abstract float earthhack$getCurBlockDamageMP();

    @Override
    @Accessor(value = "currentBreakingProgress")
    public abstract void earthhack$setCurBlockDamageMP(float damage);

    @Override
    @Accessor(value = "breakingBlock")
    public abstract boolean earthhack$getIsHittingBlock();

    @Override
    @Accessor(value = "breakingBlock")
    public abstract void earthhack$setIsHittingBlock(boolean hitting);

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

    @Inject(
            method = "attackBlock",
            at = @At(value = "HEAD"),
            cancellable = true)
    public void attackBlockHook(BlockPos pos,
                                Direction direction,
                                CallbackInfoReturnable<Boolean> cir)
    {
        ClickBlockEvent event = new ClickBlockEvent(pos, direction);
        Bus.EVENT_BUS.post(event);

        if (event.isCancelled())
        {
            cir.setReturnValue(true);
        }
    }

    @Inject(
            method = "cancelBlockBreaking",
            at = @At("HEAD"),
            cancellable = true)
    public void resetBlockRemovingHook(CallbackInfo info)
    {
        ResetBlockEvent event = new ResetBlockEvent();
        Bus.EVENT_BUS.post(event);

        if (event.isCancelled())
        {
            info.cancel();
        }
    }
}
