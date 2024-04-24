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
import net.minecraft.client.gui.DrawContext;

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

    private void render(DrawContext context) {
        if (skeetLineGradient.getValue()) {
            Render2DUtil.drawGradientRect(context.getMatrices(), 0, 0, Render2DUtil.getScreenWidth(), skeetLineWidth.getValue(), true, skeetLineColor.getValue().getRGB(), skeetLineColorGradient.getValue().getRGB());
        } else {
            Render2DUtil.drawRect(context.getMatrices(), 0, 0, Render2DUtil.getScreenWidth(), skeetLineWidth.getValue(), skeetLineColor.getValue().getRGB());
        }
    }

    public SkeetLine() {
        super("SkeetLine", HudCategory.Visual, -50, -100);
        this.setData(new SimpleHudData(this, "Displays the SkeetLine."));
    }

    @Override
    public void guiDraw(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        super.guiDraw(context, mouseX, mouseY, partialTicks);
        render(context);
    }

    @Override
    public void draw(DrawContext context) {
        render(context);
    }

    @Override
    public void guiUpdate(int mouseX, int mouseY) {
        super.guiUpdate(mouseX, mouseY);
        setWidth(getWidth());
        setHeight(Managers.TEXT.getStringHeight());
    }

    @Override
    public void update() {
        super.update();
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
