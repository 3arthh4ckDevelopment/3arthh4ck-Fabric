package me.earth.earthhack.impl.core.mixins.render;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.render.cameraclip.CameraClip;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public class MixinCamera {

    @Unique
    private static final ModuleCache<CameraClip> CAMERA_CLIP =
            Caches.getModule(CameraClip.class);

    @Inject(
            method = "clipToSpace",
            at = @At("HEAD"),
            cancellable = true
    )
    private void clipToSpace(double desiredCameraDistance, CallbackInfoReturnable<Double> info) {
        info.setReturnValue(CAMERA_CLIP.isEnabled() && CAMERA_CLIP.get().extend.getValue()
                ? CAMERA_CLIP.get().distance.getValue()
                : desiredCameraDistance);
    }
}
