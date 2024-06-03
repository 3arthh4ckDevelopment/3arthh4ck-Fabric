package me.earth.earthhack.impl.modules.render.cameraclip;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.core.mixins.render.MixinCamera;
import me.earth.earthhack.impl.util.client.SimpleData;

/**
 * {@link MixinCamera}
 */
public class CameraClip extends Module
{
    public CameraClip() {
        super("CameraClip", Category.Render);
        register(new BooleanSetting("Extend", false));
        register(new NumberSetting<>("Distance", 10.0, 0.0, 50.0));
        this.setData(new SimpleData(this, "Makes the camera clip through " +
                "blocks in F5."));
    }
}
