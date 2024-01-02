package me.earth.earthhack.impl.hud.text.direction;

import me.earth.earthhack.api.hud.HudCategory;
import me.earth.earthhack.api.hud.HudElement;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.util.client.SimpleHudData;
import me.earth.earthhack.impl.util.math.rotation.RotationUtil;
import me.earth.earthhack.impl.util.render.hud.HudRenderUtil;
import me.earth.earthhack.impl.util.text.TextColor;

public class Direction extends HudElement {

    private final Setting<Boolean> name =
            register(new BooleanSetting("Name", true));
    private final Setting<Boolean> symbol =
            register(new BooleanSetting("Symbol", true));
    private final Setting<Boolean> customBrackets =
            register(new BooleanSetting("CustomBrackets", true));
    private static String dir = "";

    private void render() {
        if (mc.player != null)
            dir = getDirection4D(name.getValue(), symbol.getValue());
        HudRenderUtil.renderText(dir, getX(), getY());
    }

    public String getDirection4D(boolean name, boolean symbol)
    {
        String nameValue, symbolValue;
        switch (RotationUtil.getDirection4D())
        {
            case 0:
                nameValue = "South";
                symbolValue = symbolBuilder("+Z", name);
                break;
            case 1:
                nameValue = "West";
                symbolValue = symbolBuilder("-X", name);
                break;
            case 2:
                nameValue = "North";
                symbolValue = symbolBuilder("-Z", name);
                break;
            case 3:
            default:
                nameValue = "East";
                symbolValue = symbolBuilder("+X", name);
                break;
        }
        return (name ? nameValue :  "") + (name && symbol ? " " : "") + (symbol ? symbolValue : "");
    }

    private String symbolBuilder(String dir, boolean name) {
        if (!name)
            return dir;
        return TextColor.GRAY + actualBracket()[0] + TextColor.WHITE + dir + TextColor.GRAY + actualBracket()[1];
    }

    private String[] actualBracket() {
        if (customBrackets.getValue())
            return new String[]{ HudRenderUtil.BracketsColor() + HudRenderUtil.Brackets()[0] + HudRenderUtil.BracketsTextColor(), HudRenderUtil.BracketsColor() + HudRenderUtil.Brackets()[1] + TextColor.WHITE };
        else
            return new String[]{ TextColor.GRAY + "[", TextColor.GRAY + "]"};
    }

    public Direction() {
        super("Direction", HudCategory.Text, 180, 90);
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
        return Managers.TEXT.getStringWidth(dir);
    }

    @Override
    public float getHeight() {
        return Managers.TEXT.getStringHeight();
    }

}
