package me.earth.earthhack.impl.hud.text.serverbrand;

import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.client.SimpleHudData;
import me.earth.earthhack.impl.util.render.hud.HudRenderUtil;
import me.earth.earthhack.impl.util.text.TextColor;

public class ServerBrand extends HudElement {

    private static String serverBrand = "";

    private final Setting<String> name =
            register(new StringSetting("BrandText", "ServerBrand"));

    private void render() {
        if (mc.player != null)
            serverBrand = name.getValue() + " " + TextColor.GRAY + mc.player.getServerBrand();
        HudRenderUtil.renderText(serverBrand, getX(), getY());
    }

    public ServerBrand() {
        super("ServerBrand", HudCategory.Text, 35, 50);
        this.setData(new SimpleHudData(this, "Displays the server brand."));
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
        setHeight(getHeight());
    }

    @Override
    public void hudUpdate(float partialTicks) {
        super.hudUpdate(partialTicks);
        setWidth(getWidth());
        setHeight(getHeight());
    }

    @Override
    public float getWidth() {
        return Managers.TEXT.getStringWidth(serverBrand);
    }

    @Override
    public float getHeight() {
        return Managers.TEXT.getStringHeight();
    }

}
