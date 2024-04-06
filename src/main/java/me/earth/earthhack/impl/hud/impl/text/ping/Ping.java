package me.earth.earthhack.impl.hud.impl.text.ping;

import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.client.SimpleHudData;
import me.earth.earthhack.impl.util.network.ServerUtil;
import me.earth.earthhack.impl.util.render.hud.HudRenderUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.gui.DrawContext;

public class Ping extends HudElement {

    private final Setting<Boolean> showMs =
            register(new BooleanSetting("ShowMs", true));
    private final Setting<String> name =
            register(new StringSetting("CustomName", "Ping"));

    private String ping = name.getValue();

    private void render(DrawContext context) {
        if (mc.player != null && mc.world != null)
            ping = name.getValue() + " " + TextColor.GRAY + ServerUtil.getPing() + (showMs.getValue() ? "ms" : "");
        HudRenderUtil.renderText(context, ping, getX(), getY());
    }

    public Ping() {
        super("Ping", HudCategory.Text, 30, 30);
        this.setData(new SimpleHudData(this, "Displays your ping."));
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
        return Managers.TEXT.getStringWidth(ping);
    }

    @Override
    public float getHeight() {
        return Managers.TEXT.getStringHeight();
    }

}
