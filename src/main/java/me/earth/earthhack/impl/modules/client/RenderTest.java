package me.earth.earthhack.impl.modules.client;

import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.event.events.render.Render2DEvent;
import me.earth.earthhack.impl.event.listeners.LambdaListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.managers.render.TextRenderer;
import me.earth.earthhack.impl.util.helpers.disabling.DisablingModule;

import java.awt.*;

public class RenderTest extends DisablingModule {

    private final Setting<Color> color =
            register(new ColorSetting("Color", new Color(255, 0, 0, 255)));
    private final Setting<Integer> width =
            register(new NumberSetting<>("Width", 1, 0, 500));
    private final Setting<Integer> height =
            register(new NumberSetting<>("Height", 1, 0, 500));

    public RenderTest() {
        super("XRENDER TEST", Category.Client);

//        this.listeners.add(new LambdaListener<>(Render3DEvent.class, event -> {
//            if (mc.world == null || mc.player == null) return;
//
//            BlockPos pos = new BlockPos(mc.player.getBlockPos().down());
//            Box bb = Interpolation.interpolatePos(pos, 1);
//
//            RenderUtil.drawOutline(event.getStack(), bb, width.getValue(), color.getValue());
//        }));

        this.listeners.add(new LambdaListener<>(Render2DEvent.class, e -> {
            if (mc.world != null && mc.player != null) {
                if (Managers.TEXT.usingCustomFont()) {
                    TextRenderer.FONTS.drawScissor(50, 50, width.getValue(), height.getValue());
                }
            }
        }));
    }
}
