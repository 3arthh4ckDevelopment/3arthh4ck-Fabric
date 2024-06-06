package me.earth.earthhack.impl.core.mixins.entity;

import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndCrystalEntity.class)
public abstract class MixinEndCrystalEntity extends MixinEntity
{
    @Inject(
            method = "<init>(Lnet/minecraft/world/World;DDD)V",
            at = @At("RETURN"))
    public void initHook(World worldIn,
                         double x,
                         double y,
                         double z,
                         CallbackInfo ci)
    {
        // Since PrevPos x, y, z are 0 in the beginning interpolation
        // can make it look like crystals get teleported on the ESP
        this.prevX = x;
        this.prevY = y;
        this.prevZ = z;
        this.lastRenderX = x;
        this.lastRenderY = y;
        this.lastRenderZ = z;
    }
}
