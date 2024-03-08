package me.earth.earthhack.impl.modules.client;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.render.Render3DEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.util.helpers.disabling.DisablingModule;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.RenderUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.awt.*;

public class RenderTest extends DisablingModule {

    private final Setting<Color> color =
            register(new ColorSetting("Color", new Color(255, 0, 0, 255)));
    private final Setting<Integer> width =
            register(new NumberSetting<>("Width", 1, 0, 10));

    public RenderTest() {
        super("XRENDER TEST", Category.Client);

        this.listeners.add(new LambdaListener<>(Render3DEvent.class, event -> {
            if (mc.world == null || mc.player == null) return;

            BlockPos pos = new BlockPos(mc.player.getBlockPos().down());
            Box bb = Interpolation.interpolatePos(pos, 1);

            RenderUtil.drawOutline(event.getStack(), bb, width.getValue(), color.getValue());
        }));
    }
}
