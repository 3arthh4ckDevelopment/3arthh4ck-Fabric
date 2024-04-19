package me.earth.earthhack.impl.hud.impl.text.watermark;

import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.client.SimpleHudData;
import me.earth.earthhack.impl.util.render.hud.HudRenderUtil;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;

public class Watermark extends HudElement {

    protected final Setting<String> logoText =
            register(new StringSetting("TextLogo", Earthhack.NAME));
    private final Setting<Boolean> showVersion =
            register(new BooleanSetting("Version", true));
    private final Setting<Boolean> sync =
            register(new BooleanSetting("RainbowSync", true));
    private final Setting<Color> color =
            register(new ColorSetting("Color", new Color(33, 150, 243)));

    private String text = "";

    private void render(DrawContext context) {
        text = logoText.getValue() + (showVersion.getValue() ? " - " + Earthhack.VERSION : "");
        if (sync.getValue())
            HudRenderUtil.renderText(context, text, getX(), getY());
        else
            Managers.TEXT.drawStringWithShadow(context, text, getX(), getY(), color.getValue().getRGB());
    }

    public Watermark() {
        super("Watermark", HudCategory.Text, 2, 2);
        this.setData(new SimpleHudData(this, "Displays a watermark."));
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
        setHeight(getHeight());
    }

    @Override
    public void update() {
        super.update();
        setWidth(getWidth());
        setHeight(getHeight());
    }

    @Override
    public float getWidth() {
        return Managers.TEXT.getStringWidth(text);
    }

    @Override
    public float getHeight() {
        return Managers.TEXT.getStringHeight();
    }

}