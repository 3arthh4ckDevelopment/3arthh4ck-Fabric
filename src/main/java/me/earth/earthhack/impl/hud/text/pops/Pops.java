package me.earth.earthhack.impl.hud.text.pops;

import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.render.hud.HudRenderUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.gui.DrawContext;

//TODO: enemies tracker
public class Pops extends HudElement {

    private final Setting<String> name =
            register(new StringSetting("CustomName", "Pops"));

    private String text = "";

    protected void onRender(DrawContext context) {
        int pops = Managers.COMBAT.getPops(mc.player);
        text = name.getValue() + " " + TextColor.GRAY + (pops <= 0 ? "0" : "-" + pops + " ");
        HudRenderUtil.renderText(context, text, getX(), getY());
    }

    public Pops() {
        super("Pops", "Displays how many totems you've popped.", HudCategory.Text, 60, 70);
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
