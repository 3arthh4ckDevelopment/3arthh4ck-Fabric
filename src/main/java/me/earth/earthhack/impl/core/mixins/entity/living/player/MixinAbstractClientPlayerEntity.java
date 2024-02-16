package me.earth.earthhack.impl.core.mixins.entity.living.player;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class MixinAbstractClientPlayerEntity extends MixinPlayerEntity
{
    @Shadow public abstract boolean isSpectator();

    @Shadow
    @Nullable
    protected abstract PlayerListEntry getPlayerListEntry();

    // private static final ModuleCache<NoRender>
    //         NO_RENDER = Caches.getModule(NoRender.class);
    // private static final ModuleCache<Capes>
    //         CAPES = Caches.getModule(Capes.class);


}
