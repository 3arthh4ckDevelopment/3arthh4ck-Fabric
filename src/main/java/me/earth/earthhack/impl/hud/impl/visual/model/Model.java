package me.earth.earthhack.impl.hud.impl.visual.model;

import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.util.client.SimpleHudData;
import me.earth.earthhack.impl.util.render.Render2DUtil;
import net.minecraft.client.gui.DrawContext;

public class Model extends HudElement {

    private final Setting<Integer> modelScale =
            register(new NumberSetting<>("ModelScale", 30, 1, 80));

    private void render(DrawContext context) {
        Render2DUtil.drawPlayer(context, mc.player, modelScale.getValue(), (int) (getX() + getWidth() / 2), (int) (getY() + getHeight()));
    }

    public Model() {
        super("Model",  HudCategory.Visual, 400, 300);
        this.setData(new SimpleHudData(this, "Displays your playermodel."));
    }

    @Override
    public void guiDraw(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        super.guiDraw(context, mouseX, mouseY, partialTicks);
        render(context);
    }

    @Override
    public void guiUpdate(int mouseX, int mouseY) {
        super.guiUpdate(mouseX, mouseY);
        setWidth(getWidth());
        setHeight(getHeight());
    }

    @Override
    public void update() {
        super.update();
        setWidth(getWidth());
        setHeight(getHeight());
    }

    @Override
    public void draw(DrawContext context) {
        render(context);
    }

    @Override
    public float getWidth() {
        return modelScale.getValue();
    }

    @Override
    public float getHeight() {
        return modelScale.getValue();
    }

}
