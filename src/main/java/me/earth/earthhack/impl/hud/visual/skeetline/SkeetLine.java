package me.earth.earthhack.impl.hud.visual.skeetline;

import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.NumberSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.client.SimpleHudData;
import me.earth.earthhack.impl.util.render.Render2DUtil;

import java.awt.*;

public class SkeetLine extends HudElement {

    protected final Setting<Boolean> skeetLineGradient =
            register(new BooleanSetting("SkeetGradient", false));
    protected final Setting<Color> skeetLineColor =
            register(new ColorSetting("SkeetColor", new Color(0x7817ff)));
    protected final Setting<Color> skeetLineColorGradient =
            register(new ColorSetting("SkeetColorGradient", new Color(0x7817ff)));
    protected final Setting<Float> skeetLineWidth =
            register(new NumberSetting<>("SkeetLineWidth", 1.3f, 0.5f, 3.0f));

    private void render() {
        if (skeetLineGradient.getValue()) {
            Render2DUtil.drawGradientRect(getContext().getMatrices(), 0, 0, Render2DUtil.getScreenWidth(), skeetLineWidth.getValue(), true, skeetLineColor.getValue().getRGB(), skeetLineColorGradient.getValue().getRGB());
        } else {
            Render2DUtil.drawRect(getContext().getMatrices(), 0, 0, Render2DUtil.getScreenWidth(), skeetLineWidth.getValue(), skeetLineColor.getValue().getRGB());
        }
    }

    public SkeetLine() {
        super("SkeetLine", HudCategory.Visual, -50, -100);
        this.setData(new SimpleHudData(this, "Displays the SkeetLine."));
    }

    @Override
    public void guiDraw(int mouseX, int mouseY, float partialTicks) {
        super.guiDraw(mouseX, mouseY, partialTicks);
        render();
    }

    @Override
    public void hudDraw(float partialTicks) {
        render();
    }

    @Override
    public void guiUpdate(int mouseX, int mouseY, float partialTicks) {
        super.guiUpdate(mouseX, mouseY, partialTicks);
        setWidth(getWidth());
        setHeight(Managers.TEXT.getStringHeight());
    }

    @Override
    public void hudUpdate(float partialTicks) {
        super.hudUpdate(partialTicks);
        setWidth(getWidth());
        setHeight(getHeight());
    }

    @Override
    public float getWidth() {
        return 0;
    }

    @Override
    public float getHeight() {
        return 0;
    }

}
