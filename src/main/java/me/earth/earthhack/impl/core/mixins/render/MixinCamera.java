package me.earth.earthhack.impl.core.mixins.render;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.cache.SettingCache;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.render.cameraclip.CameraClip;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public abstract class MixinCamera {

    @Unique
    private static final ModuleCache<CameraClip> CAMERA_CLIP =
            Caches.getModule(CameraClip.class);
    @Unique
    private static final SettingCache<Boolean, BooleanSetting, CameraClip>
            EXTEND = Caches.getSetting(CameraClip.class,
            BooleanSetting.class,
            "Extend",
            false);
    @Unique
    private static final SettingCache<Double, NumberSetting<Double>, CameraClip>
            DISTANCE = Caches.getSetting(CameraClip.class,
            Setting.class,
            "Distance",
            10.0);

    @Inject(
            method = "clipToSpace",
            at = @At("HEAD"),
            cancellable = true
    )
    private void clipToSpace(double desiredCameraDistance, CallbackInfoReturnable<Double> info) {
        if (CAMERA_CLIP.isEnabled()) {
            info.setReturnValue(EXTEND.getValue()
                    ? DISTANCE.getValue()
                    : desiredCameraDistance);
        }
    }
}
