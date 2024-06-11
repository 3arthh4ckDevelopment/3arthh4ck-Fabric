package me.earth.earthhack.impl.hud.visual.model;

import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import net.minecraft.client.gui.DrawContext;

public class Model extends HudElement {

    private final Setting<Integer> modelScale =
            register(new NumberSetting<>("ModelScale", 40, 10, 100));

    protected void onRender(DrawContext context) {
        Render2DUtil.drawPlayer(context, mc.player, modelScale.getValue(), (int) getX(), (int) getY(), (int) getWidth(), (int) getHeight());
    }

    public Model() {
        super("Model", "Displays your playermodel.", HudCategory.Visual, 400, 300);
    }

    @Override
    public float getWidth() {
        return 1.0f * modelScale.getValue();
    }

    @Override
    public float getHeight() {
        return 2.0f * modelScale.getValue();
    }
}
