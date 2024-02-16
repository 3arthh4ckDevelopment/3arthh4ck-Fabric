package me.earth.earthhack.impl.hud.text.serverbrand;

import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.client.SimpleHudData;
import me.earth.earthhack.impl.util.render.hud.HudRenderUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.gui.DrawContext;

public class ServerBrand extends HudElement {

    private static String serverBrand = "";

    private final Setting<String> name =
            register(new StringSetting("BrandText", "ServerBrand"));

    private void render(DrawContext context) {
        if (mc.player != null)
            serverBrand = name.getValue() + " " + TextColor.GRAY + (mc.isInSingleplayer() && mc.getServer() != null ? "singleplayer" : mc.getServer().getServerModName());
        HudRenderUtil.renderText(context, serverBrand, getX(), getY());
    }

    public ServerBrand() {
        super("ServerBrand", HudCategory.Text, 35, 50);
        this.setData(new SimpleHudData(this, "Displays the server brand."));
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
        return Managers.TEXT.getStringWidth(serverBrand);
    }

    @Override
    public float getHeight() {
        return Managers.TEXT.getStringHeight();
    }

}
