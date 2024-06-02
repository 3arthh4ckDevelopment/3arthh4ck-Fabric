package me.earth.earthhack.impl.modules.render.cameraclip;

import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
public class CameraClip extends Module {

    public static CameraClip INSTANCE;
    private static final int DEFAULT_DISTANCE = 4;

    public CameraClip() {
        super("CameraClip", Category.Render);
        INSTANCE = this;
    }

    public int getDistance() {
        return DEFAULT_DISTANCE;
    }
}
