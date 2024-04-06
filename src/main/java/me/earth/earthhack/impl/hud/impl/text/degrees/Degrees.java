package me.earth.earthhack.impl.hud.impl.text.degrees;

import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.client.SimpleHudData;
import me.earth.earthhack.impl.util.render.hud.HudRenderUtil;
import me.earth.earthhack.impl.util.text.TextColor;
import net.minecraft.client.gui.DrawContext;

public class Degrees extends HudElement {

    private final Setting<Boolean> pitch =
            register(new BooleanSetting("Pitch", true));
    private final Setting<Boolean> yaw =
            register(new BooleanSetting("Yaw", true));
    private final Setting<Boolean> customBrackets =
            register(new BooleanSetting("CustomBrackets", true));

    private static String deg = "";

    private void render(DrawContext context) {
        if (mc.player != null) {
            deg = actualBracket()[0] + " ";
            if (pitch.getValue())
                deg += String.valueOf(((int) (mc.player.getPitch() * 100) / 100.0f));
            if (yaw.getValue()) {
                if (pitch.getValue())
                    deg += " : ";
                deg += String.valueOf(((int) (mc.player.getBodyYaw() * 100) / 100.0f));
            }
            deg +=  " " + actualBracket()[1];
        }

        HudRenderUtil.renderText(context, deg, getX(), getY());
    }

    private String[] actualBracket() {
        if (customBrackets.getValue())
            return new String[]{ HudRenderUtil.getBracketColor() + HudRenderUtil.brackets()[0] + HudRenderUtil.bracketsTextColor(), HudRenderUtil.getBracketColor() + HudRenderUtil.brackets()[1] + TextColor.WHITE };
        else
            return new String[]{ TextColor.GRAY + "[", TextColor.GRAY + "]"};
    }

    public Degrees() {
        super("Degrees", HudCategory.Text, 180, 90);
        this.setData(new SimpleHudData(this, "Displays the direction you're currently facing."));
    }

    @Override
    public void draw(DrawContext context) {
        render(context);
    }

    @Override
    public void guiDraw(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        super.guiDraw(context, mouseX, mouseY, partialTicks);
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
        return Managers.TEXT.getStringWidth(deg);
    }

    @Override
    public float getHeight() {
        return Managers.TEXT.getStringHeight();
    }

    private enum Mode {
        Rotation,
        Camera
    }

}
