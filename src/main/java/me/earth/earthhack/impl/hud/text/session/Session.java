package me.earth.earthhack.impl.hud.text.session;

import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.client.SimpleHudData;
import me.earth.earthhack.impl.util.render.hud.HudRenderUtil;
import net.minecraft.client.gui.DrawContext;

public class Session extends HudElement {

    private final Setting<Boolean> seconds =
            register(new BooleanSetting("Show-Seconds", true));

    private void render(DrawContext context) {
        long time = System.currentTimeMillis() - Earthhack.startMS;
        long s = time / 1000;
        long m = s / 60;
        long h = m / 60;
        s %= 60;
        m %= 60;

        HudRenderUtil.renderText(context, ((h == 0 ? "" : h + ":") + (m < 10 ? "0" : "") + m + (seconds.getValue() ? ":" + (s < 10 ? "0" : "") + s : "")), getX(), getY());
    }

    public Session() {
        super("Session", HudCategory.Text, 300, 70);
        this.setData(new SimpleHudData(this, "Displays the time you've been playing."));
    }

    @Override
    public void guiDraw(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        super.guiDraw(context, mouseX, mouseY, partialTicks);
        render(context);
    }

    @Override
    public void hudDraw(DrawContext context) {
        render(context);
    }

    @Override
    public void guiUpdate(int mouseX, int mouseY) {
        super.guiUpdate(mouseX, mouseY);
        setWidth(getWidth());
        setHeight(getHeight());
    }

    @Override
    public void hudUpdate() {
        super.hudUpdate();
        setWidth(getWidth());
        setHeight(getHeight());
    }

    @Override
    public float getWidth() {
        return 30.0f;
    }

    @Override
    public float getHeight() {
        return Managers.TEXT.getStringHeight();
    }

}
