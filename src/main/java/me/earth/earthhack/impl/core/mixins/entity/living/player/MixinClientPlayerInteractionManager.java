package me.earth.earthhack.impl.core.mixins.entity.living.player;

import me.earth.earthhack.impl.core.ducks.network.IClientPlayerInteractionManager;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class MixinClientPlayerInteractionManager implements IClientPlayerInteractionManager {

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
}
