package me.earth.earthhack.impl.hud.text.session;

import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.Earthhack;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.render.hud.HudRenderUtil;
import net.minecraft.client.gui.DrawContext;

public class Session extends HudElement {

    private final Setting<Boolean> seconds =
            register(new BooleanSetting("Show-Seconds", true));

    private String text = "";

    protected void onRender(DrawContext context) {
        long time = System.currentTimeMillis() - Earthhack.startMS;
        long s = time / 1000;
        long m = s / 60;
        long h = m / 60;
        s %= 60;
        m %= 60;

        text = ((h == 0 ? "" : h + ":") + (m < 10 ? "0" : "") + m + (seconds.getValue() ? ":" + (s < 10 ? "0" : "") + s : ""));

        HudRenderUtil.renderText(context, text, getX(), getY());
    }

    public Session() {
        super("Session", "Displays the time you've been playing.", HudCategory.Text, 300, 70);
    }

    @Override
    public float getWidth() {
        return Managers.TEXT.getStringWidth(text.trim());
    }

    @Override
    public float getHeight() {
        return Managers.TEXT.getStringHeight();
    }
}
