package me.earth.earthhack.impl.hud.text.degrees;

import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.client.SimpleHudData;
import me.earth.earthhack.impl.util.render.hud.HudRenderUtil;
import me.earth.earthhack.impl.util.text.TextColor;

public class Degrees extends HudElement {

    private final Setting<Boolean> pitch =
            register(new BooleanSetting("Pitch", true));
    private final Setting<Boolean> yaw =
            register(new BooleanSetting("Yaw", true));
    private final Setting<Boolean> customBrackets =
            register(new BooleanSetting("CustomBrackets", true));

    private static String deg = "";

    private void render() {
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

        HudRenderUtil.renderText(deg, getX(), getY());
    }

    private String[] actualBracket() {
        if (customBrackets.getValue())
            return new String[]{ HudRenderUtil.BracketsColor() + HudRenderUtil.Brackets()[0] + HudRenderUtil.BracketsTextColor(), HudRenderUtil.BracketsColor() + HudRenderUtil.Brackets()[1] + TextColor.WHITE };
        else
            return new String[]{ TextColor.GRAY + "[", TextColor.GRAY + "]"};
    }

    public Degrees() {
        super("Degrees", HudCategory.Text, 180, 90);
        this.setData(new SimpleHudData(this, "Displays the direction you're currently facing."));
    }

    @Override
    public void hudDraw(float partialTicks) {
        render();
    }

    @Override
    public void guiDraw(int mouseX, int mouseY, float partialTicks) {
        super.guiDraw(mouseX, mouseY, partialTicks);
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
