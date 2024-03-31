package me.earth.earthhack.impl.hud.text.speed;

import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.client.SimpleHudData;
import me.earth.earthhack.impl.util.math.MathUtil;
import me.earth.earthhack.impl.util.render.hud.HudRenderUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.gui.DrawContext;

public class HudSpeed extends HudElement {

    private final Setting<Boolean> blocksPerSecond =
            register(new BooleanSetting("BPS", false));
    private final Setting<String> name =
            register(new StringSetting("CustomName", "Speed"));
    private String speed = name.getValue();

    private void render(DrawContext context) {
        double speedValue = MathUtil.round((blocksPerSecond.getValue() ? Managers.SPEED.getSpeed() * 0.621371 : Managers.SPEED.getSpeed()), 2);
        speed = name.getValue() + " " + TextColor.GRAY + speedValue + (blocksPerSecond.getValue() ? "bps" : "km/h");
        HudRenderUtil.renderText(context, speed, getX(), getY());
    }

    public HudSpeed() {
        super("Speed", HudCategory.Text, 160, 200);
        this.setData(new SimpleHudData(this, "Displays your current speed"));
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
        return Managers.TEXT.getStringWidth(speed);
    }

    @Override
    public float getHeight() {
        return Managers.TEXT.getStringHeight();
    }

}
