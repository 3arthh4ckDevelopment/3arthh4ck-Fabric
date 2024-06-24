package me.earth.earthhack.impl.core.mixins.render;

import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.client.render.Frustum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Frustum.class)
public abstract class MixinFrustum {

    @Inject(method = "<init>(Lnet/minecraft/client/render/Frustum;)V",
            at = @At("RETURN"))
    private void ctrHook(Frustum frustum, CallbackInfo ci) {
        RenderUtil.FRUSTUM = frustum;
    }
}
