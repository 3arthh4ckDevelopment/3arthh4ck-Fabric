package me.earth.earthhack.impl.hud.text.pops;

import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.StringSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.client.SimpleHudData;
import me.earth.earthhack.impl.util.render.hud.HudRenderUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.gui.DrawContext;

//TODO: enemies tracker
public class Pops extends HudElement {

    private final Setting<String> name =
            register(new StringSetting("CustomName", "Pops"));

    private String popsNumber = name.getValue();

    private void render(DrawContext context) {
        if (mc.player != null) {
            int pops = Managers.COMBAT.getPops(mc.player);
            popsNumber = name.getValue() + " " + TextColor.GRAY + (pops <= 0 ? "0" : "-" + pops + " ");
        }
        HudRenderUtil.renderText(context, popsNumber, getX(), getY());
    }

    public Pops() {
        super("Pops", HudCategory.Text, 60, 70);
        this.setData(new SimpleHudData(this, "Displays how many totems you've popped."));
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
        return Managers.TEXT.getStringWidth(popsNumber);
    }

    @Override
    public float getHeight() {
        return Managers.TEXT.getStringHeight();
    }

}
