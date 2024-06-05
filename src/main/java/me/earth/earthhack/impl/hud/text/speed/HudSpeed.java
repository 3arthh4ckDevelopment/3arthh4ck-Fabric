package me.earth.earthhack.impl.hud.text.speed;

import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.render.hud.HudRenderUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.gui.DrawContext;

public class HudSpeed extends HudElement {

    private final Setting<Boolean> blocksPerSecond =
            register(new BooleanSetting("BPS", false));
    private final Setting<String> name =
            register(new StringSetting("CustomName", "Speed"));

    private String text = "";

    protected void onRender(DrawContext context) {
        String speed = String.format("%.2f", (blocksPerSecond.getValue() ? Managers.SPEED.getSpeed() * 0.621371 : Managers.SPEED.getSpeed()));
        text = name.getValue() + " " + TextColor.GRAY + speed + (blocksPerSecond.getValue() ? "bps" : "km/h");
        HudRenderUtil.renderText(context, text, getX(), getY());
    }

    public HudSpeed() {
        super("Speed", "Displays your current speed", HudCategory.Text, 160, 200);
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
