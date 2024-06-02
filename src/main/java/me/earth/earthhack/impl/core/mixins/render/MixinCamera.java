package me.earth.earthhack.impl.core.mixins.render;

import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import me.earth.earthhack.impl.modules.render.cameraclip.CameraClip;

@Mixin(Camera.class)
public class MixinCamera {

    @Inject(
            method = "clipToSpace",
            at = @At("HEAD"),
            cancellable = true
    )
    private void clipToSpace(double desiredCameraDistance, CallbackInfoReturnable<Double> info) {
        if (CameraClip.INSTANCE != null && CameraClip.INSTANCE.isEnabled()) {
            info.setReturnValue((double) CameraClip.INSTANCE.getDistance());
        }
    }
}
