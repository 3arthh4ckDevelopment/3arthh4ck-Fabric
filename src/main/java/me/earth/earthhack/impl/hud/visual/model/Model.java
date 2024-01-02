package me.earth.earthhack.impl.hud.visual.model;

import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.util.client.SimpleHudData;

public class Model extends HudElement {

    protected final Setting<Float> modelScale =
            register(new NumberSetting<>("ModelScale", 0.8f, 0.4f, 2.0f));

    private void render() {
        // TODO: this
        //Render2DUtil.drawPlayer(mc.player, modelScale.getValue(), getX() + getWidth() / 2, getY() + getHeight());
    }

    public Model() {
        super("Model",  HudCategory.Visual, 400, 300);
        this.setData(new SimpleHudData(this, "Displays your playermodel."));
    }

    @Override
    public void guiDraw(int mouseX, int mouseY, float partialTicks) {
        super.guiDraw(mouseX, mouseY, partialTicks);
        render();
    }

    @Override
    public void guiUpdate(int mouseX, int mouseY, float partialTicks) {
        super.guiUpdate(mouseX, mouseY, partialTicks);
        setWidth(getWidth());
        setHeight(getHeight());
    }

    @Override
    public void hudUpdate(float partialTicks) {
        super.hudUpdate(partialTicks);
        setWidth(getWidth());
        setHeight(getHeight());
    }

    @Override
    public void hudDraw(float partialTicks) {
        render();
    }

    @Override
    public float getWidth() {
        return 66.0f * modelScale.getValue();
    }

    @Override
    public float getHeight() {
        return 90.0f * modelScale.getValue();
    }

}
